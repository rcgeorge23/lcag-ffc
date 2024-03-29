package uk.co.novinet.service;

import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.model.Charge;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.co.novinet.rest.PaymentStatus;
import uk.co.novinet.rest.PaymentType;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.beanutils.PropertyUtils.describe;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.co.novinet.service.PersistenceUtils.*;

@Service
public class PaymentService {
    private static final long REFERENCE_SEED = 90000L;
    private static final int EMAIL_NOT_SENT = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PdfRendererService pdfRendererService;

    @Value("${secretStripeApiKey}")
    private String secretStripeApiKey;


    public void executePayment(Payment payment) {
        LOGGER.info("Going to execute payment for: {}", payment);

        try {
            Stripe.apiKey = secretStripeApiKey;

            Map<String, Object> chargeMap = new HashMap<>();

            chargeMap.put("amount", payment.getGrossAmount().multiply(BigDecimal.valueOf(100)).longValue());
            chargeMap.put("currency", "gbp");
            chargeMap.put("metadata", filterEmptyStringValues(describe(payment),
                    asList("class", "uiFriendlyPaymentReceivedDate", "uiFriendlyInvoiceCreatedDate", "uiFriendlyGrossAmount",
                            "hash", "membershipToken", "errorDescription", "paymentStatus", "addressLine2",
                            "signatureData", "signedContributionAgreement", "invoiceCreated", "id")));

            chargeMap.put("source", payment.getStripeToken());

            Charge charge = Charge.create(chargeMap);

            LOGGER.info("Charge: {}", charge);

            if (!"authorized".equals(charge.getOutcome().getType())) {
                updateFfcContributionStatus(payment, PaymentStatus.DECLINED, "Payment was declined");
                throw new CardDeclinedException();
            }

            updateFfcContributionStatus(payment, PaymentStatus.AUTHORIZED, "");
        } catch (Exception e) {
            if (e instanceof CardException) {
                String errorCode = ((CardException) e).getCode();
                if ("card_declined".equals(errorCode)) {
                    updateFfcContributionStatus(payment, PaymentStatus.DECLINED, "Payment was declined");
                } else {
                    updateFfcContributionStatus(payment, PaymentStatus.UNKNOWN_ERROR, e.getMessage());
                }
            } else {
                updateFfcContributionStatus(payment, PaymentStatus.UNKNOWN_ERROR, e.getMessage());
            }

            LOGGER.error("An error occurred trying to make the payment: {}", payment);

            throw new RuntimeException(e);
        }
    }

    private Map<String, String> filterEmptyStringValues(Map<String, Object> description, List<String> excludeProperties) {
        Map<String, String> result = new HashMap<>();

        description.keySet().stream().forEach(key -> {
            if (!excludeProperties.contains(key) && description.get(key) != null && isNotBlank(valueOf(description.get(key)))) {
                result.put(key, valueOf(description.get(key)));
            }
        });

        return result;
    }

    public Payment createFfcContribution(Payment payment) {
        LOGGER.info("Going to create new contribution for payment: {}", payment);

        Long nextAvailableId = findNextAvailableId("id", contributionsTableName());

        String insertSql = "insert into " + contributionsTableName() +
                " (`id`, `user_id`, `username`, `hash`, `membership_token`, `first_name`, `last_name`, `email_address`, `gross_amount`, " +
                "`invoice_created`, `payment_received`, `payment_type`, `stripe_token`, `status`, `reference`, `payment_method`, `guid`, `address_line_1`, " +
                "`address_line_2`, `city`, `postal_code`, `country`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        LOGGER.info("Going to execute insert sql: {}", insertSql);

        int result = jdbcTemplate.update(insertSql,
                nextAvailableId,
                payment.getUserId(),
                payment.getUsername(),
                payment.getHash(),
                payment.getMembershipToken(),
                payment.getFirstName(),
                payment.getLastName(),
                payment.getEmailAddress(),
                payment.getGrossAmount(),
                unixTime(Instant.now()),
                unixTime(Instant.now()),
                payment.getPaymentType().toString(),
                payment.getStripeToken(),
                PaymentStatus.NEW.toString(),
                buildReference(nextAvailableId),
                "Card",
                payment.getGuid(),
                payment.getAddressLine1(),
                payment.getAddressLine2(),
                payment.getCity(),
                payment.getPostalCode(),
                payment.getCountry()
        );

        LOGGER.info("Insertion result: {}", result);

        payment.setId(nextAvailableId);

        return payment;
    }

    public void updateFfcContributionStatus(Payment payment, PaymentStatus paymentStatus, String errorDescription) {
        LOGGER.info("Going to update contribution: {} payment status to : {}", payment, paymentStatus);

        String updateSql = "update " + contributionsTableName() + " set `status` = ?, `error_description` = ? where id = ?;";

        LOGGER.info("Going to execute update sql: {}", updateSql);

        int result = jdbcTemplate.update(
                updateSql,
                paymentStatus.toString(),
                errorDescription,
                payment.getId()
        );

        LOGGER.info("Update result: {}", result);
    }

    private String buildReference(Long nextAvailableId) {
        return "LCAGFFC" + (REFERENCE_SEED + nextAvailableId);
    }

    public void markContributionEmailSent(Payment payment) {
        LOGGER.info("Going to mark email sent for contribution: {}", payment);
        String updateSql = "update " + contributionsTableName() + " set `email_sent` = 1 where id = ?;";

        LOGGER.info("Going to execute update sql: {}", updateSql);

        int result = jdbcTemplate.update(updateSql, payment.getId());

        LOGGER.info("Update result: {}", result);
    }

    public List<Payment> getFfcContributionsAwaitingEmails() {
        LOGGER.info("Going to find contributions awaiting emails");

        String sql = "select * from " + contributionsTableName() + " where `email_sent` = 0 and payment_type <> ? and status = ? and has_provided_signature = 1;";

        return jdbcTemplate.query(sql, new Object[] {
                PaymentType.ANONYMOUS.toString(),
                PaymentStatus.AUTHORIZED.toString()
        }, (rs, rowNum) -> buildPayment(rs));
    }

    private Payment buildPayment(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("username"),
                rs.getString("membership_token"),
                rs.getString("hash"),
                rs.getString("reference"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email_address"),
                rs.getString("address_line_1"),
                rs.getString("address_line_2"),
                rs.getString("city"),
                rs.getString("postal_code"),
                rs.getString("country"),
                rs.getBigDecimal("gross_amount"),
                dateFromMyBbRow(rs, "invoice_created"),
                dateFromMyBbRow(rs, "payment_received"),
                rs.getString("stripe_token"),
                PaymentStatus.valueOf(rs.getString("status")),
                rs.getString("error_description"),
                PaymentType.valueOf(rs.getString("payment_type")),
                rs.getString("payment_method"),
                rs.getString("guid"),
                rs.getString("signature_data"),
                rs.getBoolean("has_provided_signature"),
                rs.getBytes("signed_contribution_agreement"),
                dateFromMyBbRow(rs, "contribution_agreement_signature_date")
        );
    }

    public Payment findPaymentForGuid(String guid) {
        if (guid == null || StringUtils.isBlank(guid)) {
            return null;
        }

        LOGGER.info("Going to find payment with guid: {}", guid);

        String sql = "select * from " + contributionsTableName() + " where `guid` = ?;";

        List<Payment> payments = jdbcTemplate.query(sql, new Object[] {
                guid
        }, (rs, rowNum) -> buildPayment(rs));

        LOGGER.info("Found payments: {}", payments);

        if (payments == null || payments.isEmpty()) {
            return null;
        }

        return payments.get(0);
    }

    public void addSignatureToContributionAgreement(Payment payment, String signatureData) {
        LOGGER.info("Going to add signature: {} to payment: {}", signatureData, payment);

        String updateSql = "update " + contributionsTableName() + " set " +
                "`signature_data` = ?, " +
                "`contribution_agreement_signature_date` = ? " +
                "where id = ?;";

        LOGGER.info("Going to execute update sql: {}", updateSql);

        int result = jdbcTemplate.update(
            updateSql,
            signatureData,
            unixTime(Instant.now()),
            payment.getId()
        );

        LOGGER.info("Update result: {}", result);

        renderAndPersistSignedContributionAgreementPdf(payment);
    }

    private void renderAndPersistSignedContributionAgreementPdf(Payment payment) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdfRendererService.render(DocumentType.CONTRIBUTION_AGREEMENT, payment.getGuid(), out);

        String updateSql = "update " + contributionsTableName() + " set " +
                "`signed_contribution_agreement` = ?, " +
                "`has_provided_signature` = 1 " +
                "where id = ?;";

        LOGGER.info("Going to execute update sql: {}", updateSql);

        int result = jdbcTemplate.update(
                updateSql,
                out.toByteArray(),
                payment.getId()
        );

        LOGGER.info("Update result: {}", result);
    }
}

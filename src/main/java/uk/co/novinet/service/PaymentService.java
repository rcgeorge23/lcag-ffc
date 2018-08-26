package uk.co.novinet.service;

import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.model.Charge;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.co.novinet.rest.PaymentStatus;
import uk.co.novinet.rest.PaymentType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.apache.commons.beanutils.PropertyUtils.describe;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.co.novinet.service.PersistenceUtils.contributionsTableName;
import static uk.co.novinet.service.PersistenceUtils.dateFromMyBbRow;

@Service
public class PaymentService {
    private static final int EMAIL_NOT_SENT = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberService memberService;

    @Value("${secretStripeApiKey}")
    private String secretStripeApiKey;


    public void executePayment(Payment payment) {
        LOGGER.info("Going to execute payment for: {}", payment);

        try {
            Stripe.apiKey = secretStripeApiKey;

            Map<String, Object> chargeMap = new HashMap<>();

            chargeMap.put("amount", payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
            chargeMap.put("currency", "gbp");
            chargeMap.put("metadata", filterEmptyStringValues(describe(payment)));
            chargeMap.put("source", payment.getStripeToken()); // obtained via Stripe.js
            Charge charge = Charge.create(chargeMap);
            LOGGER.info("Charge: {}", charge);

            if (!"authorized".equals(charge.getOutcome().getType())) {
                memberService.updateFfcContributionStatus(payment, PaymentStatus.DECLINED);
                throw new CardDeclinedException();
            }

            memberService.updateFfcContributionStatus(payment, PaymentStatus.AUTHORIZED);
        } catch (Exception e) {
            if (e instanceof CardException) {
                if ("card_declined".equals(((CardException) e).getCode())) {
                    memberService.updateFfcContributionStatus(payment, PaymentStatus.DECLINED);
                } else {
                    memberService.updateFfcContributionStatus(payment, PaymentStatus.UNKNOWN_ERROR);
                }
            }
            LOGGER.error("An error occurred trying to make the payment: {}", payment);
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> filterEmptyStringValues(Map<String, Object> description) {
        Map<String, String> result = new HashMap<>();

        description.keySet().stream().forEach(key -> {
            if (description.get(key) != null && isNotBlank(valueOf(description.get(key)))) {
                result.put(key, valueOf(description.get(key)));
            }
        });

        return result;
    }

    public List<Payment> getFfcContributionsAwaitingEmails() {
        LOGGER.info("Going to find contributions awaiting emails");

        String sql = "select * from " + contributionsTableName() + " where `email_sent` = ? and payment_type <> ? and status = ?;";

        return jdbcTemplate.query(sql, new Object[] {
                EMAIL_NOT_SENT,
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
                rs.getBigDecimal("amount"),
                dateFromMyBbRow(rs, "date"),
                rs.getString("stripe_token"),
                PaymentStatus.valueOf(rs.getString("status")),
                PaymentType.valueOf(rs.getString("payment_type")),
                ContributionType.valueOf(rs.getString("contribution_type")),
                rs.getString("guid")
        );
    }

    public Payment findPaymentForGuid(String guid) {
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
}

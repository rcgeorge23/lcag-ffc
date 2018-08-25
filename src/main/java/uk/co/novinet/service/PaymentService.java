package uk.co.novinet.service;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.co.novinet.rest.PaymentStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
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
            chargeMap.put("metadata", buildMap(payment));
            chargeMap.put("source", payment.getStripeToken()); // obtained via Stripe.js
            Charge charge = Charge.create(chargeMap);

            memberService.updateFfcContributionStatus(payment, PaymentStatus.SENT);

            LOGGER.info("Charge: {}", charge);
        } catch (Exception e) {
            LOGGER.error("An error occurred trying to make the payment: {}", payment);
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> buildMap(Payment payment) {
        return null;
    }

}

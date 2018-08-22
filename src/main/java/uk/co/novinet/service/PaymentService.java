package uk.co.novinet.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.co.novinet.rest.PaymentBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${secretStripeApiKey}")
    private String secretStripeApiKey;

    public void executePayment(PaymentBean paymentBean) {
        Stripe.apiKey = secretStripeApiKey;

        Map<String, Object> chargeMap = new HashMap<>();

        chargeMap.put("amount", paymentBean.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        chargeMap.put("currency", "gbp");
        chargeMap.put("source", paymentBean.getStripeToken()); // obtained via Stripe.js

        try {
            Charge charge = Charge.create(chargeMap);
            System.out.println(charge);
        } catch (StripeException e) {
            LOGGER.error("An error occurred trying to make the payment: {}", paymentBean);
            throw new RuntimeException(e);
        }
    }

}

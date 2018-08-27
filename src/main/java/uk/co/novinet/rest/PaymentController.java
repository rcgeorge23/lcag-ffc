package uk.co.novinet.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.novinet.service.Member;
import uk.co.novinet.service.MemberService;
import uk.co.novinet.service.Payment;
import uk.co.novinet.service.PaymentService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private PaymentService paymentService;

    @Value("${publishableStripeApiKey}")
    private String publishableStripeApiKey;

    @Value("${contributionAgreementMinimumAmountGbp}")
    private String contributionAgreementMinimumAmountGbp;

    @CrossOrigin
    @GetMapping(path = "/config")
    public Map<String, String> config() {
        return new HashMap<String, String>() {{
            put("key", publishableStripeApiKey);
            put("contributionAgreementMinimumAmountGbp", contributionAgreementMinimumAmountGbp);
        }};
    }

    @CrossOrigin
    @GetMapping(path = "/payment")
    public Payment getPayment(String guid) {
        return paymentService.findPaymentForGuid(guid);
    }

}
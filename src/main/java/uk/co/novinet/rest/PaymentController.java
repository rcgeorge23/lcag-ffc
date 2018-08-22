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

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private MemberService memberService;

    @Value("${publishableStripeApiKey}")
    private String publishableStripeApiKey;

    @GetMapping("/member")
    public Member getMemberByToken(@RequestParam("token") String token) {
        return memberService.findMemberByClaimToken(token);
    }

    @CrossOrigin
    @GetMapping(path = "/publicStripeKey")
    public Map<String, String> publicStripeKey() {
        return new HashMap<String, String>() {{ put("key", publishableStripeApiKey); }};
    }

}
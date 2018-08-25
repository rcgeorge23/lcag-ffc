package uk.co.novinet.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.novinet.service.*;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/")
    public String get() {
        return "home";
    }

    @CrossOrigin
    @PostMapping(path = "/submit")
    public ModelAndView submit(ModelMap model, Payment payment) {
        try {
            LOGGER.info("model: {}", model);
            LOGGER.info("payment: {}", payment);
            memberService.fillInBlanks(payment);
            memberService.createFfcContribution(payment);
            paymentService.executePayment(payment);
            memberService.createForumUserIfNecessary(payment);
            return new ModelAndView("thankYou", model);
        } catch (Exception e) {
            LOGGER.error("Unable to make payment", e);
            return new ModelAndView("redirect:/", model);
        }
    }
}
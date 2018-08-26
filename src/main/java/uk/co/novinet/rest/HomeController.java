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

            Member member = null;
            MemberCreationResult memberCreationResult = null;

            switch (payment.getPaymentType()) {
                case EXISTING_LCAG_MEMBER:
                    member = memberService.findMemberByUsername(payment.getUsername());
                    break;
                case NEW_LCAG_MEMBER:
                    memberCreationResult = memberService.createForumUserIfNecessary(payment);
                    member = memberCreationResult.getMember();
                    break;
            }

            if (member == null) {
                throw new RuntimeException("Member not found");
            }

            memberService.fillInBlanks(payment, member, memberCreationResult == null ? false : memberCreationResult.memberAlreadyExisted());
            memberService.createFfcContribution(payment);
            paymentService.executePayment(payment);
            model.addAttribute("guid", payment.getGuid());
            return new ModelAndView("thankYou", model);
        } catch (Exception e) {
            model.addAttribute("guid", payment == null ? null : payment.getGuid());
            LOGGER.error("Unable to make payment", e);
            return new ModelAndView("redirect:/", model);
        }
    }
}
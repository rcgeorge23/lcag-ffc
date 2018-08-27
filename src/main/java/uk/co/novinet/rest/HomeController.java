package uk.co.novinet.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.novinet.service.*;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private PaymentService paymentService;

    @Value("${contributionAgreementMinimumAmountGbp}")
    private String contributionAgreementMinimumAmountGbp;

    @GetMapping("/")
    public String getHome() {
        return "home";
    }

    @GetMapping("/thankYou")
    public String getThankYou() {
        return "thankYou";
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

            memberService.fillInBlanks(payment, member, memberCreationResult == null ? false : memberCreationResult.memberAlreadyExisted());
            paymentService.createFfcContribution(payment);

            validatePayment(payment);

            paymentService.executePayment(payment);
            model.addAttribute("guid", payment.getGuid());
            return new ModelAndView("redirect:/thankYou", model);
        } catch (Exception e) {
            model.addAttribute("guid", payment == null ? null : payment.getGuid());
            LOGGER.error("Unable to make payment", e);
            return new ModelAndView("redirect:/", model);
        }
    }

    void validatePayment(Payment payment) throws LcagValidationException {
        if (payment.getAmount() == null) {
            String message = "Amount is mandatory";
            paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
            throw new LcagValidationException(message);
        }

        if (payment.getContributionType() == ContributionType.CONTRIBUTION_AGREEMENT) {
            if (payment.getAmount() == null || payment.getAmount().compareTo(new BigDecimal(contributionAgreementMinimumAmountGbp)) < 0) {
                String message = "Contribution Agreement amount must be at least £" + contributionAgreementMinimumAmountGbp;
                paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
                throw new LcagValidationException(message);
            }
        }

        if (payment.getPaymentType() == PaymentType.NEW_LCAG_MEMBER) {
            if (isBlank(payment.getFirstName())) {
                String message = "First Name is mandatory";
                paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
                throw new LcagValidationException(message);
            }

            if (isBlank(payment.getLastName())) {
                String message = "Last Name is mandatory";
                paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
                throw new LcagValidationException(message);
            }

            if (isBlank(payment.getEmailAddress())) {
                String message = "Email Address is mandatory";
                paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
                throw new LcagValidationException(message);
            }
        }

        if (payment.getPaymentType() == PaymentType.EXISTING_LCAG_MEMBER) {
            if (isBlank(payment.getUsername()) || memberService.findMemberByUsername(payment.getUsername()) == null) {
                String message = "A valid LCAG username must be provided";
                paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
                throw new LcagValidationException(message);
            }
        }
    }
}
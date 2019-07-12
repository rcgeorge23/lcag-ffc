package uk.co.novinet.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.co.novinet.service.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

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

    @Value("${vatRate}")
    private String vatRate;

    @CrossOrigin
    @GetMapping(path = "/config")
    public Map<String, String> config() {
        return new HashMap<String, String>() {{
            put("key", publishableStripeApiKey);
            put("contributionAgreementMinimumAmountGbp", contributionAgreementMinimumAmountGbp);
            put("vatRate", vatRate);
        }};
    }

    @CrossOrigin
    @GetMapping(path = "/payment")
    public Payment getPayment(String guid) {
        return paymentService.findPaymentForGuid(guid);
    }


    @CrossOrigin
    @PostMapping(path = "/submit")
    public String submitPayment(ModelMap model, Payment payment) {
        MemberCreationResult memberCreationResult = null;
        try {
            LOGGER.info("model: {}", model);
            LOGGER.info("payment: {}", payment);

            memberCreationResult = prePaymentActions(payment);

            return paymentService.startStripePaymentSession(payment);
        } catch (Exception e) {
            model.addAttribute("guid", payment == null ? null : payment.getGuid());
            LOGGER.error("Unable to start payment session", e);

            if (memberCreationResult != null && !memberCreationResult.memberAlreadyExisted()) {
                LOGGER.error("Deleting new member to free up email address for another payment attempt", e);
                memberService.softDeleteMember(memberCreationResult.getMember());
            }

            return null;
        }
    }

    private MemberCreationResult prePaymentActions(Payment payment) throws LcagValidationException {
        MemberCreationResult memberCreationResult = null;

        switch (payment.getPaymentType()) {
            case EXISTING_LCAG_MEMBER:
                memberCreationResult = new MemberCreationResult(true, memberService.findMemberByUsername(payment.getUsername()));
                break;
            case NEW_LCAG_MEMBER:
                memberCreationResult = memberService.createForumUserIfNecessary(payment);
                break;
        }

        memberService.fillInPaymentBlanks(payment, memberCreationResult);
        paymentService.createFfcContribution(payment);

        validatePayment(payment);

        return memberCreationResult;
    }

    void validatePayment(Payment payment) throws LcagValidationException {
        if (payment.getGrossAmount() == null || payment.getGrossAmount().compareTo(new BigDecimal(1)) < 0) {
            throw updatePaymentForValidationErrorAndThrowException(payment, "Amount must be at least £1.00");
        }

        if (payment.getPaymentType() == PaymentType.ANONYMOUS) {
            throw updatePaymentForValidationErrorAndThrowException(payment, "Payment cannot be anonymous if contribution is to be made by Contribution Agreement");
        }

        if (isBlank(payment.getAddressLine1())) {
            throw updatePaymentForValidationErrorAndThrowException(payment, "Address line 1 is mandatory");
        }

        if (isBlank(payment.getCity())) {
            throw updatePaymentForValidationErrorAndThrowException(payment, "City is mandatory");
        }

        if (isBlank(payment.getPostalCode())) {
            throw updatePaymentForValidationErrorAndThrowException(payment, "Postal code is mandatory");
        }

        if (isBlank(payment.getCountry())) {
            throw updatePaymentForValidationErrorAndThrowException(payment, "Country is mandatory");
        }

        if (payment.getPaymentType() == PaymentType.NEW_LCAG_MEMBER) {
            if (isBlank(payment.getFirstName())) {
                throw updatePaymentForValidationErrorAndThrowException(payment, "First Name is mandatory");
            }

            if (isBlank(payment.getLastName())) {
                throw updatePaymentForValidationErrorAndThrowException(payment, "Last Name is mandatory");
            }

            if (isBlank(payment.getEmailAddress())) {
                throw updatePaymentForValidationErrorAndThrowException(payment, "Email Address is mandatory");
            }
        }

        if (payment.getPaymentType() == PaymentType.EXISTING_LCAG_MEMBER) {
            if (isBlank(payment.getUsername()) || memberService.findMemberByUsername(payment.getUsername()) == null) {
                throw updatePaymentForValidationErrorAndThrowException(payment, "A valid LCAG username must be provided");
            }
        }
    }

    private LcagValidationException updatePaymentForValidationErrorAndThrowException(Payment payment, String message) throws LcagValidationException {
        paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
        return new LcagValidationException(message);
    }

}
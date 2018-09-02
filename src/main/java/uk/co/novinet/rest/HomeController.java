package uk.co.novinet.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.co.novinet.service.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InvoicePdfRendererService invoicePdfRendererService;

    @Value("${contributionAgreementMinimumAmountGbp}")
    private String contributionAgreementMinimumAmountGbp;

    @GetMapping("/")
    public String getHome(HttpServletRequest request) {
        return "home";
    }

    @GetMapping("/thankYou")
    public String getThankYou() {
        return "thankYou";
    }

    @GetMapping("/invoice")
    public String getInvoice(ModelMap model, @RequestParam("guid") String guid) {
        Payment payment = paymentService.findPaymentForGuid(guid);

        if (payment == null) {
            return "invoice";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("member", memberService.findMemberById(payment.getUserId()));

        return "invoice";
    }

    @GetMapping("/contributionAgreement")
    public String getContributionAgreement(ModelMap model, @RequestParam("guid") String guid) {
        Payment payment = paymentService.findPaymentForGuid(guid);

        if (payment == null) {
            return "contributionAgreement";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("member", memberService.findMemberById(payment.getUserId()));

        return "contributionAgreement";
    }

    @ResponseBody
    @GetMapping(path = "/invoiceExport", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportInvoice(@RequestParam("guid") String guid) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoicePdfRendererService.renderInvoicePdf(guid, out);
        return out.toByteArray();
    }

    @ResponseBody
    @GetMapping(path = "/contributionAgreementExport", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportContributionAgreement(@RequestParam("guid") String guid) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoicePdfRendererService.renderContributionAgreementPdf(guid, out);
        return out.toByteArray();
    }

    @CrossOrigin
    @PostMapping(path = "/submit")
    public ModelAndView submit(ModelMap model, Payment payment) {
        try {
            LOGGER.info("model: {}", model);
            LOGGER.info("payment: {}", payment);

            Member member = prePaymentActions(payment);

            paymentService.executePayment(payment);

            postPaymentActions(payment, member);

            model.addAttribute("guid", payment.getGuid());
            return new ModelAndView("redirect:/thankYou", model);
        } catch (Exception e) {
            model.addAttribute("guid", payment == null ? null : payment.getGuid());
            LOGGER.error("Unable to make payment", e);
            return new ModelAndView("redirect:/", model);
        }
    }

    private Member prePaymentActions(Payment payment) throws LcagValidationException {
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

        memberService.fillInPaymentBlanks(payment, member, memberCreationResult == null ? false : memberCreationResult.memberAlreadyExisted());
        paymentService.createFfcContribution(payment);

        validatePayment(payment);

        return member;
    }

    private void postPaymentActions(Payment payment, Member member) {
        switch (payment.getPaymentType()) {
            case EXISTING_LCAG_MEMBER:
                memberService.assignLcagFfcAdditionalGroup(member, payment);
                break;
        }
    }

    void validatePayment(Payment payment) throws LcagValidationException {
        if (payment.getGrossAmount() == null || payment.getGrossAmount().compareTo(new BigDecimal(1)) < 0) {
            throw updatePaymentForValidationErrorAndThrowException(payment, "Amount must be at least £1.00");
        }

        if (payment.getContributionType() == ContributionType.CONTRIBUTION_AGREEMENT) {
            if (payment.getGrossAmount() == null || payment.getGrossAmount().compareTo(new BigDecimal(contributionAgreementMinimumAmountGbp)) < 0) {
                throw updatePaymentForValidationErrorAndThrowException(payment, "Contribution Agreement amount must be at least £" + contributionAgreementMinimumAmountGbp);
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
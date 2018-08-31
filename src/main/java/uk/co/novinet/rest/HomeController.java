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

    @ResponseBody
    @GetMapping(path = "/invoiceExport", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportInvoice(ModelMap model, @RequestParam("guid") String guid, HttpServletRequest request) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoicePdfRendererService.renderPdf(guid, out);
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
                memberService.assignLcagFfcAdditionalGroup(member);
                break;
        }
    }

    void validatePayment(Payment payment) throws LcagValidationException {
        if (payment.getGrossAmount() == null) {
            String message = "Amount is mandatory";
            paymentService.updateFfcContributionStatus(payment, PaymentStatus.VALIDATION_ERROR, message);
            throw new LcagValidationException(message);
        }

        if (payment.getContributionType() == ContributionType.CONTRIBUTION_AGREEMENT) {
            if (payment.getGrossAmount() == null || payment.getGrossAmount().compareTo(new BigDecimal(contributionAgreementMinimumAmountGbp)) < 0) {
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
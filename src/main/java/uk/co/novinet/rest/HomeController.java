package uk.co.novinet.rest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.co.novinet.service.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
public class HomeController {

    private static final String EMPTY_BASE64_ENCODED_SIGNATURE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAUAAAACWCAYAAACvgFEsAAAEYUlEQVR4Xu3UAREAAAgCMelf2iA/GzA8do4AAQJRgUVzi02AAIEzgJ6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQMAA+gECBLICBjBbveAECBhAP0CAQFbAAGarF5wAAQPoBwgQyAoYwGz1ghMgYAD9AAECWQEDmK1ecAIEDKAfIEAgK2AAs9ULToCAAfQDBAhkBQxgtnrBCRAwgH6AAIGsgAHMVi84AQIG0A8QIJAVMIDZ6gUnQOABWPMAl9GmB/4AAAAASUVORK5CYII=";

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PdfRendererService pdfRendererService;

    @Value("${contributionAgreementMinimumAmountGbp}")
    private String contributionAgreementMinimumAmountGbp;

    @Value("${vatNumber}")
    private String vatNumber;

    @Value("${vatRate}")
    private String vatRate;

    @Value("${publishableStripeApiKey}")
    private String publishableStripeApiKey;

    @GetMapping("/")
    public String getHome(ModelMap model, @RequestParam(required = false, name = "guid") String guid) {
        model.addAttribute("formattedContributionAgreementMinimumAmountGbp", new CurrencyStyleFormatter().print(new BigDecimal(contributionAgreementMinimumAmountGbp), Locale.UK));
        model.addAttribute("contributionAgreementMinimumAmountGbp", contributionAgreementMinimumAmountGbp);

        Payment payment = paymentService.findPaymentForGuid(guid);

        if (payment != null) {
            model.addAttribute("payment", payment);
        }

        model.addAttribute("vatRate", new BigDecimal(vatRate));
        model.addAttribute("publishableStripeApiKey", publishableStripeApiKey);

        return "home";
    }

    @GetMapping("/invoice")
    public String getInvoice(ModelMap model, @RequestParam("guid") String guid) {
        Payment payment = paymentService.findPaymentForGuid(guid);

        if (payment == null) {
            return "invoice";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("vatNumber", vatNumber);
        model.addAttribute("member", memberService.findMemberById(payment.getUserId()));

        return "invoice";
    }

    @GetMapping("/termsAndConditions")
    public String getTermsAndconditions(ModelMap model) {
        model.addAttribute("formattedContributionAgreementMinimumAmountGbp", new CurrencyStyleFormatter().print(new BigDecimal(contributionAgreementMinimumAmountGbp), Locale.UK));
        model.addAttribute("contributionAgreementMinimumAmountGbp", contributionAgreementMinimumAmountGbp);

        return "termsAndConditions";
    }

    @ResponseBody
    @GetMapping(path = "/termsAndConditionsExport", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportTermsAndConditions() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdfRendererService.renderTermsAndConditionsPdf(out);
        return out.toByteArray();
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

    @GetMapping("/signContributionAgreement")
    public ModelAndView getSignContributionAgreement(ModelMap model, @RequestParam("guid") String guid) {
        Payment payment = paymentService.findPaymentForGuid(guid);

        if (payment == null) {
            return new ModelAndView("error");
        }

        model.addAttribute("guid", guid);

        if (payment.getHasProvidedSignature()) {
            return new ModelAndView("redirect:/thankYou", model);
        }

        model.addAttribute("payment", payment);
        model.addAttribute("member", memberService.findMemberById(payment.getUserId()));

        return new ModelAndView("signContributionAgreement", model);
    }

    @GetMapping("/thankYou")
    public String getThankYou(ModelMap model, @RequestParam("guid") String guid) {
        Payment payment = paymentService.findPaymentForGuid(guid);

        if (payment == null) {
            return "error";
        }

        model.addAttribute("payment", payment);
        model.addAttribute("member", memberService.findMemberById(payment.getUserId()));
        model.addAttribute("guid", guid);

        return "thankYou";
    }

    @PostMapping("/signContributionAgreement")
    public ModelAndView postSignContributionAgreement(ModelMap model, @RequestParam("signatureData") String signatureData, @RequestParam("guid") String guid) {
        model.addAttribute("guid", guid);

        if (StringUtils.isBlank(signatureData) || EMPTY_BASE64_ENCODED_SIGNATURE.equals(signatureData)) {
            LOGGER.info("Invalid signature data: {}", signatureData);
            return new ModelAndView("redirect:/signContributionAgreement", model);
        }

        Payment payment = paymentService.findPaymentForGuid(guid);

        if (payment == null) {
            return new ModelAndView("redirect:/error", model);
        }

        paymentService.addSignatureToContributionAgreement(payment, signatureData);

        model.addAttribute("payment", payment);
        model.addAttribute("member", memberService.findMemberById(payment.getUserId()));

        return new ModelAndView("redirect:/thankYou", model);
    }

    @GetMapping("/error")
    public String getError() {
        return "error";
    }

    @ResponseBody
    @GetMapping(path = "/invoiceExport", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportInvoice(@RequestParam("guid") String guid) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdfRendererService.renderInvoicePdf(guid, out);
        return out.toByteArray();
    }

    @ResponseBody
    @GetMapping(path = "/contributionAgreementExport", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] exportContributionAgreement(@RequestParam("guid") String guid) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pdfRendererService.renderContributionAgreementPdf(guid, out);
        return out.toByteArray();
    }

    @CrossOrigin
    @PostMapping(path = "/submit")
    public ModelAndView submit(ModelMap model, Payment payment) {
        MemberCreationResult memberCreationResult = null;
        try {
            LOGGER.info("model: {}", model);
            LOGGER.info("payment: {}", payment);

            memberCreationResult = prePaymentActions(payment);

            paymentService.executePayment(payment);

            postPaymentActions(payment, memberCreationResult == null ? null : memberCreationResult.getMember());

            model.addAttribute("guid", payment.getGuid());
            return new ModelAndView("redirect:/signContributionAgreement", model);
        } catch (Exception e) {
            model.addAttribute("guid", payment == null ? null : payment.getGuid());
            LOGGER.error("Unable to make payment", e);

            if (memberCreationResult != null && !memberCreationResult.memberAlreadyExisted()) {
                LOGGER.error("Deleting new member to free up email address for another payment attempt", e);
                memberService.softDeleteMember(memberCreationResult.getMember());
            }

            return new ModelAndView("redirect:/", model);
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
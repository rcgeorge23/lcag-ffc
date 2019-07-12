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

        if (paymentService.paymentHasBeenProcessedByStripe(payment)) {

            postPaymentActions(payment, memberService.findMemberById(payment.getUserId()));

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
        } else {
            return new ModelAndView("paymentPending", model);
        }

    }

    private void postPaymentActions(Payment payment, Member member) {
        switch (payment.getPaymentType()) {
            case EXISTING_LCAG_MEMBER:
                memberService.assignLcagFfcAdditionalGroup(member, payment);
                break;
        }
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

}
package uk.co.novinet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;

@Service
public class InvoicePdfRendererService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoicePdfRendererService.class);

    private static String baseUrl;

    public String getBaseUrl() {
        return InvoicePdfRendererService.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        InvoicePdfRendererService.baseUrl = baseUrl;
    }

    public void render(DocumentType documentType, String guid, OutputStream outputStream) {
        LOGGER.info("Going to try and render documentType: {}, guid: {}, baseUrl: {} ", documentType, guid, baseUrl);
        switch (documentType) {
            case CONTRIBUTION_AGREEMENT:
                renderContributionAgreementPdf(guid, outputStream);
                break;
            case INVOICE:
                renderInvoicePdf(guid, outputStream);
                break;
            case GUIDANCE_NOTES:
                renderTermsAndConditionsPdf(outputStream);
                break;
        }
    }

    public void renderInvoicePdf(String guid, OutputStream outputStream) {
        renderPdf("/invoice?guid=", guid, outputStream);
    }

    public void renderTermsAndConditionsPdf(OutputStream outputStream) {
        renderPdf("/termsAndConditions", outputStream);
    }

    public void renderContributionAgreementPdf(String guid, OutputStream outputStream) {
        renderPdf("/contributionAgreement?guid=", guid, outputStream);
    }

    public void renderPdf(String path, OutputStream outputStream) {
        renderPdf(path, null, outputStream);
    }

    public void renderPdf(String path, String guid, OutputStream outputStream) {
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(baseUrl + path + (guid == null ? "" : guid));
            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

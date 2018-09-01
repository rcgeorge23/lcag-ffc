package uk.co.novinet.service;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;

@Service
public class InvoicePdfRendererService {
    private static String baseUrl;

    public String getBaseUrl() {
        return InvoicePdfRendererService.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        InvoicePdfRendererService.baseUrl = baseUrl;
    }

    public void render(DocumentType documentType, String guid, OutputStream outputStream) {
        switch (documentType) {
            case CONTRIBUTION_AGREEMENT:
                renderContributionAgreementPdf(guid, outputStream);
            case INVOICE:
                renderInvoicePdf(guid, outputStream);
        }
    }

    public void renderInvoicePdf(String guid, OutputStream outputStream) {
        renderPdf("/invoice?guid=", guid, outputStream);
    }

    public void renderContributionAgreementPdf(String guid, OutputStream outputStream) {
        renderPdf("/contributionAgreement?guid=", guid, outputStream);
    }

    public void renderPdf(String path, String guid, OutputStream outputStream) {
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(baseUrl + path + guid);
            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

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

    public void renderPdf(String guid, OutputStream outputStream) {
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(baseUrl + "/invoice?guid=" + guid);
            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

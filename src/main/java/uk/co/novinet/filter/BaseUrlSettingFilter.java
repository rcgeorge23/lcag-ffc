package uk.co.novinet.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.co.novinet.service.InvoicePdfRendererService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class BaseUrlSettingFilter implements Filter {

    @Autowired
    private InvoicePdfRendererService invoicePdfRendererService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        invoicePdfRendererService.setBaseUrl(baseUrl((HttpServletRequest) request));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}

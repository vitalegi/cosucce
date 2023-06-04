package it.vitalegi.config.logging;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter("/*")
public class LoggingFilter implements Filter {

    private final String CORRELATION_ID = "x-request-id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        String id = UUID.randomUUID()
                        .toString();
        MDC.put("correlationId", id);
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(CORRELATION_ID, id);
        chain.doFilter(request, response);
    }
}

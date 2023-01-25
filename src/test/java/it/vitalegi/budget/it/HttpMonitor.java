package it.vitalegi.budget.it;

import lombok.extern.log4j.Log4j2;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

@Log4j2
public class HttpMonitor implements ResultHandler {

    public static HttpMonitor monitor() {
        return new HttpMonitor();
    }

    @Override
    public void handle(MvcResult result) throws Exception {
        MockHttpServletRequest request = result.getRequest();
        MockHttpServletResponse response = result.getResponse();
        log.info("{} {} status={}, request={}, response={}", request.getMethod(), request.getRequestURI(),
                response.getStatus(), request.getContentAsString(), response.getContentAsString());
    }
}

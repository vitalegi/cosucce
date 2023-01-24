package it.vitalegi.budget.it;

import lombok.extern.log4j.Log4j2;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

@Log4j2
public class HttpMonitor implements ResultHandler {

    public static HttpMonitor monitor() {
        return new HttpMonitor();
    }

    @Override
    public void handle(MvcResult result) throws Exception {
        log.info("URI={}", result.getRequest().getRequestURI());
        log.info("METHOD={}", result.getRequest().getMethod());
        log.info("BODY={}", result.getRequest().getContentAsString());
        log.info("STATUS={}", result.getResponse().getStatus());
        log.info("RESPONSE={}", result.getResponse().getContentAsString());
    }
}

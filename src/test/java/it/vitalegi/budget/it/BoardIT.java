package it.vitalegi.budget.it;


import it.vitalegi.budget.resource.BoardResource;
import it.vitalegi.budget.resource.UserResource;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
//import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

@Log4j2
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityWebAuxTestConfig.class
)
@AutoConfigureMockMvc
public class BoardIT {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    @WithUserDetails("user1")
    void test1() throws Exception {
        //EntityExchangeResult<User> out = rest.mutateWith(mockJwt()).get().uri("/user").exchange().expectBody(User.class).returnResult();
        mockMvc.perform(get("/user")).andExpect(status().isOk()).andDo(print());
    }
}

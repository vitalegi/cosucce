package it.vitalegi.cosucce.iam.resource;

import it.vitalegi.cosucce.util.MockAuth;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class AuthenticationResourceTests {
    @Autowired
    MockMvc mockMvc;

    @Nested
    class Identity {
        @Test
        void when_authenticated_then_responseContainsData() throws Exception {
            var auth = MockAuth.admin("user1");
            mockMvc.perform(get("/auth/identity").contentType(MediaType.APPLICATION_JSON).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(jsonPath("$.id").exists()) //
                    .andExpect(jsonPath("$.status", is("ACTIVE"))) //
                    .andExpect(jsonPath("$.roles", is(List.of("ADMIN")))) //
                    .andReturn();
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(get("/auth/identity").contentType(MediaType.APPLICATION_JSON)) //
                    .andDo(print()) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string("")) //
                    .andReturn();
        }
    }

    @Nested
    class Permission {
        @Test
        void when_authenticatedWithPermissions_then_responseContainsPermissions() throws Exception {
            var auth = MockAuth.admin("user1");
            mockMvc.perform(get("/auth/permissions").contentType(MediaType.APPLICATION_JSON).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().json("{'permissions': ['ADMIN', 'BUDGET_ACCESS']}")) //
                    .andReturn();
        }

        @Test
        void when_authenticatedWithNoPermissions_then_responseIsEmpty() throws Exception {
            var auth = MockAuth.guest("user1");
            mockMvc.perform(get("/auth/permissions").contentType(MediaType.APPLICATION_JSON).with(csrf()).with(auth)) //
                    .andDo(print()) //
                    .andExpect(status().isOk()) //
                    .andExpect(content().json("{'permissions': []}")) //
                    .andReturn();
        }

        @Test
        void when_notAuthenticated_then_401() throws Exception {
            mockMvc.perform(get("/auth/permissions").contentType(MediaType.APPLICATION_JSON)) //
                    .andDo(print()) //
                    .andExpect(status().is(401)) //
                    .andExpect(content().string("")) //
                    .andReturn();
        }
    }
}

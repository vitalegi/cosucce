package it.vitalegi.cosucce.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MockMvcUtil {

    public static void assert401(ResultActions resultActions) throws Exception {
        resultActions //
                .andExpect(status().is(401)) //
                .andExpect(content().string(""));
    }

    public static void assert403(ResultActions resultActions) throws Exception {
        resultActions //
                .andExpect(status().is(403)) //
                .andExpect(content().json("{'error': 'UnauthorizedAccessException'}"));
    }

    public static void assert409(ResultActions resultActions) throws Exception {
        resultActions //
                .andExpect(status().is(409)) //
                .andExpect(content().json("{'error': 'OptimisticLockException'}"));
    }


    public static UUID getUserId(MockMvc mockMvc, RequestPostProcessor auth) {
        try {
            var payload = mockMvc.perform(get("/auth/identity").with(csrf()).with(auth)) //
                    .andExpect(status().isOk()) //
                    .andReturn().getResponse().getContentAsString();
            var o = JsonUtil.readNode(new ObjectMapper(), payload);
            return UUID.fromString(o.get("id").asText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

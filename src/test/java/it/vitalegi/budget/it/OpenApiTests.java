package it.vitalegi.budget.it;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OpenApiTests {

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    @BeforeEach
    protected void init() {
        objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
    }

    @DisplayName("OpenApi JSON, should download file")
    @Test
    void test_openApi_json_shouldReturnValidJsonDefinition() throws Exception {
        String payload =
                mockMvc.perform(get("/v3/api-docs"))
                       .andExpect(status().isOk())
                       .andReturn()
                       .getResponse()
                       .getContentAsString();
        objectMapper.readTree(payload);
    }

    @DisplayName("OpenApi YAML, should download file")
    @Test
    void test_openApi_yaml_shouldReturnValidYamlDefinition() throws Exception {
        String payload =
                mockMvc.perform(get("/v3/api-docs.yaml"))
                       .andExpect(status().isOk())
                       .andReturn()
                       .getResponse()
                       .getContentAsString();
        objectMapper.readTree(payload);
    }

    @DisplayName("OpenApi YAML, should download file")
    @Test
    void test_openApi_webPage_shouldReturnContent() throws Exception {
        String payload =
                mockMvc.perform(get("/swagger-ui/index.html"))
                       .andExpect(status().isOk())
                       .andReturn()
                       .getResponse()
                       .getContentAsString();
        assertTrue(payload.trim()
                          .length() > 0);
    }

}
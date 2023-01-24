package it.vitalegi.budget.it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

@Log4j2
@Service
public class CallService {
    @Autowired
    ObjectMapper objectMapper;


    public <E> E jsonPayload(ResultActions actions, Class<E> clazz) {
        try {
            String payload = actions.andReturn().getResponse().getContentAsString();
            return objectMapper.readValue(payload, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <E> List<E> jsonPayloadList(ResultActions actions, TypeReference<List<E>> ref) {
        try {
            String payload = actions.andReturn().getResponse().getContentAsString();
            return objectMapper.readValue(payload, ref);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

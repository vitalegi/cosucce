package it.vitalegi.cosucce.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static JsonNode readNode(ObjectMapper om, String str) {
        try {
            return om.readTree(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

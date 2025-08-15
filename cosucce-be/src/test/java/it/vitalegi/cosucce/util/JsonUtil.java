package it.vitalegi.cosucce.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

    public static JsonNode readNode(ObjectMapper om, String str) {
        try {
            return om.readTree(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String json(Object obj) {
        var mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        var ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

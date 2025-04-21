package com.aurxsiu.audiotransmitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T encode(TypeReference<T> tTypeReference, String s) throws Exception {
        return mapper.readValue(s, tTypeReference);
    }

    public static String decode(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }
}

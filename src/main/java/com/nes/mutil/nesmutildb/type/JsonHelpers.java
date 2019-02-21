package com.nes.mutil.nesmutildb.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-03-21.
 */
public class JsonHelpers {
    private static Logger LOGGER = LoggerFactory.getLogger(JsonHelpers.class);

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper createObjectMapperAlways() {
        return new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    public static ObjectMapper createObjectMapperSort() {
        return createObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    public static <T> T strToObject(String str, Class<T> tClass) {
        try {
            return new ObjectMapper().readValue(str, tClass);
        } catch (IOException e) {
            LOGGER.error("strToObj error {}", e);
            return null;
        }
    }

    public static <T> Optional<String> objectToStrO(T value) {
        try {
            return Optional.of(new ObjectMapper().writeValueAsString(value));
        } catch (JsonProcessingException e) {
            LOGGER.error("strToObj error {}", e);
            return Optional.empty();
        }
    }

    public static <T> String objectToStr(T value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.error("strToObj error {}", e);
            return "{}";
        }
    }
}
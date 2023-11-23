package com.abadeksvp.integrationteststoolkit;

import java.util.Arrays;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.SneakyThrows;

public class JsonAssertUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    public static JsonNode extractField(String json, String path) {
        return objectMapper.readTree(json).at(path);
    }

    public static CustomComparator withCompareRules(JSONCompareMode compareMode, String... ignoredFields) {
        Customization[] customizations = Arrays.stream(ignoredFields)
                .map(field -> new Customization(field, (o1, o2) -> true))
                .toArray(Customization[]::new);
        return new CustomComparator(compareMode, customizations);
    }
}

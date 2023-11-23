package com.abadeksvp.integrationteststoolkit.resource;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import lombok.SneakyThrows;

public class ClasspathResourceReader implements ResourceReader {

    @SneakyThrows
    @Override
    public String readString(String name) {
        URL resource = getClass().getResource(name);
        if (resource == null) {
            throw new IOException("Resource not found: " + name);
        }
        return IOUtils.toString(resource, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    @Override
    public ReadResult read(String name) {
        URL resource = getClass().getResource(name);
        if (resource == null) {
            throw new IOException("Resource not found: " + name);
        }
        return new ReadResult(IOUtils.toString(resource, StandardCharsets.UTF_8));
    }
}

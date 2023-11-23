package com.abadeksvp.integrationteststoolkit;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import lombok.SneakyThrows;

public class ResourceReader {

    @SneakyThrows
    public String readString(String name) {
        URL resource = getClass().getResource(name);
        if (resource == null) {
            throw new IOException("Resource not found: " + name);
        }
        return IOUtils.toString(resource, StandardCharsets.UTF_8);
    }

    public ReadResult read(String name) throws IOException {
        URL resource = getClass().getResource(name);
        if (resource == null) {
            throw new IOException("Resource not found: " + name);
        }
        return new ReadResult(IOUtils.toString(resource, StandardCharsets.UTF_8));
    }

    public static class ReadResult {

        private String text;

        ReadResult(String text) {
            this.text = text;
        }

        public ReadResult andReplace(String target, String replacement) {
            this.text = text.replaceAll(target, replacement);
            return this;
        }

        public String get() {
            return this.text;
        }
    }
}

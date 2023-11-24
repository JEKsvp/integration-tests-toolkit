package com.abadeksvp.integrationteststoolkit.resource;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import lombok.SneakyThrows;

/**
 * A class that implements the {@link ResourceReader} interface and provides methods to read resources from the
 * classpath.
 */
public class ClasspathResourceReader implements ResourceReader {

    private final Charset charset;

    public ClasspathResourceReader() {
        this.charset = StandardCharsets.UTF_8;
    }

    public ClasspathResourceReader(Charset charset) {
        this.charset = charset;
    }

    @SneakyThrows
    @Override
    public String readString(String name) {
        URL resource = getClass().getResource(name);
        if (resource == null) {
            throw new IOException("Resource not found: " + name);
        }
        return IOUtils.toString(resource, charset);
    }

    @SneakyThrows
    @Override
    public ReadResult read(String name) {
        URL resource = getClass().getResource(name);
        if (resource == null) {
            throw new IOException("Resource not found: " + name);
        }
        return new ReadResult(IOUtils.toString(resource, charset));
    }
}

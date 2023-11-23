package com.abadeksvp.integrationteststoolkit.resource;

public class ReadResult {


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

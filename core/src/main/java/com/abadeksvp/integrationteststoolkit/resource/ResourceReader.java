package com.abadeksvp.integrationteststoolkit.resource;


public interface ResourceReader {

    public String readString(String name);

    public ReadResult read(String name);
}

package com.abadeksvp.integrationteststoolkit.resource;


/**
 * The ResourceReader interface provides methods to read resources from various sources.
 */
public interface ResourceReader {


    /**
     * Reads the content of a resource as a string.
     *
     * @param name the name of the resource to read
     * @return the content of the resource as a string
     */
    public String readString(String name);

    /**
     * Reads the content of a resource and returns a ReadResult object.
     *
     * @param name the name of the resource to read
     * @return a ReadResult object that contains the content of the resource
     */
    public ReadResult read(String name);
}

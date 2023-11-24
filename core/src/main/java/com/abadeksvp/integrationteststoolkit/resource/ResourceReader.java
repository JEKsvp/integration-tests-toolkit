package com.abadeksvp.integrationteststoolkit.resource;


/**
 * ResourceReader is an interface that provides methods for reading resources as a string;
 */
public interface ResourceReader {


    /**
     * Reads a string from a specified source.
     *
     * @param name the name of the source from which to read the string
     * @return the string read from the specified source
     */
    public String readString(String name);

    /**
     * Performs a read operation from a specified source.
     *
     * @param name the name of the source from which to perform the read operation
     * @return the result of the read operation
     */
    public ReadResult read(String name);
}

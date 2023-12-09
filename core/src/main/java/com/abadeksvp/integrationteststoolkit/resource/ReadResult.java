package com.abadeksvp.integrationteststoolkit.resource;

import java.util.regex.Pattern;

/**
 * The ReadResult class represents the result of reading a resource. It encapsulates the content of the resource as a
 * string.
 */
public class ReadResult {

    private String text;

    ReadResult(String text) {
        this.text = text;
    }

    /**
     * This method replaces all occurrences of a specified target string with a replacement string in the encapsulated
     * text.
     *
     * @param target      the target string to be replaced
     * @param replacement the replacement string
     * @return the modified ReadResult object with the replacements applied
     */
    public ReadResult andReplace(String target, String replacement) {
        this.text = text.replaceAll(target, replacement);
        return this;
    }

    /**
     * This method replaces all occurrences of a specified pattern with a replacement string in the encapsulated text.
     *
     * @param target      the pattern that represents the strings to be replaced
     * @param replacement the replacement string
     * @return the modified ReadResult object with the replacements applied
     */
    public ReadResult andReplace(Pattern target, String replacement) {
        this.text = target.matcher(text).replaceAll(replacement);
        return this;
    }

    /**
     * Retrieves the encapsulated text content of a resource.
     *
     * @return the text content of the resource
     */
    public String get() {
        return this.text;
    }
}

package com.abadeksvp.integrationteststoolkit;

import java.util.Arrays;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

public class JsonAssertUtils {

    /**
     * Creates a CustomComparator with specified compare mode and fields to ignore during comparison.
     *
     * @param compareMode    The compare mode to use during comparison.
     * @param ignoredFields  The fields to ignore during comparison.
     * @return The CustomComparator object with specified compare mode and ignored fields.
     */
    public static CustomComparator withCompareRules(JSONCompareMode compareMode, String... ignoredFields) {
        Customization[] customizations = Arrays.stream(ignoredFields)
                .map(field -> new Customization(field, (o1, o2) -> true))
                .toArray(Customization[]::new);
        return new CustomComparator(compareMode, customizations);
    }
}

package com.abadeksvp.integrationteststoolkit;

import java.util.Arrays;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

/**
 * The JsonAssertUtils class provides utility methods for asserting JSON objects.
 */
public class JsonAssertUtils {

    /**
     * Creates a CustomComparator with specified compare mode and fields to ignore during comparison.
     *
     * @param compareMode   The compare mode to use during comparison.
     * @param ignoredFields The fields to ignore during comparison.
     * @return The CustomComparator object with specified compare mode and ignored fields.
     */
    public static CustomComparator withCompareRules(JSONCompareMode compareMode, String... ignoredFields) {
        return withCompareMode(compareMode)
                .withIgnoredFields(ignoredFields)
                .build();
    }

    /**
     * Creates a CustomComparatorBuilder with the specified compare mode.
     *
     * @param compareMode The compare mode to use during comparison.
     * @return The CustomComparatorBuilder object with the specified compare mode.
     */
    public static CustomComparatorBuilder withCompareMode(JSONCompareMode compareMode) {
        return new CustomComparatorBuilder(compareMode);
    }

    public static class CustomComparatorBuilder {

        private JSONCompareMode compareMode;
        private String[] ignoredFields;

        public CustomComparatorBuilder(JSONCompareMode compareMode) {
            this.compareMode = compareMode;
        }

        public CustomComparatorBuilder withIgnoredFields(String... ignoredFields) {
            this.ignoredFields = ignoredFields;
            return this;
        }

        public CustomComparator build() {
            Customization[] customizations = Arrays.stream(ignoredFields)
                    .map(field -> new Customization(field, (o1, o2) -> true))
                    .toArray(Customization[]::new);
            return new CustomComparator(compareMode, customizations);
        }
    }
}

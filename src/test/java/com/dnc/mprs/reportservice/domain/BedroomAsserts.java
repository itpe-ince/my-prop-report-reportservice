package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class BedroomAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBedroomAllPropertiesEquals(Bedroom expected, Bedroom actual) {
        assertBedroomAutoGeneratedPropertiesEquals(expected, actual);
        assertBedroomAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBedroomAllUpdatablePropertiesEquals(Bedroom expected, Bedroom actual) {
        assertBedroomUpdatableFieldsEquals(expected, actual);
        assertBedroomUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBedroomAutoGeneratedPropertiesEquals(Bedroom expected, Bedroom actual) {
        assertThat(expected)
            .as("Verify Bedroom auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBedroomUpdatableFieldsEquals(Bedroom expected, Bedroom actual) {
        assertThat(expected)
            .as("Verify Bedroom relevant properties")
            .satisfies(e -> assertThat(e.getBedroomName()).as("check bedroomName").isEqualTo(actual.getBedroomName()))
            .satisfies(e -> assertThat(e.getConditionLevel()).as("check conditionLevel").isEqualTo(actual.getConditionLevel()))
            .satisfies(e ->
                assertThat(e.getRoomSize()).as("check roomSize").usingComparator(bigDecimalCompareTo).isEqualTo(actual.getRoomSize())
            )
            .satisfies(e -> assertThat(e.getClosetYn()).as("check closetYn").isEqualTo(actual.getClosetYn()))
            .satisfies(e -> assertThat(e.getAcYn()).as("check acYn").isEqualTo(actual.getAcYn()))
            .satisfies(e -> assertThat(e.getWindowLocation()).as("check windowLocation").isEqualTo(actual.getWindowLocation()))
            .satisfies(e -> assertThat(e.getWindowSize()).as("check windowSize").isEqualTo(actual.getWindowSize()))
            .satisfies(e -> assertThat(e.getRemarks()).as("check remarks").isEqualTo(actual.getRemarks()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBedroomUpdatableRelationshipsEquals(Bedroom expected, Bedroom actual) {
        assertThat(expected)
            .as("Verify Bedroom relationships")
            .satisfies(e -> assertThat(e.getReport()).as("check report").isEqualTo(actual.getReport()));
    }
}

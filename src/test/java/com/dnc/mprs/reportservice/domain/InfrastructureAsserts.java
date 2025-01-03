package com.dnc.mprs.reportservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class InfrastructureAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfrastructureAllPropertiesEquals(Infrastructure expected, Infrastructure actual) {
        assertInfrastructureAutoGeneratedPropertiesEquals(expected, actual);
        assertInfrastructureAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfrastructureAllUpdatablePropertiesEquals(Infrastructure expected, Infrastructure actual) {
        assertInfrastructureUpdatableFieldsEquals(expected, actual);
        assertInfrastructureUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfrastructureAutoGeneratedPropertiesEquals(Infrastructure expected, Infrastructure actual) {
        assertThat(expected)
            .as("Verify Infrastructure auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfrastructureUpdatableFieldsEquals(Infrastructure expected, Infrastructure actual) {
        assertThat(expected)
            .as("Verify Infrastructure relevant properties")
            .satisfies(e -> assertThat(e.getInfraType()).as("check infraType").isEqualTo(actual.getInfraType()))
            .satisfies(e -> assertThat(e.getInfraName()).as("check infraName").isEqualTo(actual.getInfraName()))
            .satisfies(e -> assertThat(e.getConditionLevel()).as("check conditionLevel").isEqualTo(actual.getConditionLevel()))
            .satisfies(e -> assertThat(e.getInfraDistance()).as("check infraDistance").isEqualTo(actual.getInfraDistance()))
            .satisfies(e -> assertThat(e.getInfraDistanceUnit()).as("check infraDistanceUnit").isEqualTo(actual.getInfraDistanceUnit()))
            .satisfies(e -> assertThat(e.getRemarks()).as("check remarks").isEqualTo(actual.getRemarks()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertInfrastructureUpdatableRelationshipsEquals(Infrastructure expected, Infrastructure actual) {
        assertThat(expected)
            .as("Verify Infrastructure relationships")
            .satisfies(e -> assertThat(e.getReport()).as("check report").isEqualTo(actual.getReport()));
    }
}

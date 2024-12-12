package com.dnc.mprs.reportservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class KitchenAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenAllPropertiesEquals(Kitchen expected, Kitchen actual) {
        assertKitchenAutoGeneratedPropertiesEquals(expected, actual);
        assertKitchenAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenAllUpdatablePropertiesEquals(Kitchen expected, Kitchen actual) {
        assertKitchenUpdatableFieldsEquals(expected, actual);
        assertKitchenUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenAutoGeneratedPropertiesEquals(Kitchen expected, Kitchen actual) {
        assertThat(expected)
            .as("Verify Kitchen auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenUpdatableFieldsEquals(Kitchen expected, Kitchen actual) {
        assertThat(expected)
            .as("Verify Kitchen relevant properties")
            .satisfies(e -> assertThat(e.getReportId()).as("check reportId").isEqualTo(actual.getReportId()))
            .satisfies(e -> assertThat(e.getKitchenName()).as("check kitchenName").isEqualTo(actual.getKitchenName()))
            .satisfies(e -> assertThat(e.getConditionLevel()).as("check conditionLevel").isEqualTo(actual.getConditionLevel()))
            .satisfies(e -> assertThat(e.getBuiltInCabinet()).as("check builtInCabinet").isEqualTo(actual.getBuiltInCabinet()))
            .satisfies(e -> assertThat(e.getSinkCondition()).as("check sinkCondition").isEqualTo(actual.getSinkCondition()))
            .satisfies(e -> assertThat(e.getVentilationSystem()).as("check ventilationSystem").isEqualTo(actual.getVentilationSystem()))
            .satisfies(e -> assertThat(e.getApplianceProvision()).as("check applianceProvision").isEqualTo(actual.getApplianceProvision()))
            .satisfies(e -> assertThat(e.getRemarks()).as("check remarks").isEqualTo(actual.getRemarks()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertKitchenUpdatableRelationshipsEquals(Kitchen expected, Kitchen actual) {
        assertThat(expected)
            .as("Verify Kitchen relationships")
            .satisfies(e -> assertThat(e.getReport()).as("check report").isEqualTo(actual.getReport()));
    }
}

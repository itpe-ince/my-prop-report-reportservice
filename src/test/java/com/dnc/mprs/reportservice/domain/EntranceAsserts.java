package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class EntranceAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntranceAllPropertiesEquals(Entrance expected, Entrance actual) {
        assertEntranceAutoGeneratedPropertiesEquals(expected, actual);
        assertEntranceAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntranceAllUpdatablePropertiesEquals(Entrance expected, Entrance actual) {
        assertEntranceUpdatableFieldsEquals(expected, actual);
        assertEntranceUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntranceAutoGeneratedPropertiesEquals(Entrance expected, Entrance actual) {
        assertThat(expected)
            .as("Verify Entrance auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntranceUpdatableFieldsEquals(Entrance expected, Entrance actual) {
        assertThat(expected)
            .as("Verify Entrance relevant properties")
            .satisfies(e -> assertThat(e.getEntranceName()).as("check entranceName").isEqualTo(actual.getEntranceName()))
            .satisfies(e -> assertThat(e.getCondtionLevel()).as("check condtionLevel").isEqualTo(actual.getCondtionLevel()))
            .satisfies(e ->
                assertThat(e.getEntranceSize())
                    .as("check entranceSize")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getEntranceSize())
            )
            .satisfies(e ->
                assertThat(e.getShoeRackSize())
                    .as("check shoeRackSize")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getShoeRackSize())
            )
            .satisfies(e -> assertThat(e.getPantryPresence()).as("check pantryPresence").isEqualTo(actual.getPantryPresence()))
            .satisfies(e -> assertThat(e.getRemarks()).as("check remarks").isEqualTo(actual.getRemarks()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertEntranceUpdatableRelationshipsEquals(Entrance expected, Entrance actual) {
        assertThat(expected)
            .as("Verify Entrance relationships")
            .satisfies(e -> assertThat(e.getReport()).as("check report").isEqualTo(actual.getReport()));
    }
}

package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EntranceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Entrance getEntranceSample1() {
        return new Entrance().id(1L).reportId(1L).entranceName("entranceName1").pantryPresence("pantryPresence1").remarks("remarks1");
    }

    public static Entrance getEntranceSample2() {
        return new Entrance().id(2L).reportId(2L).entranceName("entranceName2").pantryPresence("pantryPresence2").remarks("remarks2");
    }

    public static Entrance getEntranceRandomSampleGenerator() {
        return new Entrance()
            .id(longCount.incrementAndGet())
            .reportId(longCount.incrementAndGet())
            .entranceName(UUID.randomUUID().toString())
            .pantryPresence(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString());
    }
}

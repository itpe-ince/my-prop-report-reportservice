package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InfrastructureTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Infrastructure getInfrastructureSample1() {
        return new Infrastructure().id(1L).infraName("infraName1").infraDistance(1).remarks("remarks1");
    }

    public static Infrastructure getInfrastructureSample2() {
        return new Infrastructure().id(2L).infraName("infraName2").infraDistance(2).remarks("remarks2");
    }

    public static Infrastructure getInfrastructureRandomSampleGenerator() {
        return new Infrastructure()
            .id(longCount.incrementAndGet())
            .infraName(UUID.randomUUID().toString())
            .infraDistance(intCount.incrementAndGet())
            .remarks(UUID.randomUUID().toString());
    }
}

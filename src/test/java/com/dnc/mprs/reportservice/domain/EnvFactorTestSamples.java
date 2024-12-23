package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EnvFactorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EnvFactor getEnvFactorSample1() {
        return new EnvFactor().id(1L).envFactorName("envFactorName1").remarks("remarks1");
    }

    public static EnvFactor getEnvFactorSample2() {
        return new EnvFactor().id(2L).envFactorName("envFactorName2").remarks("remarks2");
    }

    public static EnvFactor getEnvFactorRandomSampleGenerator() {
        return new EnvFactor()
            .id(longCount.incrementAndGet())
            .envFactorName(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString());
    }
}

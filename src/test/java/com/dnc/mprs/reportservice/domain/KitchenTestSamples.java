package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KitchenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Kitchen getKitchenSample1() {
        return new Kitchen()
            .id(1L)
            .kitchenName("kitchenName1")
            .builtInCabinet("builtInCabinet1")
            .ventilationSystem("ventilationSystem1")
            .applianceProvision("applianceProvision1")
            .remarks("remarks1");
    }

    public static Kitchen getKitchenSample2() {
        return new Kitchen()
            .id(2L)
            .kitchenName("kitchenName2")
            .builtInCabinet("builtInCabinet2")
            .ventilationSystem("ventilationSystem2")
            .applianceProvision("applianceProvision2")
            .remarks("remarks2");
    }

    public static Kitchen getKitchenRandomSampleGenerator() {
        return new Kitchen()
            .id(longCount.incrementAndGet())
            .kitchenName(UUID.randomUUID().toString())
            .builtInCabinet(UUID.randomUUID().toString())
            .ventilationSystem(UUID.randomUUID().toString())
            .applianceProvision(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString());
    }
}

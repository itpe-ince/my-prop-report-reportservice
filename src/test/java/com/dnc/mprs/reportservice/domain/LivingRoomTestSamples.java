package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LivingRoomTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static LivingRoom getLivingRoomSample1() {
        return new LivingRoom()
            .id(1L)
            .livingRoomName("livingRoomName1")
            .floorMaterial("floorMaterial1")
            .sunlight("sunlight1")
            .remarks("remarks1");
    }

    public static LivingRoom getLivingRoomSample2() {
        return new LivingRoom()
            .id(2L)
            .livingRoomName("livingRoomName2")
            .floorMaterial("floorMaterial2")
            .sunlight("sunlight2")
            .remarks("remarks2");
    }

    public static LivingRoom getLivingRoomRandomSampleGenerator() {
        return new LivingRoom()
            .id(longCount.incrementAndGet())
            .livingRoomName(UUID.randomUUID().toString())
            .floorMaterial(UUID.randomUUID().toString())
            .sunlight(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString());
    }
}

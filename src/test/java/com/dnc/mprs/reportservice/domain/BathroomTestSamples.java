package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BathroomTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Bathroom getBathroomSample1() {
        return new Bathroom()
            .id(1L)
            .reportId(1L)
            .bathroomName("bathroomName1")
            .showerBoothPresence("showerBoothPresence1")
            .bathtubPresence("bathtubPresence1")
            .remarks("remarks1");
    }

    public static Bathroom getBathroomSample2() {
        return new Bathroom()
            .id(2L)
            .reportId(2L)
            .bathroomName("bathroomName2")
            .showerBoothPresence("showerBoothPresence2")
            .bathtubPresence("bathtubPresence2")
            .remarks("remarks2");
    }

    public static Bathroom getBathroomRandomSampleGenerator() {
        return new Bathroom()
            .id(longCount.incrementAndGet())
            .reportId(longCount.incrementAndGet())
            .bathroomName(UUID.randomUUID().toString())
            .showerBoothPresence(UUID.randomUUID().toString())
            .bathtubPresence(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString());
    }
}

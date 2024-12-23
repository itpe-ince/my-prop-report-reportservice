package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BedroomTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Bedroom getBedroomSample1() {
        return new Bedroom()
            .id(1L)
            .bedroomName("bedroomName1")
            .closetYn("closetYn1")
            .acYn("acYn1")
            .windowLocation("windowLocation1")
            .windowSize("windowSize1")
            .remarks("remarks1");
    }

    public static Bedroom getBedroomSample2() {
        return new Bedroom()
            .id(2L)
            .bedroomName("bedroomName2")
            .closetYn("closetYn2")
            .acYn("acYn2")
            .windowLocation("windowLocation2")
            .windowSize("windowSize2")
            .remarks("remarks2");
    }

    public static Bedroom getBedroomRandomSampleGenerator() {
        return new Bedroom()
            .id(longCount.incrementAndGet())
            .bedroomName(UUID.randomUUID().toString())
            .closetYn(UUID.randomUUID().toString())
            .acYn(UUID.randomUUID().toString())
            .windowLocation(UUID.randomUUID().toString())
            .windowSize(UUID.randomUUID().toString())
            .remarks(UUID.randomUUID().toString());
    }
}

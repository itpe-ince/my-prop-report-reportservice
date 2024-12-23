package com.dnc.mprs.reportservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Report getReportSample1() {
        return new Report()
            .id(1L)
            .reportTitle("reportTitle1")
            .summary("summary1")
            .constructionYear(1)
            .parkingFacility("parkingFacility1")
            .parkingCount(1)
            .cctvYn("cctvYn1")
            .maintenanceFee(1)
            .redevelopmentYn("redevelopmentYn1")
            .rentalDemand("rentalDemand1")
            .communityRules("communityRules1")
            .complexId(1L)
            .complexName("complexName1")
            .propertyId(1L)
            .propertyName("propertyName1");
    }

    public static Report getReportSample2() {
        return new Report()
            .id(2L)
            .reportTitle("reportTitle2")
            .summary("summary2")
            .constructionYear(2)
            .parkingFacility("parkingFacility2")
            .parkingCount(2)
            .cctvYn("cctvYn2")
            .maintenanceFee(2)
            .redevelopmentYn("redevelopmentYn2")
            .rentalDemand("rentalDemand2")
            .communityRules("communityRules2")
            .complexId(2L)
            .complexName("complexName2")
            .propertyId(2L)
            .propertyName("propertyName2");
    }

    public static Report getReportRandomSampleGenerator() {
        return new Report()
            .id(longCount.incrementAndGet())
            .reportTitle(UUID.randomUUID().toString())
            .summary(UUID.randomUUID().toString())
            .constructionYear(intCount.incrementAndGet())
            .parkingFacility(UUID.randomUUID().toString())
            .parkingCount(intCount.incrementAndGet())
            .cctvYn(UUID.randomUUID().toString())
            .maintenanceFee(intCount.incrementAndGet())
            .redevelopmentYn(UUID.randomUUID().toString())
            .rentalDemand(UUID.randomUUID().toString())
            .communityRules(UUID.randomUUID().toString())
            .complexId(longCount.incrementAndGet())
            .complexName(UUID.randomUUID().toString())
            .propertyId(longCount.incrementAndGet())
            .propertyName(UUID.randomUUID().toString());
    }
}

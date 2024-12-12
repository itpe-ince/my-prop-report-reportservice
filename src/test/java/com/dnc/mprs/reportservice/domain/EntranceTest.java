package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.EntranceTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EntranceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Entrance.class);
        Entrance entrance1 = getEntranceSample1();
        Entrance entrance2 = new Entrance();
        assertThat(entrance1).isNotEqualTo(entrance2);

        entrance2.setId(entrance1.getId());
        assertThat(entrance1).isEqualTo(entrance2);

        entrance2 = getEntranceSample2();
        assertThat(entrance1).isNotEqualTo(entrance2);
    }

    @Test
    void reportTest() {
        Entrance entrance = getEntranceRandomSampleGenerator();
        Report reportBack = getReportRandomSampleGenerator();

        entrance.setReport(reportBack);
        assertThat(entrance.getReport()).isEqualTo(reportBack);

        entrance.report(null);
        assertThat(entrance.getReport()).isNull();
    }
}

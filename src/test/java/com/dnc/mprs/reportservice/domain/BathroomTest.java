package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.BathroomTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BathroomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bathroom.class);
        Bathroom bathroom1 = getBathroomSample1();
        Bathroom bathroom2 = new Bathroom();
        assertThat(bathroom1).isNotEqualTo(bathroom2);

        bathroom2.setId(bathroom1.getId());
        assertThat(bathroom1).isEqualTo(bathroom2);

        bathroom2 = getBathroomSample2();
        assertThat(bathroom1).isNotEqualTo(bathroom2);
    }

    @Test
    void reportTest() {
        Bathroom bathroom = getBathroomRandomSampleGenerator();
        Report reportBack = getReportRandomSampleGenerator();

        bathroom.setReport(reportBack);
        assertThat(bathroom.getReport()).isEqualTo(reportBack);

        bathroom.report(null);
        assertThat(bathroom.getReport()).isNull();
    }
}

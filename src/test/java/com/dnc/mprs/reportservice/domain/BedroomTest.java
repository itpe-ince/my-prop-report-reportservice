package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.BedroomTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BedroomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bedroom.class);
        Bedroom bedroom1 = getBedroomSample1();
        Bedroom bedroom2 = new Bedroom();
        assertThat(bedroom1).isNotEqualTo(bedroom2);

        bedroom2.setId(bedroom1.getId());
        assertThat(bedroom1).isEqualTo(bedroom2);

        bedroom2 = getBedroomSample2();
        assertThat(bedroom1).isNotEqualTo(bedroom2);
    }

    @Test
    void reportTest() {
        Bedroom bedroom = getBedroomRandomSampleGenerator();
        Report reportBack = getReportRandomSampleGenerator();

        bedroom.setReport(reportBack);
        assertThat(bedroom.getReport()).isEqualTo(reportBack);

        bedroom.report(null);
        assertThat(bedroom.getReport()).isNull();
    }
}

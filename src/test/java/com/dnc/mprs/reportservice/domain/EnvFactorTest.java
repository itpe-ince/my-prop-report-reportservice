package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.EnvFactorTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnvFactorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvFactor.class);
        EnvFactor envFactor1 = getEnvFactorSample1();
        EnvFactor envFactor2 = new EnvFactor();
        assertThat(envFactor1).isNotEqualTo(envFactor2);

        envFactor2.setId(envFactor1.getId());
        assertThat(envFactor1).isEqualTo(envFactor2);

        envFactor2 = getEnvFactorSample2();
        assertThat(envFactor1).isNotEqualTo(envFactor2);
    }

    @Test
    void reportTest() {
        EnvFactor envFactor = getEnvFactorRandomSampleGenerator();
        Report reportBack = getReportRandomSampleGenerator();

        envFactor.setReport(reportBack);
        assertThat(envFactor.getReport()).isEqualTo(reportBack);

        envFactor.report(null);
        assertThat(envFactor.getReport()).isNull();
    }
}

package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.InfrastructureTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InfrastructureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Infrastructure.class);
        Infrastructure infrastructure1 = getInfrastructureSample1();
        Infrastructure infrastructure2 = new Infrastructure();
        assertThat(infrastructure1).isNotEqualTo(infrastructure2);

        infrastructure2.setId(infrastructure1.getId());
        assertThat(infrastructure1).isEqualTo(infrastructure2);

        infrastructure2 = getInfrastructureSample2();
        assertThat(infrastructure1).isNotEqualTo(infrastructure2);
    }

    @Test
    void reportTest() {
        Infrastructure infrastructure = getInfrastructureRandomSampleGenerator();
        Report reportBack = getReportRandomSampleGenerator();

        infrastructure.setReport(reportBack);
        assertThat(infrastructure.getReport()).isEqualTo(reportBack);

        infrastructure.report(null);
        assertThat(infrastructure.getReport()).isNull();
    }
}

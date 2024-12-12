package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.KitchenTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KitchenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Kitchen.class);
        Kitchen kitchen1 = getKitchenSample1();
        Kitchen kitchen2 = new Kitchen();
        assertThat(kitchen1).isNotEqualTo(kitchen2);

        kitchen2.setId(kitchen1.getId());
        assertThat(kitchen1).isEqualTo(kitchen2);

        kitchen2 = getKitchenSample2();
        assertThat(kitchen1).isNotEqualTo(kitchen2);
    }

    @Test
    void reportTest() {
        Kitchen kitchen = getKitchenRandomSampleGenerator();
        Report reportBack = getReportRandomSampleGenerator();

        kitchen.setReport(reportBack);
        assertThat(kitchen.getReport()).isEqualTo(reportBack);

        kitchen.report(null);
        assertThat(kitchen.getReport()).isNull();
    }
}

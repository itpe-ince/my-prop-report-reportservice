package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.LivingRoomTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LivingRoomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LivingRoom.class);
        LivingRoom livingRoom1 = getLivingRoomSample1();
        LivingRoom livingRoom2 = new LivingRoom();
        assertThat(livingRoom1).isNotEqualTo(livingRoom2);

        livingRoom2.setId(livingRoom1.getId());
        assertThat(livingRoom1).isEqualTo(livingRoom2);

        livingRoom2 = getLivingRoomSample2();
        assertThat(livingRoom1).isNotEqualTo(livingRoom2);
    }

    @Test
    void reportTest() {
        LivingRoom livingRoom = getLivingRoomRandomSampleGenerator();
        Report reportBack = getReportRandomSampleGenerator();

        livingRoom.setReport(reportBack);
        assertThat(livingRoom.getReport()).isEqualTo(reportBack);

        livingRoom.report(null);
        assertThat(livingRoom.getReport()).isNull();
    }
}

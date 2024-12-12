package com.dnc.mprs.reportservice.domain;

import static com.dnc.mprs.reportservice.domain.AuthorTestSamples.*;
import static com.dnc.mprs.reportservice.domain.ReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnc.mprs.reportservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Report.class);
        Report report1 = getReportSample1();
        Report report2 = new Report();
        assertThat(report1).isNotEqualTo(report2);

        report2.setId(report1.getId());
        assertThat(report1).isEqualTo(report2);

        report2 = getReportSample2();
        assertThat(report1).isNotEqualTo(report2);
    }

    @Test
    void authorTest() {
        Report report = getReportRandomSampleGenerator();
        Author authorBack = getAuthorRandomSampleGenerator();

        report.setAuthor(authorBack);
        assertThat(report.getAuthor()).isEqualTo(authorBack);

        report.author(null);
        assertThat(report.getAuthor()).isNull();
    }
}

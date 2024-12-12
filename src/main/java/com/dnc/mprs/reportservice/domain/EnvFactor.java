package com.dnc.mprs.reportservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A EnvFactor.
 */
@Entity
@Table(name = "env_factor")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "envfactor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnvFactor implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @NotNull
    @Size(max = 100)
    @Column(name = "env_factor_name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String envFactorName;

    @Column(name = "env_factor_distance", precision = 21, scale = 2)
    private BigDecimal envFactorDistance;

    @Column(name = "remarks")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author" }, allowSetters = true)
    private Report report;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EnvFactor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public EnvFactor reportId(Long reportId) {
        this.setReportId(reportId);
        return this;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getEnvFactorName() {
        return this.envFactorName;
    }

    public EnvFactor envFactorName(String envFactorName) {
        this.setEnvFactorName(envFactorName);
        return this;
    }

    public void setEnvFactorName(String envFactorName) {
        this.envFactorName = envFactorName;
    }

    public BigDecimal getEnvFactorDistance() {
        return this.envFactorDistance;
    }

    public EnvFactor envFactorDistance(BigDecimal envFactorDistance) {
        this.setEnvFactorDistance(envFactorDistance);
        return this;
    }

    public void setEnvFactorDistance(BigDecimal envFactorDistance) {
        this.envFactorDistance = envFactorDistance;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public EnvFactor remarks(String remarks) {
        this.setRemarks(remarks);
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Report getReport() {
        return this.report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public EnvFactor report(Report report) {
        this.setReport(report);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvFactor)) {
            return false;
        }
        return getId() != null && getId().equals(((EnvFactor) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvFactor{" +
            "id=" + getId() +
            ", reportId=" + getReportId() +
            ", envFactorName='" + getEnvFactorName() + "'" +
            ", envFactorDistance=" + getEnvFactorDistance() +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

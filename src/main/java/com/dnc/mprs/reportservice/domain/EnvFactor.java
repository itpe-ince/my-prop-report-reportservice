package com.dnc.mprs.reportservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EnvFactor.
 */
@Table("env_factor")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "envfactor")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnvFactor implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    @Column("env_factor_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String envFactorName;

    @Column("env_factor_distance")
    private BigDecimal envFactorDistance;

    @Column("remarks")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String remarks;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "author" }, allowSetters = true)
    private Report report;

    @Column("report_id")
    private Long reportId;

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
        this.envFactorDistance = envFactorDistance != null ? envFactorDistance.stripTrailingZeros() : null;
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
        this.reportId = report != null ? report.getId() : null;
    }

    public EnvFactor report(Report report) {
        this.setReport(report);
        return this;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public void setReportId(Long report) {
        this.reportId = report;
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
            ", envFactorName='" + getEnvFactorName() + "'" +
            ", envFactorDistance=" + getEnvFactorDistance() +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

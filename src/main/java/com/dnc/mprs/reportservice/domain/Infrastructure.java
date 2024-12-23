package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.InfraType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Infrastructure.
 */
@Table("infrastructure")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "infrastructure")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Infrastructure implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("infra_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private InfraType infraType;

    @NotNull(message = "must not be null")
    @Size(max = 200)
    @Column("infra_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String infraName;

    @NotNull(message = "must not be null")
    @Column("condition_level")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType conditionLevel;

    @Column("infra_distance")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer infraDistance;

    @Column("infra_distance_unit")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType infraDistanceUnit;

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

    public Infrastructure id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InfraType getInfraType() {
        return this.infraType;
    }

    public Infrastructure infraType(InfraType infraType) {
        this.setInfraType(infraType);
        return this;
    }

    public void setInfraType(InfraType infraType) {
        this.infraType = infraType;
    }

    public String getInfraName() {
        return this.infraName;
    }

    public Infrastructure infraName(String infraName) {
        this.setInfraName(infraName);
        return this;
    }

    public void setInfraName(String infraName) {
        this.infraName = infraName;
    }

    public QualityStateType getConditionLevel() {
        return this.conditionLevel;
    }

    public Infrastructure conditionLevel(QualityStateType conditionLevel) {
        this.setConditionLevel(conditionLevel);
        return this;
    }

    public void setConditionLevel(QualityStateType conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public Integer getInfraDistance() {
        return this.infraDistance;
    }

    public Infrastructure infraDistance(Integer infraDistance) {
        this.setInfraDistance(infraDistance);
        return this;
    }

    public void setInfraDistance(Integer infraDistance) {
        this.infraDistance = infraDistance;
    }

    public QualityStateType getInfraDistanceUnit() {
        return this.infraDistanceUnit;
    }

    public Infrastructure infraDistanceUnit(QualityStateType infraDistanceUnit) {
        this.setInfraDistanceUnit(infraDistanceUnit);
        return this;
    }

    public void setInfraDistanceUnit(QualityStateType infraDistanceUnit) {
        this.infraDistanceUnit = infraDistanceUnit;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Infrastructure remarks(String remarks) {
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

    public Infrastructure report(Report report) {
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
        if (!(o instanceof Infrastructure)) {
            return false;
        }
        return getId() != null && getId().equals(((Infrastructure) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Infrastructure{" +
            "id=" + getId() +
            ", infraType='" + getInfraType() + "'" +
            ", infraName='" + getInfraName() + "'" +
            ", conditionLevel='" + getConditionLevel() + "'" +
            ", infraDistance=" + getInfraDistance() +
            ", infraDistanceUnit='" + getInfraDistanceUnit() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

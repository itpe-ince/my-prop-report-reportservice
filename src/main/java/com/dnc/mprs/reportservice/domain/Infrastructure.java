package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.InfraType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Infrastructure.
 */
@Entity
@Table(name = "infrastructure")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "infrastructure")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Infrastructure implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "infra_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private InfraType infraType;

    @NotNull
    @Size(max = 200)
    @Column(name = "infra_name", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String infraName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_level", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType conditionLevel;

    @Column(name = "infra_distance")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer infraDistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "infra_distance_unit")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType infraDistanceUnit;

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

    public Infrastructure id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public Infrastructure reportId(Long reportId) {
        this.setReportId(reportId);
        return this;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
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
    }

    public Infrastructure report(Report report) {
        this.setReport(report);
        return this;
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
            ", reportId=" + getReportId() +
            ", infraType='" + getInfraType() + "'" +
            ", infraName='" + getInfraName() + "'" +
            ", conditionLevel='" + getConditionLevel() + "'" +
            ", infraDistance=" + getInfraDistance() +
            ", infraDistanceUnit='" + getInfraDistanceUnit() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

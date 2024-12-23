package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Bathroom.
 */
@Table("bathroom")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bathroom")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bathroom implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    @Column("bathroom_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bathroomName;

    @NotNull(message = "must not be null")
    @Column("condtion_level")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType condtionLevel;

    @Column("bathroom_size")
    private BigDecimal bathroomSize;

    @NotNull(message = "must not be null")
    @Column("water_pressure")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType waterPressure;

    @Size(min = 1, max = 1)
    @Column("shower_booth_presence")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String showerBoothPresence;

    @Size(min = 1, max = 1)
    @Column("bathtub_presence")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bathtubPresence;

    @NotNull(message = "must not be null")
    @Column("floor_and_ceiling")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType floorAndCeiling;

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

    public Bathroom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBathroomName() {
        return this.bathroomName;
    }

    public Bathroom bathroomName(String bathroomName) {
        this.setBathroomName(bathroomName);
        return this;
    }

    public void setBathroomName(String bathroomName) {
        this.bathroomName = bathroomName;
    }

    public QualityStateType getCondtionLevel() {
        return this.condtionLevel;
    }

    public Bathroom condtionLevel(QualityStateType condtionLevel) {
        this.setCondtionLevel(condtionLevel);
        return this;
    }

    public void setCondtionLevel(QualityStateType condtionLevel) {
        this.condtionLevel = condtionLevel;
    }

    public BigDecimal getBathroomSize() {
        return this.bathroomSize;
    }

    public Bathroom bathroomSize(BigDecimal bathroomSize) {
        this.setBathroomSize(bathroomSize);
        return this;
    }

    public void setBathroomSize(BigDecimal bathroomSize) {
        this.bathroomSize = bathroomSize != null ? bathroomSize.stripTrailingZeros() : null;
    }

    public QualityStateType getWaterPressure() {
        return this.waterPressure;
    }

    public Bathroom waterPressure(QualityStateType waterPressure) {
        this.setWaterPressure(waterPressure);
        return this;
    }

    public void setWaterPressure(QualityStateType waterPressure) {
        this.waterPressure = waterPressure;
    }

    public String getShowerBoothPresence() {
        return this.showerBoothPresence;
    }

    public Bathroom showerBoothPresence(String showerBoothPresence) {
        this.setShowerBoothPresence(showerBoothPresence);
        return this;
    }

    public void setShowerBoothPresence(String showerBoothPresence) {
        this.showerBoothPresence = showerBoothPresence;
    }

    public String getBathtubPresence() {
        return this.bathtubPresence;
    }

    public Bathroom bathtubPresence(String bathtubPresence) {
        this.setBathtubPresence(bathtubPresence);
        return this;
    }

    public void setBathtubPresence(String bathtubPresence) {
        this.bathtubPresence = bathtubPresence;
    }

    public QualityStateType getFloorAndCeiling() {
        return this.floorAndCeiling;
    }

    public Bathroom floorAndCeiling(QualityStateType floorAndCeiling) {
        this.setFloorAndCeiling(floorAndCeiling);
        return this;
    }

    public void setFloorAndCeiling(QualityStateType floorAndCeiling) {
        this.floorAndCeiling = floorAndCeiling;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Bathroom remarks(String remarks) {
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

    public Bathroom report(Report report) {
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
        if (!(o instanceof Bathroom)) {
            return false;
        }
        return getId() != null && getId().equals(((Bathroom) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bathroom{" +
            "id=" + getId() +
            ", bathroomName='" + getBathroomName() + "'" +
            ", condtionLevel='" + getCondtionLevel() + "'" +
            ", bathroomSize=" + getBathroomSize() +
            ", waterPressure='" + getWaterPressure() + "'" +
            ", showerBoothPresence='" + getShowerBoothPresence() + "'" +
            ", bathtubPresence='" + getBathtubPresence() + "'" +
            ", floorAndCeiling='" + getFloorAndCeiling() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

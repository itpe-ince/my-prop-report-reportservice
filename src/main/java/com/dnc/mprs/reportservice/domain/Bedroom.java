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
 * A Bedroom.
 */
@Table("bedroom")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bedroom")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bedroom implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    @Column("bedroom_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bedroomName;

    @NotNull(message = "must not be null")
    @Column("condition_level")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType conditionLevel;

    @Column("room_size")
    private BigDecimal roomSize;

    @Size(min = 1, max = 1)
    @Column("closet_yn")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String closetYn;

    @Size(min = 1, max = 1)
    @Column("ac_yn")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String acYn;

    @Size(max = 100)
    @Column("window_location")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String windowLocation;

    @Size(max = 100)
    @Column("window_size")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String windowSize;

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

    public Bedroom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBedroomName() {
        return this.bedroomName;
    }

    public Bedroom bedroomName(String bedroomName) {
        this.setBedroomName(bedroomName);
        return this;
    }

    public void setBedroomName(String bedroomName) {
        this.bedroomName = bedroomName;
    }

    public QualityStateType getConditionLevel() {
        return this.conditionLevel;
    }

    public Bedroom conditionLevel(QualityStateType conditionLevel) {
        this.setConditionLevel(conditionLevel);
        return this;
    }

    public void setConditionLevel(QualityStateType conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public BigDecimal getRoomSize() {
        return this.roomSize;
    }

    public Bedroom roomSize(BigDecimal roomSize) {
        this.setRoomSize(roomSize);
        return this;
    }

    public void setRoomSize(BigDecimal roomSize) {
        this.roomSize = roomSize != null ? roomSize.stripTrailingZeros() : null;
    }

    public String getClosetYn() {
        return this.closetYn;
    }

    public Bedroom closetYn(String closetYn) {
        this.setClosetYn(closetYn);
        return this;
    }

    public void setClosetYn(String closetYn) {
        this.closetYn = closetYn;
    }

    public String getAcYn() {
        return this.acYn;
    }

    public Bedroom acYn(String acYn) {
        this.setAcYn(acYn);
        return this;
    }

    public void setAcYn(String acYn) {
        this.acYn = acYn;
    }

    public String getWindowLocation() {
        return this.windowLocation;
    }

    public Bedroom windowLocation(String windowLocation) {
        this.setWindowLocation(windowLocation);
        return this;
    }

    public void setWindowLocation(String windowLocation) {
        this.windowLocation = windowLocation;
    }

    public String getWindowSize() {
        return this.windowSize;
    }

    public Bedroom windowSize(String windowSize) {
        this.setWindowSize(windowSize);
        return this;
    }

    public void setWindowSize(String windowSize) {
        this.windowSize = windowSize;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Bedroom remarks(String remarks) {
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

    public Bedroom report(Report report) {
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
        if (!(o instanceof Bedroom)) {
            return false;
        }
        return getId() != null && getId().equals(((Bedroom) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bedroom{" +
            "id=" + getId() +
            ", bedroomName='" + getBedroomName() + "'" +
            ", conditionLevel='" + getConditionLevel() + "'" +
            ", roomSize=" + getRoomSize() +
            ", closetYn='" + getClosetYn() + "'" +
            ", acYn='" + getAcYn() + "'" +
            ", windowLocation='" + getWindowLocation() + "'" +
            ", windowSize='" + getWindowSize() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

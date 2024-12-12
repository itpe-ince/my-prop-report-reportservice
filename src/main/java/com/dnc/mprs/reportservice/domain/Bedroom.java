package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Bedroom.
 */
@Entity
@Table(name = "bedroom")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bedroom")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bedroom implements Serializable {

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
    @Column(name = "bedroom_name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bedroomName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_level", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType conditionLevel;

    @Column(name = "room_size", precision = 21, scale = 2)
    private BigDecimal roomSize;

    @Size(min = 1, max = 1)
    @Column(name = "closet_yn", length = 1)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String closetYn;

    @Size(min = 1, max = 1)
    @Column(name = "ac_yn", length = 1)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String acYn;

    @Size(max = 100)
    @Column(name = "window_location", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String windowLocation;

    @Size(max = 100)
    @Column(name = "window_size", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String windowSize;

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

    public Bedroom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public Bedroom reportId(Long reportId) {
        this.setReportId(reportId);
        return this;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
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
        this.roomSize = roomSize;
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
    }

    public Bedroom report(Report report) {
        this.setReport(report);
        return this;
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
            ", reportId=" + getReportId() +
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

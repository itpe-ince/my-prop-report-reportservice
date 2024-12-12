package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A LivingRoom.
 */
@Entity
@Table(name = "living_room")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "livingroom")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LivingRoom implements Serializable {

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
    @Column(name = "living_room_name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String livingRoomName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_level", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType conditionLevel;

    @Column(name = "room_size", precision = 21, scale = 2)
    private BigDecimal roomSize;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "wall_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType wallState;

    @Size(max = 100)
    @Column(name = "floor_material", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String floorMaterial;

    @Size(max = 100)
    @Column(name = "sunlight", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String sunlight;

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

    public LivingRoom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public LivingRoom reportId(Long reportId) {
        this.setReportId(reportId);
        return this;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getLivingRoomName() {
        return this.livingRoomName;
    }

    public LivingRoom livingRoomName(String livingRoomName) {
        this.setLivingRoomName(livingRoomName);
        return this;
    }

    public void setLivingRoomName(String livingRoomName) {
        this.livingRoomName = livingRoomName;
    }

    public QualityStateType getConditionLevel() {
        return this.conditionLevel;
    }

    public LivingRoom conditionLevel(QualityStateType conditionLevel) {
        this.setConditionLevel(conditionLevel);
        return this;
    }

    public void setConditionLevel(QualityStateType conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public BigDecimal getRoomSize() {
        return this.roomSize;
    }

    public LivingRoom roomSize(BigDecimal roomSize) {
        this.setRoomSize(roomSize);
        return this;
    }

    public void setRoomSize(BigDecimal roomSize) {
        this.roomSize = roomSize;
    }

    public QualityStateType getWallState() {
        return this.wallState;
    }

    public LivingRoom wallState(QualityStateType wallState) {
        this.setWallState(wallState);
        return this;
    }

    public void setWallState(QualityStateType wallState) {
        this.wallState = wallState;
    }

    public String getFloorMaterial() {
        return this.floorMaterial;
    }

    public LivingRoom floorMaterial(String floorMaterial) {
        this.setFloorMaterial(floorMaterial);
        return this;
    }

    public void setFloorMaterial(String floorMaterial) {
        this.floorMaterial = floorMaterial;
    }

    public String getSunlight() {
        return this.sunlight;
    }

    public LivingRoom sunlight(String sunlight) {
        this.setSunlight(sunlight);
        return this;
    }

    public void setSunlight(String sunlight) {
        this.sunlight = sunlight;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public LivingRoom remarks(String remarks) {
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

    public LivingRoom report(Report report) {
        this.setReport(report);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LivingRoom)) {
            return false;
        }
        return getId() != null && getId().equals(((LivingRoom) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LivingRoom{" +
            "id=" + getId() +
            ", reportId=" + getReportId() +
            ", livingRoomName='" + getLivingRoomName() + "'" +
            ", conditionLevel='" + getConditionLevel() + "'" +
            ", roomSize=" + getRoomSize() +
            ", wallState='" + getWallState() + "'" +
            ", floorMaterial='" + getFloorMaterial() + "'" +
            ", sunlight='" + getSunlight() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

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
 * A LivingRoom.
 */
@Table("living_room")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "livingroom")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LivingRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    @Column("living_room_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String livingRoomName;

    @NotNull(message = "must not be null")
    @Column("condition_level")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType conditionLevel;

    @Column("room_size")
    private BigDecimal roomSize;

    @NotNull(message = "must not be null")
    @Column("wall_state")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType wallState;

    @Size(max = 100)
    @Column("floor_material")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String floorMaterial;

    @Size(max = 100)
    @Column("sunlight")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String sunlight;

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

    public LivingRoom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.roomSize = roomSize != null ? roomSize.stripTrailingZeros() : null;
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
        this.reportId = report != null ? report.getId() : null;
    }

    public LivingRoom report(Report report) {
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

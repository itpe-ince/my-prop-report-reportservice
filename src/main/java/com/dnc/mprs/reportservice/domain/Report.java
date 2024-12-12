package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "report")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "report_title", length = 200, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String reportTitle;

    @Column(name = "report_date")
    private Instant reportDate;

    @NotNull
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "summary")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String summary;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "exterior_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType exteriorState;

    @Column(name = "construction_year")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer constructionYear;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType maintenanceState;

    @Size(max = 500)
    @Column(name = "parking_facility", length = 500)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String parkingFacility;

    @Column(name = "parking_count")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer parkingCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "elevator_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType elevatorState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "noise_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType noiseState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "homepad_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType homepadState;

    @Size(min = 1, max = 1)
    @Column(name = "cctv_yn", length = 1)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String cctvYn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fire_safety_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType fireSafetyState;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "door_security_state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType doorSecurityState;

    @Column(name = "maintenance_fee")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maintenanceFee;

    @Size(min = 1, max = 1)
    @Column(name = "redevelopment_yn", length = 1)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String redevelopmentYn;

    @Size(max = 200)
    @Column(name = "rental_demand", length = 200)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String rentalDemand;

    @Size(max = 2000)
    @Column(name = "community_rules", length = 2000)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String communityRules;

    @NotNull
    @Column(name = "complex_id", nullable = false)
    private Long complexId;

    @NotNull
    @Size(max = 255)
    @Column(name = "complex_name", length = 255, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String complexName;

    @NotNull
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @NotNull
    @Size(max = 255)
    @Column(name = "property_name", length = 255, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String propertyName;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Author author;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Report id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportTitle() {
        return this.reportTitle;
    }

    public Report reportTitle(String reportTitle) {
        this.setReportTitle(reportTitle);
        return this;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public Instant getReportDate() {
        return this.reportDate;
    }

    public Report reportDate(Instant reportDate) {
        this.setReportDate(reportDate);
        return this;
    }

    public void setReportDate(Instant reportDate) {
        this.reportDate = reportDate;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public Report authorId(Long authorId) {
        this.setAuthorId(authorId);
        return this;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getSummary() {
        return this.summary;
    }

    public Report summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public QualityStateType getExteriorState() {
        return this.exteriorState;
    }

    public Report exteriorState(QualityStateType exteriorState) {
        this.setExteriorState(exteriorState);
        return this;
    }

    public void setExteriorState(QualityStateType exteriorState) {
        this.exteriorState = exteriorState;
    }

    public Integer getConstructionYear() {
        return this.constructionYear;
    }

    public Report constructionYear(Integer constructionYear) {
        this.setConstructionYear(constructionYear);
        return this;
    }

    public void setConstructionYear(Integer constructionYear) {
        this.constructionYear = constructionYear;
    }

    public QualityStateType getMaintenanceState() {
        return this.maintenanceState;
    }

    public Report maintenanceState(QualityStateType maintenanceState) {
        this.setMaintenanceState(maintenanceState);
        return this;
    }

    public void setMaintenanceState(QualityStateType maintenanceState) {
        this.maintenanceState = maintenanceState;
    }

    public String getParkingFacility() {
        return this.parkingFacility;
    }

    public Report parkingFacility(String parkingFacility) {
        this.setParkingFacility(parkingFacility);
        return this;
    }

    public void setParkingFacility(String parkingFacility) {
        this.parkingFacility = parkingFacility;
    }

    public Integer getParkingCount() {
        return this.parkingCount;
    }

    public Report parkingCount(Integer parkingCount) {
        this.setParkingCount(parkingCount);
        return this;
    }

    public void setParkingCount(Integer parkingCount) {
        this.parkingCount = parkingCount;
    }

    public QualityStateType getElevatorState() {
        return this.elevatorState;
    }

    public Report elevatorState(QualityStateType elevatorState) {
        this.setElevatorState(elevatorState);
        return this;
    }

    public void setElevatorState(QualityStateType elevatorState) {
        this.elevatorState = elevatorState;
    }

    public QualityStateType getNoiseState() {
        return this.noiseState;
    }

    public Report noiseState(QualityStateType noiseState) {
        this.setNoiseState(noiseState);
        return this;
    }

    public void setNoiseState(QualityStateType noiseState) {
        this.noiseState = noiseState;
    }

    public QualityStateType getHomepadState() {
        return this.homepadState;
    }

    public Report homepadState(QualityStateType homepadState) {
        this.setHomepadState(homepadState);
        return this;
    }

    public void setHomepadState(QualityStateType homepadState) {
        this.homepadState = homepadState;
    }

    public String getCctvYn() {
        return this.cctvYn;
    }

    public Report cctvYn(String cctvYn) {
        this.setCctvYn(cctvYn);
        return this;
    }

    public void setCctvYn(String cctvYn) {
        this.cctvYn = cctvYn;
    }

    public QualityStateType getFireSafetyState() {
        return this.fireSafetyState;
    }

    public Report fireSafetyState(QualityStateType fireSafetyState) {
        this.setFireSafetyState(fireSafetyState);
        return this;
    }

    public void setFireSafetyState(QualityStateType fireSafetyState) {
        this.fireSafetyState = fireSafetyState;
    }

    public QualityStateType getDoorSecurityState() {
        return this.doorSecurityState;
    }

    public Report doorSecurityState(QualityStateType doorSecurityState) {
        this.setDoorSecurityState(doorSecurityState);
        return this;
    }

    public void setDoorSecurityState(QualityStateType doorSecurityState) {
        this.doorSecurityState = doorSecurityState;
    }

    public Integer getMaintenanceFee() {
        return this.maintenanceFee;
    }

    public Report maintenanceFee(Integer maintenanceFee) {
        this.setMaintenanceFee(maintenanceFee);
        return this;
    }

    public void setMaintenanceFee(Integer maintenanceFee) {
        this.maintenanceFee = maintenanceFee;
    }

    public String getRedevelopmentYn() {
        return this.redevelopmentYn;
    }

    public Report redevelopmentYn(String redevelopmentYn) {
        this.setRedevelopmentYn(redevelopmentYn);
        return this;
    }

    public void setRedevelopmentYn(String redevelopmentYn) {
        this.redevelopmentYn = redevelopmentYn;
    }

    public String getRentalDemand() {
        return this.rentalDemand;
    }

    public Report rentalDemand(String rentalDemand) {
        this.setRentalDemand(rentalDemand);
        return this;
    }

    public void setRentalDemand(String rentalDemand) {
        this.rentalDemand = rentalDemand;
    }

    public String getCommunityRules() {
        return this.communityRules;
    }

    public Report communityRules(String communityRules) {
        this.setCommunityRules(communityRules);
        return this;
    }

    public void setCommunityRules(String communityRules) {
        this.communityRules = communityRules;
    }

    public Long getComplexId() {
        return this.complexId;
    }

    public Report complexId(Long complexId) {
        this.setComplexId(complexId);
        return this;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public String getComplexName() {
        return this.complexName;
    }

    public Report complexName(String complexName) {
        this.setComplexName(complexName);
        return this;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Long getPropertyId() {
        return this.propertyId;
    }

    public Report propertyId(Long propertyId) {
        this.setPropertyId(propertyId);
        return this;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Report propertyName(String propertyName) {
        this.setPropertyName(propertyName);
        return this;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Report createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Report updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Report author(Author author) {
        this.setAuthor(author);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Report)) {
            return false;
        }
        return getId() != null && getId().equals(((Report) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", reportTitle='" + getReportTitle() + "'" +
            ", reportDate='" + getReportDate() + "'" +
            ", authorId=" + getAuthorId() +
            ", summary='" + getSummary() + "'" +
            ", exteriorState='" + getExteriorState() + "'" +
            ", constructionYear=" + getConstructionYear() +
            ", maintenanceState='" + getMaintenanceState() + "'" +
            ", parkingFacility='" + getParkingFacility() + "'" +
            ", parkingCount=" + getParkingCount() +
            ", elevatorState='" + getElevatorState() + "'" +
            ", noiseState='" + getNoiseState() + "'" +
            ", homepadState='" + getHomepadState() + "'" +
            ", cctvYn='" + getCctvYn() + "'" +
            ", fireSafetyState='" + getFireSafetyState() + "'" +
            ", doorSecurityState='" + getDoorSecurityState() + "'" +
            ", maintenanceFee=" + getMaintenanceFee() +
            ", redevelopmentYn='" + getRedevelopmentYn() + "'" +
            ", rentalDemand='" + getRentalDemand() + "'" +
            ", communityRules='" + getCommunityRules() + "'" +
            ", complexId=" + getComplexId() +
            ", complexName='" + getComplexName() + "'" +
            ", propertyId=" + getPropertyId() +
            ", propertyName='" + getPropertyName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}

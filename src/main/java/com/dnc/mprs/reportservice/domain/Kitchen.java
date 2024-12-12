package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Kitchen.
 */
@Entity
@Table(name = "kitchen")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "kitchen")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Kitchen implements Serializable {

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
    @Column(name = "kitchen_name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String kitchenName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_level", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType conditionLevel;

    @Size(min = 1, max = 1)
    @Column(name = "built_in_cabinet", length = 1)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String builtInCabinet;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sink_condition", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType sinkCondition;

    @Size(max = 100)
    @Column(name = "ventilation_system", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String ventilationSystem;

    @Size(max = 100)
    @Column(name = "appliance_provision", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String applianceProvision;

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

    public Kitchen id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public Kitchen reportId(Long reportId) {
        this.setReportId(reportId);
        return this;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getKitchenName() {
        return this.kitchenName;
    }

    public Kitchen kitchenName(String kitchenName) {
        this.setKitchenName(kitchenName);
        return this;
    }

    public void setKitchenName(String kitchenName) {
        this.kitchenName = kitchenName;
    }

    public QualityStateType getConditionLevel() {
        return this.conditionLevel;
    }

    public Kitchen conditionLevel(QualityStateType conditionLevel) {
        this.setConditionLevel(conditionLevel);
        return this;
    }

    public void setConditionLevel(QualityStateType conditionLevel) {
        this.conditionLevel = conditionLevel;
    }

    public String getBuiltInCabinet() {
        return this.builtInCabinet;
    }

    public Kitchen builtInCabinet(String builtInCabinet) {
        this.setBuiltInCabinet(builtInCabinet);
        return this;
    }

    public void setBuiltInCabinet(String builtInCabinet) {
        this.builtInCabinet = builtInCabinet;
    }

    public QualityStateType getSinkCondition() {
        return this.sinkCondition;
    }

    public Kitchen sinkCondition(QualityStateType sinkCondition) {
        this.setSinkCondition(sinkCondition);
        return this;
    }

    public void setSinkCondition(QualityStateType sinkCondition) {
        this.sinkCondition = sinkCondition;
    }

    public String getVentilationSystem() {
        return this.ventilationSystem;
    }

    public Kitchen ventilationSystem(String ventilationSystem) {
        this.setVentilationSystem(ventilationSystem);
        return this;
    }

    public void setVentilationSystem(String ventilationSystem) {
        this.ventilationSystem = ventilationSystem;
    }

    public String getApplianceProvision() {
        return this.applianceProvision;
    }

    public Kitchen applianceProvision(String applianceProvision) {
        this.setApplianceProvision(applianceProvision);
        return this;
    }

    public void setApplianceProvision(String applianceProvision) {
        this.applianceProvision = applianceProvision;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Kitchen remarks(String remarks) {
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

    public Kitchen report(Report report) {
        this.setReport(report);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Kitchen)) {
            return false;
        }
        return getId() != null && getId().equals(((Kitchen) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Kitchen{" +
            "id=" + getId() +
            ", reportId=" + getReportId() +
            ", kitchenName='" + getKitchenName() + "'" +
            ", conditionLevel='" + getConditionLevel() + "'" +
            ", builtInCabinet='" + getBuiltInCabinet() + "'" +
            ", sinkCondition='" + getSinkCondition() + "'" +
            ", ventilationSystem='" + getVentilationSystem() + "'" +
            ", applianceProvision='" + getApplianceProvision() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

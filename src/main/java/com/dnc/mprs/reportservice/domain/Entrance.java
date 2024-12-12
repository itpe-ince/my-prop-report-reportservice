package com.dnc.mprs.reportservice.domain;

import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Entrance.
 */
@Entity
@Table(name = "entrance")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "entrance")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Entrance implements Serializable {

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
    @Column(name = "entrance_name", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String entranceName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condtion_level", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType condtionLevel;

    @Column(name = "entrance_size", precision = 21, scale = 2)
    private BigDecimal entranceSize;

    @Column(name = "shoe_rack_size", precision = 21, scale = 2)
    private BigDecimal shoeRackSize;

    @Size(min = 1, max = 1)
    @Column(name = "pantry_presence", length = 1)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String pantryPresence;

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

    public Entrance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public Entrance reportId(Long reportId) {
        this.setReportId(reportId);
        return this;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getEntranceName() {
        return this.entranceName;
    }

    public Entrance entranceName(String entranceName) {
        this.setEntranceName(entranceName);
        return this;
    }

    public void setEntranceName(String entranceName) {
        this.entranceName = entranceName;
    }

    public QualityStateType getCondtionLevel() {
        return this.condtionLevel;
    }

    public Entrance condtionLevel(QualityStateType condtionLevel) {
        this.setCondtionLevel(condtionLevel);
        return this;
    }

    public void setCondtionLevel(QualityStateType condtionLevel) {
        this.condtionLevel = condtionLevel;
    }

    public BigDecimal getEntranceSize() {
        return this.entranceSize;
    }

    public Entrance entranceSize(BigDecimal entranceSize) {
        this.setEntranceSize(entranceSize);
        return this;
    }

    public void setEntranceSize(BigDecimal entranceSize) {
        this.entranceSize = entranceSize;
    }

    public BigDecimal getShoeRackSize() {
        return this.shoeRackSize;
    }

    public Entrance shoeRackSize(BigDecimal shoeRackSize) {
        this.setShoeRackSize(shoeRackSize);
        return this;
    }

    public void setShoeRackSize(BigDecimal shoeRackSize) {
        this.shoeRackSize = shoeRackSize;
    }

    public String getPantryPresence() {
        return this.pantryPresence;
    }

    public Entrance pantryPresence(String pantryPresence) {
        this.setPantryPresence(pantryPresence);
        return this;
    }

    public void setPantryPresence(String pantryPresence) {
        this.pantryPresence = pantryPresence;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public Entrance remarks(String remarks) {
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

    public Entrance report(Report report) {
        this.setReport(report);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entrance)) {
            return false;
        }
        return getId() != null && getId().equals(((Entrance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Entrance{" +
            "id=" + getId() +
            ", reportId=" + getReportId() +
            ", entranceName='" + getEntranceName() + "'" +
            ", condtionLevel='" + getCondtionLevel() + "'" +
            ", entranceSize=" + getEntranceSize() +
            ", shoeRackSize=" + getShoeRackSize() +
            ", pantryPresence='" + getPantryPresence() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

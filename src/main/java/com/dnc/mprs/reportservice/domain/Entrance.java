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
 * A Entrance.
 */
@Table("entrance")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "entrance")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Entrance implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 100)
    @Column("entrance_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String entranceName;

    @NotNull(message = "must not be null")
    @Column("condtion_level")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private QualityStateType condtionLevel;

    @Column("entrance_size")
    private BigDecimal entranceSize;

    @Column("shoe_rack_size")
    private BigDecimal shoeRackSize;

    @Size(min = 1, max = 1)
    @Column("pantry_presence")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String pantryPresence;

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

    public Entrance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.entranceSize = entranceSize != null ? entranceSize.stripTrailingZeros() : null;
    }

    public BigDecimal getShoeRackSize() {
        return this.shoeRackSize;
    }

    public Entrance shoeRackSize(BigDecimal shoeRackSize) {
        this.setShoeRackSize(shoeRackSize);
        return this;
    }

    public void setShoeRackSize(BigDecimal shoeRackSize) {
        this.shoeRackSize = shoeRackSize != null ? shoeRackSize.stripTrailingZeros() : null;
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
        this.reportId = report != null ? report.getId() : null;
    }

    public Entrance report(Report report) {
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
            ", entranceName='" + getEntranceName() + "'" +
            ", condtionLevel='" + getCondtionLevel() + "'" +
            ", entranceSize=" + getEntranceSize() +
            ", shoeRackSize=" + getShoeRackSize() +
            ", pantryPresence='" + getPantryPresence() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}

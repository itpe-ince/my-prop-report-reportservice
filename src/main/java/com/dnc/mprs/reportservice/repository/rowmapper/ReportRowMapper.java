package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.Report;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Report}, with proper type conversions.
 */
@Service
public class ReportRowMapper implements BiFunction<Row, String, Report> {

    private final ColumnConverter converter;

    public ReportRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Report} stored in the database.
     */
    @Override
    public Report apply(Row row, String prefix) {
        Report entity = new Report();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReportTitle(converter.fromRow(row, prefix + "_report_title", String.class));
        entity.setReportDate(converter.fromRow(row, prefix + "_report_date", Instant.class));
        entity.setSummary(converter.fromRow(row, prefix + "_summary", String.class));
        entity.setExteriorState(converter.fromRow(row, prefix + "_exterior_state", QualityStateType.class));
        entity.setConstructionYear(converter.fromRow(row, prefix + "_construction_year", Integer.class));
        entity.setMaintenanceState(converter.fromRow(row, prefix + "_maintenance_state", QualityStateType.class));
        entity.setParkingFacility(converter.fromRow(row, prefix + "_parking_facility", String.class));
        entity.setParkingCount(converter.fromRow(row, prefix + "_parking_count", Integer.class));
        entity.setElevatorState(converter.fromRow(row, prefix + "_elevator_state", QualityStateType.class));
        entity.setNoiseState(converter.fromRow(row, prefix + "_noise_state", QualityStateType.class));
        entity.setHomepadState(converter.fromRow(row, prefix + "_homepad_state", QualityStateType.class));
        entity.setCctvYn(converter.fromRow(row, prefix + "_cctv_yn", String.class));
        entity.setFireSafetyState(converter.fromRow(row, prefix + "_fire_safety_state", QualityStateType.class));
        entity.setDoorSecurityState(converter.fromRow(row, prefix + "_door_security_state", QualityStateType.class));
        entity.setMaintenanceFee(converter.fromRow(row, prefix + "_maintenance_fee", Integer.class));
        entity.setRedevelopmentYn(converter.fromRow(row, prefix + "_redevelopment_yn", String.class));
        entity.setRentalDemand(converter.fromRow(row, prefix + "_rental_demand", String.class));
        entity.setCommunityRules(converter.fromRow(row, prefix + "_community_rules", String.class));
        entity.setComplexId(converter.fromRow(row, prefix + "_complex_id", Long.class));
        entity.setComplexName(converter.fromRow(row, prefix + "_complex_name", String.class));
        entity.setPropertyId(converter.fromRow(row, prefix + "_property_id", Long.class));
        entity.setPropertyName(converter.fromRow(row, prefix + "_property_name", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setAuthorId(converter.fromRow(row, prefix + "_author_id", Long.class));
        return entity;
    }
}

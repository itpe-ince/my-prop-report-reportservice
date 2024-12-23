package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.Infrastructure;
import com.dnc.mprs.reportservice.domain.enumeration.InfraType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Infrastructure}, with proper type conversions.
 */
@Service
public class InfrastructureRowMapper implements BiFunction<Row, String, Infrastructure> {

    private final ColumnConverter converter;

    public InfrastructureRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Infrastructure} stored in the database.
     */
    @Override
    public Infrastructure apply(Row row, String prefix) {
        Infrastructure entity = new Infrastructure();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setInfraType(converter.fromRow(row, prefix + "_infra_type", InfraType.class));
        entity.setInfraName(converter.fromRow(row, prefix + "_infra_name", String.class));
        entity.setConditionLevel(converter.fromRow(row, prefix + "_condition_level", QualityStateType.class));
        entity.setInfraDistance(converter.fromRow(row, prefix + "_infra_distance", Integer.class));
        entity.setInfraDistanceUnit(converter.fromRow(row, prefix + "_infra_distance_unit", QualityStateType.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}

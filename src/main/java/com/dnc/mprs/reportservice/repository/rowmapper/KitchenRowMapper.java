package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Kitchen}, with proper type conversions.
 */
@Service
public class KitchenRowMapper implements BiFunction<Row, String, Kitchen> {

    private final ColumnConverter converter;

    public KitchenRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Kitchen} stored in the database.
     */
    @Override
    public Kitchen apply(Row row, String prefix) {
        Kitchen entity = new Kitchen();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setKitchenName(converter.fromRow(row, prefix + "_kitchen_name", String.class));
        entity.setConditionLevel(converter.fromRow(row, prefix + "_condition_level", QualityStateType.class));
        entity.setBuiltInCabinet(converter.fromRow(row, prefix + "_built_in_cabinet", String.class));
        entity.setSinkCondition(converter.fromRow(row, prefix + "_sink_condition", QualityStateType.class));
        entity.setVentilationSystem(converter.fromRow(row, prefix + "_ventilation_system", String.class));
        entity.setApplianceProvision(converter.fromRow(row, prefix + "_appliance_provision", String.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}

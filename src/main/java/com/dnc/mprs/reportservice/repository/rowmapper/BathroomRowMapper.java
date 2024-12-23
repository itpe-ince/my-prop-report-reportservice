package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Bathroom}, with proper type conversions.
 */
@Service
public class BathroomRowMapper implements BiFunction<Row, String, Bathroom> {

    private final ColumnConverter converter;

    public BathroomRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Bathroom} stored in the database.
     */
    @Override
    public Bathroom apply(Row row, String prefix) {
        Bathroom entity = new Bathroom();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBathroomName(converter.fromRow(row, prefix + "_bathroom_name", String.class));
        entity.setCondtionLevel(converter.fromRow(row, prefix + "_condtion_level", QualityStateType.class));
        entity.setBathroomSize(converter.fromRow(row, prefix + "_bathroom_size", BigDecimal.class));
        entity.setWaterPressure(converter.fromRow(row, prefix + "_water_pressure", QualityStateType.class));
        entity.setShowerBoothPresence(converter.fromRow(row, prefix + "_shower_booth_presence", String.class));
        entity.setBathtubPresence(converter.fromRow(row, prefix + "_bathtub_presence", String.class));
        entity.setFloorAndCeiling(converter.fromRow(row, prefix + "_floor_and_ceiling", QualityStateType.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}

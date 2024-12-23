package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.Bedroom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Bedroom}, with proper type conversions.
 */
@Service
public class BedroomRowMapper implements BiFunction<Row, String, Bedroom> {

    private final ColumnConverter converter;

    public BedroomRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Bedroom} stored in the database.
     */
    @Override
    public Bedroom apply(Row row, String prefix) {
        Bedroom entity = new Bedroom();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBedroomName(converter.fromRow(row, prefix + "_bedroom_name", String.class));
        entity.setConditionLevel(converter.fromRow(row, prefix + "_condition_level", QualityStateType.class));
        entity.setRoomSize(converter.fromRow(row, prefix + "_room_size", BigDecimal.class));
        entity.setClosetYn(converter.fromRow(row, prefix + "_closet_yn", String.class));
        entity.setAcYn(converter.fromRow(row, prefix + "_ac_yn", String.class));
        entity.setWindowLocation(converter.fromRow(row, prefix + "_window_location", String.class));
        entity.setWindowSize(converter.fromRow(row, prefix + "_window_size", String.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}

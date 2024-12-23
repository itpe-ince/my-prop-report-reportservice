package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.LivingRoom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link LivingRoom}, with proper type conversions.
 */
@Service
public class LivingRoomRowMapper implements BiFunction<Row, String, LivingRoom> {

    private final ColumnConverter converter;

    public LivingRoomRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link LivingRoom} stored in the database.
     */
    @Override
    public LivingRoom apply(Row row, String prefix) {
        LivingRoom entity = new LivingRoom();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLivingRoomName(converter.fromRow(row, prefix + "_living_room_name", String.class));
        entity.setConditionLevel(converter.fromRow(row, prefix + "_condition_level", QualityStateType.class));
        entity.setRoomSize(converter.fromRow(row, prefix + "_room_size", BigDecimal.class));
        entity.setWallState(converter.fromRow(row, prefix + "_wall_state", QualityStateType.class));
        entity.setFloorMaterial(converter.fromRow(row, prefix + "_floor_material", String.class));
        entity.setSunlight(converter.fromRow(row, prefix + "_sunlight", String.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}

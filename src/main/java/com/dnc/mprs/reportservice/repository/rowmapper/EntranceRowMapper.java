package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.Entrance;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Entrance}, with proper type conversions.
 */
@Service
public class EntranceRowMapper implements BiFunction<Row, String, Entrance> {

    private final ColumnConverter converter;

    public EntranceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Entrance} stored in the database.
     */
    @Override
    public Entrance apply(Row row, String prefix) {
        Entrance entity = new Entrance();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEntranceName(converter.fromRow(row, prefix + "_entrance_name", String.class));
        entity.setCondtionLevel(converter.fromRow(row, prefix + "_condtion_level", QualityStateType.class));
        entity.setEntranceSize(converter.fromRow(row, prefix + "_entrance_size", BigDecimal.class));
        entity.setShoeRackSize(converter.fromRow(row, prefix + "_shoe_rack_size", BigDecimal.class));
        entity.setPantryPresence(converter.fromRow(row, prefix + "_pantry_presence", String.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}

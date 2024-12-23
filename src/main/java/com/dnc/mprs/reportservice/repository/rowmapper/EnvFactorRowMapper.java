package com.dnc.mprs.reportservice.repository.rowmapper;

import com.dnc.mprs.reportservice.domain.EnvFactor;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link EnvFactor}, with proper type conversions.
 */
@Service
public class EnvFactorRowMapper implements BiFunction<Row, String, EnvFactor> {

    private final ColumnConverter converter;

    public EnvFactorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link EnvFactor} stored in the database.
     */
    @Override
    public EnvFactor apply(Row row, String prefix) {
        EnvFactor entity = new EnvFactor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEnvFactorName(converter.fromRow(row, prefix + "_env_factor_name", String.class));
        entity.setEnvFactorDistance(converter.fromRow(row, prefix + "_env_factor_distance", BigDecimal.class));
        entity.setRemarks(converter.fromRow(row, prefix + "_remarks", String.class));
        entity.setReportId(converter.fromRow(row, prefix + "_report_id", Long.class));
        return entity;
    }
}

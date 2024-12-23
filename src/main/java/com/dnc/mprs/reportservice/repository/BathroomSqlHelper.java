package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BathroomSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("bathroom_name", table, columnPrefix + "_bathroom_name"));
        columns.add(Column.aliased("condtion_level", table, columnPrefix + "_condtion_level"));
        columns.add(Column.aliased("bathroom_size", table, columnPrefix + "_bathroom_size"));
        columns.add(Column.aliased("water_pressure", table, columnPrefix + "_water_pressure"));
        columns.add(Column.aliased("shower_booth_presence", table, columnPrefix + "_shower_booth_presence"));
        columns.add(Column.aliased("bathtub_presence", table, columnPrefix + "_bathtub_presence"));
        columns.add(Column.aliased("floor_and_ceiling", table, columnPrefix + "_floor_and_ceiling"));
        columns.add(Column.aliased("remarks", table, columnPrefix + "_remarks"));

        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}

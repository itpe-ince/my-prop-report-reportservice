package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EntranceSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("entrance_name", table, columnPrefix + "_entrance_name"));
        columns.add(Column.aliased("condtion_level", table, columnPrefix + "_condtion_level"));
        columns.add(Column.aliased("entrance_size", table, columnPrefix + "_entrance_size"));
        columns.add(Column.aliased("shoe_rack_size", table, columnPrefix + "_shoe_rack_size"));
        columns.add(Column.aliased("pantry_presence", table, columnPrefix + "_pantry_presence"));
        columns.add(Column.aliased("remarks", table, columnPrefix + "_remarks"));

        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}

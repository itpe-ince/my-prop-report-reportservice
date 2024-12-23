package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BedroomSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("bedroom_name", table, columnPrefix + "_bedroom_name"));
        columns.add(Column.aliased("condition_level", table, columnPrefix + "_condition_level"));
        columns.add(Column.aliased("room_size", table, columnPrefix + "_room_size"));
        columns.add(Column.aliased("closet_yn", table, columnPrefix + "_closet_yn"));
        columns.add(Column.aliased("ac_yn", table, columnPrefix + "_ac_yn"));
        columns.add(Column.aliased("window_location", table, columnPrefix + "_window_location"));
        columns.add(Column.aliased("window_size", table, columnPrefix + "_window_size"));
        columns.add(Column.aliased("remarks", table, columnPrefix + "_remarks"));

        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}

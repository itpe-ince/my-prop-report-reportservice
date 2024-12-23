package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ReportSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("report_title", table, columnPrefix + "_report_title"));
        columns.add(Column.aliased("report_date", table, columnPrefix + "_report_date"));
        columns.add(Column.aliased("summary", table, columnPrefix + "_summary"));
        columns.add(Column.aliased("exterior_state", table, columnPrefix + "_exterior_state"));
        columns.add(Column.aliased("construction_year", table, columnPrefix + "_construction_year"));
        columns.add(Column.aliased("maintenance_state", table, columnPrefix + "_maintenance_state"));
        columns.add(Column.aliased("parking_facility", table, columnPrefix + "_parking_facility"));
        columns.add(Column.aliased("parking_count", table, columnPrefix + "_parking_count"));
        columns.add(Column.aliased("elevator_state", table, columnPrefix + "_elevator_state"));
        columns.add(Column.aliased("noise_state", table, columnPrefix + "_noise_state"));
        columns.add(Column.aliased("homepad_state", table, columnPrefix + "_homepad_state"));
        columns.add(Column.aliased("cctv_yn", table, columnPrefix + "_cctv_yn"));
        columns.add(Column.aliased("fire_safety_state", table, columnPrefix + "_fire_safety_state"));
        columns.add(Column.aliased("door_security_state", table, columnPrefix + "_door_security_state"));
        columns.add(Column.aliased("maintenance_fee", table, columnPrefix + "_maintenance_fee"));
        columns.add(Column.aliased("redevelopment_yn", table, columnPrefix + "_redevelopment_yn"));
        columns.add(Column.aliased("rental_demand", table, columnPrefix + "_rental_demand"));
        columns.add(Column.aliased("community_rules", table, columnPrefix + "_community_rules"));
        columns.add(Column.aliased("complex_id", table, columnPrefix + "_complex_id"));
        columns.add(Column.aliased("complex_name", table, columnPrefix + "_complex_name"));
        columns.add(Column.aliased("property_id", table, columnPrefix + "_property_id"));
        columns.add(Column.aliased("property_name", table, columnPrefix + "_property_name"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));

        columns.add(Column.aliased("author_id", table, columnPrefix + "_author_id"));
        return columns;
    }
}

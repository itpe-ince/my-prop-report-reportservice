package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class InfrastructureSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("infra_type", table, columnPrefix + "_infra_type"));
        columns.add(Column.aliased("infra_name", table, columnPrefix + "_infra_name"));
        columns.add(Column.aliased("condition_level", table, columnPrefix + "_condition_level"));
        columns.add(Column.aliased("infra_distance", table, columnPrefix + "_infra_distance"));
        columns.add(Column.aliased("infra_distance_unit", table, columnPrefix + "_infra_distance_unit"));
        columns.add(Column.aliased("remarks", table, columnPrefix + "_remarks"));

        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}

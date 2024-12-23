package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EnvFactorSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("env_factor_name", table, columnPrefix + "_env_factor_name"));
        columns.add(Column.aliased("env_factor_distance", table, columnPrefix + "_env_factor_distance"));
        columns.add(Column.aliased("remarks", table, columnPrefix + "_remarks"));

        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}

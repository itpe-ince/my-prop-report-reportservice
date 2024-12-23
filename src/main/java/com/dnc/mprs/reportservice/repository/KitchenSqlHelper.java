package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class KitchenSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("kitchen_name", table, columnPrefix + "_kitchen_name"));
        columns.add(Column.aliased("condition_level", table, columnPrefix + "_condition_level"));
        columns.add(Column.aliased("built_in_cabinet", table, columnPrefix + "_built_in_cabinet"));
        columns.add(Column.aliased("sink_condition", table, columnPrefix + "_sink_condition"));
        columns.add(Column.aliased("ventilation_system", table, columnPrefix + "_ventilation_system"));
        columns.add(Column.aliased("appliance_provision", table, columnPrefix + "_appliance_provision"));
        columns.add(Column.aliased("remarks", table, columnPrefix + "_remarks"));

        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}

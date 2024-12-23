package com.dnc.mprs.reportservice.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class LivingRoomSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("living_room_name", table, columnPrefix + "_living_room_name"));
        columns.add(Column.aliased("condition_level", table, columnPrefix + "_condition_level"));
        columns.add(Column.aliased("room_size", table, columnPrefix + "_room_size"));
        columns.add(Column.aliased("wall_state", table, columnPrefix + "_wall_state"));
        columns.add(Column.aliased("floor_material", table, columnPrefix + "_floor_material"));
        columns.add(Column.aliased("sunlight", table, columnPrefix + "_sunlight"));
        columns.add(Column.aliased("remarks", table, columnPrefix + "_remarks"));

        columns.add(Column.aliased("report_id", table, columnPrefix + "_report_id"));
        return columns;
    }
}

package com.silentgo.core.plugin.db.bridge.mysql;

import com.silentgo.utils.StringKit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by teddy on 2015/9/24.
 */
public class SQLTool {

    private static String EmptySplit = " ";
    private String tableName;

    private boolean cached = false;

    private String sql = "";

    private SQLType type;

    private StringBuilder selectSQL = new StringBuilder();

    private List<String> insertList = new ArrayList<>();

    private int insertCount = 1;

    private StringBuilder exceptSelectSQL = new StringBuilder();

    private List<String> selectList = new ArrayList<>();

    private List<String> updateList = new ArrayList<>();

    private List<String> updateWhereList = new ArrayList<>();

    private List<String> joinList = new ArrayList<>();

    private List<String> whereList = new ArrayList<>();

    private List<String> orderList = new ArrayList<>();

    private List<String> groupList = new ArrayList<>();

    private List<Object> params = new ArrayList<>();

    private String limit = "";

    public SQLTool() {

    }

    public SQLTool(String sql, List<Object> paras) {
        this.sql = sql;
        this.params = paras;
    }

    public String getSelectSQL() {
        return " select " + StringKit.join(selectList, ",");
    }

    public String getExceptSQL() {

        return " from " + tableName + getWhereSQL() + getGroupSQL() + getOrderSQL() + getLimit();
    }

    public String getSQL() {
        if (cached) {
            if (StringKit.isBlank(sql)) {
                sql = getSql(type);
            }
            return sql;
        } else {
            return getSql(type);
        }
    }

    private String getSql(SQLType type) {
        switch (type) {
            case DELETE: {
                break;
            }
            case UPDATE: {
                break;
            }
            case INSERT: {
                return getInsertSQL();
            }
            case QUERY: {
                return getSelectSQL() + getExceptSQL();
            }
        }
        return "";
    }

    private String getInsertSQL() {
        String sql = getListSQL(this.insertList, "insert ( ", " ) ", " , ", EmptySplit);
        String value = getListSQL(this.insertList.size(), " ( ", " ) ", "?", EmptySplit);
        return sql + getListSQL(this.insertList.size(), value, "", ",", EmptySplit);
    }

    public String getWhereSQL() {
        return getListSQL(whereList, " where ( ", " ) ", " AND ", EmptySplit);
    }

    public String getGroupSQL() {
        return getListSQL(groupList, " group by ", "", ",", EmptySplit);
    }

    public String getOrderSQL() {
        return getListSQL(orderList, " order by ", "", ",", EmptySplit);
    }

    public String getLimit() {
        return limit;
    }

    private String getListSQL(List<String> list, String prefix, String suffix, String split, String empty) {
        return list.size() > 0 ? prefix + StringKit.join(list, split) + suffix : empty;
    }

    private String getListSQL(int len, String prefix, String suffix, String split, String empty) {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        for (int i = 0; i < len; i++) {
            builder.append(split);
        }
        builder.append(suffix).append(empty);
        return builder.toString();
    }

    public Object[] getParams() {
        return params.toArray();
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }


    public SQLTool appendParam(Object... objects) {
        Collections.addAll(this.params, objects);
        return this;
    }

    //update
    public SQLTool update(String tableName) {
        this.type = SQLType.UPDATE;
        this.tableName = tableName;
        return this;
    }

    public SQLTool update(String tableName, Collection<String> columns) {
        this.type = SQLType.UPDATE;
        this.tableName = tableName;
        this.updateWhereList.addAll(columns);
        return this;
    }


    public SQLTool update(String tableName, String... columns) {
        this.type = SQLType.UPDATE;
        this.tableName = tableName;
        Collections.addAll(this.updateWhereList, columns);
        return this;
    }

    public SQLTool update(String... columns) {
        Collections.addAll(this.updateWhereList, columns);
        return this;
    }


    //end update

    // select start

    public SQLTool select(String tableName, Collection<String> columns) {
        this.type = SQLType.QUERY;
        this.tableName = tableName;
        this.selectList.addAll(columns);
        return this;
    }

    public SQLTool select(String tableName, String... columns) {
        this.type = SQLType.QUERY;
        this.tableName = tableName;
        Collections.addAll(this.selectList, columns);
        return this;
    }

    //end select

    //insert start
    public SQLTool insert(String tableName) {
        this.type = SQLType.INSERT;
        this.tableName = tableName;
        return this;
    }

    public SQLTool insert(String tableName, String... columns) {
        this.type = SQLType.INSERT;
        this.tableName = tableName;
        Collections.addAll(this.insertList, columns);
        return this;
    }

    public SQLTool insert(String tableName, Collection<String> columns) {
        this.type = SQLType.INSERT;
        this.tableName = tableName;
        this.insertList.addAll(columns);
        return this;
    }

    public SQLTool insert(String... columns) {
        Collections.addAll(this.insertList, columns);
        return this;
    }
    //insert end

    public SQLTool value(int count) {
        this.insertCount = count;
        return this;
    }


    public SQLTool from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SQLTool whereEquals(String condition) {
        this.whereList.add(condition + " = ?");
        return this;
    }

    public SQLTool whereIn(String condition) {
        return whereIn(condition, 1);
    }

    public SQLTool whereIn(String condition, int len) {
        StringBuilder builder = new StringBuilder();
        builder.append(condition).append(" in (");
        for (int i = 0; i < len - 1; i++) {
            builder.append("?,");
        }
        builder.append("? )");
        this.whereList.add(builder.toString());
        return this;
    }


    public SQLTool where(String... conditions) {
        Collections.addAll(this.whereList, conditions);
        return this;
    }

    private String join(String direct, String tableName) {
        return EmptySplit + direct + " join " + tableName;
    }

    public SQLTool leftJoin(String tableName, String condition) {
        this.joinList.add(join("left", tableName) + " on " + condition);
        return this;
    }

    public SQLTool rightJoin(String tableName, String condition) {
        this.joinList.add(join("right", tableName) + " on " + condition);
        return this;
    }

    public SQLTool join(String direct, String tableName, String condition) {
        this.joinList.add(join(direct, tableName) + " on " + condition);
        return this;
    }

    public SQLTool groupBy(String... columns) {
        Collections.addAll(this.groupList, columns);
        return this;
    }

    public SQLTool orderByDesc(String... columns) {
        Collections.addAll(this.orderList, orderBy(" DESC ", columns));
        return this;
    }

    public SQLTool orderByAsc(String... columns) {
        Collections.addAll(this.orderList, orderBy(" ASC ", columns));
        return this;
    }

    private String[] orderBy(String order, String... columns) {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = EmptySplit + columns[i] + EmptySplit + order;
        }
        return columns;
    }

    public SQLTool findFirst(int size, int page) {
        this.limit = " limit 0,1 ";
        return this;
    }

    public SQLTool limit(int size, int page) {
        this.limit = " limit " + (page - 1) * size + "," + size;
        return this;
    }

    @Override
    public String toString() {
        return getSQL();
    }

    public static String NOTIN(String column, String condition) {
        return column + " NOT IN (" + condition + ")";
    }

    public static String IN(String column, String condition) {
        return column + " IN (" + condition + ")";
    }

    public static String SELECT(String... columns) {
        return " select " + StringKit.join(columns, ",");
    }

    public static String FROM(String tableName) {
        return " from " + tableName;
    }

    public static String WHERE(String logic, String... conditions) {

        return " where ( " + StringKit.join(conditions, " " + logic + " ") + " ) ";
    }

    public static String WHERE(String conditions) {
        return " where ( " + conditions + " ) ";
    }

    public static String OR(String... conditions) {
        return " ( " + StringKit.join(conditions, " OR ") + " ) ";
    }

    public static String AND(String... conditions) {
        return " ( " + StringKit.join(conditions, " AND ") + " ) ";
    }

    public static String COUNT(String column) {
        return " count(" + column + ")";
    }

    public static String AS(String column, String newColumn) {
        return column + " as " + newColumn;
    }

}

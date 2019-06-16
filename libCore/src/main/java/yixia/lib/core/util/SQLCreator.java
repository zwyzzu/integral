package yixia.lib.core.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangwy on 2018/6/9 下午3:49.
 * Updated by zhangwy on 2018/6/9 下午3:49.
 * Description sql语句拼接
 */
@SuppressWarnings("unused")
public class SQLCreator {

    public static SQLCreator newInstance(String tableName) {
        return new SQLCreator(tableName);
    }

    private SQLCreator(String tableName) {
        this.tableName = tableName;
    }

    private String tableName;
    private Param primaryKey;
    private List<Param> params = new ArrayList<>();
    private HashMap<String, Param> map = new HashMap<>();
    private String createSQL;
    private String querySQL;

    public SQLCreator setPrimaryKey(String column, Format format) {
        if (TextUtils.isEmpty(column) || format == null)
            return this;
        this.primaryKey = new Param(column, format, false);
        return this;
    }

    public SQLCreator put(String column, Format format, boolean canNull) {
        if (TextUtils.isEmpty(column) || format == null) {
            return this;
        }
        Param param;
        if (this.map.containsKey(column) && (param = this.map.get(column)) != null) {
            params.remove(param);
        }
        param = new Param(column, format, canNull);
        this.params.add(param);
        this.map.put(column, param);
        return this;
    }

    public boolean build() {
        if (TextUtils.isEmpty(this.tableName)) {
            return false;
        }
        this.buildCreateSQL();
        this.buildQuerySQL();
        return true;
    }

    public String create() {
        if (TextUtils.isEmpty(this.createSQL)) {
            return this.buildCreateSQL();
        } else {
            return this.createSQL;
        }
    }

    private String buildCreateSQL() {
        String sql = "CREATE TABLE IF NOT EXISTS %s (%s)";
        String column = "%s %s";
        String columnNotNull = "%s %s NOT NULL";
        StringBuilder builder = new StringBuilder();
        if (primaryKey != null) {
            builder.append(String.format(columnNotNull, primaryKey.column, primaryKey.format.code));
            builder.append(" PRIMARY KEY");
        }
        for (Param param : params) {
            if (param == null) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(", ");
            }
            if (param.canNull) {
                builder.append(String.format(column, param.column, param.format.code));
            } else {
                builder.append(String.format(columnNotNull, param.column, param.format.code));
            }
        }
        this.createSQL = String.format(sql, this.tableName, builder.toString());
        return this.createSQL;
    }

    public String query() {
        if (TextUtils.isEmpty(this.querySQL)) {
            return this.buildQuerySQL();
        } else {
            return this.querySQL;
        }
    }

    public String queryWhereAnd(String... wheres) {
        if (Util.isEmpty(wheres)) {
            return this.query();
        }
        String where = Util.array2Strings(Arrays.asList(wheres), " AND ");
        return String.format(" %s where %s", this.query(), where);
    }

    public String queryWhereOr(String... wheres) {
        if (Util.isEmpty(wheres)) {
            return this.query();
        }
        String where = Util.array2Strings(Arrays.asList(wheres), " OR ");
        return String.format(" %s where %s", this.query(), where);
    }

    public void addColumn(SQLiteDatabase database, String column, Format format) {
        if (this.columnExists(database, column)) {
            return;
        }
        try {
            //1:table name;2:column;3:column-type
            final String sqlAddColumn = "ALTER TABLE %1$s ADD COLUMN %2$s %3$s";
            String sql = String.format(sqlAddColumn, this.tableName, column, format.code);
            database.execSQL(sql);
        } catch (Exception e) {
            Logger.e("addColumn", e);
        }
    }

    private boolean columnExists(SQLiteDatabase database, String column) {
        Cursor cursor = null;
        try {
            String sql = "select * from sqlite_master where name = ? and sql like ?";
            cursor = database.rawQuery(sql, new String[]{this.tableName, "%" + column + "%"});
            return null != cursor && cursor.moveToFirst();
        } catch (Exception e) {
            Logger.e("columnExists", e);
            return false;
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private String buildQuerySQL() {
        String sql = "SELECT %s FROM %s";
        StringBuilder builder = new StringBuilder();
        if (primaryKey != null) {
            builder.append(primaryKey.column);
        }
        for (Param param : params) {
            if (param == null) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(param.column);
        }
        this.querySQL = String.format(sql, builder.toString(), this.tableName);
        return this.querySQL;
    }

    private class Param {
        String column;
        Format format;
        boolean canNull;

        Param(String column, Format format, boolean canNull) {
            this.column = column;
            this.format = format;
            this.canNull = canNull;
        }
    }

    public enum Format {
        INTEGER("INTEGER", "整型数据类型"),
        DOUBLE("DOUBLE", "双精度浮点型"),
        FLOAT("FLOAT", "浮点类型数据"),
        LONG("LONG", "长整型数据类型"),
        TEXT("TEXT", "字符串类型");
        public String code;
        public String desc;

        Format(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}

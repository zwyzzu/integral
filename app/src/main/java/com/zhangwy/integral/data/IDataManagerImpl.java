package com.zhangwy.integral.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.IntegralBindEntity;
import com.zhangwy.integral.entity.IntegralEntity;
import com.zhangwy.integral.entity.MemberEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yixia.lib.core.db.DatabaseHelper;
import yixia.lib.core.db.DatabaseHelper.DatabaseConfig;
import yixia.lib.core.exception.CodeException;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.SQLCreator;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/12/11 上午10:37.
 * Updated by zhangwy on 2018/12/11 上午10:37.
 * Description
 */
public class IDataManagerImpl extends IDataManager implements DatabaseHelper.UpgradeListener {

    private final int DATABASE_VERSION_1 = 1;
    private final int DATABASE_VERSION_2 = 2;
    private final int DATABASE_VERSION = DATABASE_VERSION_2;

    private final String TABLE_NAME_MEMBER = "member_data";
    private final String TABLE_NAME_INTEGRAL = "integral_data";
    private final String TABLE_NAME_ADDRESS = "address_data";
    private final String TABLE_NAME_INTEGRAL_BIND = "integral_bind_data";

    private final String SQL_WHERECLAUSE_BIND = " bind = ? ";
    private final String SQL_WHERECLAUSE_ID = " id = ? ";

    private final SQLCreator SQL_CREATOR_MEMBER = SQLCreator.newInstance(this.TABLE_NAME_MEMBER);
    private final SQLCreator SQL_CREATOR_INTEGRAL = SQLCreator.newInstance(this.TABLE_NAME_INTEGRAL);
    private final SQLCreator SQL_CREATOR_ADDRESS = SQLCreator.newInstance(this.TABLE_NAME_ADDRESS);
    private final SQLCreator SQL_CREATOR_INTEGRAL_BIND = SQLCreator.newInstance(this.TABLE_NAME_INTEGRAL_BIND);

    {//
        SQL_CREATOR_MEMBER.setPrimaryKey("id", SQLCreator.Format.TEXT)
                .put("name", SQLCreator.Format.TEXT, false)
                .put("icon", SQLCreator.Format.TEXT, false)
                .put("desc", SQLCreator.Format.TEXT, true)
                .put("phone", SQLCreator.Format.TEXT, false)
                .put("birthday", SQLCreator.Format.TEXT, false)
                .put("sex", SQLCreator.Format.INTEGER, true)
                .put("age", SQLCreator.Format.INTEGER, true)
                .put("marital", SQLCreator.Format.INTEGER, true)
                .put("sonCount", SQLCreator.Format.INTEGER, true)
                .put("daughterCount", SQLCreator.Format.INTEGER, true)
                .put("created", SQLCreator.Format.LONG, false)
                .put("modified", SQLCreator.Format.LONG, false)
                .build();

        SQL_CREATOR_INTEGRAL.setPrimaryKey("id", SQLCreator.Format.TEXT)
                .put("name", SQLCreator.Format.TEXT, false)
                .put("desc", SQLCreator.Format.TEXT, false)
                .put("score", SQLCreator.Format.FLOAT, false)
                .put("checkCoefficient", SQLCreator.Format.INTEGER, false)
                .build();

        SQL_CREATOR_ADDRESS.setPrimaryKey("id", SQLCreator.Format.TEXT)
                .put("tag", SQLCreator.Format.TEXT, true)
                .put("phone", SQLCreator.Format.TEXT, false)
                .put("consignee", SQLCreator.Format.TEXT, false)
                .put("province", SQLCreator.Format.TEXT, true)
                .put("city", SQLCreator.Format.TEXT, false)
                .put("area", SQLCreator.Format.TEXT, true)
                .put("town", SQLCreator.Format.TEXT, true)
                .put("address", SQLCreator.Format.TEXT, true)
                .put("desc", SQLCreator.Format.TEXT, true)
                .put("bind", SQLCreator.Format.TEXT, false)
                .put("position", SQLCreator.Format.INTEGER, true)
                .build();

        SQL_CREATOR_INTEGRAL_BIND.setPrimaryKey("id", SQLCreator.Format.TEXT)
                .put("bind", SQLCreator.Format.TEXT, false)
                .put("desc", SQLCreator.Format.TEXT, true)
                .put("scoreBind", SQLCreator.Format.TEXT, true)
                .put("score", SQLCreator.Format.FLOAT, false)
                .put("usedScore", SQLCreator.Format.FLOAT, true)
                .put("createDate", SQLCreator.Format.LONG, false)
                .put("usedDate", SQLCreator.Format.LONG, true)
                .build();
    }

    private Context mContext;
    private DatabaseHelper helper;

    @Override
    protected IDataManagerImpl init(Context context) {
        this.mContext = context == null ? null : context.getApplicationContext();
        this.initDatabase(this.mContext);
        return this;
    }

    @Override
    public Context getAppContext() {
        if (this.mContext != null) {
            return this.mContext.getApplicationContext();
        }
        return null;
    }

    private void initDatabase(Context context) {
        if (this.helper != null || context == null) {
            return;
        }
        String database_name = "com_integral_data";
        DatabaseConfig databaseConfig = DatabaseConfig.newInstance(database_name, DATABASE_VERSION, this);
        this.helper = DatabaseHelper.create(context, databaseConfig);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        if (this.openDatabase(database)) {
            database.execSQL(this.SQL_CREATOR_MEMBER.create());
            database.execSQL(this.SQL_CREATOR_INTEGRAL.create());
            database.execSQL(this.SQL_CREATOR_ADDRESS.create());
            database.execSQL(this.SQL_CREATOR_INTEGRAL_BIND.create());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (!this.openDatabase(database)) {
            return;
        }
        switch (oldVersion) {
            case DATABASE_VERSION_1:
                this.SQL_CREATOR_INTEGRAL.addColumn(database, "checkCoefficient", SQLCreator.Format.INTEGER);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    /**
     * 获取成员列表
     *
     * @return 成员列表
     */
    @Override
    public List<MemberEntity> getMembers() {
        List<MemberEntity> array = new ArrayList<>();
        if (this.emptyHelper()) {
            return array;
        }

        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            String query = SQL_CREATOR_MEMBER.query();
            Cursor cursor = database.rawQuery(query, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                MemberEntity member = this.cursor2Member(database, cursor);
                array.add(member);
            }
            cursor.close();
        } catch (Exception e) {
            Logger.d("getMember", e);
        } finally {
            this.endTransaction(database);
        }
        return array;
    }

    /**
     * 获取成员
     *
     * @param id 成员ID
     * @return 成员对象
     */
    @Override
    public MemberEntity getMember(String id) {
        if (this.emptyHelper() || TextUtils.isEmpty(id)) {
            return null;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            String query = SQL_CREATOR_MEMBER.queryWhereAnd(SQL_WHERECLAUSE_ID);
            Cursor cursor = database.rawQuery(query, new String[]{id});
            if (cursor == null) {
                return null;
            }
            if (cursor.moveToNext()) {
                return this.cursor2Member(database, cursor);
            }
            cursor.close();
        } catch (Exception e) {
            Logger.d("getMember", e);
        } finally {
            this.endTransaction(database);
        }
        return null;
    }

    private MemberEntity cursor2Member(SQLiteDatabase database, Cursor cursor) {
        int columnIndex = 0;
        MemberEntity member = new MemberEntity();
        member.setId(cursor.getString(columnIndex++));
        member.setName(cursor.getString(columnIndex++));
        member.setIcon(cursor.getString(columnIndex++));
        member.setDesc(cursor.getString(columnIndex++));
        member.setPhone(cursor.getString(columnIndex++));
        member.setBirthday(cursor.getString(columnIndex++));
        member.setSex(cursor.getInt(columnIndex++));
        member.setAge(cursor.getInt(columnIndex++));
        member.setMarital(cursor.getInt(columnIndex++));
        member.setSonCount(cursor.getInt(columnIndex++));
        member.setDaughterCount(cursor.getInt(columnIndex++));
        member.setCreated(cursor.getLong(columnIndex++));
        member.setModified(cursor.getLong(columnIndex++));
        Logger.d(String.format("the table's column count is %s", columnIndex + ""));
        member.setAddress(this.queryAddress(database, member.getId()));
        member.setIntegrals(this.queryMemberIntegral(database, member.getId()));
        return member;
    }

    private List<AddressEntity> queryAddress(SQLiteDatabase database, String bindId) {
        List<AddressEntity> array = new ArrayList<>();
        String query = SQL_CREATOR_ADDRESS.queryWhereAnd(SQL_WHERECLAUSE_BIND);
        Cursor cursor = database.rawQuery(query, new String[]{bindId});
        if (cursor == null) {
            return array;
        }
        while (cursor.moveToNext()) {
            AddressEntity address = this.cursor2Address(cursor);
            array.add(address);
        }
        cursor.close();
        return array;
    }

    private AddressEntity cursor2Address(Cursor cursor) {
        int columnIndex = 0;
        AddressEntity address = new AddressEntity();
        address.setId(cursor.getString(columnIndex++));
        address.setTag(cursor.getString(columnIndex++));
        address.setPhone(cursor.getString(columnIndex++));
        address.setConsignee(cursor.getString(columnIndex++));
        address.setProvince(cursor.getString(columnIndex++));
        address.setCity(cursor.getString(columnIndex++));
        address.setArea(cursor.getString(columnIndex++));
        address.setTown(cursor.getString(columnIndex++));
        address.setAddress(cursor.getString(columnIndex++));
        address.setDesc(cursor.getString(columnIndex++));
        address.setBind(cursor.getString(columnIndex++));
        address.setPosition(cursor.getInt(columnIndex++));
        Logger.d(String.format("the table's column count is %s", columnIndex + ""));
        return address;
    }

    private List<IntegralBindEntity> queryMemberIntegral(SQLiteDatabase database, String bindId) {
        List<IntegralBindEntity> array = new ArrayList<>();
        String query = SQL_CREATOR_INTEGRAL_BIND.queryWhereAnd(SQL_WHERECLAUSE_BIND);
        Cursor cursor = database.rawQuery(query, new String[]{bindId});
        if (cursor == null) {
            return array;
        }
        while (cursor.moveToNext()) {
            IntegralBindEntity address = this.cursor2MemberIntegral(cursor);
            array.add(address);
        }
        cursor.close();
        return array;
    }

    private IntegralBindEntity cursor2MemberIntegral(Cursor cursor) {
        int columnIndex = 0;
        IntegralBindEntity integral = new IntegralBindEntity();
        integral.setId(cursor.getString(columnIndex++));
        integral.setBind(cursor.getString(columnIndex++));
        integral.setDesc(cursor.getString(columnIndex++));
        integral.setScoreBind(cursor.getString(columnIndex++));
        integral.setScore(cursor.getFloat(columnIndex++));
        integral.setUsedScore(cursor.getFloat(columnIndex++));
        integral.setCreateDate(cursor.getLong(columnIndex++));
        integral.setUsedDate(cursor.getLong(columnIndex++));
        Logger.d(String.format("the table's column count is %s", columnIndex + ""));
        return integral;
    }

    /**
     * 添加成员
     *
     * @param member 成员对象
     */
    @Override
    public void addMember(MemberEntity member) {
        if (member == null || this.emptyHelper()) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (this.hasMember(database, member.getId())) {
                this.updateMember(member);
                return;
            }
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("id", member.getId());
            values.put("name", member.getName());
            values.put("icon", member.getIcon());
            values.put("desc", member.getDesc());
            values.put("phone", member.getPhone());
            values.put("birthday", member.getBirthday());
            values.put("sex", member.getSex());
            values.put("age", member.getAge());
            values.put("marital", member.getMarital());
            values.put("sonCount", member.getSonCount());
            values.put("daughterCount", member.getDaughterCount());
            values.put("created", member.getCreated());
            values.put("modified", System.currentTimeMillis());
            long raw = database.insertWithOnConflict(TABLE_NAME_MEMBER, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            if (raw < 0) {
                return;
            }
            this.updateMemberData(database, member);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.d("addMember", e);
        } finally {
            this.endTransaction(database);
        }
    }

    /**
     * 更新成员
     *
     * @param member 成员对象
     */
    @Override
    public void updateMember(MemberEntity member) {
        if (member == null || this.emptyHelper()) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (!this.hasMember(database, member.getId())) {
                this.addMember(member);
                return;
            }
            database.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("name", member.getName());
            values.put("icon", member.getIcon());
            values.put("desc", member.getDesc());
            values.put("phone", member.getPhone());
            values.put("birthday", member.getBirthday());
            values.put("sex", member.getSex());
            values.put("age", member.getAge());
            values.put("marital", member.getMarital());
            values.put("sonCount", member.getSonCount());
            values.put("daughterCount", member.getDaughterCount());
            values.put("modified", System.currentTimeMillis());
            long raw = database.update(TABLE_NAME_MEMBER, values, SQL_WHERECLAUSE_ID, new
                    String[]{member.getId()});
            if (raw < 0) {
                return;
            }
            this.updateMemberData(database, member);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.d("addMember", e);
        } finally {
            this.endTransaction(database);
        }
    }

    private void updateMemberData(SQLiteDatabase database, MemberEntity member) {
        if (database == null || member == null) {
            return;
        }
        String[] whereArgs = new String[]{member.getId()};
        database.delete(TABLE_NAME_ADDRESS, SQL_WHERECLAUSE_BIND, whereArgs);
        if (!Util.isEmpty(member.getAddress())) {
            for (AddressEntity address : member.getAddress()) {
                if (address == null) {
                    continue;
                }
                this.addAddress(false, address);
            }
        }
        database.delete(TABLE_NAME_INTEGRAL_BIND, SQL_WHERECLAUSE_BIND, whereArgs);
        if (!Util.isEmpty(member.getIntegrals())) {
            for (IntegralBindEntity integral : member.getIntegrals()) {
                if (integral == null) {
                    continue;
                }
                this.addMemberIntegral(false, integral);
            }
        }
    }

    /**
     * 删除成员
     *
     * @param id 成员ID
     */
    @Override
    public void dldMember(String id) {
        if (TextUtils.isEmpty(id) || this.emptyHelper()) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            if (this.hasMember(database, id)) {
                String[] whereArgs = new String[]{id};
                database.delete(TABLE_NAME_MEMBER, SQL_WHERECLAUSE_ID, whereArgs);
                database.delete(TABLE_NAME_ADDRESS, SQL_WHERECLAUSE_BIND, whereArgs);
                database.delete(TABLE_NAME_INTEGRAL_BIND, SQL_WHERECLAUSE_BIND, whereArgs);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.d("dldMember", e);
        } finally {
            this.endTransaction(database);
        }
    }

    @Override
    public void updateMessage(String mmId, String message) {
        if (TextUtils.isEmpty(mmId) || this.emptyHelper()) {
            return;
        }

        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            if (this.hasMember(database, mmId)) {
                String[] whereArgs = new String[]{mmId};
                ContentValues values = new ContentValues();
                values.put("desc", message);
                values.put("modified", System.currentTimeMillis());
                database.update(TABLE_NAME_MEMBER, values, SQL_WHERECLAUSE_ID, whereArgs);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.d("dldMember", e);
        } finally {
            this.endTransaction(database);
        }
    }

    /**
     * 添加成员地址
     *
     * @param address 地址
     */
    @Override
    public void addAddress(AddressEntity address) {
        if (this.emptyHelper() || address == null || TextUtils.isEmpty(address.getBind())) {
            return;
        }
        this.addAddress(true, address);
    }

    private boolean addAddress(boolean beginTransaction, AddressEntity address) {
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (beginTransaction) {
                database.beginTransaction();
            }
            if (!this.hasMember(database, address.getBind())) {
                return false;
            }
            ContentValues values = new ContentValues();
            values.put("id", address.getId());
            values.put("tag", address.getTag());
            values.put("phone", address.getPhone());
            values.put("consignee", address.getConsignee());
            values.put("province", address.getProvince());
            values.put("city", address.getCity());
            values.put("area", address.getArea());
            values.put("town", address.getTown());
            values.put("address", address.getAddress());
            values.put("desc", address.getDesc());
            values.put("bind", address.getBind());
            values.put("position", address.getPosition());
            long raw = database.insertWithOnConflict(TABLE_NAME_ADDRESS, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            if (raw >= 0) {
                this.updateModified(database, address.getBind());
                if (beginTransaction) {
                    database.setTransactionSuccessful();
                }
                return true;
            }
        } catch (Exception e) {
            Logger.d("addAddress", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
        return false;
    }

    /**
     * 更新地址
     *
     * @param address 地址
     */
    @Override
    public void updateAddress(AddressEntity address) {
        if (this.emptyHelper() || address == null || TextUtils.isEmpty(address.getBind())) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            if (!this.has(database, TABLE_NAME_ADDRESS, "id", address.getId())) {
                if (this.addAddress(false, address)) {
                    database.setTransactionSuccessful();
                }
                return;
            }
            ContentValues values = new ContentValues();
            values.put("tag", address.getTag());
            values.put("phone", address.getPhone());
            values.put("consignee", address.getConsignee());
            values.put("province", address.getProvince());
            values.put("city", address.getCity());
            values.put("area", address.getArea());
            values.put("town", address.getTown());
            values.put("address", address.getAddress());
            values.put("desc", address.getDesc());
            values.put("bind", address.getBind());
            values.put("position", address.getPosition());
            long raw = database.update(TABLE_NAME_ADDRESS, values, SQL_WHERECLAUSE_ID, new
                    String[]{address.getId()});
            if (raw >= 0) {
                this.updateModified(database, address.getBind());
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Logger.d("updateAddress", e);
        } finally {
            this.endTransaction(database);
        }
    }

    /**
     * 删除地址
     *
     * @param id 地址ID
     */
    @Override
    public void dldAddress(String id) {
        if (TextUtils.isEmpty(id) || this.emptyHelper()) {
            return;
        }
        try {
            SQLiteDatabase database = this.helper.open();
            database.delete(TABLE_NAME_ADDRESS, SQL_WHERECLAUSE_ID, new String[]{id});
        } catch (Exception e) {
            Logger.d("clearAddress", e);
        }
    }

    /**
     * 情况成员地址
     *
     * @param memberId 成员ID
     */
    @Override
    public void clearAddress(String memberId) {
        if (TextUtils.isEmpty(memberId) || this.emptyHelper()) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            if (this.hasMember(database, memberId)) {
                database.delete(TABLE_NAME_ADDRESS, SQL_WHERECLAUSE_BIND, new String[]{memberId});
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.d("clearAddress", e);
        } finally {
            this.endTransaction(database);
        }
    }

    @Override
    public List<AddressEntity> getAddresses(String memberId) {
        if (this.emptyHelper() || TextUtils.isEmpty(memberId)) {
            return new ArrayList<>();
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            return this.queryAddress(database, memberId);
        } catch (Exception e) {
            Logger.d("getMember", e);
        } finally {
            this.endTransaction(database);
        }
        return new ArrayList<>();
    }

    @Override
    public List<IntegralEntity> getIntegrals() {
        List<IntegralEntity> array = new ArrayList<>();
        if (this.emptyHelper()) {
            return array;
        }

        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            String query = SQL_CREATOR_INTEGRAL.query();
            Cursor cursor = database.rawQuery(query, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                IntegralEntity entity = this.cursor2Integral(cursor);
                array.add(entity);
            }
            cursor.close();
        } catch (Exception e) {
            Logger.d("getMember", e);
        } finally {
            this.endTransaction(database);
        }
        return array;
    }

    @Override
    public IntegralEntity getIntegral(String integralId) {
        if (this.emptyHelper() || TextUtils.isEmpty(integralId)) {
            return null;
        }
        IntegralEntity entity = null;
        try {
            SQLiteDatabase database = this.helper.open();
            String query = SQL_CREATOR_INTEGRAL.queryWhereAnd(this.SQL_WHERECLAUSE_ID);
            Cursor cursor = database.rawQuery(query, new String[]{integralId});
            if (cursor == null) {
                return null;
            }
            if (cursor.moveToNext()) {
                entity = this.cursor2Integral(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            Logger.d("getIntegral", e);
        }
        return entity;
    }

    private IntegralEntity cursor2Integral(Cursor cursor) {
        int columnIndex = 0;
        IntegralEntity entity = new IntegralEntity();
        entity.setId(cursor.getString(columnIndex++));
        entity.setName(cursor.getString(columnIndex++));
        entity.setDesc(cursor.getString(columnIndex++));
        entity.setScore(cursor.getFloat(columnIndex++));
        entity.setCheckCoefficient(cursor.getInt(columnIndex++) == 1);
        Logger.d(String.format("the table's column count is %s", columnIndex + ""));
        return entity;
    }

    /**
     * 添加积分
     *
     * @param integral 积分
     */
    @Override
    public void addIntegral(IntegralEntity integral) {
        if (this.emptyHelper() || integral == null) {
            return;
        }
        this.addIntegral(true, integral);
    }

    private boolean addIntegral(boolean beginTransaction, IntegralEntity integral) {
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (beginTransaction) {
                database.beginTransaction();
            }
            ContentValues values = new ContentValues();
            values.put("id", integral.getId());
            values.put("name", integral.getName());
            values.put("desc", integral.getDesc());
            values.put("score", integral.getScore());
            values.put("checkCoefficient", integral.isCheckCoefficient() ? 1 : 0);
            long raw = database.insertWithOnConflict(TABLE_NAME_INTEGRAL, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            if (raw >= 0 && beginTransaction) {
                database.setTransactionSuccessful();
            }
            return raw >= 0;
        } catch (Exception e) {
            Logger.d("addIntegral", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
        return false;
    }

    /**
     * 更新积分
     *
     * @param integral 积分
     */
    @Override
    public void updateIntegral(IntegralEntity integral) {
        if (this.emptyHelper() || integral == null) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            if (!this.has(database, TABLE_NAME_INTEGRAL, "id", integral.getId())) {
                if (this.addIntegral(false, integral)) {
                    database.setTransactionSuccessful();
                }
                return;
            }
            ContentValues values = new ContentValues();
            values.put("name", integral.getName());
            values.put("desc", integral.getDesc());
            values.put("score", integral.getScore());
            values.put("checkCoefficient", integral.isCheckCoefficient() ? 1 : 0);
            long raw = database.update(TABLE_NAME_INTEGRAL, values, SQL_WHERECLAUSE_ID, new String[]{integral.getId()});
            if (raw >= 0) {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Logger.d("updateIntegral", e);
        } finally {
            this.endTransaction(database);
        }
    }

    /**
     * 添加积分
     *
     * @param id 积分ID
     */
    @Override
    public void dldIntegral(String id) {
        if (TextUtils.isEmpty(id) || this.emptyHelper()) {
            return;
        }
        try {
            SQLiteDatabase database = this.helper.open();
            database.delete(TABLE_NAME_INTEGRAL, SQL_WHERECLAUSE_ID, new String[]{id});
        } catch (Exception e) {
            Logger.d("dldIntegral", e);
        }
    }

    /**
     * 添加用户积分
     *
     * @param integral 用户积分
     */
    @Override
    public void addMemberIntegral(IntegralBindEntity integral) {
        if (integral == null || this.emptyHelper() || TextUtils.isEmpty(integral.getBind())) {
            return;
        }
        this.addMemberIntegral(true, integral);
    }

    private void addMemberIntegral(boolean beginTransaction, IntegralBindEntity integral) {
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (beginTransaction) {
                database.beginTransaction();
            }
            if (!this.hasMember(database, integral.getBind())) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put("id", integral.getId());
            values.put("bind", integral.getBind());
            values.put("desc", integral.getDesc());
            values.put("scoreBind", integral.getScoreBind());
            values.put("score", integral.getScore());
            values.put("usedScore", integral.getUsedScore());
            values.put("createDate", integral.getCreateDate());
            values.put("usedDate", integral.getUsedDate());
            long raw = database.insertWithOnConflict(TABLE_NAME_INTEGRAL_BIND, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (raw >= 0) {
                this.updateModified(database, integral.getBind());
                if (beginTransaction) {
                    database.setTransactionSuccessful();
                }
            }
        } catch (Exception e) {
            Logger.d("addMemberIntegral", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
    }

    /**
     * 修改用户积分
     *
     * @param integral 用户积分
     */
    @Override
    public void updateMemberIntegral(IntegralBindEntity integral) {
        if (integral == null || this.emptyHelper() || TextUtils.isEmpty(integral.getBind())) {
            return;
        }
        this.updateMemberIntegral(true, true, integral);
    }

    @Override
    public List<IntegralBindEntity> getMemberIntegrals(String memberId) {
        if (this.emptyHelper() || TextUtils.isEmpty(memberId)) {
            return new ArrayList<>();
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            return this.queryMemberIntegral(database, memberId);
        } catch (Exception e) {
            Logger.d("getMember", e);
        } finally {
            this.endTransaction(database);
        }
        return new ArrayList<>();
    }

    private void updateMemberIntegral(boolean beginTransaction, boolean modifiedMember, IntegralBindEntity integral) {
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (beginTransaction) {
                database.beginTransaction();
            }
            if (!this.hasMember(database, integral.getBind())) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put("bind", integral.getBind());
            values.put("desc", integral.getDesc());
            values.put("scoreBind", integral.getScoreBind());
            values.put("score", integral.getScore());
            values.put("usedScore", integral.getUsedScore());
            values.put("usedDate", integral.getUsedDate());
            long raw = database.update(TABLE_NAME_INTEGRAL_BIND, values, SQL_WHERECLAUSE_ID, new String[]{integral.getId()});
            if (raw < 0) {
                return;
            }
            if (modifiedMember) {
                this.updateModified(database, integral.getBind());
            }
            if (beginTransaction) {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Logger.d("addMemberIntegral", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
    }

    /**
     * 使用积分
     *
     * @param memberId 成员ID
     * @param useScore 使用积分数
     */
    @Override
    public void useIntegral(String memberId, float useScore) throws CodeException {
        if (TextUtils.isEmpty(memberId) || useScore <= 0) {
            throw new CodeException(IDataCode.PARAMETER_UNUSABLE);
        }
        if (this.emptyHelper()) {
            throw new CodeException(IDataCode.DATABASE_UNINITIALIZED);
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            if (!this.hasMember(database, memberId)) {
                throw new CodeException(IDataCode.MEMBER_NOFOUND);
            }
            List<IntegralBindEntity> array = this.queryMemberIntegral(database, memberId);
            if (Util.isEmpty(array)) {
                throw new CodeException(IDataCode.INTEGRAL_INSUFFICIENT);
            }
            Collections.sort(array);
            for (int i = array.size() - 1; i >= 0; i--) {
                if (useScore <= 0) {
                    break;
                }
                IntegralBindEntity entity = array.get(i);
                if (entity != null && entity.useable()) {
                    float useable = entity.useableScore();
                    if (useable <= useScore) {
                        entity.setUsedScore(entity.getScore());
                        entity.setUsedDate(System.currentTimeMillis());
                    } else {
                        entity.setUsedScore(entity.getUsedScore() + useScore);
                        entity.setUsedDate(System.currentTimeMillis());
                    }
                    this.updateMemberIntegral(false, false, entity);
                    useScore -= useable;
                }
            }
            if (useScore > 0) {
                throw new CodeException(IDataCode.INTEGRAL_INSUFFICIENT);
            } else {
                this.updateModified(database, memberId);
                database.setTransactionSuccessful();
            }
        } catch (CodeException e) {
            throw e;
        } catch (Exception e) {
            Logger.d("useIntegral", e);
        } finally {
            this.endTransaction(database);
        }
    }

    private void updateModified(SQLiteDatabase database, String memberId) {
        if (database == null || TextUtils.isEmpty(memberId)) {
            return;
        }
        try {
            ContentValues values = new ContentValues();
            values.put("", System.currentTimeMillis());
            database.update(TABLE_NAME_MEMBER, values, SQL_WHERECLAUSE_ID, new String[]{memberId});
        } catch (Exception e) {
            Logger.d("updateModified", e);
        }
    }

    private boolean openDatabase(SQLiteDatabase database) {
        if (!database.isOpen()) {
            if (this.emptyHelper()) {
                return false;
            }
            this.helper.onOpen(database);
        }
        return true;
    }

    private boolean emptyHelper() {
        if (this.helper == null) {
            this.initDatabase(this.mContext);
        }
        return this.helper == null;
    }

    private boolean hasMember(SQLiteDatabase database, String memberId) {
        return this.has(database, TABLE_NAME_MEMBER, "id", memberId);
    }

    private boolean has(SQLiteDatabase database, String tabName, String column, Object value) {
        Cursor cursor = null;
        String query = String.format("SELECT %s FROM %s where %s = ? ", column, tabName, column);
        try {
            cursor = database.rawQuery(query, new String[]{String.valueOf(value)});
            return cursor != null && cursor.moveToNext();
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void endTransaction(SQLiteDatabase database) {
        if (database == null || !database.inTransaction()) {
            return;
        }
        database.endTransaction();
    }
}

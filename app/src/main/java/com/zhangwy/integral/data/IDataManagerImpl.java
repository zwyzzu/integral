package com.zhangwy.integral.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.CouponsBindEntity;
import com.zhangwy.integral.entity.CouponsEntity;
import com.zhangwy.integral.entity.CouponsExpiryEntity;
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
    private final int DATABASE_VERSION_3 = 3;
    private final int DATABASE_VERSION = DATABASE_VERSION_3;

    private final String TABLE_NAME_MEMBER = "member_data";
    private final String TABLE_NAME_INTEGRAL = "integral_data";
    private final String TABLE_NAME_ADDRESS = "address_data";
    private final String TABLE_NAME_INTEGRAL_BIND = "integral_bind_data";
    private final String TABLE_NAME_COUPONS = "coupons_data";
    private final String TABLE_NAME_COUPONS_BIND = "coupons_bind_data";
    private final String TABLE_NAME_EXPIRY = "expiry_data";

    private final String SQL_WHERECLAUSE_BIND = " bind = ? ";
    private final String SQL_WHERECLAUSE_ID = " id = ? ";

    private final SQLCreator SQL_CREATOR_MEMBER = SQLCreator.newInstance(this.TABLE_NAME_MEMBER);
    private final SQLCreator SQL_CREATOR_INTEGRAL = SQLCreator.newInstance(this.TABLE_NAME_INTEGRAL);
    private final SQLCreator SQL_CREATOR_ADDRESS = SQLCreator.newInstance(this.TABLE_NAME_ADDRESS);
    private final SQLCreator SQL_CREATOR_INTEGRAL_BIND = SQLCreator.newInstance(this.TABLE_NAME_INTEGRAL_BIND);
    private final SQLCreator SQL_CREATOR_COUPONS = SQLCreator.newInstance(this.TABLE_NAME_COUPONS);
    private final SQLCreator SQL_CREATOR_COUPONS_BIND = SQLCreator.newInstance(this.TABLE_NAME_COUPONS_BIND);
    private final SQLCreator SQL_CREATOR_EXPIRY = SQLCreator.newInstance(this.TABLE_NAME_EXPIRY);

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

        SQL_CREATOR_COUPONS.setPrimaryKey("id", SQLCreator.Format.TEXT)
                .put("amount", SQLCreator.Format.FLOAT, false)
                .put("name", SQLCreator.Format.TEXT, false)
                .put("desc", SQLCreator.Format.TEXT, true)
                .put("checkCoefficient", SQLCreator.Format.INTEGER, false)
                .build();

        SQL_CREATOR_COUPONS_BIND.setPrimaryKey("id", SQLCreator.Format.TEXT)
                .put("amount", SQLCreator.Format.FLOAT, false)
                .put("createDate", SQLCreator.Format.LONG, false)
                .put("expiryDate", SQLCreator.Format.LONG, false)
                .put("usedDate", SQLCreator.Format.LONG, false)
                .put("expiryCode", SQLCreator.Format.TEXT, false)
                .put("name", SQLCreator.Format.TEXT, false)
                .put("desc", SQLCreator.Format.TEXT, true)
                .put("bind", SQLCreator.Format.TEXT, false)
                .put("couponsBind", SQLCreator.Format.TEXT, false)
                .put("expiryBind", SQLCreator.Format.TEXT, false)
                .put("bindName", SQLCreator.Format.TEXT, false)
                .put("tag", SQLCreator.Format.TEXT, true)
                .build();

        SQL_CREATOR_EXPIRY.setPrimaryKey("id", SQLCreator.Format.TEXT)
                .put("count", SQLCreator.Format.INTEGER, false)
                .put("expiryCode", SQLCreator.Format.TEXT, false)
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
            database.execSQL(this.SQL_CREATOR_EXPIRY.create());
            database.execSQL(this.SQL_CREATOR_COUPONS.create());
            database.execSQL(this.SQL_CREATOR_COUPONS_BIND.create());
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
            case DATABASE_VERSION_2:
                database.execSQL(this.SQL_CREATOR_EXPIRY.create());
                database.execSQL(this.SQL_CREATOR_COUPONS.create());
                database.execSQL(this.SQL_CREATOR_COUPONS_BIND.create());
            case DATABASE_VERSION_3:
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
        member.setDBIcon(cursor.getString(columnIndex++));
        member.setDesc(cursor.getString(columnIndex++));
        member.setDBPhone(cursor.getString(columnIndex++));
        member.setDBBirthday(cursor.getString(columnIndex++));
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
            values.put("icon", member.getDBIcon());
            values.put("desc", member.getDesc());
            values.put("phone", member.getDBPhone());
            values.put("birthday", member.getDBBirthday());
            values.put("sex", member.getSex());
            values.put("age", member.getAge());
            values.put("marital", member.getMarital());
            values.put("sonCount", member.getSonCount());
            values.put("daughterCount", member.getDaughterCount());
            values.put("created", member.getCreated());
            values.put("modified", System.currentTimeMillis());
            long raw = database.insertWithOnConflict(TABLE_NAME_MEMBER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
            values.put("icon", member.getDBIcon());
            values.put("desc", member.getDesc());
            values.put("phone", member.getDBPhone());
            values.put("birthday", member.getDBBirthday());
            values.put("sex", member.getSex());
            values.put("age", member.getAge());
            values.put("marital", member.getMarital());
            values.put("sonCount", member.getSonCount());
            values.put("daughterCount", member.getDaughterCount());
            values.put("modified", System.currentTimeMillis());
            long raw = database.update(TABLE_NAME_MEMBER, values, SQL_WHERECLAUSE_ID, new String[]{member.getId()});
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
            long raw = database.insertWithOnConflict(TABLE_NAME_ADDRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
            long raw = database.update(TABLE_NAME_ADDRESS, values, SQL_WHERECLAUSE_ID, new String[]{address.getId()});
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
            long raw = database.insertWithOnConflict(TABLE_NAME_INTEGRAL, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
            Logger.d("updateMemberIntegral", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
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
            values.put("modified", System.currentTimeMillis());
            database.update(TABLE_NAME_MEMBER, values, SQL_WHERECLAUSE_ID, new String[]{memberId});
        } catch (Exception e) {
            Logger.d("updateModified", e);
        }
    }

    @Override
    public boolean checkExpiry(CouponsExpiryEntity expiry) {
        if (!this.emptyHelper() || expiry == null || TextUtils.isEmpty(expiry.getExpiryCode())) {
            return false;
        }
        try {
            SQLiteDatabase database = this.helper.open();
            return this.hasExpiry(database, expiry);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasExpiry(SQLiteDatabase database, CouponsExpiryEntity expiry) {
        Cursor cursor = null;
        String query = SQL_CREATOR_EXPIRY.queryWhereAnd("count = ? ", "expiryCode = ? ");
        try {
            cursor = database.rawQuery(query, new String[]{String.valueOf(expiry.getCount()), expiry.getExpiryCode()});
            return cursor != null && cursor.moveToNext();
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * @param expiry 有效期对象
     * @throws CodeException "
     */
    @Override
    public void addExpiry(CouponsExpiryEntity expiry) throws CodeException {
        if (expiry == null || TextUtils.isEmpty(expiry.getExpiryCode())) {
            return;
        }
        if (this.emptyHelper()) {
            throw new CodeException(IDataCode.DATABASE_UNINITIALIZED);
        }
        SQLiteDatabase database;
        try {
            database = this.helper.open();
            if (this.hasExpiry(database, expiry)) {
                throw new CodeException(IDataCode.DATABASE_HAS_EXPIRY);
            }
            ContentValues values = new ContentValues();
            values.put("id", expiry.getId());
            values.put("count", expiry.getCount());
            values.put("expiryCode", expiry.getExpiryCode());
            database.insertWithOnConflict(TABLE_NAME_EXPIRY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (CodeException e) {
            throw e;
        } catch (Exception e) {
            Logger.d("addExpiry", e);
        }
    }

    @Override
    public void dldExpiry(String expiryId) {
        if (this.emptyHelper() || TextUtils.isEmpty(expiryId)) {
            return;
        }
        try {
            SQLiteDatabase database = this.helper.open();
            database.delete(TABLE_NAME_EXPIRY, SQL_WHERECLAUSE_ID, new String[]{expiryId});
        } catch (Exception e) {
            Logger.d("clearAddress", e);
        }
    }

    @Override
    public List<CouponsExpiryEntity> getExpiries() {
        List<CouponsExpiryEntity> array = new ArrayList<>();
        if (this.emptyHelper()) {
            return array;
        }

        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            String query = SQL_CREATOR_EXPIRY.query();
            Cursor cursor = database.rawQuery(query, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                CouponsExpiryEntity entity = this.cursor2Expiry(cursor);
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

    private CouponsExpiryEntity cursor2Expiry(Cursor cursor) {
        int columnIndex = 0;
        CouponsExpiryEntity entity = new CouponsExpiryEntity();
        entity.setId(cursor.getString(columnIndex++));
        entity.setCount(cursor.getInt(columnIndex++));
        entity.setExpiryCode(cursor.getString(columnIndex++));
        Logger.d(String.format("the table's column count is %s", columnIndex + ""));
        return entity;
    }

    @Override
    public void addCoupons(CouponsEntity coupons) {
        if (this.emptyHelper() || coupons == null) {
            return;
        }

        this.addCoupons(true, coupons);
    }

    private boolean addCoupons(boolean beginTransaction, CouponsEntity coupons) {
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (beginTransaction) {
                database.beginTransaction();
            }
            ContentValues values = new ContentValues();
            values.put("id", coupons.getId());
            values.put("amount", coupons.getAmount());
            values.put("name", coupons.getName());
            values.put("desc", coupons.getDesc());
            values.put("checkCoefficient", coupons.isCheckCoefficient() ? 1 : 0);
            long raw = database.insertWithOnConflict(TABLE_NAME_COUPONS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (raw >= 0 && beginTransaction) {
                database.setTransactionSuccessful();
            }
            return raw >= 0;
        } catch (Exception e) {
            Logger.d("addCoupons", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
        return false;
    }

    @Override
    public void updateCoupons(CouponsEntity coupons) {
        if (this.emptyHelper() || coupons == null) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            if (!this.has(database, TABLE_NAME_COUPONS, "id", coupons.getId())) {
                if (this.addCoupons(false, coupons)) {
                    database.setTransactionSuccessful();
                }
                return;
            }
            ContentValues values = new ContentValues();
            values.put("amount", coupons.getAmount());
            values.put("name", coupons.getName());
            values.put("desc", coupons.getDesc());
            values.put("checkCoefficient", coupons.isCheckCoefficient() ? 1 : 0);
            long raw = database.update(TABLE_NAME_COUPONS, values, SQL_WHERECLAUSE_ID, new String[]{coupons.getId()});
            if (raw >= 0) {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Logger.d("updateCoupons", e);
        } finally {
            this.endTransaction(database);
        }
    }

    @Override
    public void dldCoupons(String couponsId) {
        if (TextUtils.isEmpty(couponsId) || this.emptyHelper()) {
            return;
        }
        try {
            SQLiteDatabase database = this.helper.open();
            database.delete(TABLE_NAME_COUPONS, SQL_WHERECLAUSE_ID, new String[]{couponsId});
        } catch (Exception e) {
            Logger.d("dldIntegral", e);
        }
    }

    @Override
    public List<CouponsEntity> getCoupons() {
        List<CouponsEntity> array = new ArrayList<>();
        if (this.emptyHelper()) {
            return array;
        }

        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            String query = SQL_CREATOR_COUPONS.query();
            Cursor cursor = database.rawQuery(query, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                CouponsEntity entity = this.cursor2Coupons(cursor);
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

    private CouponsEntity cursor2Coupons(Cursor cursor) {
        int columnIndex = 0;
        CouponsEntity entity = new CouponsEntity();
        entity.setId(cursor.getString(columnIndex++));
        entity.setAmount(cursor.getFloat(columnIndex++));
        entity.setName(cursor.getString(columnIndex++));
        entity.setDesc(cursor.getString(columnIndex++));
        entity.setCheckCoefficient(cursor.getInt(columnIndex++) == 1);
        Logger.d(String.format("the table's column count is %s", columnIndex + ""));
        return entity;
    }

    @Override
    public void addMemberCoupons(CouponsBindEntity coupons) {
        if (coupons == null || this.emptyHelper() || TextUtils.isEmpty(coupons.getBind())) {
            return;
        }
        this.addMemberCoupons(true, coupons);
    }

    private void addMemberCoupons(boolean beginTransaction, CouponsBindEntity coupons) {
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (beginTransaction) {
                database.beginTransaction();
            }
            if (!this.hasMember(database, coupons.getBind())) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put("id", coupons.getId());
            values.put("amount", coupons.getAmount());
            values.put("createDate", coupons.getCreateDate());
            values.put("expiryDate", coupons.getExpiryDate());
            values.put("usedDate", coupons.getUsedDate());
            values.put("expiryCode", coupons.getExpiryCode());
            values.put("name", coupons.getName());
            values.put("desc", coupons.getDesc());
            values.put("bind", coupons.getBind());
            values.put("couponsBind", coupons.getCouponsBind());
            values.put("expiryBind", coupons.getExpiryBind());
            values.put("bindName", coupons.getBindName());
            values.put("tag", coupons.getTag());
            long raw = database.insertWithOnConflict(TABLE_NAME_COUPONS_BIND, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (raw >= 0) {
                this.updateModified(database, coupons.getBind());
                if (beginTransaction) {
                    database.setTransactionSuccessful();
                }
            }
        } catch (Exception e) {
            Logger.d("addMemberCoupons", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
    }

    @Override
    public void updateMemberCoupons(CouponsBindEntity coupons) {
        if (coupons == null || this.emptyHelper() || TextUtils.isEmpty(coupons.getBind())) {
            return;
        }
        this.updateMemberCoupons(true, coupons);
    }

    private void updateMemberCoupons(boolean beginTransaction, CouponsBindEntity coupons) {
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            if (beginTransaction) {
                database.beginTransaction();
            }
            if (!this.hasMember(database, coupons.getBind())) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put("amount", coupons.getAmount());
            values.put("createDate", coupons.getCreateDate());
            values.put("expiryDate", coupons.getExpiryDate());
            values.put("usedDate", coupons.getUsedDate());
            values.put("expiryCode", coupons.getExpiryCode());
            values.put("name", coupons.getName());
            values.put("desc", coupons.getDesc());
            values.put("bind", coupons.getBind());
            values.put("couponsBind", coupons.getCouponsBind());
            values.put("expiryBind", coupons.getExpiryBind());
            values.put("bindName", coupons.getBindName());
            values.put("tag", coupons.getTag());
            long raw = database.update(TABLE_NAME_COUPONS_BIND, values, SQL_WHERECLAUSE_ID, new String[]{coupons.getId()});
            if (raw < 0) {
                return;
            }
            this.updateModified(database, coupons.getBind());
            if (beginTransaction) {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Logger.d("updateMemberCoupons", e);
        } finally {
            if (beginTransaction) {
                this.endTransaction(database);
            }
        }
    }

    @Override
    public CouponsBindEntity getMemberCoupons(String memberId, String couponsId) {
        if (this.emptyHelper() || TextUtils.isEmpty(memberId) || TextUtils.isEmpty(couponsId)) {
            return null;
        }
        try {
            return this.queryMemberCoupons(this.helper.open(), memberId, couponsId);
        } catch (Exception e) {
            return null;
        }
    }

    private CouponsBindEntity queryMemberCoupons(SQLiteDatabase database, String memberId, String couponsId) {
        String query = SQL_CREATOR_COUPONS_BIND.queryWhereAnd(SQL_WHERECLAUSE_ID, SQL_WHERECLAUSE_BIND);
        Cursor cursor = database.rawQuery(query, new String[]{couponsId, memberId});
        if (cursor == null || !cursor.moveToNext()) {
            return null;
        }
        CouponsBindEntity coupons = this.cursor2MemberCoupons(cursor);
        cursor.close();
        return coupons;
    }

    private CouponsBindEntity cursor2MemberCoupons(Cursor cursor) {
        int columnIndex = 0;
        CouponsBindEntity coupons = new CouponsBindEntity();
        coupons.setId(cursor.getString(columnIndex++));
        coupons.setAmount(cursor.getFloat(columnIndex++));
        coupons.setCreateDate(cursor.getLong(columnIndex++));
        coupons.setExpiryDate(cursor.getLong(columnIndex++));
        coupons.setUsedDate(cursor.getLong(columnIndex++));
        coupons.setExpiryCode(cursor.getString(columnIndex++));
        coupons.setName(cursor.getString(columnIndex++));
        coupons.setDesc(cursor.getString(columnIndex++));
        coupons.setBind(cursor.getString(columnIndex++));
        coupons.setCouponsBind(cursor.getString(columnIndex++));
        coupons.setExpiryBind(cursor.getString(columnIndex++));
        coupons.setBindName(cursor.getString(columnIndex++));
        coupons.setTag(cursor.getString(columnIndex++));
        Logger.d(String.format("the table's column count is %s", columnIndex + ""));
        return coupons;
    }

    @Override
    public void useCoupons(String memberId, String couponsId) throws CodeException {
        if (TextUtils.isEmpty(couponsId) || TextUtils.isEmpty(memberId)) {
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
            CouponsBindEntity coupons = this.queryMemberCoupons(database, memberId, couponsId);
            if (coupons == null) {
                throw new CodeException(IDataCode.COUPONS_NOFOUND);
            }

            if (coupons.useable()) {
                coupons.setUsedDate(System.currentTimeMillis());
                this.updateMemberCoupons(false, coupons);
                database.setTransactionSuccessful();
            } else {
                throw new CodeException(IDataCode.COUPONS_UNAVAILABLE);
            }
        } catch (CodeException e) {
            throw e;
        } catch (Exception e) {
            Logger.d("useIntegral", e);
        } finally {
            this.endTransaction(database);
        }
    }

    @Override
    public List<CouponsBindEntity> getMemberCoupons(String memberId) {
        if (this.emptyHelper()) {
            return new ArrayList<>();
        }
        SQLiteDatabase database = null;
        try {
            database = this.helper.open();
            database.beginTransaction();
            return this.queryMemberCoupons(database, memberId);
        } catch (Exception e) {
            Logger.d("getMemberCoupons", e);
        } finally {
            this.endTransaction(database);
        }
        return new ArrayList<>();
    }

    private List<CouponsBindEntity> queryMemberCoupons(SQLiteDatabase database, String bindId) {
        List<CouponsBindEntity> array = new ArrayList<>();
        Cursor cursor;
        if (TextUtils.isEmpty(bindId)) {
            String query = SQL_CREATOR_COUPONS_BIND.queryWhereAnd();
            cursor = database.rawQuery(query, null);
        } else {
            String query = SQL_CREATOR_COUPONS_BIND.queryWhereAnd(SQL_WHERECLAUSE_BIND);
            cursor = database.rawQuery(query, new String[]{bindId});
        }
        if (cursor == null) {
            return array;
        }
        while (cursor.moveToNext()) {
            CouponsBindEntity address = this.cursor2MemberCoupons(cursor);
            array.add(address);
        }
        cursor.close();
        return array;
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

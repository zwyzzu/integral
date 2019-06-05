package com.zhangwy.integral.data;

import android.content.Context;

import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.CouponsBindEntity;
import com.zhangwy.integral.entity.CouponsEntity;
import com.zhangwy.integral.entity.CouponsExpiryEntity;
import com.zhangwy.integral.entity.IntegralBindEntity;
import com.zhangwy.integral.entity.IntegralEntity;
import com.zhangwy.integral.entity.MemberEntity;

import java.util.List;

import yixia.lib.core.exception.CodeException;

/**
 * Created by zhangwy on 2018/12/11 上午10:37.
 * Updated by zhangwy on 2019/06/05 上午21:58.
 * Description
 */
@SuppressWarnings("unused")
public abstract class IDataManager {

    public static IDataManager initialize(Context context) {
        return getInstance().init(context);
    }

    private static IDataManager instance;

    public static IDataManager getInstance() {
        if (instance == null) {
            synchronized (IDataManager.class) {
                if (instance == null) {
                    instance = new IDataManagerImpl();
                }
            }
        }
        return instance;
    }

    protected abstract IDataManager init(Context context);

    public abstract Context getAppContext();

    /**
     * 获取成员列表
     *
     * @return 成员列表
     */
    public abstract List<MemberEntity> getMembers();

    /**
     * 获取成员
     *
     * @param id 成员ID
     * @return 成员对象
     */
    public abstract MemberEntity getMember(String id);

    /**
     * 添加成员
     *
     * @param member 成员对象
     */
    public abstract void addMember(MemberEntity member);

    /**
     * 更新成员
     *
     * @param member 成员对象
     */
    public abstract void updateMember(MemberEntity member);

    /**
     * 删除成员
     *
     * @param id 成员ID
     */
    public abstract void dldMember(String id);

    /**
     * 修改成员描述信息
     *
     * @param mmId    成员ID
     * @param message 成员描述信息
     */
    public abstract void updateMessage(String mmId, String message);

    /**
     * 添加成员地址
     *
     * @param address 地址
     */
    public abstract void addAddress(AddressEntity address);

    /**
     * 更新地址
     *
     * @param address 地址
     */
    public abstract void updateAddress(AddressEntity address);

    /**
     * 删除地址
     *
     * @param id 地址ID
     */
    public abstract void dldAddress(String id);

    /**
     * 情况成员地址
     *
     * @param memberId 成员ID
     */
    public abstract void clearAddress(String memberId);

    /**
     * 获取用户地址列表
     *
     * @param memberId 成员ID
     * @return 成员地址列表
     */
    public abstract List<AddressEntity> getAddresses(String memberId);

    /**
     * 获取积分项
     *
     * @return 积分项列表
     */
    public abstract List<IntegralEntity> getIntegrals();

    /**
     * 获取积分项
     *
     * @param integralId 积分项ID
     * @return 积分项
     */
    public abstract IntegralEntity getIntegral(String integralId);

    /**
     * 添加积分项
     *
     * @param integral 积分
     */
    public abstract void addIntegral(IntegralEntity integral);

    /**
     * 更新积分项
     *
     * @param integral 积分
     */
    public abstract void updateIntegral(IntegralEntity integral);

    /**
     * 添加积分项
     *
     * @param id 积分ID
     */
    public abstract void dldIntegral(String id);

    /**
     * 添加用户积分
     *
     * @param integralBind 用户积分
     */
    public abstract void addMemberIntegral(IntegralBindEntity integralBind);

    /**
     * 修改用户积分
     *
     * @param integralBind 用户积分
     */
    public abstract void updateMemberIntegral(IntegralBindEntity integralBind);

    /**
     * 获取用户积分列表
     *
     * @param memberId 用户ID
     * @return 返回用户积分列表
     */
    public abstract List<IntegralBindEntity> getMemberIntegrals(String memberId);

    /**
     * 使用积分
     *
     * @param memberId 成员ID
     * @param useScore 使用积分数
     */
    public abstract void useIntegral(String memberId, float useScore) throws CodeException;

    /**
     * 检测有效期是否存在
     *
     * @param expiry 有效期
     * @return TRUE 存在否则不存在
     */
    public abstract boolean checkExpiry(CouponsExpiryEntity expiry);

    /**
     * 添加有效期配置
     *
     * @param expiry 有效期对象
     * @throws CodeException 抛异常
     */
    public abstract void addExpiry(CouponsExpiryEntity expiry) throws CodeException;

    /**
     * 删除有效期配置
     *
     * @param expiryId 有效期ID
     */
    public abstract void dldExpiry(String expiryId);

    /**
     * 获取所有的有效期列表
     *
     * @return 有效期项列表
     */
    public abstract List<CouponsExpiryEntity> getExpiries();

    /**
     * 添加优惠券项
     *
     * @param coupons 优惠券实体类
     */
    public abstract void addCoupons(CouponsEntity coupons);

    /**
     * 修改优惠券项
     *
     * @param coupons 优惠券实体类
     */
    public abstract void updateCoupons(CouponsEntity coupons);

    /**
     * 删除优惠券项
     *
     * @param couponsId 优惠券ID
     */
    public abstract void dldCoupons(String couponsId);

    /**
     * 添加成员优惠券
     *
     * @param coupons 优惠券对象
     */
    public abstract void addMemberCoupons(CouponsBindEntity coupons);

    /**
     * 修改成员优惠券
     *
     * @param coupons 优惠券对象
     */
    public abstract void updateMemberCoupons(CouponsBindEntity coupons);

    /**
     * 获取成员优惠券
     *
     * @param memberId  成员ID
     * @param couponsId 优惠券ID
     * @return 优惠券对象
     */
    public abstract CouponsBindEntity getMemberCoupons(String memberId, String couponsId);

    /**
     * 使用优惠券
     *
     * @param memberId  成员ID
     * @param couponsId 优惠券ID
     * @throws CodeException 使用优惠券期间出现异常
     */
    public abstract void useCoupons(String memberId, String couponsId) throws CodeException;

    /**
     * 获取成员优惠券列表
     *
     * @return 成员优惠券列表
     */
    public abstract List<CouponsBindEntity> getMemberCoupons(String memberId);
}

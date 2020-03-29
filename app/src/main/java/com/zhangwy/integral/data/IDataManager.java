package com.zhangwy.integral.data;

import android.content.Context;

import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.BookingBindEntity;
import com.zhangwy.integral.entity.BookingEntity;
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
    public abstract boolean addMember(MemberEntity member);

    /**
     * 更新成员
     *
     * @param member 成员对象
     */
    public abstract boolean updateMember(MemberEntity member);

    /**
     * 删除成员
     *
     * @param id 成员ID
     * @return true dld success
     */
    public abstract boolean dldMember(String id);

    /**
     * 修改成员描述信息
     *
     * @param mmId    成员ID
     * @param message 成员描述信息
     */
    public abstract boolean updateMessage(String mmId, String message);

    /**
     * 添加成员地址
     *
     * @param address 地址
     */
    public abstract boolean addAddress(AddressEntity address);

    /**
     * 更新地址
     *
     * @param address 地址
     */
    public abstract boolean updateAddress(AddressEntity address);

    /**
     * 删除地址
     *
     * @param id 地址ID
     * @return true dld success
     */
    public abstract boolean dldAddress(String id);

    /**
     * 情况成员地址
     *
     * @param memberId 成员ID
     */
    public abstract boolean clearAddress(String memberId);

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
    public abstract boolean addIntegral(IntegralEntity integral);

    /**
     * 更新积分项
     *
     * @param integral 积分
     */
    public abstract boolean updateIntegral(IntegralEntity integral);

    /**
     * 删除积分项
     *
     * @param id 积分ID
     * @return true dld success
     */
    public abstract boolean dldIntegral(String id);

    /**
     * 添加用户积分
     *
     * @param integralBind 用户积分
     */
    public abstract boolean addMemberIntegral(IntegralBindEntity integralBind);

    /**
     * 修改用户积分
     *
     * @param integralBind 用户积分
     */
    public abstract boolean updateMemberIntegral(IntegralBindEntity integralBind);

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
    public abstract boolean addExpiry(CouponsExpiryEntity expiry) throws CodeException;

    /**
     * 删除有效期配置
     *
     * @param expiryId 有效期ID
     * @return true dld success
     */
    public abstract boolean dldExpiry(String expiryId);

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
    public abstract boolean addCoupons(CouponsEntity coupons);

    /**
     * 修改优惠券项
     *
     * @param coupons 优惠券实体类
     */
    public abstract boolean updateCoupons(CouponsEntity coupons);

    /**
     * 删除优惠券项
     *
     * @param couponsId 优惠券ID
     * @return true dld success
     */
    public abstract boolean dldCoupons(String couponsId);

    /**
     * 获取优惠券项
     *
     * @return 返回优惠券项
     */
    public abstract List<CouponsEntity> getCoupons();

    /**
     * 添加成员优惠券
     *
     * @param coupons 优惠券对象
     */
    public abstract boolean addMemberCoupons(CouponsBindEntity coupons);

    /**
     * 修改成员优惠券
     *
     * @param coupons 优惠券对象
     */
    public abstract boolean updateMemberCoupons(CouponsBindEntity coupons);

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
    public abstract boolean useCoupons(String memberId, String couponsId) throws CodeException;

    /**
     * 获取成员优惠券列表
     *
     * @param memberId 成员ID 为空时将返回全部成员的优惠券
     * @return 成员优惠券列表
     */
    public abstract List<CouponsBindEntity> getMemberCoupons(String memberId);

    /**
     * 获取预订项
     *
     * @return 预订项列表
     */
    public abstract List<BookingEntity> getBookings();

    /**
     * 获取预订项
     *
     * @param bookingId 预订项ID
     * @return 积分项
     */
    public abstract BookingEntity getBooking(String bookingId);

    /**
     * 添加预订项
     *
     * @param booking 预订项
     * @return true add success
     */
    public abstract boolean addBooking(BookingEntity booking);

    /**
     * 更新预订项
     *
     * @param booking 预订项
     * @return true update success
     */
    public abstract boolean updateBooking(BookingEntity booking);

    /**
     * 删除预订项
     *
     * @param id 预订ID
     * @return true dld success
     */
    public abstract boolean dldBooking(String id);

    /**
     * 添加成员预订
     *
     * @param bookingBindEntity 预订对象
     */
    public abstract boolean addMemberBooking(BookingBindEntity bookingBindEntity);

    /**
     * 修改成员预订
     *
     * @param bookingBindEntity 预订对象
     */
    public abstract boolean updateMemberBooking(BookingBindEntity bookingBindEntity);

    /**
     * 获取成员预订
     *
     * @param memberId  成员ID
     * @param bookingId 预订ID
     * @return 预订对象
     */
    public abstract BookingBindEntity getMemberBooking(String memberId, String bookingId);

    /**
     * 使用优惠券
     *
     * @param memberId  成员ID 不得为空
     * @param bookingId 预订ID 为空时将设置该成员下所有预订项为下单
     * @throws CodeException 下单预订时出现异常
     */
    public abstract boolean orderBooking(String memberId, String bookingId) throws CodeException;

    /**
     * 使用优惠券
     *
     * @param bookingBindId 预订项ID 为空时将设置该成员下所有预订项为下单
     * @throws CodeException 下单预订时出现异常
     */
    public abstract boolean orderBooking(String bookingBindId) throws CodeException;

    /**
     * 获取成员预订列表
     *
     * @return 成员预订列表
     */
    public abstract List<BookingBindEntity> getMemberBookings();

    /**
     * 获取成员预订列表
     *
     * @param memberId 成员ID 为空时将返回全部成员的预订列表
     * @return 成员预订列表
     */
    public abstract List<BookingBindEntity> getMemberBookings(String memberId);

}

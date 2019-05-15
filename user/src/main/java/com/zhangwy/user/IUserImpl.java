package com.zhangwy.user;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.zhangwy.common.ErrorMessage;
import com.zhangwy.common.entities.IUserEntity;
import com.zhangwy.http.ServiceGenerator;
import com.zhangwy.http.UserService;

import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yixia.lib.core.exception.CodeException;
import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Device;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.NetWorkObservable;

/**
 * Author: zhangwy
 * 创建时间：2015年12月1日 下午3:34:35
 * 修改时间：2015年12月1日 下午3:34:35
 * Description:
 **/
public class IUserImpl extends IUser {
    private Context context;
    private PreferencesHelper helper = PreferencesHelper.defaultInstance();
    private final String PREF_USER_INFO = "pref_user_info";
    private HashSet<Login> mLogins = new HashSet<>();

    private IUserEntity userEntity;

    private boolean autoLogining = false;
    private long lastAutoLoginTime = 0;
    private NetWorkObservable.NetWorkObserver netWorkObserver;
    private Device.NetType lastNetType;
    private UserService userService = ServiceGenerator.getInstance().getUserService();

    @Override
    public void init(Context context) {
        try {
            this.context = context.getApplicationContext();
            helper.init(this.context);
            String json = helper.getString(this.PREF_USER_INFO, "");
            this.userEntity = JSON.parseObject(json, IUserEntity.class);
        } catch (Exception e) {
            Logger.d("initUserInfo", e);
        }
        autoLogin(false);
    }

    @Override
    public void autoLogin(boolean force) {
        if (!force && this.isAvailableLogin(this.userEntity)) {
            return;
        }
        this.verifyLogin();
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public boolean isVip() {
        return false;
    }

    @Override
    public void login(String token, LoginCallBack callBack) throws CodeException {
        if (TextUtils.isEmpty(token)) {
            throw new CodeException(ErrorMessage.ERROR_CODE_TOKEN_EMPTY);
        }
        this.unregisterNetObserver();
        this.userEntity = null;
        Call<IUserEntity> call = ServiceGenerator.getInstance().getUserService().login(token);
        call.enqueue(new Callback<IUserEntity>() {
            @Override
            public void onResponse(Call<IUserEntity> call, Response<IUserEntity> response) {
                onSuccess(response.body(), callBack);
            }

            @Override
            public void onFailure(Call<IUserEntity> call, Throwable t) {
            }
        });
    }

    @Override
    public void verifyVip(String token, VipCallBack callBack) throws CodeException {
        if (TextUtils.isEmpty(token)) {
            throw new CodeException(ErrorMessage.ERROR_CODE_TOKEN_EMPTY);
        }
        //TODO
    }

    private void verifyLogin() {
        //10 * 60 * 1000;超过10分钟自动登录无响应将再下次触发时执行自动登录
        long INTERVAL_TIME_AUTOREQ = 600000;
        if (this.autoLogining && (System.currentTimeMillis() - this.lastAutoLoginTime < INTERVAL_TIME_AUTOREQ)) //正在自动登陆中
            return;

        this.autoLogining = true;
        this.lastAutoLoginTime = System.currentTimeMillis();

        String json = helper.getString(this.PREF_USER_INFO, "");
        if (TextUtils.isEmpty(json)) {
            this.autoLogining = false;
            return;
        }

        IUserEntity entity = JSON.parseObject(json, IUserEntity.class);
        if (!this.isUseableLogin(entity)) {
            this.autoLogining = false;
            return;
        }

        this.unregisterNetObserver();
        Call<IUserEntity> call = this.userService.check(entity.getUserId(), entity.getToken());
        call.enqueue(new Callback<IUserEntity>() {
            @Override
            public void onResponse(Call<IUserEntity> call, Response<IUserEntity> response) {
                autoLogining = false;
                onSuccess(response.body(), null);
            }

            @Override
            public void onFailure(Call<IUserEntity> call, Throwable t) {
                if (!Device.NetWork.isAvailable(context)) {
                    registerNetObserver();
                }
            }
        });
    }

    private void onSuccess(IUserEntity userEntity, LoginCallBack callBack) {
        if (isUseableLogin(userEntity)) {
            userEntity.setReqLocalTime(System.currentTimeMillis());
            helper.commitString(PREF_USER_INFO, JSON.toJSONString(userEntity));
            this.userEntity = userEntity;
            if (callBack != null) {
                callBack.onSuccess(userEntity);
            }
        }
    }

    @Override
    public void logout() {
    }

    @Override
    public IUserEntity getUserInfo() {
        return this.userEntity;
    }

    @Override
    public void register(Login login) {
        if (this.mLogins.contains(login)) {
            return;
        }
        this.mLogins.add(login);
    }

    @Override
    public void unRegister(Login login) {
        this.mLogins.remove(login);
    }

    private boolean isAvailableLogin(IUserEntity userEntity) {
        return userEntity != null && userEntity.isAvailable();
    }

    private boolean isUseableLogin(IUserEntity userEntity) {
        return userEntity != null && userEntity.useable();
    }

    private void registerNetObserver() {
        if (this.netWorkObserver == null) {
            this.netWorkObserver = new NetWorkObservable.NetWorkObserver() {
                @Override
                public void updateNetWork(Device.NetType netType) {
                    //如果网络有变化这时候有网络且需要更新用户信息(VIP信息以及用户信息)。（信息过期或者没有信息）
                    if (!netType.equals(lastNetType) && netType.isAvailable() && !isAvailableLogin(userEntity)) {
                        lastNetType = netType;
                        verifyLogin();
                    }
                }
            };
        }
        NetWorkObservable.getInstance().register(this.netWorkObserver);
    }

    private void unregisterNetObserver() {
        if (this.netWorkObserver == null)
            return;
        NetWorkObservable.getInstance().unRegister(this.netWorkObserver);
    }
}
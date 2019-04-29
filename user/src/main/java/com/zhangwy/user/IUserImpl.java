/*************************************************************************************
* Module Name:com.funshion.video.login
* File Name:FSLoginImpl.java
* Author: 张维亚
* Copyright 2007-, Funshion Online Technologies Ltd.
* All Rights Reserved
* 版权 2007-，北京风行在线技术有限公司
* 所有版权保护
* This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
* the contents of this file may not be disclosed to third parties, copied or
* duplicated in any form, in whole or in part, without the prior written
* permission of Funshion Online Technologies Ltd.
* 这是北京风行在线技术有限公司未公开的私有源代码。本文件及相关内容未经风行在线技术有
* 限公司事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份。
***************************************************************************************/
package com.zhangwy.user;

import java.util.HashSet;
import java.util.Iterator;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.funshion.video.config.FSPreference;
import com.funshion.video.config.FSPreference.PrefID;
import com.funshion.video.entity.FSUserInfoEntity;
import com.funshion.video.entity.FSUserVipInfoEntity;
import com.funshion.video.logger.FSLogcat;
import com.funshion.video.net.FSNetMonitor;
import com.funshion.video.net.FSNetObserver;
import com.funshion.video.user.login.LoginException;
import com.funshion.video.util.TimeUtil;
import com.google.gson.Gson;

import yixia.lib.core.exception.CodeException;
import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.NetWorkObservable;

/**
 * Author: zhangwy
 * 创建时间：2015年12月1日 下午3:34:35
 * 修改时间：2015年12月1日 下午3:34:35
 * Description: 
 **/
public class IUserImpl extends IUser {
	private final static String TAG = "IUserImpl";
	private PreferencesHelper helper = PreferencesHelper.defaultInstance();
	private final String PREF_USER_INFO = "pref_user_info";
	private final String PREF_VIP_INFO = "pref_user_vip_info";
	private HashSet<Login> mLogins = new HashSet<>();

	private IUserInfoEntity mInfoEntity;
	private IUserVipInfoEntity mVipInfoEntity;
	private NetWorkObservable.NetWorkObserver mNetObserver;

	private boolean autoLogining = false;
	private long lastAutoLoginTime = 0;
	private long lastLoginTime = 0;
	private final long INTERVAL_TIME_AUTOREQ = 600000;//10 * 60 * 1000;超过10分钟自动登录无响应将再下次触发时执行自动登录
	private final long INTERVAL_TIME_VERIFY = 7200000;//2 * 60 * 60 * 1000;
	private FSNetObserver.NetAction mNetAction;
	@Override
	public void init(Context context) {
		try{
			helper.init(context.getApplicationContext());

			String json = helper.getString(this.PREF_USER_INFO, "");
			this.mInfoEntity = JSON.parseObject(json, IUserInfoEntity.class);
		} catch (Exception e) {
			Logger.d("initUserInfo", e);
		}

		try{
			String json = helper.getString(this.PREF_VIP_INFO, "");
			this.mVipInfoEntity = JSON.parseObject(json, IUserVipInfoEntity.class);
		} catch (Exception e) {
			Logger.d(TAG, "initVipInfo", e);
		}
		autoLogin(false);
	}

	@Override
	public void autoLogin(boolean force) {
		if (force || isVerifyLogin() || isVerifyVip()) {
			try {
				verifyLogin();
			} catch (CodeException e) {
				Logger.d("autoLogin", e);
			}
		}
	}

	private void verifyLogin() throws CodeException {
		if (autoLogining && (System.currentTimeMillis() - lastAutoLoginTime < INTERVAL_TIME_AUTOREQ)) //正在自动登陆中
			return;

		autoLogining = true;
		lastAutoLoginTime = System.currentTimeMillis();

		String json = this.helper.getString(this.PREF_USER_INFO, "");
		if (TextUtils.isEmpty(json)){
			autoLogining = false;
			return;
		}

		final IUserInfoEntity infoEntity = JSON.parseObject(json, IUserInfoEntity.class);
		if (!this.verifyLogin(infoEntity)){
			autoLogining = false;
			return;
		}

		try {
			this.unregisterNetObserver();
			this.lastLoginTime = 0;
			new IUserLoginStatus().request(infoEntity.getUser_id(), infoEntity.getToken(), new IUserCallback() {

				@Override
				public void onSuccess(IUserInfoEntity userInfo) {
					if (verifyLogin(mInfoEntity)){
						autoLogining = false;
						return;
					}

					try {
						login(infoEntity.getUser_id(), infoEntity.getToken(), new LoginCallBack() {
							@Override
							public void onSuccess(IUserInfoEntity userInfo) {
								autoLogining = false;
								unregisterNetObserver();
							}

							@Override
							public void onException(CodeException e) {
								Logger.d("verifyLogin.IUserLoginStatus().login", e);
								callVerifyLoginErr(e);
							}

							@Override
							public boolean isDestroy() {
								return false;
							}
						}, true);
					} catch (CodeException e) {
						Logger.d("verifyLogin.login(userid, token, listener)", e);
						callVerifyLoginErr(e);
					}
				}

				@Override
				public void onException(CodeException e) {
					Logger.d(TAG, "verifyLogin.IUserLoginStatus().login", e);
					callVerifyLoginErr(e);
				}

				private void callVerifyLoginErr(UserException e){
					FSLogcat.d(TAG, "callVerifyLoginErr", e);
					autoLogining = false;
					if (!isVerifyLogin())
						return;
					switch (e.getCode()) {
					case UserConstants.ERROR_CODE_NETWORK:
						registerNetObserver();
						break;
					case UserConstants.ERROR_CODE_TOKEN_INVALID:
						logout();
						break;
					default:
						break;
					}
				}
			});
		} catch (UserException e) {
			FSLogcat.d(TAG, "verifyLogin", e);
			this.autoLogining = false;
			throw e;
		}
	}

	@Override
	public boolean isLogin() {
		try {
			return this.verifyLogin(this.mInfoEntity);
		} finally {
			if (this.isVerifyLogin()) {
				try {
					this.verifyLogin();
				} catch (CodeException e) {
					Logger.d("isLogin.verifyLogin", e);
					mInfoEntity = null;
				}
			}
		}
	}

	@Override
	public boolean isVip() {
		try{
			return this.verifyVip(this.mVipInfoEntity);
		} finally {
			if (this.isVerifyVip()) {
				try {
					this.verifyVip(mInfoEntity.getUser_id(), mInfoEntity.getToken(), null);
				} catch (CodeException e) {
					Logger.d("isVip.verifyVip", e);
					this.mVipInfoEntity = null;
				}
			}
		}
	}

	@Override
	public void login(Model model, String userid, String password, LoginCallBack callBack) throws UserException {
		if (TextUtils.isEmpty(userid))
			throw new LoginException(UserConstants.ERROR_CODE_USER_EMPTY, "userid is empty");
		if (TextUtils.isEmpty(password))
			throw new LoginException(UserConstants.ERROR_CODE_PASS_EMPTY, "password is empty");

		this.unregisterNetObserver();
		this.lastLoginTime = 0;
		this.mInfoEntity = null;
		this.mVipInfoEntity = null;

		switch (model) {
		case FUNSHION:
			this.loginFun(userid, password, callBack);
			break;
		case TENCENT:
			new UserLoginTencent().request(userid, password, newUserCallback(callBack));
			break;
		case WEIXIN:
			new IUserLoginWeiXin().request(userid, password, newUserCallback(callBack));
			break;
		case SINA:
			new UserLoginSina().request(userid, password, newUserCallback(callBack));
			break;
		default:
			break;
		}
	}

	private void loginFun(String userid, String password, final LoginCallBack callBack) throws UserException {
		new UserLoginFunshion().request(userid, password, new LoginUserCallback(callBack) {

			@Override
			public void onSuccess(IUserInfoEntity userInfo) {
				try {
					login(userInfo.getUser_id(), userInfo.getToken(), callBack);
				} catch (UserException e) {
					this.onException(e);
				}
			}
		});
	}

	@Override
	public void login(String userid, String token, LoginCallBack callBack) throws CodeException {
		if (TextUtils.isEmpty(userid))
			throw new CodeException(UserConstants.ERROR_CODE_USER_EMPTY, "userid is empty");
		if (TextUtils.isEmpty(password))
			throw new CodeException(UserConstants.ERROR_CODE_PASS_EMPTY, "password is empty");

		this.login(userid, token, callBack, false);
	}

	private void login(String userid, String token, LoginCallBack callBack, boolean autoLogin) throws CodeException {
		new IUserLoginWeiXin().request(userid, token, newUserCallback(callBack));
	}

	private LoginUserCallback newUserCallback(final LoginCallBack callBack) {
		return new LoginUserCallback(callBack) {

			@Override
			public void onSuccess(final IUserInfoEntity userInfo) {
				try {
					verify(userInfo);
					new UserLoginVipInfo().request(userInfo.getUser_id(), userInfo.getToken(), new UserLoginVipInfo.VipInfoCallback(null) {

						@Override
						public void onSuccess(FSUserVipInfoEntity vipInfo) {
							success(userInfo, vipInfo);
						}

						@Override
						public void onException(UserException e) {
							error(e);
						}
					});
				} catch (UserException e) {
					onException(e);
				}
			}

			private IUserInfoEntity verify(IUserInfoEntity entity) throws LoginException {
				if (entity == null){
					FSPreference.getInstance().putString(PrefID.PREF_USER_INFO, "");
					throw new LoginException(UserConstants.ERROR_CODE_USER_NULL, "user info is null");
				}

				if (!verifyLogin(entity)){
					FSPreference.getInstance().putString(PrefID.PREF_USER_INFO, "");
					throw new LoginException(UserConstants.ERROR_CODE_USER_INVALID, "user info is invalid");
				}

				return entity;
			}

			private void success(IUserInfoEntity userInfo, FSUserVipInfoEntity vipInfo){
				try{
					if (this.isAutoLogin() && mInfoEntity != null && (!TextUtils.equals(userInfo.getUser_id(), mInfoEntity.getUser_id()) || !TextUtils.equals(userInfo.getToken(), mInfoEntity.getToken()) || !isVerifyLogin())) {
						return;
					}

					IUserImpl.this.notify(userInfo, vipInfo);
				} finally {
					callSuccess(userInfo);
				}
			}

			private void error(UserException e){
				LoginCallBack callBack = getCallback();
				if (verifyCallBack(callBack))
					callBack.onException(e);
			}
		};

	}

	@Override
	public void verifyVip(String userid, String token, VipCallBack callBack) throws UserException {
		new UserLoginVipInfo().request(userid, token, new UserLoginVipInfo.VipInfoCallback(callBack) {

			@Override
			public void onSuccess(FSUserVipInfoEntity vipInfo) {

				IUserImpl.this.notify(mInfoEntity, vipInfo);
				//通知CallBack登录验证成功.
				this.callSuccess(verifyVip(vipInfo));
			}

		});
	}

	@Override
	public IUserInfoEntity getUserInfo() {
		return this.mInfoEntity;
	}

	@Override
	public void logout() {
		this.lastLoginTime = 0;
		this.mInfoEntity = null;
		FSPreference.getInstance().putString(PrefID.PREF_USER_INFO, "");
		this.mVipInfoEntity = null;
		FSPreference.getInstance().putString(PrefID.PREF_VIP_INFO, "");
		this.notify(false);
	}

	@Override
	public void register(Login login) {
		try {
			if (mLogins.contains(login))
				return;
			this.mLogins.add(login);
		} finally {
			if (login != null && mInfoEntity != null)
				login.login(mInfoEntity, mVipInfoEntity);
		}
	}

	@Override
	public void unRegister(Login login) {
		if (!mLogins.contains(login))
			return;
		this.mLogins.remove(login);
	}

	private void notify(IUserInfoEntity userInfo, FSUserVipInfoEntity vipInfo) {
		//
		mInfoEntity = userInfo;
		FSPreference.getInstance().putString(PrefID.PREF_USER_INFO, JSON.toJSONString(userInfo));
		//
		mVipInfoEntity = vipInfo;
		FSPreference.getInstance().putString(PrefID.PREF_VIP_INFO, JSON.toJSONString(vipInfo));
		lastLoginTime = System.currentTimeMillis();
		//通知观察者登录验证成功
		this.notify(true);
	}

	private void notify(boolean state){
		if (mLogins == null || mLogins.isEmpty())
			return;

		Iterator<Login> iterator = mLogins.iterator();
		while (iterator.hasNext()) {
			Login login = iterator.next();
			if (state) {
				login.login(mInfoEntity, mVipInfoEntity);
			} else {
				login.logout();
			}
		}
	}

	private void registerNetObserver(){
		if (mNetObserver == null){
			mNetObserver = new FSNetObserver() {
				@Override
				public void notify(NetAction action) {
					//如果网络有变化这时候有网络且需要更新用户信息(VIP信息以及用户信息)。（信息过期或者没有信息）
					if(!action.equals(mNetAction) && action.isAvailable() && (isVerifyLogin() || isVerifyVip())) {
						mNetAction = action;
						try {
							verifyLogin();
						} catch (Exception e) {
							return;
						}
					}
				}
			};
		}
		FSNetMonitor.getInstance().addObserver(mNetObserver);
	}

	private void unregisterNetObserver(){
		if (mNetObserver == null)
			return;
		FSNetMonitor.getInstance().delObserver(mNetObserver);
	}

	//返回True不需要登录
	private boolean verifyLogin(IUserInfoEntity entity){
		return entity != null && !TextUtils.isEmpty(entity.getToken()) && !TextUtils.isEmpty(entity.getUser_id());
	}

	private boolean verifyVip(FSUserVipInfoEntity entity){
		if (entity == null || entity.getData() == null)
			return false;
		String userid = entity.getData().getUser_id();
		if (TextUtils.isEmpty(userid))
			return false;
		String rettime = entity.getRettime();
		if (TextUtils.isEmpty(rettime))
			return false;

		String isVip = entity.getData().getIs_vip();
		if (isVip == null || isVip.equals("") || isVip.equals("0"))
			return false;

		String endtm = entity.getData().getEndtm();
		if (TextUtils.isEmpty(endtm) || TimeUtil.dateString2DateLong(endtm, TimeUtil.PATTERN_DATE) <= System.currentTimeMillis())
			return false;

		try {
			if (TextUtils.isEmpty(entity.getRetsign()) || !entity.getRetsign().equals(UserConstants.makeSign(userid, rettime, endtm, isVip)))
				return false;
		} catch (UserException e) {
			return false;
		}
		return true;
	}

	private boolean isVerifyLogin(){
		if (!this.verifyLogin(mInfoEntity))
			return true;
		long cTime = System.currentTimeMillis() - lastLoginTime;
		if (cTime >= 0 && cTime < INTERVAL_TIME_VERIFY)
			return false;
		return true;
	}

	//返回True需要刷新数据
	private boolean isVerifyVip(){
		if (!this.verifyLogin(mInfoEntity))
			return false;
		if (this.mVipInfoEntity == null)
			return true;
		long cTime = System.currentTimeMillis() - lastLoginTime;
		if (cTime >= 0 && cTime < INTERVAL_TIME_VERIFY)
			return false;
		return true;
	}

	private static abstract class LoginUserCallback implements UserCallback {
		private boolean autoLogin = false;
		private LoginCallBack mCallBack;
		public LoginUserCallback(LoginCallBack callBack) {
			this.mCallBack = callBack;
		}

		@Override
		public abstract void onSuccess(IUserInfoEntity userInfo);

		public void callSuccess(IUserInfoEntity userInfo){
			LoginCallBack callBack = getCallback();
			if (this.verifyCallBack(callBack))
				callBack.onSuccess(userInfo);
		}

		@Override
		public void onException(UserException e) {
			LoginCallBack callBack = getCallback();
			if (this.verifyCallBack(callBack))
				callBack.onException(e);
		}

		public LoginCallBack getCallback(){
			return mCallBack;
		}

		public boolean verifyCallBack(LoginCallBack callBack){
			return callBack != null && !callBack.isDestroy();
		}

		public boolean isAutoLogin() {
			return autoLogin;
		}

		public LoginUserCallback setAutoLogin(boolean autoLogin) {
			this.autoLogin = autoLogin;
			return this;
		}
	}
}
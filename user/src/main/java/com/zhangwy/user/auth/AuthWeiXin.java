package com.zhangwy.user.auth;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhangwy.common.ErrorMessage;
import com.zhangwy.user.R;

import yixia.lib.core.exception.CodeException;

/**
 * Author: zhangwy
 * 创建时间：2015年12月3日 下午2:23:34
 * 修改时间：2015年12月3日 下午2:23:34
 * Description: :
 * 1.重要：发起 登录的主工程必须在起package下
 * 新建wxapi&创建WXEntryActivity
 * &添加到manifest
 * &执行FSAuthWeiXin.getInstance().handleIntent(intent);
 **/
@SuppressWarnings("unused")
public class AuthWeiXin implements AuthLogin<IWXAPI>, IWXAPIEventHandler {

	private IWXAPI iwxApi = null;
	private HashMap<Object, AuthCallback> mCallBacks = new HashMap<>();
	private static AuthWeiXin mInstance = null;

	public static AuthWeiXin getInstance() {
		if (mInstance == null) {
			synchronized (AuthWeiXin.class) {
				if (mInstance == null) {
					mInstance = new AuthWeiXin();
				}
			}
		}
		return mInstance;
	}

	private AuthWeiXin() {
	}

	@Override
	public IWXAPI login(Activity ctx, String appkey, AuthCallback callBack) throws CodeException {
		String key = String.valueOf(callBack.hashCode());
		if (!mCallBacks.containsKey(key))
			mCallBacks.put(key, callBack);
		this.createApiEntity(ctx, appkey);
		if(!iwxApi.isWXAppInstalled())
			throw new CodeException(ErrorMessage.ERROR_CODE_NOT_INSTALL, ctx.getString(R.string.error_message_wechat_uninstall));
		boolean registerAppKey = this.registerWXApi(appkey);
		SendAuth.Req req = new SendAuth.Req();
	    req.scope = "snsapi_userinfo";
	    req.state = key;
		iwxApi.sendReq(req);
		return getApiEntity();
	}

	@Override
	public IWXAPI createApiEntity(Activity ctx, String appkey) throws CodeException {
		if (iwxApi == null){
			if (TextUtils.isEmpty(appkey))
	            throw new CodeException(ErrorMessage.ERROR_CODE_APPKEY_EMPTY, ctx.getString(R.string.error_message_appkey_empty));
			iwxApi = WXAPIFactory.createWXAPI(ctx.getApplicationContext(), appkey);
		}
		return iwxApi;
	}

	public boolean registerWXApi(String appId) {
		return iwxApi.registerApp(appId);
	}

	@Override
	public IWXAPI getApiEntity() {
		return iwxApi;
	}

	@Override
	public boolean unRegister(AuthCallback callBack) {
		if (callBack != null && mCallBacks.containsValue(callBack))
			return mCallBacks.remove(String.valueOf(callBack.hashCode())) != null;
		return false;
	}

	public void handleIntent(Intent intent){
		try{
			iwxApi.handleIntent(intent, this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp baseResp) {
		if (baseResp == null || baseResp.getType() != ConstantsAPI.COMMAND_SENDAUTH || !(baseResp instanceof SendAuth.Resp))
			return;

		SendAuth.Resp resp = (SendAuth.Resp) baseResp;

		AuthCallback callBack = mCallBacks.remove(resp.state);
		if (callBack == null)
			return;

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			/* 微信登录只需要code即可，这里为了统一接口 */
			callBack.onAuthSuccess(resp.code, resp.code);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			callBack.onAuthException(new CodeException(ErrorMessage.ERROR_CODE_USER_CANCEL));
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			callBack.onAuthException(new CodeException(ErrorMessage.ERROR_CODE_AUTH_DENY));
			break;
		default:
			callBack.onAuthException(new CodeException(ErrorMessage.ERROR_CODE_AUTH_FAILED));
			break;
		}
	}

}

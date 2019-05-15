package com.zhangwy.user.pay;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhangwy.common.ErrorMessage;

import yixia.lib.core.exception.CodeException;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Util;

/**
 * Author: 张维亚
 * 创建时间：2015年12月23日 下午12:31:25
 * 修改时间：2015年12月23日 下午12:31:25
 * Description:
 **/
@SuppressWarnings("unused")
public class PayWeiXin implements Pay<IWXAPI>, IWXAPIEventHandler {
	private IWXAPI mIwxApi;
	private HashMap<Object, PayCallback> mCallBacks = new HashMap<>();
	private static PayWeiXin mInstance = null;

	public static PayWeiXin getInstance() {
		if (mInstance == null) {
			synchronized (PayWeiXin.class) {
				if (mInstance == null) {
					mInstance = new PayWeiXin();
				}
			}
		}
		return mInstance;
	}

	private PayWeiXin() {
	}

	@Override
	public IWXAPI pay(Activity ctx, String payInfo, PayCallback callBack) throws CodeException {
		PayReq payReq = parseObject(payInfo);
		this.mCallBacks.put(payReq.prepayId, callBack);
		this.createApiEntity(ctx, payReq.appId);
		boolean register = this.registerWXApi(payReq.appId);
		if(!this.mIwxApi.isWXAppInstalled())
			throw new CodeException(ErrorMessage.ERROR_CODE_NOT_INSTALL, "Weixin not install");
		this.mIwxApi.sendReq(payReq);
		return this.getApiEntity();
	}

	private PayReq parseObject(String text) throws CodeException {
		if (TextUtils.isEmpty(text))
			throw new CodeException(ErrorMessage.ERROR_CODE_PAY_INFO_EMPTY, "pay info is empty");
		text = text.replace("package=", "packageValue=");
		return Util.parseObject(text, PayReq.class, '&', '=');
	}

	@Override
	public IWXAPI createApiEntity(Activity ctx, String appid) throws CodeException {
		if (this.mIwxApi == null){
			if (TextUtils.isEmpty(appid))
	            throw new CodeException(ErrorMessage.ERROR_CODE_APPKEY_EMPTY, "appkey is empty");
			this.mIwxApi = WXAPIFactory.createWXAPI(ctx.getApplicationContext(), appid);
		}
		return this.mIwxApi;
	}

	public boolean registerWXApi(String appKey) {
		return this.mIwxApi.registerApp(appKey);
	}

	@Override
	public IWXAPI getApiEntity() {
		return this.mIwxApi;
	}

	public void handleIntent(Intent intent){
		try{
			this.mIwxApi.handleIntent(intent, this);
		}catch(Exception e){
            Logger.d("handleIntent", e);
		}
	}

	@Override
	public boolean unRegister(PayCallback callBack) {
		if (callBack != null && mCallBacks.containsValue(callBack))
			return mCallBacks.remove(String.valueOf(callBack.hashCode())) != null;
		return false;
	}

	@Override
	public void onReq(BaseReq baseReq) {
	}

	@Override
	public void onResp(BaseResp baseResp) {
		if (baseResp == null || baseResp.getType() != ConstantsAPI.COMMAND_PAY_BY_WX || !(baseResp instanceof PayResp))
			return;
		PayResp resp = (PayResp) baseResp;

		PayCallback callback = this.mCallBacks.remove(resp.prepayId);
		if (callback == null)
			return;

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			callback.onPaySuccess();
			break;
		case BaseResp.ErrCode.ERR_COMM:
			callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_PAY_COMM, "签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常"));
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_SENT_FAILED, "send http failed"));
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_USER_CANCEL, "user cancel"));
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_AUTH_DENY, "auth denied"));
			break;
		default:
			callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_AUTH_FAILED, "pay failed"));
			break;
		}
	}
}
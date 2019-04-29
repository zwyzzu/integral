package com.zhangwy.user;

import yixia.lib.core.exception.CodeException;

/**
 * Author: 张维亚
 * 创建时间：2015年12月21日 下午6:28:32
 * 修改时间：2015年12月21日 下午6:28:32
 * Description: 用户登录到风行服务器
 **/
interface IUserLogin {

	void request(String userid, String password, IUserCallback callback) throws CodeException;
	public static abstract class UserHandler extends FSHandler {
		protected IUserCallback mCallback = null;

		public UserHandler(IUserCallback callBack) {
			this.mCallback = callBack;
		}

		@Override
		public void onFailed(EResp eresp) {
			FSLogcat.d(UserConstants.TAG, eresp.getNetErrCode()+ eresp.getErrCode() + "\nEResp.httpcode:" + eresp.getHttpCode() + "\nEResp.msg:" + eresp.getErrMsg());
			if (eresp.getErrCode() == FSDasError.ERROR_NETWORK) {
				mCallback.onException(new LoginException(UserConstants.ERROR_CODE_NETWORK, eresp.getErrMsg()));
			} else {
				String errmsg = "EResp.code:" + eresp.getNetErrCode() + "\nEResp.httpcode:" + eresp.getHttpCode() + "\nEResp.msg:" + eresp.getErrMsg();
				mCallback.onException(new LoginException(netErrCode2Code(eresp.getNetErrCode()), errmsg));
			}
		}

		private int netErrCode2Code(int netCode){
			switch (netCode) {
			case 405:
			case 1401:
			case 1403:
				return UserConstants.ERROR_CODE_USERNAME_NOT_REGISTERED;
			case 403:
			case 1304:
			case 1027:
				return UserConstants.ERROR_CODE_WRONG_PASSWORD;
			case 404:
			case 1412:
				return UserConstants.ERROR_CODE_ACCOUNT_LOCKOUT;
			case 400:
			case 1008:
			case 1301:
				return UserConstants.ERROR_CODE_TOKEN_INVALID;
			default:
				return netCode;
			}
		}

	}
}

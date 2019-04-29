package com.zhangwy.user;

/**
 * Author: 张维亚
 * 创建时间：2015年12月22日 上午9:51:28
 * 修改时间：2015年12月22日 上午9:51:28
 * Description:
 **/
public class IUserLoginWeiXin implements IUserLogin {

	@Override
	public void request(String userid, String auth_code, IUserCallback callback) throws UserException {
		FSHttpParams params = UserConstants.newHttpParams();
		params.put(UserConstants.PARAMS_KEY_AUTH_CODE, auth_code);
		try {
			FSDas.getInstance().get(FSDasReq.PUSER_OAUTH_WEIXIN, params, new IUserLogin.UserHandler(callback) {

				@Override
				public void onSuccess(SResp sresp) {
					this.mCallback.onSuccess((FSUserInfoEntity) sresp.getEntity());
				}

			});
		} catch (FSDasException e) {
			throw new LoginException(UserConstants.ERROR_CODE_LOGIN_WEIXIN, e);
		}
	}

}

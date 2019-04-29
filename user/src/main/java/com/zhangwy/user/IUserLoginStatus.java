/*************************************************************************************
 * Module Name: 具体模块见相应注释
 * File Name: IUserLoginStatus.java
 * Author: 张维亚
 * All Rights Reserved
 * 所有版权保护
 * Copyright 2014, Funshion Online Technologies Ltd.
 * 版权 2014，北京风行在线技术有限公司
 * This is UNPUBLISHED PROPRIETARY SOURCE CODE of Funshion Online Technologies Ltd.;
 * the contents of this file may not be disclosed to third parties, copied or
 * duplicated in any form, in whole or in part, without the prior written
 * permission of Funshion Online Technologies Ltd.
 * 这是北京风行在线技术有限公司未公开的私有源代码。本文件及相关内容未经风行在线技术有
 * 限公司事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份。
 ***************************************************************************************/
package com.zhangwy.user;

/**
 * Author: 张维亚
 * 创建时间：2016年1月13日 下午4:42:16
 * 修改时间：2016年1月13日 下午4:42:16
 * Description: 检验本地登录是否有效
 **/
public class IUserLoginStatus implements IUserLogin {

	@Override
	public void request(final String userid, final String token, UserCallback callback) throws UserException {
		FSHttpParams params = UserConstants.newHttpParams();
		params.put(UserConstants.PARAMS_KEY_USERID, userid);
		params.put(UserConstants.PARAMS_KEY_TOKEN, token);
		try {
			FSDas.getInstance().get(FSDasReq.PUSER_ISLOGIN, params, new UserHandler(callback) {

				@Override
				public void onSuccess(SResp sresp) {
					FSUserInfoEntity entity = (FSUserInfoEntity) sresp.getEntity();
					entity.setUser_id(userid);
					entity.setToken(token);
					this.mCallback.onSuccess(entity);
				}
			});
		} catch (FSDasException e) {
			throw new UserException(UserConstants.ERROR_CODE_LOGIN_STATUS, e);
		}
	}

}

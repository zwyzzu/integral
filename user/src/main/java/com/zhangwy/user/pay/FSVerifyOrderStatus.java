//package com.zhangwy.user.pay;
//
//import android.text.TextUtils;
//
//import com.zhangwy.common.ErrorMessage;
//
//import yixia.lib.core.exception.CodeException;
//
///**
// * Author: 张维亚
// * 创建时间：2015年12月23日 下午6:00:17
// * 修改时间：2015年12月23日 下午6:00:17
// * Description:
// **/
//public class FSVerifyOrderStatus {
//
//	private final String PARAMS_KEY_USERID = "user_id";
//	private final String PARAMS_KEY_TOKEN = "token";
//	private final String PARAMS_KEY_ORDER = "order_code";
//
//	public void verify(Model model, String userid, String token, String order, StatusCallback callback) throws CodeException{
//		if (TextUtils.isEmpty(userid))
//			throw new CodeException(ErrorMessage.ERROR_CODE_USER_EMPTY, "userid is enpty");
//		if (TextUtils.isEmpty(token))
//			throw new CodeException(ErrorMessage.ERROR_CODE_TOKEN_EMPTY, "token is enpty");
//
//		FSHttpParams params = UserConstants.newHttpParams();
//		params.put(PARAMS_KEY_USERID, userid);
//		params.put(PARAMS_KEY_TOKEN, token);
//		params.put(PARAMS_KEY_ORDER, order);
//		try {
//			FSDasReq dasReq = getDasReq(model);
//			if (dasReq == null)
//				throw new PayException(UserConstants.ERROR_CODE_DASREQ_NULL, "dasreq is null");
//
//			FSDas.getInstance().get(dasReq, params, new StatusHandler(callback, order, userid), false);
//		} catch (FSDasException e) {
//			throw new PayException(UserConstants.ERROR_CODE_ORDER_STATUS, e);
//		}
//	}
//
//	private FSDasReq getDasReq(Model model) {
//		switch (model) {
//		case MEDIA:
//			return FSDasReq.PVIP_ORDER_STATUS_MEDIA;
//		case VIP:
//			return FSDasReq.PVIP_ORDER_STATUS_VIP;
//		default:
//			return null;
//		}
//	}
//
//	public static enum Model {
//        MEDIA("media","媒体"),VIP("vip","vip"), UNKNOWN("unknow","未知的");
//        public String type;
//        public String desc;
//        private Model(String code, String desc) {
//            this.type = code;
//            this.desc = desc;
//        }
//
//        public static Model find(String code) {
//            for (Model type : Model.values()) {
//                if (type.type.equals(code))
//                    return type;
//            }
//            return UNKNOWN;
//        }
//    }
//
//	public static interface StatusCallback {
//		public void status(String order, FSOrderStatusEntity.Status status);
//		public void error(UserException e);
//	}
//
//	public static class StatusHandler extends FSHandler {
//
//		private StatusCallback mCallback;
//		private String mOrder;
//		private String mUserid;
//		public StatusHandler(StatusCallback callback, String order, String userid) {
//			this.mCallback = callback;
//			this.mOrder = order;
//			this.mUserid = userid;
//		}
//
//		@Override
//		public void onSuccess(SResp sresp) {
//			FSLogcat.d(UserConstants.TAG, "onSuccess.EResp.timeUsed:" + sresp.getTimeUsed());
//			if (sresp == null || sresp.getEntity() == null){
//				mCallback.error(new PayException(UserConstants.ERROR_CODE_SRESP_NULL, "result is null"));
//				return;
//			}
//			FSOrderStatusEntity entity = (FSOrderStatusEntity) sresp.getEntity();
//			if (TextUtils.isEmpty(entity.getRetsign())){
//				mCallback.error(new PayException(UserConstants.ERROR_CODE_SIGN_EMPTY, "sign is empty"));
//				return;
//			}
//			try {
//				String sign = UserConstants.makeSign(mUserid, entity.getRettime());
//				if (!TextUtils.equals(sign, entity.getRetsign())){
//					mCallback.error(new UserException(UserConstants.ERROR_CODE_SIGN_INVALID, "sign is invalid"));
//					return;
//				}
//
//				mCallback.status(mOrder, entity.getStatus());
//			} catch (UserException e) {
//				mCallback.error(e);
//			}
//		}
//
//		@Override
//		public void onFailed(EResp eresp) {
//			FSLogcat.d(UserConstants.TAG, "EResp.code:" + eresp.getErrCode() + "EResp.msg:" + eresp.getErrMsg());
//			if (eresp.getErrCode() == FSDasError.ERROR_NETWORK) {
//				mCallback.error(new PayException(UserConstants.ERROR_CODE_NETWORK, eresp.getErrMsg()));
//			} else {
//				mCallback.error(new PayException(UserConstants.ERROR_CODE_VERIFY_ORDER, "EResp.netcode:" + eresp.getNetErrCode() + "EResp.errCode:" +eresp.getErrCode() + "EResp.msg:" + eresp.getErrMsg()));
//			}
//		}
//
//	}
//}
//package com.zhangwy.user.pay;
//
//import android.app.Activity;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//
//import com.zhangwy.common.ErrorMessage;
//
//import yixia.lib.core.exception.CodeException;
//import yixia.lib.core.util.Util;
//
///**
// * Author: zhangwy
// * 创建时间：2015年12月10日 下午3:41:05
// * 修改时间：2015年12月10日 下午3:41:05
// * Description:
// **/
//public class PayALiPay implements Pay<PayTask> {
//
//	private final static int WAIT_PAY = 100;
//	private PayTask mAlipay = null;
//	private PayCallback mCallback;
//
//	@Override
//	public PayTask pay(Activity ctx, String payInfo, PayCallback callBack) throws CodeException {
//		if (TextUtils.isEmpty(payInfo))
//			throw new CodeException(ErrorMessage.ERROR_CODE_PAY_INFO_EMPTY, "pay info is empty");
//		this.mCallback = callBack;
//		this.createApiEntity(ctx, null);
//		new Thread(new AlipayPayRunnable(this.mAlipay, payInfo, new AlipayHandler(callBack))).start();
//		return getApiEntity();
//	}
//
//	@Override
//	public PayTask createApiEntity(Activity ctx, String appkey) throws CodeException {
//		return createApiEntity(ctx);
//	}
//
//	private PayTask createApiEntity(Activity ctx) throws CodeException {
//		if (mAlipay == null)
//			mAlipay = new PayTask(ctx);
//		return mAlipay;
//	}
//
//	@Override
//	public PayTask getApiEntity() throws CodeException {
//		return mAlipay;
//	}
//
//	@Override
//	public boolean unRegister(PayCallback callBack) {
//		if (this.mCallback == null || this.mCallback == callBack) {
//			this.mCallback = null;
//			return true;
//		}
//		return false;
//	}
//
//	public static class AlipayPayRunnable implements Runnable{
//
//		private PayTask mPayTask;
//		private Handler mHandler;
//		private String mPayInfo;
//		public AlipayPayRunnable(PayTask payTask, String payInfo, Handler handler) {
//			this.mPayTask = payTask;
//			this.mHandler = handler;
//			this.mPayInfo = payInfo;
//		}
//
//		@Override
//		public void run() {
//			if (TextUtils.isEmpty(mPayInfo))
//				return;
//
//			// 调用支付接口，获取支付结果
//			String pay = mPayTask.pay(mPayInfo);
//			Resp resp = Resp.create(pay);
//			if (resp != null)
//				resp.setRequestInfo(mPayInfo);
//
//			Message msg = new Message();
//			msg.what = WAIT_PAY;
//			msg.obj = resp;
//			mHandler.sendMessage(msg);
//		}
//	}
//
//	public static class AlipayHandler extends Handler {
//
//
//
//		private PayCallback mCallback;
//		public AlipayHandler(PayCallback callback) {
//			this.mCallback = callback;
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case WAIT_PAY:
//				onResp(msg, mCallback);
//				break;
//
//			default:
//				break;
//			}
//		}
//
//		private void onResp(Message msg, PayCallback callback) {
//			if (msg.obj == null || !(msg.obj instanceof Resp)){
//				callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_PAY_REQNULL, "result is null"));
//				return;
//			}
//			Resp resp = (Resp) msg.obj;
//			switch (resp.getStatus()) {
//			case SUCCESS:
//				PayResp payResp = resp.getRes();
//				if (payResp == null) {
//					callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_PAY_REQNULL, "resultInfo is null"));
//				} else {
//					callback.onPaySuccess();
//				}
//				break;
//			case NETWORK:
//				callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_SENT_FAILED, "send http failed"));
//				break;
//			case CANCEL:
//				callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_USER_CANCEL, "user cancel"));
//				break;
//			case FAILED:
//				callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_PAY_FAILED, "pay failed"));
//				break;
//			case WAITING:
//				callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_PAY_WAITING, "支付渠道原因或者系统原因还在等待支付结果确认"));
//				break;
//			default:
//				callback.onPayException(new CodeException(ErrorMessage.ERROR_CODE_PAY_FAILED, "pay failed"));
//				break;
//			}
//		}
//	}
//
//	public static class Resp {
//		private String resultStatus;
//		private String result;// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//		private String requestInfo;
//		private String memo;
//		private PayResp req;
//		private PayResp res;
//
//		public static Resp create(String text){
//			if (TextUtils.isEmpty(text))
//				return null;
//			return Utils.parseObject(text.replace("{", "").replace("}", ""), Resp.class, ';', '=');
//		}
//
//		public String getResultStatus() {
//			return resultStatus;
//		}
//
//		public void setResultStatus(String resultStatus) {
//			this.resultStatus = resultStatus;
//		}
//
//		public String getRequestInfo() {
//			return requestInfo;
//		}
//
//		public void setRequestInfo(String requestInfo) {
//			this.requestInfo = requestInfo;
//		}
//
//		public String getResult() {
//			return result;
//		}
//
//		public void setResult(String result) {
//			this.result = result;
//		}
//
//		public String getMemo() {
//			return memo;
//		}
//
//		public void setMemo(String memo) {
//			this.memo = memo;
//		}
//
//		public PayResp getReq(){
//			if (req == null && !TextUtils.isEmpty(requestInfo))
//				req = Util.parseObject(requestInfo.replace("\"", ""), PayResp.class, '&', '=');
//			return req;
//		}
//
//		public PayResp getRes() {
//			if (res == null && !TextUtils.isEmpty(result))
//				res = Util.parseObject(result.replace("\"", ""), PayResp.class, '&', '=');
//			return res;
//		}
//
//		public PayStatus getStatus(){
//			return PayStatus.find(resultStatus);
//		}
//	}
//
//	public enum PayStatus {
//		SUCCESS("9000", "支付成功"),
//		WAITING("8000", "支付结果因为支付渠道原因或者系统原因还在等待支付结果确认"),
//		NETWORK("6002", "网络连接出错"),
//		CANCEL("6001", "用户中途取消"),
//		FAILED("4000", "订单支付失败"), ;
//		public String code;
//		public String desc;
//		private PayStatus(String code, String desc) {
//			this.code = code;
//			this.desc = desc;
//		}
//
//		public static PayStatus find(String code) {
//			for (PayStatus status : PayStatus.values()) {
//				if (status.code.equals(code))
//					return status;
//			}
//			return FAILED;
//		}
//	}
//
//	public static class PayResp {
//		private String partner;
//		private String seller_id;
//		private String out_trade_no;
//		private String subject;
//		private String body;
//		private String total_fee;
//		private String notify_url;
//		private String service;
//		private String payment_type;
//		private String _input_charset;
//		private String it_b_pay;
//		private String success;
//		private String sign_type;
//		private String sign;
//
//		public String getPartner() {
//			return partner;
//		}
//
//		public void setPartner(String partner) {
//			this.partner = partner;
//		}
//
//		public String getSeller_id() {
//			return seller_id;
//		}
//
//		public void setSeller_id(String seller_id) {
//			this.seller_id = seller_id;
//		}
//
//		public String getOut_trade_no() {
//			return out_trade_no;
//		}
//
//		public void setOut_trade_no(String out_trade_no) {
//			this.out_trade_no = out_trade_no;
//		}
//
//		public String getSubject() {
//			return subject;
//		}
//
//		public void setSubject(String subject) {
//			this.subject = subject;
//		}
//
//		public String getBody() {
//			return body;
//		}
//
//		public void setBody(String body) {
//			this.body = body;
//		}
//
//		public String getTotal_fee() {
//			return total_fee;
//		}
//
//		public void setTotal_fee(String total_fee) {
//			this.total_fee = total_fee;
//		}
//
//		public String getNotify_url() {
//			return notify_url;
//		}
//
//		public void setNotify_url(String notify_url) {
//			this.notify_url = notify_url;
//		}
//
//		public String getService() {
//			return service;
//		}
//
//		public void setService(String service) {
//			this.service = service;
//		}
//
//		public String getPayment_type() {
//			return payment_type;
//		}
//
//		public void setPayment_type(String payment_type) {
//			this.payment_type = payment_type;
//		}
//
//		public String get_input_charset() {
//			return _input_charset;
//		}
//
//		public void set_input_charset(String _input_charset) {
//			this._input_charset = _input_charset;
//		}
//
//		public String getIt_b_pay() {
//			return it_b_pay;
//		}
//
//		public void setIt_b_pay(String it_b_pay) {
//			this.it_b_pay = it_b_pay;
//		}
//
//		public String getSuccess() {
//			return success;
//		}
//
//		public void setSuccess(String success) {
//			this.success = success;
//		}
//
//		public String getSign_type() {
//			return sign_type;
//		}
//
//		public void setSign_type(String sign_type) {
//			this.sign_type = sign_type;
//		}
//
//		public String getSign() {
//			return sign;
//		}
//
//		public void setSign(String sign) {
//			this.sign = sign;
//		}
//
//	}
//}
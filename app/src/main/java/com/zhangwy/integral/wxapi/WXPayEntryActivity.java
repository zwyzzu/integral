package com.zhangwy.integral.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.zhangwy.user.pay.PayWeiXin;

/**
 * Author: zhangwy
 * 创建时间：2015年12月4日 下午2:58:51
 * 修改时间：2015年12月4日 下午2:58:51
 * Description: 
 **/
public class WXPayEntryActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

	private void handleIntent(Intent intent) {
		PayWeiXin.getInstance().handleIntent(intent);
		this.finish();
	}

}
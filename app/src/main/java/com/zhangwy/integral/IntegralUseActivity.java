package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.zhangwy.integral.data.IDataCode;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.MemberEntity;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.exception.CodeException;
import yixia.lib.core.util.Util;

public class IntegralUseActivity extends BaseActivity {


    private static final String EXTRA_BIND = "extra_bind_id";
    public static void start(Activity activity, String bindId, int requestCode) {
        Intent intent = new Intent(activity, IntegralUseActivity.class);
        intent.putExtra(EXTRA_BIND, bindId);
        activity.startActivityForResult(intent, requestCode);
    }

    private EditText wantUse;
    private String mmbId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_use);
        TextView usable = this.findViewById(R.id.integralUseUsableValue);
        this.wantUse = this.findViewById(R.id.integralUseWantValue);
        this.setToolbar();
        this.mmbId = this.getIntent().getStringExtra(EXTRA_BIND);
        MemberEntity member = IDataManager.getInstance().getMember(this.mmbId);
        usable.setText(Util.float2String(member.getIntegral(), 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_integral_use, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.integralUseSave:
                this.save();
                break;
            case android.R.id.home:
                this.setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.integralUseToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void save() {
        String wantString = this.wantUse.getText().toString();
        if (TextUtils.isEmpty(wantString)) {
            this.showMessage(true, R.string.error_uninput);
            return;
        }
        float want = Float.parseFloat(wantString);
        if (want <= 0) {
            this.showMessage(true, R.string.error_invalid_number);
            return;
        }
        try {
            IDataManager.getInstance().useIntegral(this.mmbId, want);
            setResult(RESULT_OK);
            this.finish();
        } catch (CodeException e) {
            if (e.code == IDataCode.INTEGRAL_INSUFFICIENT) {
                this.showMessage(true, R.string.error_integral_insufficient);
            } else  {
                this.showMessage(true, e.getMessage());
            }
        }
    }
}

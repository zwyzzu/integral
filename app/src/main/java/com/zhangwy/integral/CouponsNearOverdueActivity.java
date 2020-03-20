package com.zhangwy.integral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zhangwy.integral.data.ICouponsManager;

import yixia.lib.core.base.BaseActivity;

public class CouponsNearOverdueActivity extends BaseActivity {

    public static void start(Context context) {
        if (context == null)
            return;
        context.startActivity(new Intent(context, CouponsNearOverdueActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_near_overdue);
        Fragment fragment = FragmentCoupons.newInstance("", ICouponsManager.CouponsMold.NEAROVERDUE);
        this.addFragment(fragment, R.id.couponsNearOverdueContent);
        this.setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.couponsNearOverdueToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.zhangwy.integral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.yixia.widget.VSTabLayout;

import yixia.lib.core.base.BaseActivity;

public class CouponsConfigActivity extends BaseActivity implements VSTabLayout.OnTabClickListener {

    public static void start(Context context) {
        context.startActivity(new Intent(context, CouponsConfigActivity.class));
    }
    private Fragment fragment;
    private FragmentCouponsElement fragmentCoupons;
    private FragmentCouponsExpiryElement fragmentCouponsExpiry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_config);
        this.initTabLayout();
        this.setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.couponsConfigToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coupons_config, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.couponsConfigAdd:
                if (fragment == null) {
                    break;
                } else if (fragment instanceof FragmentCouponsElement) {
                    ((FragmentCouponsElement) fragment).add(null);
                } else if (fragment instanceof FragmentCouponsExpiryElement) {
                    ((FragmentCouponsExpiryElement) fragment).add(null);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTabLayout() {
        VSTabLayout tabLayout = this.findViewById(R.id.couponsConfigTopBar);
        tabLayout.setTabClickListener(this);
        tabLayout.setTabs(R.string.coupons_config_items, R.string.coupons_config_expiry);
    }

    @Override
    public void onClickTab(int position) {
        this.switchFragment(position);
    }

    private void switchFragment(int position) {
        switch (position) {
            case 0:
                if (this.fragmentCoupons == null) {
                    this.fragmentCoupons = FragmentCouponsElement.newInstance();
                }
                fragment = this.fragmentCoupons;
                this.showMessage(false, true, "coupons");
                break;
            case 1:
                if (this.fragmentCouponsExpiry == null) {
                    this.fragmentCouponsExpiry = FragmentCouponsExpiryElement.newInstance();
                }
                fragment = this.fragmentCouponsExpiry;
                this.showMessage(false, true, "couponsExpiry");
                break;
            default:
                if (this.fragmentCoupons == null) {
                    this.fragmentCoupons = FragmentCouponsElement.newInstance();
                }
                fragment = this.fragmentCoupons;
                this.showMessage(false, true, "default");
                break;
        }
        this.addFragment(fragment, R.id.couponsConfigContent);
    }
}

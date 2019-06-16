package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.yixia.widget.VSTabLayout;
import com.zhangwy.integral.data.ICouponsManager;

import yixia.lib.core.base.BaseActivity;

public class CouponsActivity extends BaseActivity implements VSTabLayout.OnTabClickListener, ICouponsManager.OnCouponsDataListener {

    private static final String EXTRA_MEMBERID = "extraMemberId";
    public static void start(Activity activity, String memberId, int requestCode) {
        Intent intent = new Intent(activity, CouponsActivity.class);
        intent.putExtra(EXTRA_MEMBERID, memberId);
        activity.startActivityForResult(intent, requestCode);
    }

    private String memberId;
    private FragmentCoupons fragmentUnused;
    private FragmentCoupons fragmentUsed;
    private FragmentCoupons fragmentOverDue;
    private int lastPosition = 0;
    private int lastUseableSize = -1;
    private int lastUsedSize = -1;
    private int lastOverDueSize = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);
        this.memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
        if (!this.initTabLayout()) {
            this.finish();
            return;
        }
        this.setToolbar();
        ICouponsManager.getInstance().register(this);
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.couponsToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coupons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.couponsAdd:
                CouponsGrantActivity.start(this, this.memberId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean initTabLayout() {
        if (TextUtils.isEmpty(this.memberId)) {
            return false;
        }
        VSTabLayout tabLayout = this.findViewById(R.id.couponsTopBar);
        int cUseableSize = ICouponsManager.getInstance().getUseable(this.memberId).size();
        int cUsedSize = ICouponsManager.getInstance().getUsed(this.memberId).size();
        int cOverDueSize = ICouponsManager.getInstance().getOverDue(this.memberId).size();
        if (cOverDueSize == lastOverDueSize && cUsedSize == lastUsedSize && cUseableSize == lastUseableSize) {
            return true;
        }
        this.lastUseableSize = cUseableSize;
        this.lastUsedSize = cUsedSize;
        this.lastOverDueSize = cOverDueSize;
        String tabUseable = getString(R.string.coupons_unused, this.lastUseableSize);
        String tabUsed = getString(R.string.coupons_used, this.lastUsedSize);
        String tabOverDue = getString(R.string.coupons_overdue, this.lastOverDueSize);
        tabLayout.setStringTabs(tabUseable, tabUsed, tabOverDue);
        tabLayout.setTabClickListener(this);
        tabLayout.setPosition(this.lastPosition);
        return true;
    }

    @Override
    public void onClickTab(int position) {
        this.switchFragment(position);
        this.lastPosition = position;
    }

    private void switchFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                if (this.fragmentUnused == null) {
                    this.fragmentUnused = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.USEABLE);
                }
                fragment = this.fragmentUnused;
                this.showMessage(false, true, "useable");
                break;
            case 1:
                if (this.fragmentUsed == null) {
                    this.fragmentUsed = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.USED);
                }
                fragment = this.fragmentUsed;
                this.showMessage(false, true, "used");
                break;
            case 2:
                if (this.fragmentOverDue == null) {
                    this.fragmentOverDue = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.OVERDUE);
                }
                fragment = this.fragmentOverDue;
                this.showMessage(false, true, "overdue");
                break;
            default:
                if (this.fragmentUnused == null) {
                    this.fragmentUnused = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.USEABLE);
                }
                fragment = this.fragmentUnused;
                this.showMessage(false, true, "default");
                break;
        }
        this.addFragment(fragment, R.id.couponsContent);
    }

    @Override
    public void onLoadCouponsSuccess() {
        this.initTabLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ICouponsManager.getInstance().unRegister(this);
    }
}

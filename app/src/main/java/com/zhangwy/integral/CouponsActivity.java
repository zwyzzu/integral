package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.yixia.widget.VSTabLayout;
import com.zhangwy.integral.data.ICouponsManager;
import com.zhangwy.integral.entity.CouponsBindEntity;

import java.util.List;

import yixia.lib.core.base.BaseActivity;

public class CouponsActivity extends BaseActivity implements VSTabLayout.OnTabClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);
        this.memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
        if (!this.initTabLayout()) {
            this.finish();
        }
    }

    private boolean initTabLayout() {
        if (TextUtils.isEmpty(this.memberId)) {
            return false;
        }
        VSTabLayout tabLayout = this.findViewById(R.id.couponsTopBar);
        tabLayout.setTabClickListener(this);
        List<CouponsBindEntity> useable = ICouponsManager.getInstance().getUseable(this.memberId);
        List<CouponsBindEntity> used = ICouponsManager.getInstance().getUsed(this.memberId);
        List<CouponsBindEntity> overDue = ICouponsManager.getInstance().getOverDue(this.memberId);
        String tabUseable = getString(R.string.coupons_unused, useable.size());
        String tabUsed = getString(R.string.coupons_used, used.size());
        String tabOverDue = getString(R.string.coupons_overdue, overDue.size());
        tabLayout.setStringTabs(tabUseable, tabUsed, tabOverDue);
        return true;
    }

    @Override
    public void onClickTab(int position) {
        this.switchFragment(position);
    }

    private void switchFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                if (this.fragmentUnused == null) {
                    this.fragmentUnused = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.USEABLE);
                }
                fragment = this.fragmentUnused;
                this.showMessage(false, true, "main");
                break;
            case 1:
                if (this.fragmentUsed == null) {
                    this.fragmentUsed = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.USED);
                }
                fragment = this.fragmentUsed;
                this.showMessage(false, true, "add");
                break;
            case 2:
                if (this.fragmentOverDue == null) {
                    this.fragmentOverDue = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.OVERDUE);
                }
                fragment = this.fragmentOverDue;
                this.showMessage(false, true, "mine");
                break;
            default:
                if (this.fragmentUnused == null) {
                    this.fragmentUnused = FragmentCoupons.newInstance(this.memberId, ICouponsManager.CouponsMold.USEABLE);
                }
                fragment = this.fragmentUnused;
                this.showMessage(false, true, "default");
                break;
        }
        this.addFragment(fragment, R.id.mainContent);
    }
}

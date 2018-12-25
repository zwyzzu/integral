package com.zhangwy.integral;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.RadioGroup;

import yixia.lib.core.base.BaseActivity;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private FragmentMain fragmentMain;
    private FragmentAdd fragmentAdd;
    private FragmentMine fragmentMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((RadioGroup) this.findViewById(R.id.mainBottomBar)).setOnCheckedChangeListener(this);
        this.switchFragment(R.id.mainBottomBarMain);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        this.switchFragment(checkedId);
    }

    private void switchFragment(int checkedId) {
        Fragment fragment;
        switch (checkedId) {
            case R.id.mainBottomBarMain:
                if (this.fragmentMain == null) {
                    this.fragmentMain = FragmentMain.newInstance();
                }
                fragment = this.fragmentMain;
                this.showMessage(false, true, "main");
                break;
            case R.id.mainBottomBarAdd:
                if (this.fragmentAdd == null) {
                    this.fragmentAdd = FragmentAdd.newInstance();
                }
                fragment = this.fragmentAdd;
                this.showMessage(false, true, "add");
                break;
            case R.id.mainBottomBarMine:
                if (this.fragmentMine == null) {
                    this.fragmentMine = FragmentMine.newInstance();
                }
                fragment = this.fragmentMine;
                this.showMessage(false, true, "mine");
                break;
            default:
                if (this.fragmentMain == null) {
                    this.fragmentMain = FragmentMain.newInstance();
                }
                fragment = this.fragmentMain;
                this.showMessage(false, true, "default");
                break;
        }
        this.addFragment(fragment, R.id.mainContent);
    }
}

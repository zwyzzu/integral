package com.zhangwy.integral;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.zhangwy.upgrade.Upgrade;

import java.util.HashMap;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Screen;
import yixia.lib.core.util.Util;
import yixia.lib.core.util.VSPermission;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, OnRequestPermission {

    private FragmentMain fragmentMain;
    private FragmentAdd fragmentAdd;
    private FragmentMine fragmentMine;
    private final String PRFKEY_SHOW_DISCLAIMER = "showDisclaimer";
    private boolean hasUpgraded = false;
    private final int REQUESTCODE_PERMISSION = 100;
    private VSPermission permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((RadioGroup) this.findViewById(R.id.mainBottomBar)).setOnCheckedChangeListener(this);
        this.switchFragment(R.id.mainBottomBarMain);
        this.showDisclaimer();
        this.checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.hasUpgraded) {
            this.hasUpgraded = true;
            Upgrade upgrade = Upgrade.newInstance(this, true);
            upgrade.check(false, false, true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        this.switchFragment(checkedId);
    }

    private void checkPermission() {
        HashMap<String, String> permissions = new HashMap<>();
        permissions.put(Manifest.permission.READ_PHONE_STATE, getString(R.string.permission_read_phone_state));
        permissions.put(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_external_storage));
        permissions.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.permission_write_external_storage));
        this.permission = VSPermission.newInstance(this, permissions);
        this.permission.requestPermission(this.REQUESTCODE_PERMISSION);
    }

    @Override
    public void requestPermission(String permission, String message) {
        if (this.permission == null) {
            this.permission = VSPermission.newInstance(this);
        }
        this.permission.requestPermission(this.REQUESTCODE_PERMISSION, permission, message);
    }

    @Override
    public void requestPermission(HashMap<String, String> permissions) {
        if (this.permission == null) {
            this.permission = VSPermission.newInstance(this);
        }
        this.permission.requestPermissions(this.REQUESTCODE_PERMISSION, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.permission != null) {
            this.permission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Util.REQUEST_CODE_APP_INSTALL && resultCode == RESULT_OK) {
            Upgrade upgrade = Upgrade.newInstance(this, true);
            upgrade.check(false, false, true);
        } else if (requestCode == REQUESTCODE_PERMISSION) {
            //TODO
        }
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

    private void showDisclaimer() {
        if (PreferencesHelper.defaultInstance().getBoolean(PRFKEY_SHOW_DISCLAIMER, false)) {
            return;
        }
        Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.show();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.dialog_disclaimer, null);
        int width = Screen.getScreenWidth(this) * 4 / 5;
        int height = Screen.getScreenHeight(this) * 4 / 5;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        dialog.setContentView(viewDialog, params);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final CheckBox checkBox = viewDialog.findViewById(R.id.disclaimerCheckBox);
        viewDialog.findViewById(R.id.disclaimerAgreed).setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                PreferencesHelper.defaultInstance().applyBoolean(PRFKEY_SHOW_DISCLAIMER, true);
                dialog.dismiss();
            } else {
                showMessage(true, R.string.disclaimer_unCheck_remind);
            }
        });
        viewDialog.findViewById(R.id.disclaimerUnAgreed).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
    }
}

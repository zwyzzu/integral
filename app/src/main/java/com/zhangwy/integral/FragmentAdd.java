package com.zhangwy.integral;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.bean.DateType;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yixia.widget.VSVerificationLayout;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.TimeUtil;

/**
 * Created by zhangwy on 2018/12/21 下午8:12.
 * Updated by zhangwy on 2018/12/21 下午8:12.
 * Description
 */
public class FragmentAdd extends BaseFragment implements View.OnClickListener {

    public static FragmentAdd newInstance() {
        return new FragmentAdd();
    }

    private SimpleDraweeView avatar;
    private VSVerificationLayout addName;
    private EditText addNameInput;
    private VSVerificationLayout addPhone;
    private EditText addPhoneInput;
    private Spinner addSex;
    private VSVerificationLayout addAge;
    private EditText addAgeInput;
    private VSVerificationLayout addBirthday;
    private TextView addBirthdayInput;
    private Spinner addMarital;
    private EditText childrenSon;
    private EditText childrenDaughter;
    private EditText addMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        showMessage(false, true, "add.Init");
        this.initToolbar();
        this.avatar = view.findViewById(R.id.addIcon);
        this.addName = view.findViewById(R.id.addName);
        this.addNameInput = view.findViewById(R.id.addNameInput);
        this.addPhone = view.findViewById(R.id.addPhone);
        this.addPhoneInput = view.findViewById(R.id.addPhoneInput);
        this.addSex = view.findViewById(R.id.addSexSpinner);
        this.addAge = view.findViewById(R.id.addAge);
        this.addAgeInput = view.findViewById(R.id.addAgeInput);
        this.addBirthday = view.findViewById(R.id.addBirthday);
        this.addBirthdayInput = view.findViewById(R.id.addBirthdayInput);
        this.addMarital = view.findViewById(R.id.addMaritalSpinner);
        this.childrenSon = view.findViewById(R.id.addChildrenSonCount);
        this.childrenDaughter = view.findViewById(R.id.addChildrenDaughterCount);

        this.avatar.setOnClickListener(this);
        this.addBirthdayInput.setOnClickListener(this);
        view.findViewById(R.id.addButtonClear).setOnClickListener(this);
        view.findViewById(R.id.addButtonSave).setOnClickListener(this);
    }

    private void initToolbar() {
        this.addToolbar(R.id.addToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addIcon:
                break;
            case R.id.addBirthdayInput:
                this.showTime();
                break;
            case R.id.addButtonClear:
                break;
            case R.id.addButtonSave:
                break;
        }
    }

    private void showTime(){
        DatePickDialog dialog = new DatePickDialog(getContext());
        //设置上下年分限制
        dialog.setYearLimt(5);
        //设置标题
        dialog.setTitle("选择日期");
        //设置类型
        dialog.setType(DateType.TYPE_YMD);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat(TimeUtil.PATTERN_DAY2Y);
        //设置选择回调
        dialog.setOnChangeListener(null);
        //设置点击确定按钮回调
        dialog.setOnSureListener(null);
        dialog.show();
    }
}

package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.bean.DateType;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.MemberEntity;

import java.util.Date;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/12/21 下午8:12.
 * Updated by zhangwy on 2018/12/21 下午8:12.
 * Description
 */
public class FragmentAdd extends BaseFragment implements View.OnClickListener {

    public static FragmentAdd newInstance() {
        return new FragmentAdd();
    }

    private static final int REQUESTCODE_SELECT_IMAGE = 1000;

    private SimpleDraweeView avatar;
    private EditText addNameInput;
    private EditText addPhoneInput;
    private Spinner addSex;
    private EditText addAgeInput;
    private TextView addBirthdayInput;
    private Spinner addMarital;
    private EditText childrenSon;
    private EditText childrenDaughter;
    private EditText addMessage;
    private Date lastBirthday;
    private Uri avatarUri;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        showMessage(false, true, "add.Init");
        this.initToolbar();
        this.avatar = view.findViewById(R.id.addIcon);
        this.addNameInput = view.findViewById(R.id.addNameInput);
        this.addPhoneInput = view.findViewById(R.id.addPhoneInput);
        this.addSex = view.findViewById(R.id.addSexSpinner);
        this.addAgeInput = view.findViewById(R.id.addAgeInput);
        this.addBirthdayInput = view.findViewById(R.id.addBirthdayInput);
        this.addMarital = view.findViewById(R.id.addMaritalSpinner);
        this.childrenSon = view.findViewById(R.id.addChildrenSonCount);
        this.childrenDaughter = view.findViewById(R.id.addChildrenDaughterCount);
        this.addMessage = view.findViewById(R.id.addMessage);

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
                SelectImageActivity.start(this, true, REQUESTCODE_SELECT_IMAGE);
                break;
            case R.id.addBirthdayInput:
                this.showTime();
                break;
            case R.id.addButtonClear:
                this.clearInput();
                break;
            case R.id.addButtonSave:
                saveData();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_SELECT_IMAGE: {
                if (resultCode == Activity.RESULT_OK) {
                    this.avatarUri = data.getData();
                    this.avatar.setImageURI(this.avatarUri);
                }
                break;
            }
        }
    }

    private void showTime(){
        DatePickDialog dialog = new DatePickDialog(getContext());
        //设置标题
        dialog.setTitle("选择日期");
        //设置类型
        dialog.setType(DateType.TYPE_YMD);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat(TimeUtil.PATTERN_DAY2Y);
        //设置点击确定按钮回调
        dialog.setOnSureListener(date -> {
            Logger.d("setOnSureListener", TimeUtil.date2String(date, TimeUtil.PATTERN_DATE));
            addBirthdayInput.setText(TimeUtil.date2String(date, TimeUtil.PATTERN_DAY4Y));
            completionAge(date);
            lastBirthday = date;
        });
        int yearPast = 200;
        int yearFuture = 0;
        if (this.lastBirthday != null) {
            try {
                yearFuture = TimeUtil.birthDay2Age(this.lastBirthday) + 1;
            } catch (Exception e) {
                Logger.d("", e);
            }
            dialog.setStartDate(this.lastBirthday);
        }
        //设置上下年分限制
        dialog.setYearLimit(yearPast, yearFuture);
        dialog.show();
    }

    private void completionAge(Date date) {
        try {
            int year = TimeUtil.birthDay2Age(date);
            if (year < 0) {
                return;
            }
            this.addAgeInput.getText().clear();
            this.addAgeInput.getText().append(String.valueOf(year));
        } catch (Exception e) {
            Logger.d("setAge", e);
        }
    }

    private void clearInput() {
        this.avatarUri = null;
        this.avatar.setImageResource(R.mipmap.icon_avatar);
        this.addNameInput.getText().clear();
        this.addPhoneInput.getText().clear();
        this.addSex.setSelection(0, true);
        this.addBirthdayInput.setText("");
        this.addAgeInput.getText().clear();
        this.addMarital.setSelection(0, true);
        this.childrenSon.getText().clear();
        this.childrenDaughter.getText().clear();
        this.addMessage.getText().clear();
    }

    private void saveData() {
        try {
            MemberEntity entity = new MemberEntity();
            if (this.avatarUri != null) {
                entity.setIcon(this.avatarUri.getPath());
            }
            String name = this.addNameInput.getText().toString();
            if (TextUtils.isEmpty(name)) {
                throw new Exception("用户名为空");
            }
            entity.setName(name);
            String phone = this.addPhoneInput.getText().toString();
            if (!TextUtils.isEmpty(phone) && !Util.isMobile(phone) && !Util.isPhone(phone)) {
                throw new Exception("电话号码不正确");
            }
            entity.setPhone(phone);
            entity.setSex(this.addSex.getSelectedItemPosition());
            if (this.lastBirthday != null) {
                entity.setBirthday(TimeUtil.date2String(this.lastBirthday, TimeUtil.PATTERN_DAY4Y));
            }
            String ageString = this.addAgeInput.getText().toString();
            if (!TextUtils.isEmpty(ageString)) {
                int age = Integer.parseInt(ageString);
                entity.setAge(age);
            }
            entity.setDesc(this.addMessage.getText().toString());
            entity.setMarital(this.addMarital.getSelectedItemPosition());
            entity.setSonCount(this.getCount(this.childrenSon));
            entity.setDaughterCount(this.getCount(this.childrenDaughter));
            IDataManager manager = IDataManager.getInstance();
            manager.addMember(entity);
            this.clearInput();
            MemberActivity.start(this, entity.getId(), 0);
        } catch (NumberFormatException e) {
            this.showMessage(true, "用户年龄格式不对");
        } catch (Exception e) {
            this.showMessage(true, e.getMessage());
        }
    }

    private int getCount(EditText text) {
        if (text == null) {
            return 0;
        }
        String string = text.getText().toString();
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

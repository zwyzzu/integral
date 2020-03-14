package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.yixia.widget.VSVerificationLayout;
import com.yixia.widget.flowlayout.FlowLayout;
import com.zhangwy.address.AreaPickerView;
import com.zhangwy.address.entities.AddressEntity;
import com.zhangwy.integral.data.IAddressTags;
import com.zhangwy.integral.data.IDataManager;

import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.Util;

public class AddressActivity extends BaseActivity {

    private static final String EXTRA_BIND = "extra_bind_id";
    public static void start(Activity activity, String bindId, int requestCode) {
        Intent intent = new Intent(activity, AddressActivity.class);
        intent.putExtra(EXTRA_BIND, bindId);
        activity.startActivityForResult(intent, requestCode);
    }

    private EditText consignee;
    private EditText phone;
    private VSVerificationLayout areaLayout;
    private TextView area;
    private EditText detailed;
    private FlowLayout<String> flowLayout;
    private TextView tagAdd;
    private View tagAddLayout;
    private EditText tagAddInput;
    private TextView tagAddOk;
    private List<AddressEntity> addresses = new ArrayList<>();
    private String bindId;
    private String tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        this.consignee = this.findViewById(R.id.addressConsignee);
        this.phone = this.findViewById(R.id.addressPhone);
        this.areaLayout = this.findViewById(R.id.addressAreaHome);
        this.area = this.findViewById(R.id.addressArea);
        this.detailed = this.findViewById(R.id.addressDetailed);
        this.flowLayout = this.findViewById(R.id.addressTags);
        this.tagAdd = this.findViewById(R.id.addressTagAddTitle);
        this.tagAddLayout = this.findViewById(R.id.addressTagAddLayout);
        this.tagAddInput = this.findViewById(R.id.addressTagAddInput);
        this.tagAddOk = this.findViewById(R.id.addressTagAddOk);
        this.initArea();
        this.setToolbar();
        this.initTagAdd();
        this.consignee.requestFocus();
        this.bindId = getIntent().getStringExtra(EXTRA_BIND);
        this.initTags();
    }

    private void initArea() {
        this.area.setOnClickListener(v -> {
            this.areaLayout.hideHint();
            AreaPickerView areaPickerView = new AreaPickerView(this, com.zhangwy.address.R.style.Dialog);
            areaPickerView.setCallback(value -> {
                this.addresses.clear();
                StringBuilder buffer = new StringBuilder();
                for (AddressEntity address : value) {
                    if (address == null) {
                        break;
                    }
                    if (buffer.length() != 0) {
                        buffer.append("  ");
                    }
                    if (TextUtils.isEmpty(address.getCode())) {
                        address.setName("");
                    }
                    addresses.add(address);
                    buffer.append(address.getName());
                }
                this.area.setText(buffer.toString());
                this.area.setSelected(true);
            });
            areaPickerView.show(this.addresses.toArray(new AddressEntity[addresses.size()]));
        });
    }

    private void initTags() {
        List<String> tags = IAddressTags.getInstance().getTags();
        this.flowLayout.loadData(tags, new FlowLayout.OnItemLoading<String>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return getLayoutInflater().inflate(R.layout.view_item_tag, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, String entity, int position) {
                if (!TextUtils.isEmpty(entity)) {
                    if (TextUtils.equals(entity, tag)) {
                        root.setBackgroundResource(R.drawable.background_address_tags_checked);
                    } else {
                        root.setBackgroundResource(R.drawable.background_address_tags);
                    }
                    TextView title = root.findViewById(R.id.tagTitle);
                    title.setText(entity);
                }
            }
        });
        this.flowLayout.setOnItemListener((view, viewType, entity, position) -> {
            this.tag = entity;
            this.tagAdd.setBackgroundResource(R.drawable.background_address_tags);
            this.flowLayout.refresh();
        });
    }

    private void initTagAdd() {
        this.tagAdd.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            tagAddLayout.setVisibility(View.VISIBLE);
        });
        this.tagAddInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tagAddOk.setBackgroundResource(R.drawable.background_address_tags_add_save_unclick);
                } else {
                    tagAddOk.setBackgroundResource(R.drawable.background_address_tags_add_save);
                }
            }
        });
        this.tagAddOk.setOnClickListener(v -> {
            String tag = tagAddInput.getText().toString();
            if (TextUtils.isEmpty(tag)) {
                return;
            }
            tagAdd.setVisibility(View.VISIBLE);
            tagAddLayout.setVisibility(View.GONE);
            tagAdd.setBackgroundResource(R.drawable.background_address_tags_checked);
            flowLayout.refresh();
            tagAdd.setText(tag);
            tagAdd.setOnClickListener(v1 -> {
                tagAdd.setBackgroundResource(R.drawable.background_address_tags_checked);
                this.tag = this.tagAdd.getText().toString();
                this.flowLayout.refresh();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addressSave:
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
        Toolbar toolbar = this.findViewById(R.id.addressToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void save() {
        try {
            com.zhangwy.integral.entity.AddressEntity address = new com.zhangwy.integral.entity.AddressEntity();
            String consignee = this.consignee.getText().toString();
            address.setBind(this.bindId);
            if (TextUtils.isEmpty(consignee)) {
                throw new Exception("收件人不得为空");
            }
            address.setConsignee(consignee);
            String phone = this.phone.getText().toString();
            if (!Util.isMobile(phone) && !Util.isPhone(phone)) {
                throw new Exception("电话号码不正确");
            }
            address.setPhone(phone);
            if (Util.isEmpty(this.addresses)) {
                throw new Exception("您未选择所在区域");
            }
            AddressEntity addressEntity;
            if (this.addresses.size() > 0 && (addressEntity = addresses.get(0)) != null) {
                address.setProvince(addressEntity.getName());
            }
            if (this.addresses.size() > 1 && (addressEntity = addresses.get(1)) != null) {
                address.setCity(addressEntity.getName());
            }
            if (this.addresses.size() > 2 && (addressEntity = addresses.get(2)) != null) {
                address.setArea(addressEntity.getName());
            }
            if (this.addresses.size() > 3 && (addressEntity = addresses.get(3)) != null) {
                address.setTown(addressEntity.getName());
            }
            String detailed = this.detailed.getText().toString();
            if (TextUtils.isEmpty(detailed)) {
                throw new Exception("您未输入详细地址");
            }
            if (TextUtils.isEmpty(this.tag)) {
                throw new Exception("请选择标签");
            }
            address.setTag(this.tag);
            address.setAddress(detailed);
            TextView desc = this.findViewById(R.id.addressDesc);
            address.setDesc(desc.getText().toString());
            IDataManager manager = IDataManager.getInstance();
            manager.addAddress(address);
            this.setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            this.showMessage(true, e.getMessage());
        }
    }
}

package com.zhangwy.integral;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yixia.widget.VSVerificationLayout;
import com.yixia.widget.VSVerificationLayout.Command;
import com.yixia.widget.adapter.BaseAdapter;
import com.yixia.widget.flowlayout.FlowLayout;
import com.zhangwy.integral.data.ICouponsManager;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.data.ITagManager;
import com.zhangwy.integral.entity.CouponsBindEntity;
import com.zhangwy.integral.entity.CouponsEntity;
import com.zhangwy.integral.entity.CouponsExpiryEntity;
import com.zhangwy.integral.entity.MemberEntity;

import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.KeyboardUtils;
import yixia.lib.core.util.Util;

public class CouponsGrantActivity extends BaseActivity {

    private static final String EXTRA_MEMBERID = "extraMemberId";

    public static void start(Context context, String memberId) {
        Intent intent = new Intent(context, CouponsGrantActivity.class);
        intent.putExtra(EXTRA_MEMBERID, memberId);
        context.startActivity(intent);
    }

    private TextView donee;
    private Spinner coupons;
    private CheckBox hasCoefficient;
    private VSVerificationLayout coefficientHome;
    private Spinner expiry;
    private EditText message;
    private FlowLayout<String> flowLayout;
    private TextView emptyElement;
    private String tag;
    private String memberId;
    private List<CouponsEntity> couponsEntities;
    private List<CouponsExpiryEntity> expiryEntities;
    private MemberEntity member;
    private boolean emptyCoupons = false;
    private boolean emptyExpiry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_grant);
        this.donee = this.findViewById(R.id.couponsGrantDonee);
        this.coupons = this.findViewById(R.id.couponsGrantCoupons);
        this.hasCoefficient = this.findViewById(R.id.couponsGrantCouponsHasCoefficient);
        this.coefficientHome = this.findViewById(R.id.couponsGrantCouponsCoefficientHome);
        this.expiry = this.findViewById(R.id.couponsGrantExpiry);
        this.message = this.findViewById(R.id.couponsGrantMessage);
        this.flowLayout = this.findViewById(R.id.couponsGrantTag);
        this.emptyElement = this.findViewById(R.id.couponsGrantEmpty);
        this.memberId = this.getIntent().getStringExtra(EXTRA_MEMBERID);
        this.setDonee();
        this.initCoefficient();
        this.initCoupons();
        this.initExpiry();
        this.initTags();
        this.showEmptyRemind();
        this.setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.couponsGrantToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!this.emptyCoupons && !this.emptyExpiry) {
            getMenuInflater().inflate(R.menu.menu_coupons_grant, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.couponsGrantSave:
                if (this.emptyCoupons || this.emptyExpiry) {
                    break;
                }
                this.save();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDonee() {
        member = IDataManager.getInstance().getMember(this.memberId);
        this.donee.setText(this.member.getName());
    }

    private void initCoefficient() {
        this.hasCoefficient.setOnCheckedChangeListener((buttonView, isChecked) -> coefficientHome.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE));
    }

    private void initCoupons() {
        this.couponsEntities = IDataManager.getInstance().getCoupons();
        this.emptyCoupons = Util.isEmpty(this.couponsEntities);
        BaseAdapter<CouponsEntity> adapter = new BaseAdapter<>(this.couponsEntities, (parent, convertView, entity) -> {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_item_coupons_spinner, parent, false);
            }
            TextView first = convertView.findViewById(R.id.couponsSpinnerFirst);
            TextView second = convertView.findViewById(R.id.couponsSpinnerSecond);
            first.setText(entity.getName());
            second.setText(getString(R.string.coupons_grant_coupons_spinner_second, Util.float2String(entity.getAmount(), 2), entity.getDesc()));
            return convertView;
        });
        this.coupons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CouponsEntity entity = adapter.getItem(position);
                if (entity != null) {
                    hasCoefficient.setChecked(entity.isCheckCoefficient());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.coupons.setAdapter(adapter);
    }

    private void initExpiry() {
        this.expiryEntities = IDataManager.getInstance().getExpiries();
        this.emptyExpiry = Util.isEmpty(this.expiryEntities);
        BaseAdapter<CouponsExpiryEntity> adapter = new BaseAdapter<>(this.expiryEntities, (parent, convertView, entity) -> {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_item_expiry_spinner, parent, false);
            }
            TextView first = convertView.findViewById(R.id.expirySpinnerFirst);
            if (entity.getExpiry() == CouponsExpiryEntity.Expiry.FOREVER) {
                first.setText(entity.getExpiry().res);
            } else {
                String text = entity.getCount() + "\t" + getString(entity.getExpiry().res);
                first.setText(text);
            }
            return convertView;
        });
        this.expiry.setAdapter(adapter);
    }

    private void initTags() {
        List<String> tags = ITagManager.create(this, ITagManager.KEY_TAG_COUPONS).get();
        tags.add(0, "");
        this.flowLayout.loadData(tags, new FlowLayout.OnItemLoading<String>() {

            @Override
            public int getItemViewType(String entity, int position) {
                if (TextUtils.isEmpty(entity)) {
                    return 1;
                }
                return super.getItemViewType(entity, position);
            }

            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case 0:
                        return getLayoutInflater().inflate(R.layout.view_item_tag, parent, false);
                    case 1:
                        return getLayoutInflater().inflate(R.layout.view_item_tag_add, parent, false);
                }
                return getLayoutInflater().inflate(R.layout.view_item_tag, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, String entity, int position) {
                if (viewType == 1) {
                    loadAddTag(root);
                } else {
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
            this.flowLayout.refresh();
        });
    }

    private void loadAddTag(View root) {
        View tagAddImage = root.findViewById(R.id.tagAddButton);
        View tagAddLayout = root.findViewById(R.id.tagAddLayout);
        EditText tagAddInput = root.findViewById(R.id.tagAddInput);
        View tagAddOk = root.findViewById(R.id.tagAddOk);

        tagAddImage.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            tagAddInput.requestFocus();
            tagAddLayout.setVisibility(View.VISIBLE);
        });
        tagAddInput.addTextChangedListener(new TextWatcher() {
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
        tagAddOk.setOnClickListener(v -> {
            String tag = tagAddInput.getText().toString();
            if (TextUtils.isEmpty(tag)) {
                return;
            }
            tagAddImage.setVisibility(View.VISIBLE);
            tagAddLayout.setVisibility(View.GONE);
            this.tag = tag;
            flowLayout.add(tag, 1);
        });
    }

    private void save() {
        CouponsBindEntity bindEntity = new CouponsBindEntity();
        bindEntity.setBindName(this.member.getName());
        bindEntity.setBind(this.memberId);
        bindEntity.setCreateDate(System.currentTimeMillis());
        final float[] coefficient = {1.0f};
        if (this.hasCoefficient.isChecked()) {
            boolean verify = coefficientHome.verify(new Command() {
                boolean empty = false;

                @Override
                public String hint() {
                    if (empty) {
                        return getString(R.string.hint_empty);
                    }
                    return getString(R.string.hint_nonnegative);
                }

                @Override
                public boolean verify(String text) {
                    if (TextUtils.isEmpty(text)) {
                        empty = true;
                        return true;
                    }
                    float cfc = Float.parseFloat(text);
                    if (cfc > 0) {
                        coefficient[0] = cfc;
                        return false;
                    }
                    return true;
                }
            });
            if (verify) {
                return;
            }
        }
        CouponsEntity couponsEntity = this.couponsEntities.get(this.coupons.getSelectedItemPosition());
        if (couponsEntity == null) {
            showMessage(true, R.string.error_coupons_unselect);
            return;
        }
        bindEntity.setCoupons(couponsEntity, coefficient[0]);
        CouponsExpiryEntity expiryEntity = this.expiryEntities.get(this.expiry.getSelectedItemPosition());
        if (expiryEntity == null) {
            showMessage(true, R.string.error_expiry_unselect);
            return;
        }
        bindEntity.setExpiry(expiryEntity);
        String messageText = this.message.getText().toString();
        if (!TextUtils.isEmpty(messageText)) {
            bindEntity.setDesc(messageText);
        }
        if (!TextUtils.isEmpty(this.tag)) {
            bindEntity.setTag(this.tag);
            ITagManager.create(this, ITagManager.KEY_TAG_COUPONS).put(this.tag);
        }

        IDataManager.getInstance().addMemberCoupons(bindEntity);
        ICouponsManager.getInstance().addCoupons(bindEntity);
        this.finish();
    }

    private void showEmptyRemind() {
        boolean show = !this.emptyCoupons && !this.emptyExpiry;
        this.findViewById(R.id.couponsGrantDoneeHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.couponsGrantCouponsHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.couponsGrantExpiryHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.couponsGrantMessageHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.couponsGrantTagHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.couponsGrantCouponsHasCoefficientText).setVisibility(show ? View.VISIBLE : View.GONE);
        this.donee.setVisibility(show ? View.VISIBLE : View.GONE);
        this.coupons.setVisibility(show ? View.VISIBLE : View.GONE);
        this.coefficientHome.setVisibility(show ? View.VISIBLE : View.GONE);
        this.hasCoefficient.setVisibility(show ? View.VISIBLE : View.GONE);
        this.expiry.setVisibility(show ? View.VISIBLE : View.GONE);
        this.message.setVisibility(show ? View.VISIBLE : View.GONE);
        this.flowLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        this.emptyElement.setVisibility(!show ? View.VISIBLE : View.GONE);
        if (!show) {
            List<String> empty = new ArrayList<>();
            if (this.emptyCoupons) {
                empty.add(getString(R.string.coupons_grant_coupons));
            }
            if (this.emptyExpiry) {
                empty.add(getString(R.string.coupons_grant_expiry));
            }
            String emptyString = Util.array2ArrayString(',', empty);
            this.emptyElement.setText(getString(R.string.coupons_grant_empty_element, emptyString));
        }
    }
}
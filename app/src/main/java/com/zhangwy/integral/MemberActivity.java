package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.address.AddressActivity;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.IntegralBindEntity;
import com.zhangwy.integral.entity.MemberEntity;
import com.zhangwy.integral.entity.MemberItemEntity;

import java.io.File;
import java.util.List;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.Util;

@SuppressWarnings("unused")
public class MemberActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_INTEGRAL_LIST = 101;
    private static final int REQUEST_CODE_ADDRESS_LIST = 102;
    private static final String EXTRA_MEMBERID = "extraMemberId";

    public static void start(Activity activity, String memberId, int requestCode) {
        Intent intent = new Intent(activity, MemberActivity.class);
        intent.putExtra(EXTRA_MEMBERID, memberId);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Fragment fragment, String memberId, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), MemberActivity.class);
        intent.putExtra(EXTRA_MEMBERID, memberId);
        fragment.startActivityForResult(intent, requestCode);
    }

    private SimpleDraweeView icon;
    private TextView title;
    private TextView age;
    private TextView sex;
    private TextView integral;
    private TextView marital;
    private TextView children;
    private ImageView msgIcon;
    private TextView message;
    private VSRecyclerView<MemberItemEntity> recyclerView;
    private String mmbId;
    private MemberEntity member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        this.icon = this.findViewById(R.id.memberIcon);
        this.title = this.findViewById(R.id.memberTitle);
        this.age = this.findViewById(R.id.memberAge);
        this.sex = this.findViewById(R.id.memberSex);
        this.integral = this.findViewById(R.id.memberIntegral);
        this.marital = this.findViewById(R.id.memberMarital);
        this.children = this.findViewById(R.id.memberChildren);
        this.msgIcon = this.findViewById(R.id.memberMessageIcon);
        this.message = this.findViewById(R.id.memberMessage);
        this.recyclerView = this.findViewById(R.id.memberList);
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.mmbId = this.getIntent().getStringExtra(EXTRA_MEMBERID);
        this.refreshData();
        this.refreshRecycler();
        this.setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.memberToolbar);
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

    private void refreshData() {
        this.member = IDataManager.getInstance().getMember(mmbId);
        this.icon.setImageURI(Uri.fromFile(new File(member.getIcon())));
        this.title.setText(member.getName());
        this.age.setText(getString(R.string.member_age, member.getAge()));
        this.sex.setText(getString(R.string.member_sex, getString(member.findSex().res)));
        this.integral.setText(getString(R.string.member_integral, member.getIntegral()));
        this.marital.setText(getString(R.string.member_marital, getString(member.findMarital().res)));
        String children = getString(R.string.member_children, member.getSonCount(), member.getDaughterCount());
        this.children.setText(children);
        this.msgIcon.setImageResource(R.mipmap.icon_arrow_down);
        this.message.setText(member.getDesc());
    }

    private void refreshRecycler() {
        this.member = IDataManager.getInstance().getMember(mmbId);
        List<MemberItemEntity> entities = MemberItemEntity.createMembers(this.member);
        if (Util.isEmpty(entities)) {
            return;
        }
        this.recyclerView.loadData(entities, new RecyclerAdapter.OnItemLoading<MemberItemEntity>() {

            @Override
            public int getItemViewType(MemberItemEntity entity, int position) {
                return entity.viewType;
            }

            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                int layout = -1;
                switch (viewType) {
                    case MemberItemEntity.TYPE_INTEGRAL_HEAD:
                        layout = R.layout.view_item_member_title;
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL:
                        layout = R.layout.view_item_member_integral;
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL_MORE:
                        layout = R.layout.view_item_member_more;
                        break;
                    case MemberItemEntity.TYPE_ADDRESS_HEAD:
                        layout = R.layout.view_item_member_title;
                        break;
                    case MemberItemEntity.TYPE_ADDRESS:
                        layout = R.layout.view_item_member_address;
                        break;
                    case MemberItemEntity.TYPE_ADDRESS_MORE:
                        layout = R.layout.view_item_member_more;
                        break;
                }
                return LayoutInflater.from(MemberActivity.this).inflate(layout, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, MemberItemEntity entity, int position) {
                switch (viewType) {
                    case MemberItemEntity.TYPE_INTEGRAL_HEAD:
                        refreshHead(root, true);
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL:
                        refreshIntegral(root, (IntegralBindEntity) entity.data);
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL_MORE:
                        refreshMore(root, true);
                        break;
                    case MemberItemEntity.TYPE_ADDRESS_HEAD:
                        refreshHead(root, false);
                        break;
                    case MemberItemEntity.TYPE_ADDRESS:
                        refreshAddress(root, (AddressEntity) entity.data);
                        break;
                    case MemberItemEntity.TYPE_ADDRESS_MORE:
                        refreshMore(root, false);
                        break;
                }
            }
        });
    }

    private void refreshHead(View root, boolean integral) {
        TextView title = root.findViewById(R.id.memberTitleText);
        ImageView add = root.findViewById(R.id.memberTitleAdd);
        if (integral) {
            title.setText(R.string.member_list_integral);
        } else {
            title.setText(R.string.member_list_address);
        }
        add.setOnClickListener(v -> {
            if (integral) {
                IntegralAddActivity.start(MemberActivity.this, mmbId, REQUEST_CODE_ADD);
            } else {
                AddressActivity addressActivity = new AddressActivity(this);
            }
        });
    }

    private void refreshIntegral(View root, IntegralBindEntity entity) {
        TextView total = root.findViewById(R.id.memberIntegralTotal);
        TextView used = root.findViewById(R.id.memberIntegralUsed);
        TextView desc = root.findViewById(R.id.memberIntegralDesc);
        String create = TimeUtil.dateMilliSecond2String(entity.getCreateDate(), TimeUtil.PATTERN_DAY4Y);
        total.setText(getString(R.string.member_integral_total, Util.float2String(entity.getScore(), 2), create));
        if (entity.getUsedDate() > entity.getCreateDate()) {
            used.setVisibility(View.VISIBLE);
            String usedTime = TimeUtil.dateMilliSecond2String(entity.getUsedDate(), TimeUtil.PATTERN_DAY4Y);
            used.setText(getString(R.string.member_integral_used, Util.float2String(entity.getUsedScore(), 2), usedTime));
        } else {
            used.setVisibility(View.GONE);
        }
        desc.setText(entity.getDesc());
    }

    private void refreshAddress(View root, AddressEntity address) {
        TextView namePhone = root.findViewById(R.id.memberAddressNamePhone);
        TextView info = root.findViewById(R.id.memberAddressInfo);
        namePhone.setText(getString(R.string.member_address_name_phone, address.getConsignee(), address.getPhone()));
        info.setText(getString(R.string.member_address_info, address.getProvince(),
                address.getCity(), address.getCounty(), address.getDistrict(), address.getAddress()));
    }

    private void refreshMore(View root, boolean integral) {
        root.setOnClickListener(v -> {
            if (integral) {
                IntegralsActivity.start(this, this.mmbId, REQUEST_CODE_INTEGRAL_LIST);
            } else {
                //TODO
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            this.refreshRecycler();
        }
    }
}

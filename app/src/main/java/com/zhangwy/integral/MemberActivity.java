package com.zhangwy.integral;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IBookingManager;
import com.zhangwy.integral.data.ICouponsManager;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.BookingBindEntity;
import com.zhangwy.integral.entity.CouponsBindEntity;
import com.zhangwy.integral.entity.IntegralBindEntity;
import com.zhangwy.integral.entity.MemberEntity;
import com.zhangwy.integral.entity.MemberItemEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.FileUtil;
import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.Util;
import yixia.lib.core.util.WindowUtil;

@SuppressWarnings("unused")
public class MemberActivity extends BaseActivity implements ICouponsManager.OnCouponsDataListener {

    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_INTEGRAL_LIST = 101;
    private static final int REQUEST_CODE_ADDRESS_LIST = 102;
    private static final int REQUEST_CODE_COUPONS_LIST = 103;
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
        this.initRecycler();
        this.refreshRecycler();
        this.setToolbar();
        this.setMsgClick();
        ICouponsManager.getInstance().register(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.memberDelete:
                this.deleteMember();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        this.member = IDataManager.getInstance().getMember(mmbId);
        if (!TextUtils.isEmpty(member.getIcon())) {
            this.icon.setImageURI(Uri.fromFile(new File(member.getIcon())));
        }
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

    private void initRecycler() {
        this.recyclerView.loadData(new ArrayList<>(), new RecyclerAdapter.OnItemLoading<MemberItemEntity>() {

            @Override
            public int getItemViewType(MemberItemEntity entity, int position) {
                return entity.viewType;
            }

            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                int layout = -1;
                switch (viewType) {
                    case MemberItemEntity.TYPE_INTEGRAL_HEAD:
                    case MemberItemEntity.TYPE_ADDRESS_HEAD:
                    case MemberItemEntity.TYPE_COUPONS_HEAD:
                    case MemberItemEntity.TYPE_BOOKING_HEAD:
                        layout = R.layout.view_item_member_title;
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL:
                        layout = R.layout.view_item_member_integral;
                        break;
                    case MemberItemEntity.TYPE_ADDRESS:
                        layout = R.layout.view_item_member_address;
                        break;
                    case MemberItemEntity.TYPE_COUPONS:
                        layout = R.layout.view_item_coupons;
                        break;
                    case MemberItemEntity.TYPE_BOOKING:
                        layout = R.layout.view_item_member_booking;
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL_MORE:
                    case MemberItemEntity.TYPE_ADDRESS_MORE:
                    case MemberItemEntity.TYPE_COUPONS_MORE:
                    case MemberItemEntity.TYPE_BOOKING_MORE:
                        layout = R.layout.view_item_member_more;
                        break;
                }
                return LayoutInflater.from(MemberActivity.this).inflate(layout, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, MemberItemEntity entity, int position) {
                switch (viewType) {
                    case MemberItemEntity.TYPE_INTEGRAL_HEAD:
                    case MemberItemEntity.TYPE_ADDRESS_HEAD:
                    case MemberItemEntity.TYPE_COUPONS_HEAD:
                    case MemberItemEntity.TYPE_BOOKING_HEAD:
                        refreshHead(root, viewType);
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL:
                        refreshIntegral(root, (IntegralBindEntity) entity.data);
                        break;
                    case MemberItemEntity.TYPE_ADDRESS:
                        refreshAddress(root, (AddressEntity) entity.data);
                        break;
                    case MemberItemEntity.TYPE_COUPONS:
                        refreshCoupons(root, (CouponsBindEntity) entity.data);
                        break;
                    case MemberItemEntity.TYPE_BOOKING:
                        refreshBooking(root, (BookingBindEntity) entity.data);
                        break;
                    case MemberItemEntity.TYPE_INTEGRAL_MORE:
                    case MemberItemEntity.TYPE_ADDRESS_MORE:
                    case MemberItemEntity.TYPE_COUPONS_MORE:
                    case MemberItemEntity.TYPE_BOOKING_MORE:
                        refreshMore(root, viewType);
                        break;
                }
            }
        });
    }

    private void refreshRecycler() {
        this.member = IDataManager.getInstance().getMember(mmbId);
        List<MemberItemEntity> entities = MemberItemEntity.createMembers(this.member);
        this.recyclerView.reload(entities);
    }

    private void refreshHead(View root, int viewType) {
        TextView title = root.findViewById(R.id.memberTitleText);
        ImageView add = root.findViewById(R.id.memberTitleAdd);
        ImageView use = root.findViewById(R.id.memberTitleUse);
        switch (viewType) {
            case MemberItemEntity.TYPE_INTEGRAL_HEAD:
                use.setVisibility(View.VISIBLE);
                title.setText(R.string.member_list_integral);
                break;
            case MemberItemEntity.TYPE_ADDRESS_HEAD:
                use.setVisibility(View.GONE);
                title.setText(R.string.member_list_address);
                break;
            case MemberItemEntity.TYPE_COUPONS_HEAD:
                use.setVisibility(View.GONE);
                title.setText(R.string.member_list_coupons);
                break;
            case MemberItemEntity.TYPE_BOOKING_HEAD:
                use.setVisibility(View.GONE);
                title.setText(R.string.member_list_booking);
                break;
        }
        add.setOnClickListener(v -> {
            switch (viewType) {
                case MemberItemEntity.TYPE_INTEGRAL_HEAD:
                    IntegralAddActivity.start(this, mmbId, REQUEST_CODE_ADD);
                    break;
                case MemberItemEntity.TYPE_ADDRESS_HEAD:
                    AddressActivity.start(this, mmbId, REQUEST_CODE_ADD);
                    break;
                case MemberItemEntity.TYPE_COUPONS_HEAD:
                    CouponsGrantActivity.start(this, mmbId);
                    break;
                case MemberItemEntity.TYPE_BOOKING_HEAD:
                    //TODO
                    break;
            }
        });
        use.setOnClickListener(v -> IntegralUseActivity.start(this, this.mmbId, REQUEST_CODE_INTEGRAL_LIST));
    }

    private void refreshIntegral(View root, IntegralBindEntity entity) {
        TextView total = root.findViewById(R.id.memberIntegralTotal);
        TextView used = root.findViewById(R.id.memberIntegralUsed);
        TextView desc = root.findViewById(R.id.memberIntegralDesc);
        String create = TimeUtil.dateMilliSecond2String(entity.getCreateDate(), TimeUtil.PATTERN_DAY4Y2);
        total.setText(getString(R.string.member_integral_total, Util.float2String(entity.getScore(), 2), create));
        if (entity.getUsedDate() > entity.getCreateDate()) {
            used.setVisibility(View.VISIBLE);
            String usedTime = TimeUtil.dateMilliSecond2String(entity.getUsedDate(), TimeUtil.PATTERN_DAY4Y2);
            String text = getString(R.string.member_integral_used, Util.float2String(entity.getUsedScore(), 2), usedTime);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                used.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
            } else {
                used.setText(Html.fromHtml(text));
            }
        } else {
            used.setVisibility(View.GONE);
        }
        desc.setText(entity.getDesc());
    }

    private void refreshAddress(View root, AddressEntity address) {
        TextView namePhone = root.findViewById(R.id.memberAddressNamePhone);
        TextView info = root.findViewById(R.id.memberAddressInfo);
        namePhone.setText(getString(R.string.member_address_name_phone, address.getConsignee(), address.getPhone(), address.getTag()));
        info.setText(getString(R.string.member_address_info, address.getProvince(), address.getCity(), address.getArea(), address.getTown(), address.getAddress()));
        root.findViewById(R.id.memberAddressExport).setOnClickListener(v -> {
            Util.copy2Clipboard(this, address.address() + "，" + address.getConsignee() + "，" + address.getPhone());
            showMessage(true, R.string.member_address_export_over);
        });
    }

    private void refreshCoupons(View root, CouponsBindEntity entity) {
        View amount = root.findViewById(R.id.itemCouponsAmount);
        TextView amountMember = root.findViewById(R.id.itemCouponsAmountNumber);
        TextView tag = root.findViewById(R.id.itemCouponsTag);
        TextView member = root.findViewById(R.id.itemCouponsMember);
        TextView desc = root.findViewById(R.id.itemCouponsDesc);
        TextView expiry = root.findViewById(R.id.itemCouponsExpiry);
        ImageView mark = root.findViewById(R.id.itemCouponsMark);
        View use = root.findViewById(R.id.itemCouponsUse);
        amountMember.setText(Util.float2String(entity.getAmount(), 2));
        if (TextUtils.isEmpty(entity.getTag())) {
            tag.setText(entity.getTag());
            tag.setVisibility(View.INVISIBLE);
        } else {
            tag.setText(entity.getTag());
            tag.setVisibility(View.VISIBLE);
        }
        member.setText("");
        member.setVisibility(View.GONE);
        String descContent = entity.getName() + "  " + entity.getDesc();
        desc.setText(descContent);
        expiry.setText(entity.getExpiry(this));
        mark.setVisibility(View.VISIBLE);
        use.setVisibility(View.GONE);
        if (entity.used()) {
            amount.setBackgroundResource(R.color.lighter_gray);
            mark.setImageResource(R.mipmap.icon_coupons_used);
        } else if (entity.overdue()) {
            amount.setBackgroundResource(R.color.lighter_gray);
            mark.setImageResource(R.mipmap.icon_coupons_overdue);
        } else if (entity.nearOverDue()) {
            amount.setBackgroundResource(R.color.colorPrimary);
            mark.setImageResource(R.mipmap.icon_coupons_near_overdue);
            use.setVisibility(View.VISIBLE);
            use.setOnClickListener(v -> ICouponsManager.getInstance().useCoupons(this, entity));
        } else {
            amount.setBackgroundResource(R.color.colorPrimary);
            mark.setVisibility(View.GONE);
            use.setVisibility(View.VISIBLE);
            use.setOnClickListener(v -> ICouponsManager.getInstance().useCoupons(MemberActivity.this, entity));
        }
    }

    private void refreshBooking(View root, BookingBindEntity entity) {
        TextView bookingText = root.findViewById(R.id.itemMemberBookingText);
        TextView bookingDesc = root.findViewById(R.id.itemMemberBookingDesc);
        TextView bookingNumber = root.findViewById(R.id.itemMemberBookingNumber);
        TextView bookingAddress = root.findViewById(R.id.itemMemberBookingAddress);
        TextView bookingCreateTime = root.findViewById(R.id.itemMemberBookingCreateTime);
        TextView bookingOrderTime = root.findViewById(R.id.itemMemberBookingOrderTime);
        TextView bookingOrder = root.findViewById(R.id.itemMemberBookingOrder);
        ImageView mark = root.findViewById(R.id.itemMemberBookingMark);
        bookingText.setText(entity.getText());
        bookingDesc.setText(entity.getDesc());
        bookingNumber.setText(getString(R.string.number, entity.getCount()));
        bookingAddress.setText(entity.getAddressText());
        bookingCreateTime.setText(TimeUtil.dateMilliSecond2String(entity.getCreateTime(), TimeUtil.PATTERN_DAY4Y2));
        bookingOrderTime.setText(TimeUtil.dateMilliSecond2String(entity.getOrderTime(), TimeUtil.PATTERN_DAY4Y2));
        bookingOrderTime.setVisibility(entity.getOrderTime() < entity.getCreateTime() ? View.GONE : View.VISIBLE);
        mark.setVisibility(View.VISIBLE);
        if (!entity.isOrdered()) {
            mark.setVisibility(View.GONE);
            bookingOrder.setVisibility(View.VISIBLE);
            bookingOrder.setOnClickListener(v -> IBookingManager.getInstance().order(this, entity));
        }
    }

    private void refreshMore(View root, int viewType) {
        root.setOnClickListener(v -> {
            switch (viewType) {
                case MemberItemEntity.TYPE_INTEGRAL_MORE:
                    IntegralsActivity.start(this, this.mmbId, REQUEST_CODE_INTEGRAL_LIST);
                    break;
                case MemberItemEntity.TYPE_ADDRESS_MORE:
                    AddressesActivity.start(this, this.mmbId, REQUEST_CODE_ADDRESS_LIST);
                    break;
                case MemberItemEntity.TYPE_COUPONS_MORE:
                    CouponsActivity.start(this, mmbId, REQUEST_CODE_COUPONS_LIST);
                    break;
                case MemberItemEntity.TYPE_BOOKING_MORE:
                    //TODO
                    break;
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

    private void setMsgClick() {
        this.msgIcon.setOnClickListener(v -> this.updateMessage());
    }

    private void updateMessage() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_member_message, null);
        EditText input = view.findViewById(R.id.updateMemberMessage);
        input.setText(this.member.getDesc());
        input.setSelection(input.getText().length());
        Dialog dialog = WindowUtil.createAlertDialog(this, 0, view, (dialog1, which) -> {
            String message = input.getText().toString();
            IDataManager.getInstance().updateMessage(mmbId, message);
            this.msgIcon.setImageResource(R.mipmap.icon_arrow_down);
            this.message.setText(message);
            this.member.setDesc(message);
        }, R.string.ok, (dialog12, which) -> msgIcon.setImageResource(R.mipmap.icon_arrow_down), R.string.cancel);
        if (dialog == null) {
            return;
        }
        this.msgIcon.setImageResource(R.mipmap.icon_arrow_up);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ICouponsManager.getInstance().unRegister(this);
    }

    @Override
    public void onLoadCouponsSuccess() {
        this.refreshRecycler();
    }

    private void deleteMember() {
        String title = "";
        String message = getString(R.string.member_delete, member.getName());
        DialogInterface.OnClickListener okListener = (dialog, which) -> {
            if (IDataManager.getInstance().dldMember(mmbId)) {
                if (!TextUtils.isEmpty(member.getIcon())) {
                    FileUtil.deleteFile(member.getIcon());
                }
                finish();
            }
        };
        WindowUtil.createAlertDialog(this, title, message,
                getString(R.string.ok), okListener,
                getString(R.string.cancel), (dialog, which) -> dialog.dismiss()).show();
    }
}

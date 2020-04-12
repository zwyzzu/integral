package com.zhangwy.integral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zhangwy.integral.data.IBookingManager;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.BookingBindEntity;

import yixia.lib.core.base.BaseActivity;

@SuppressWarnings("unused")
public class BookingInfoActivity extends BaseActivity implements View.OnClickListener, IBookingManager.OnBookingDataListener {

    private static final String EXTRA_MEMBER = "extra.member.id";
    private static final String EXTRA_BOOKING = "extra.booking.id";

    public static void start(Context context, String memberId, String bookingId) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, BookingInfoActivity.class);
        intent.putExtra(EXTRA_MEMBER, memberId);
        intent.putExtra(EXTRA_BOOKING, bookingId);
        context.startActivity(intent);
    }

    private TextView memberText;
    private FrameLayout addressHome;
    private FrameLayout commodityHome;
    private TextView commodityCount;
    private TextView message;
    private Button buttonOrder;
    private Button buttonInvalid;

    private BookingBindEntity bindEntity;
    private String bookingId;
    private String memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_info);
        this.memberText = this.findViewById(R.id.bookingInfoMember);
        this.addressHome = this.findViewById(R.id.bookingInfoAddressHome);
        this.commodityHome = this.findViewById(R.id.bookingInfoCommodityHome);
        this.commodityCount = this.findViewById(R.id.bookingInfoCommodityCount);
        this.message = this.findViewById(R.id.bookingInfoMessage);
        this.buttonOrder = this.findViewById(R.id.bookingInfoOrder);
        this.buttonInvalid = this.findViewById(R.id.bookingInfoInvalid);
        this.reload();
        this.setToolbar();
        IBookingManager.getInstance().register(this);
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.bookingInfoToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void reload() {
        Intent intent = getIntent();
        if (intent == null) {
            this.invalidData(true);
            return;
        }
        this.bookingId = this.getIntent().getStringExtra(EXTRA_BOOKING);
        this.memberId = this.getIntent().getStringExtra(EXTRA_MEMBER);
        this.bindEntity = IDataManager.getInstance().getMemberBooking(memberId, bookingId);
        if (this.bindEntity == null) {
            this.invalidData(true);
            return;
        }
        this.buttonOrder.setOnClickListener(this);
        this.buttonInvalid.setOnClickListener(this);
        this.memberText.setText(this.bindEntity.getBindName());
        this.reloadAddress(this.bindEntity.getAddress());
        this.reloadCommodity(this.bindEntity);
        this.commodityCount.setText(getString(R.string.number, this.bindEntity.getCount()));
        this.message.setText(this.bindEntity.getDesc());
        if (this.bindEntity.isOrdered() || this.bindEntity.isInvalid()) {
            this.invalidData(false);
        }
    }

    private void invalidData(boolean showMessage) {
        this.buttonInvalid.setEnabled(false);
        this.buttonInvalid.setOnClickListener(null);
        this.buttonOrder.setEnabled(false);
        this.buttonOrder.setOnClickListener(null);
        if (showMessage) {
            this.showMessage(true, true, R.string.error_booking_empty);
        }
    }

    private void reloadAddress(AddressEntity entity) {
        View view = getLayoutInflater().inflate(R.layout.view_item_2lines_spinner, this.addressHome, true);
        TextView first = view.findViewById(R.id.spinner2LinesFirst);
        first.setText(getString(R.string.member_address_name_phone, entity.getConsignee(), entity.getPhone(), entity.getTag()));
        TextView second = view.findViewById(R.id.spinner2LinesSecond);
        second.setText(getString(R.string.member_address_info, entity.getProvince(), entity.getCity(), entity.getArea(), entity.getTown(), entity.getAddress()));
    }

    private void reloadCommodity(BookingBindEntity entity) {
        View view = getLayoutInflater().inflate(R.layout.view_item_2lines_spinner, this.commodityHome, true);
        TextView first = view.findViewById(R.id.spinner2LinesFirst);
        first.setText(entity.getText());
        TextView second = view.findViewById(R.id.spinner2LinesSecond);
        second.setText(entity.getDesc());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookingInfoOrder:
                IBookingManager.getInstance().order(this, this.bindEntity);
                break;
            case R.id.bookingInfoInvalid:
                IBookingManager.getInstance().invalid(this, this.bindEntity);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IBookingManager.getInstance().unRegister(this);
    }

    @Override
    public void onBookingDataChanged(String memberId) {
        if (!TextUtils.equals(memberId, this.memberId)) {
            return;
        }
        this.bindEntity = IDataManager.getInstance().getMemberBooking(this.memberId, this.bookingId);
        if (this.bindEntity.isOrdered() || this.bindEntity.isInvalid()) {
            this.invalidData(false);
        }
    }
}
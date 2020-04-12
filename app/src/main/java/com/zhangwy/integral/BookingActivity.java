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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yixia.widget.VSVerificationLayout;
import com.yixia.widget.adapter.BaseAdapter;
import com.zhangwy.integral.data.IBookingManager;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.AddressEntity;
import com.zhangwy.integral.entity.BookingBindEntity;
import com.zhangwy.integral.entity.BookingEntity;
import com.zhangwy.integral.entity.MemberEntity;

import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.Util;

@SuppressWarnings("unused")
public class BookingActivity extends BaseActivity {

    private static final String EXTRA_MEMBER = "extra.member.id";
    private static final String EXTRA_BOOKING = "extra.booking.id";

    public static void startByMember(Context context, String memberId) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, BookingActivity.class);
        intent.putExtra(EXTRA_MEMBER, memberId);
        context.startActivity(intent);
    }

    public static void startByBooking(Context context, String bookingId) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, BookingActivity.class);
        intent.putExtra(EXTRA_BOOKING, bookingId);
        context.startActivity(intent);
    }

    public static void start(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, BookingActivity.class));
    }

    private TextView memberText;
    private Spinner memberSpinner;
    private Spinner addressSpinner;
    private Spinner commoditySpinner;
    private VSVerificationLayout commodityCountHome;
    private EditText message;
    private TextView emptyElement;

    private MemberEntity memberEntity;
    private AddressEntity addressEntity;
    private BookingEntity bookingEntity;

    private boolean emptyMember = false;
    private boolean emptyAddress = false;
    private boolean emptyBooking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        this.initView();
        this.initMember();
        this.initCommoditySpinner();
        this.setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.bookingToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!this.emptyMember && !this.emptyAddress && !this.emptyBooking) {
            getMenuInflater().inflate(R.menu.menu_booking, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookingSave:
                if (this.emptyMember || this.emptyAddress || this.emptyBooking) {
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

    private void initView() {
        this.memberText = this.findViewById(R.id.bookingMemberText);
        this.memberSpinner = this.findViewById(R.id.bookingMemberSpinner);
        this.addressSpinner = this.findViewById(R.id.bookingAddressSpinner);
        this.commoditySpinner = this.findViewById(R.id.bookingCommoditySpinner);
        this.commodityCountHome = this.findViewById(R.id.bookingCommodityCountHome);
        this.message = this.findViewById(R.id.bookingMessage);
        this.emptyElement = this.findViewById(R.id.bookingEmpty);
    }

    private void initMember() {
        String memberId = "";
        if (this.getIntent() != null) {
            memberId = this.getIntent().getStringExtra(EXTRA_MEMBER);
        }
        this.memberEntity = IDataManager.getInstance().getMember(memberId);
        if (this.memberEntity == null) {
            this.initMemberSpinner();
        } else {
            this.emptyMember = false;
            this.memberText.setVisibility(View.VISIBLE);
            this.memberSpinner.setVisibility(View.GONE);
            this.memberText.setText(this.memberEntity.getName());
            this.initAddressSpinner();
        }
    }

    private void initMemberSpinner() {
        this.memberText.setVisibility(View.GONE);
        this.memberSpinner.setVisibility(View.VISIBLE);
        List<MemberEntity> memberEntities = IDataManager.getInstance().getMembers();
        this.emptyMember = Util.isEmpty(memberEntities);
        BaseAdapter<MemberEntity> adapter = new BaseAdapter<>(memberEntities, (parent, convertView, entity) -> {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_item_1lines_spinner, parent, false);
            }
            TextView first = convertView.findViewById(R.id.spinner1Lines);
            first.setText(entity.getName());
            return convertView;
        });
        this.memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                memberEntity = adapter.getItem(position);
                initAddressSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.memberSpinner.setAdapter(adapter);
    }

    private void initAddressSpinner() {
        if (this.memberEntity == null) {
            return;
        }
        this.emptyAddress = Util.isEmpty(this.memberEntity.getAddress());
        BaseAdapter<AddressEntity> adapter = new BaseAdapter<>(this.memberEntity.getAddress(), (parent, convertView, entity) -> {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_item_2lines_spinner, parent, false);
            }
            TextView first = convertView.findViewById(R.id.spinner2LinesFirst);
            first.setText(getString(R.string.member_address_name_phone, entity.getConsignee(), entity.getPhone(), entity.getTag()));
            TextView second = convertView.findViewById(R.id.spinner2LinesSecond);
            second.setText(getString(R.string.member_address_info, entity.getProvince(), entity.getCity(), entity.getArea(), entity.getTown(), entity.getAddress()));
            return convertView;
        });
        this.addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addressEntity = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.addressSpinner.setAdapter(adapter);
    }

    private void initCommoditySpinner() {
        List<BookingEntity> bookingEntities = IDataManager.getInstance().getBookings();
        this.emptyBooking = Util.isEmpty(bookingEntities);
        BaseAdapter<BookingEntity> adapter = new BaseAdapter<>(bookingEntities, (parent, convertView, entity) -> {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_item_2lines_spinner, parent, false);
            }
            TextView first = convertView.findViewById(R.id.spinner2LinesFirst);
            first.setText(entity.getText());
            TextView second = convertView.findViewById(R.id.spinner2LinesSecond);
            second.setText(entity.getDesc());
            return convertView;
        });
        this.commoditySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookingEntity = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.commoditySpinner.setAdapter(adapter);
        String bookingId = this.getIntent().getStringExtra(EXTRA_BOOKING);
        if (!TextUtils.isEmpty(bookingId)) {
            for (int i = 0; i < bookingEntities.size(); i++) {
                BookingEntity entity = bookingEntities.get(i);
                if (entity != null && TextUtils.equals(entity.getId(), bookingId)) {
                    this.commoditySpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void save() {
        if (this.memberEntity == null) {
            this.showMessage(true, getString(R.string.error_unselect, getString(R.string.booking_member)));
            return;
        }
        if (this.addressEntity == null) {
            this.showMessage(true, getString(R.string.error_unselect, getString(R.string.booking_address)));
            return;
        }
        if (this.bookingEntity == null) {
            this.showMessage(true, getString(R.string.error_unselect, getString(R.string.booking_commodity)));
            return;
        }
        final int[] count = {1};
        boolean verify = commodityCountHome.verify(new VSVerificationLayout.Command() {
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
                int cfc = Integer.parseInt(text);
                if (cfc > 0) {
                    count[0] = cfc;
                    return false;
                }
                return true;
            }
        });
        if (verify) {
            return;
        }
        BookingBindEntity bindEntity = new BookingBindEntity();
        bindEntity.setBind(this.memberEntity.getId());
        bindEntity.setBindName(this.memberEntity.getName());
        bindEntity.setBindIcon(this.memberEntity.getIcon());
        bindEntity.setAddress(addressEntity);
        bindEntity.setBookingId(this.bookingEntity.getId());
        bindEntity.setCount(count[0]);
        bindEntity.setText(this.bookingEntity.getText());
        if (TextUtils.isEmpty(this.message.getText())) {
            bindEntity.setDesc(this.bookingEntity.getDesc());
        } else {
            bindEntity.setDesc(this.message.getText().toString());
        }
        bindEntity.setCreateTime(System.currentTimeMillis());
        IDataManager.getInstance().addMemberBooking(bindEntity);
        IBookingManager.getInstance().addBooking(bindEntity);
        this.finish();
    }

    private void showEmptyRemind() {
        boolean show = !this.emptyMember && !this.emptyAddress && !this.emptyBooking;
        this.findViewById(R.id.bookingMemberHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.bookingAddressHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.bookingCommodityHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.bookingMessageHead).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.bookingCommodityCountHome).setVisibility(show ? View.VISIBLE : View.GONE);
        this.memberText.setVisibility(show ? View.VISIBLE : View.GONE);
        this.memberSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        this.addressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        this.commoditySpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        this.message.setVisibility(show ? View.VISIBLE : View.GONE);
        if (!show) {
            List<String> empty = new ArrayList<>();
            if (this.emptyMember) {
                empty.add(getString(R.string.message_hint_empty_member));
            }
            if (this.emptyAddress) {
                empty.add(getString(R.string.message_hint_empty_address));
            }
            if (this.emptyBooking) {
                empty.add(getString(R.string.message_hint_empty_booking));
            }
            String emptyString = Util.array2ArrayString('\n', empty);
            this.emptyElement.setText(emptyString);
        }
    }

}

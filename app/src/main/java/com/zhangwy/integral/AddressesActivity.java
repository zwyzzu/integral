package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.AddressEntity;

import java.util.Collections;
import java.util.List;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.Util;

public class AddressesActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD = 100;
    private static final String EXTRA_MEMBERID = "extraMemberId";
    public static void start(Activity activity, String memberId, int requestCode) {
        Intent intent = new Intent(activity, AddressesActivity.class);
        intent.putExtra(EXTRA_MEMBERID, memberId);
        activity.startActivityForResult(intent, requestCode);
    }

    private boolean modified = false;
    private String memberId;
    private VSRecyclerView<AddressEntity> recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);
        this.memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
        this.setToolbar();
        this.initRecycler();
        this.reloadData();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.addressesToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addresses, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (this.modified) {
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case R.id.addressesAdd:
                AddressActivity.start(this, this.memberId, REQUEST_CODE_ADD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.modified) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    private void initRecycler() {
        this.recycler = this.findViewById(R.id.addressesRecycler);
        this.recycler.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.recycler.loadData(null, new RecyclerAdapter.OnItemLoading<AddressEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                int layout = R.layout.view_item_member_address;
                return LayoutInflater.from(AddressesActivity.this).inflate(layout, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, AddressEntity address, int position) {
                loadItem(root, address);
            }
        });
    }

    private void loadItem(View root, AddressEntity address) {
        TextView namePhone = root.findViewById(R.id.memberAddressNamePhone);
        TextView info = root.findViewById(R.id.memberAddressInfo);
        namePhone.setText(getString(R.string.member_address_name_phone,
                address.getConsignee(), address.getPhone(), address.getTag()));
        info.setText(getString(R.string.member_address_info, address.getProvince(),
                address.getCity(), address.getArea(), address.getTown(), address.getAddress()));
        root.findViewById(R.id.memberAddressExport).setOnClickListener(v -> {
            Util.copy2Clipboard(this, address.address() + "，" + address.getConsignee() + "，" + address.getPhone());
            showMessage(true, R.string.member_address_export_over);
        });
    }

    private void reloadData() {
        List<AddressEntity> entities = IDataManager.getInstance().getAddresses(this.memberId);
        Collections.reverse(entities);
        this.recycler.reload(entities);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            this.modified = true;
            this.reloadData();
        }
    }
}

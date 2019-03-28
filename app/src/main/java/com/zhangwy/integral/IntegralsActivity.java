package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.IntegralBindEntity;

import java.util.Collections;
import java.util.List;

import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.Util;

public class IntegralsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 100;
    private static final String EXTRA_MEMBERID = "extraMemberId";
    public static void start(Activity activity, String memberId, int requestCode) {
        Intent intent = new Intent(activity, IntegralsActivity.class);
        intent.putExtra(EXTRA_MEMBERID, memberId);
        activity.startActivityForResult(intent, requestCode);
    }

    private boolean modified = false;
    private String memberId;
    private VSRecyclerView<IntegralBindEntity> recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrals);
        this.memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
        this.setToolbar();
        this.initRecycler();
        this.reloadData();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.integralsToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_integrals, menu);
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
            case R.id.integralsAdd:
                IntegralAddActivity.start(this, this.memberId, REQUEST_CODE_ADD);
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
        this.recycler = this.findViewById(R.id.integralsRecycler);
        this.recycler.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.recycler.loadData(null, new RecyclerAdapter.OnItemLoading<IntegralBindEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                int layout = R.layout.view_item_member_integral;
                return LayoutInflater.from(IntegralsActivity.this).inflate(layout, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, IntegralBindEntity entity, int position) {
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
        });
    }

    private void reloadData() {
        List<IntegralBindEntity> entities = IDataManager.getInstance().getMemberIntegrals(this.memberId);
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

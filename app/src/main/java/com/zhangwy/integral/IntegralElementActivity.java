package com.zhangwy.integral;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.RecyclerDivider;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.IntegralEntity;

import java.util.List;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.Util;
import yixia.lib.core.util.WindowUtil;

public class IntegralElementActivity extends BaseActivity {

    public static void start(Context context) {
        if (context == null)
            return;
        context.startActivity(new Intent(context, IntegralElementActivity.class));
    }

    private VSRecyclerView<IntegralEntity> recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_element);
        Toolbar toolbar = this.findViewById(R.id.integralElementToolbar);
        setSupportActionBar(toolbar);
        this.initRecycler();
        this.reload();
        this.setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.integralElementToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_integral_element, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.integralElementAdd:
                this.add(null);
                return true;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecycler() {
        this.recyclerView = this.findViewById(R.id.integralElementRecyclerView);
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        RecyclerDivider divider = RecyclerDivider.create(this, VSRecyclerView.VERTICAL, 1, Color.TRANSPARENT);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.loadData(null, new RecyclerAdapter.OnItemLoading<IntegralEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return getLayoutInflater().inflate(R.layout.view_item_integral_element, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, IntegralEntity entity, int position) {
                TextView score = root.findViewById(R.id.integralElementItemScore);
                TextView title = root.findViewById(R.id.integralElementItemTitle);
                TextView message = root.findViewById(R.id.integralElementItemMessage);
                score.setText(getString(R.string.element_item_score, Util.float2String(entity.getScore(), 2)));
                title.setText(getString(R.string.element_item_title, entity.getName()));
                message.setText(getString(R.string.element_item_message, entity.getDesc()));
            }
        });
        this.recyclerView.setOnItemClickListener((view, viewType, entity, position) -> add(entity));
    }

    private void reload() {
        List<IntegralEntity> array = IDataManager.getInstance().getIntegrals();
        this.recyclerView.reload(array);
    }

    private void add(IntegralEntity oldEntity) {
        View root = getLayoutInflater().inflate(R.layout.dialog_integral_element, this.recyclerView, false);
        final EditText score = root.findViewById(R.id.elementAddScore);
        final EditText title = root.findViewById(R.id.elementAddTitle);
        final EditText message = root.findViewById(R.id.elementAddMessage);
        final CheckBox checkBox = root.findViewById(R.id.elementAddCheckBox);
        Button cancel = root.findViewById(R.id.elementAddCancel);
        Button ok = root.findViewById(R.id.elementAddOk);
        if (oldEntity != null) {
            score.setText(String.valueOf(oldEntity.getScore()));
            title.setText(oldEntity.getName());
            message.setText(oldEntity.getDesc());
            checkBox.setChecked(oldEntity.isCheckCoefficient());
        }
        final Dialog dialog = WindowUtil.createAlertDialog(this, 0, root, null, R.string.ok, null, R.string.cancel);
        ok.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            float scoreValue = Float.valueOf(score.getText().toString());
            String titleValue = title.getText().toString();
            String messageValue = message.getText().toString();
            IntegralEntity entity = new IntegralEntity();
            entity.setScore(scoreValue);
            entity.setName(titleValue);
            entity.setDesc(messageValue);
            entity.setCheckCoefficient(checkBox.isChecked());
            if (oldEntity == null) {
                IDataManager.getInstance().addIntegral(entity);
            } else {
                entity.setId(oldEntity.getId());
                IDataManager.getInstance().updateIntegral(entity);
            }
            reload();
        });
        cancel.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        if (dialog == null) {
            this.showMessage(true, R.string.retry);
        } else {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
}

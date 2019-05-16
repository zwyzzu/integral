package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yixia.widget.VSVerificationLayout;
import com.yixia.widget.adapter.BaseAdapter;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.IntegralBindEntity;
import com.zhangwy.integral.entity.IntegralEntity;

import java.util.List;

import yixia.lib.core.util.Util;

public class IntegralAddActivity extends AppCompatActivity {

    private static final String EXTRA_BIND = "extra_bind_id";

    public static void start(Activity activity, String bindId, int requestCode) {
        Intent intent = new Intent(activity, IntegralAddActivity.class);
        intent.putExtra(EXTRA_BIND, bindId);
        activity.startActivityForResult(intent, requestCode);
    }

    private Spinner spinner;
    private CheckBox hasCoefficient;
    private VSVerificationLayout coefficientHome;
    private EditText coefficient;
    private List<IntegralEntity> array;
    private String bindId;
    private boolean emptyElement = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_add);
        this.spinner = this.findViewById(R.id.integralAddSpinner);
        this.hasCoefficient = this.findViewById(R.id.integralAddHasCoefficient);
        this.coefficientHome = this.findViewById(R.id.integralAddCoefficientHome);
        this.coefficient = this.findViewById(R.id.integralAddCoefficient);
        this.setToolbar();
        this.initCoefficient();
        this.initSpinner();
        this.bindId = getIntent().getStringExtra(EXTRA_BIND);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!this.emptyElement) {
            getMenuInflater().inflate(R.menu.menu_integral_add, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.integralAddSave:
                if (this.emptyElement) {
                    break;
                }
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
        Toolbar toolbar = this.findViewById(R.id.integralAddToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initCoefficient() {
        this.hasCoefficient.setOnCheckedChangeListener((buttonView, isChecked) -> coefficientHome.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE));
    }

    private void initSpinner() {
        this.array = IDataManager.getInstance().getIntegrals();
        this.emptyElement = Util.isEmpty(this.array);
        this.showEmptyRemind(this.emptyElement);
        BaseAdapter<IntegralEntity> adapter = new BaseAdapter<>(array, (parent, convertView, entity) -> {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_item_integral_spinner, parent, false);
            }
            TextView first = convertView.findViewById(R.id.integralSpinnerFirst);
            TextView second = convertView.findViewById(R.id.integralSpinnerSecond);
            first.setText(entity.getName());
            second.setText(getString(R.string.add_integral_spinner_second, Util.float2String(entity.getScore(), 2), entity.getDesc()));
            return convertView;
        });
        this.spinner.setAdapter(adapter);
    }

    private void save() {
        IntegralEntity entity = this.array.get(this.spinner.getSelectedItemPosition());
        IntegralBindEntity bindEntity = new IntegralBindEntity();
        bindEntity.setBind(this.bindId);
        bindEntity.setScoreBind(entity.getId());
        bindEntity.setCreateDate(System.currentTimeMillis());
        bindEntity.setDesc(entity.getDesc());
        if (this.hasCoefficient.isChecked()) {
            String coefficientString = this.coefficient.getText().toString();
            if (TextUtils.isEmpty(coefficientString)) {
                this.coefficientHome.empty();
                return;
            }
            float coefficient = Float.parseFloat(coefficientString);
            bindEntity.setScore(coefficient * entity.getScore());
        } else {
            bindEntity.setScore(entity.getScore());
        }
        IDataManager.getInstance().addMemberIntegral(bindEntity);
        this.setResult(RESULT_OK);
        this.finish();
    }

    private void showEmptyRemind(boolean show) {
        this.findViewById(R.id.integralAddEmpty).setVisibility(show ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.integralAddHasCoefficientText).setVisibility(!show ? View.VISIBLE : View.GONE);
        this.spinner.setVisibility(!show ? View.VISIBLE : View.GONE);
        this.hasCoefficient.setVisibility(!show ? View.VISIBLE : View.GONE);
        this.coefficientHome.setVisibility(!show && this.hasCoefficient.isChecked() ? View.VISIBLE : View.GONE);
    }
}

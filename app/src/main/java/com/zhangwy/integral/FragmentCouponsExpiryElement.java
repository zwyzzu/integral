package com.zhangwy.integral;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.RecyclerDivider;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IDataCode;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.CouponsExpiryEntity;

import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.exception.CodeException;
import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2019/6/13.
 * Updated by zhangwy on 2019/6/13.
 * Description
 */
@SuppressWarnings("unused")
public class FragmentCouponsExpiryElement extends BaseFragment {

    public static FragmentCouponsExpiryElement newInstance() {
        return new FragmentCouponsExpiryElement();
    }

    private VSRecyclerView<CouponsExpiryEntity> recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_coupons_expiry_element;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.recyclerView = view.findViewById(R.id.couponsExpiryElementRecyclerView);
        this.initRecycler();
        this.reload();
    }

    private void initRecycler() {
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        RecyclerDivider divider = RecyclerDivider.create(this.getAppCompatActivity(), VSRecyclerView.VERTICAL, 1, Color.TRANSPARENT);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.loadData(null, new RecyclerAdapter.OnItemLoading<CouponsExpiryEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return getLayoutInflater().inflate(R.layout.view_item_coupons_expiry_element, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, CouponsExpiryEntity entity, int position) {
                TextView number = root.findViewById(R.id.couponsExpiryElementItemNumber);
                TextView type = root.findViewById(R.id.couponsExpiryElementItemType);
                number.setText(getString(R.string.element_item_number, String.valueOf(entity.getCount())));
                type.setText(getString(R.string.element_item_type, entity.getExpiry().getName(getContext())));
            }
        });
    }

    private void reload() {
        List<CouponsExpiryEntity> array = IDataManager.getInstance().getExpiries();
        this.recyclerView.reload(array);
    }

    public void add(CouponsExpiryEntity oldEntity) {
        View root = getLayoutInflater().inflate(R.layout.dialog_coupons_expiry_element, this.recyclerView, false);
        final EditText number = root.findViewById(R.id.elementAddNumber);
        final Spinner type = root.findViewById(R.id.elementAddType);
        Button cancel = root.findViewById(R.id.elementAddCancel);
        Button ok = root.findViewById(R.id.elementAddOk);
        if (oldEntity != null) {
            number.setText(String.valueOf(oldEntity.getCount()));
            type.setSelection(oldEntity.getExpiry().position);
        }
        final Dialog dialog = WindowUtil.createAlertDialog(this.getAppCompatActivity(), 0, root, null, R.string.ok, null, R.string.cancel);
        ok.setOnClickListener(v -> {
            String numberString = number.getText().toString();
            CouponsExpiryEntity.Expiry expiry = CouponsExpiryEntity.Expiry.find(type.getSelectedItemPosition());
            if (expiry == CouponsExpiryEntity.Expiry.FOREVER) {
                numberString = "1";
            }
            if (TextUtils.isEmpty(numberString)) {
                showMessage(true, R.string.error_number_empty);
                return;
            }
            int numberValue = Integer.parseInt(numberString);
            if (numberValue <= 0) {
                showMessage(true, R.string.error_number_positive);
                return;
            }
            CouponsExpiryEntity entity = new CouponsExpiryEntity();
            entity.setCount(numberValue);
            entity.setExpiry(expiry);
            if (oldEntity == null) {
                try {
                    IDataManager.getInstance().addExpiry(entity);
                } catch (CodeException e) {
                    if (e.code == IDataCode.DATABASE_HAS_EXPIRY) {
                        showMessage(true, R.string.error_has_expiry);
                    }
                    return;
                }
            }
            if (dialog != null) {
                dialog.dismiss();
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

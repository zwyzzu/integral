package com.zhangwy.integral;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
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
import com.zhangwy.integral.entity.CouponsEntity;

import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.Util;
import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2019/6/13.
 * Updated by zhangwy on 2019/6/13.
 * Description
 */
@SuppressWarnings("unused")
public class FragmentCouponsElement extends BaseFragment {

    public static FragmentCouponsElement newInstance() {
        return new FragmentCouponsElement();
    }

    private VSRecyclerView<CouponsEntity> recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_coupons_element;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.recyclerView = view.findViewById(R.id.couponsElementRecyclerView);
        this.initRecycler();
        this.reload();
    }

    private void initRecycler() {
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        RecyclerDivider divider = RecyclerDivider.create(this.getAppCompatActivity(), VSRecyclerView.VERTICAL, 1, Color.TRANSPARENT);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.loadData(null, new RecyclerAdapter.OnItemLoading<CouponsEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return getLayoutInflater().inflate(R.layout.view_item_coupons_element, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, CouponsEntity entity, int position) {
                TextView score = root.findViewById(R.id.couponsElementItemAmount);
                TextView title = root.findViewById(R.id.couponsElementItemTitle);
                TextView message = root.findViewById(R.id.couponsElementItemMessage);
                score.setText(getString(R.string.element_item_amount, Util.float2String(entity.getAmount(), 2)));
                title.setText(getString(R.string.element_item_title, entity.getName()));
                message.setText(getString(R.string.element_item_message, entity.getDesc()));
            }
        });
        this.recyclerView.setOnItemClickListener((view, viewType, entity, position) -> add(entity));
    }

    private void reload() {
        List<CouponsEntity> array = IDataManager.getInstance().getCoupons();
        this.recyclerView.reload(array);
    }

    public void add(CouponsEntity oldEntity) {
        View root = getLayoutInflater().inflate(R.layout.dialog_coupons_element, this.recyclerView, false);
        final EditText amount = root.findViewById(R.id.elementAddAmount);
        final EditText title = root.findViewById(R.id.elementAddTitle);
        final EditText message = root.findViewById(R.id.elementAddMessage);
        final CheckBox checkBox = root.findViewById(R.id.elementAddCheckBox);
        Button cancel = root.findViewById(R.id.elementAddCancel);
        Button ok = root.findViewById(R.id.elementAddOk);
        if (oldEntity != null) {
            amount.setText(String.valueOf(oldEntity.getAmount()));
            title.setText(oldEntity.getName());
            message.setText(oldEntity.getDesc());
            checkBox.setChecked(oldEntity.isCheckCoefficient());
        }
        final Dialog dialog = WindowUtil.createAlertDialog(this.getAppCompatActivity(), 0, root, null, R.string.ok, null, R.string.cancel);
        ok.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            float amountValue = Float.valueOf(amount.getText().toString());
            String titleValue = title.getText().toString();
            String messageValue = message.getText().toString();
            CouponsEntity entity = new CouponsEntity();
            entity.setAmount(amountValue);
            entity.setName(titleValue);
            entity.setDesc(messageValue);
            entity.setCheckCoefficient(checkBox.isChecked());
            if (oldEntity == null) {
                IDataManager.getInstance().addCoupons(entity);
            } else {
                entity.setId(oldEntity.getId());
                IDataManager.getInstance().updateCoupons(entity);
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

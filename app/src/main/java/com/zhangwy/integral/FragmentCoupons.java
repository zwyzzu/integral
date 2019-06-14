package com.zhangwy.integral;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.ICouponsManager;
import com.zhangwy.integral.data.ICouponsManager.CouponsMold;
import com.zhangwy.integral.entity.CouponsBindEntity;

import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2019/6/3.
 * Updated by zhangwy on 2019/6/3.
 * Description
 */
@SuppressWarnings("unused")
public class FragmentCoupons extends BaseFragment implements ICouponsManager.OnCouponsDataListener {

    private final static String EXTRA_MEMBERID = "extraMemberId";
    private final static String EXTRA_MOLD = "extraMold";
    public static FragmentCoupons newInstance(String memberId, CouponsMold mold) {
        FragmentCoupons coupons = new FragmentCoupons();
        Bundle args = new Bundle();
        args.putString(EXTRA_MEMBERID, memberId);
        args.putString(EXTRA_MOLD, mold.code);
        coupons.setArguments(args);
        return coupons;
    }

    private VSRecyclerView<CouponsBindEntity> recyclerView;
    private TextView tip;
    private String memberId;
    private CouponsMold mold;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_coupons;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.recyclerView = view.findViewById(R.id.couponsRecycler);
        this.tip = view.findViewById(R.id.couponsTip);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.memberId = getArguments().getString(EXTRA_MEMBERID);
            this.mold = CouponsMold.find(bundle.getString(EXTRA_MOLD));
        }
        if (this.mold == CouponsMold.UNKNOWN || (this.mold != CouponsMold.NEAROVERDUE && TextUtils.isEmpty(memberId))) {
            this.recyclerView.setVisibility(View.GONE);
            this.tip.setVisibility(View.VISIBLE);
            this.tip.setText(R.string.error_params_data);
        } else {
            this.reload();
        }
        ICouponsManager.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICouponsManager.getInstance().unRegister(this);
    }

    public void reload() {
        List<CouponsBindEntity> array = ICouponsManager.getInstance().getCoupons(this.memberId, this.mold);
        if (Util.isEmpty(array)) {
            this.recyclerView.setVisibility(View.GONE);
            this.tip.setVisibility(View.VISIBLE);
            this.tip.setText(R.string.error_no_data);
            return;
        }
        this.recyclerView.setVisibility(View.VISIBLE);
        this.tip.setVisibility(View.GONE);
        List<String> nearOverDueIds = new ArrayList<>();
        if (this.mold == CouponsMold.USEABLE) {
            List<CouponsBindEntity> entities = ICouponsManager.getInstance().getNearOverDue(this.memberId);
            for (CouponsBindEntity entity : entities) {
                if (entity != null) {
                    nearOverDueIds.add(entity.getId());
                }
            }
        }

        this.recyclerView.loadData(array, new RecyclerAdapter.OnItemLoading<CouponsBindEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return LayoutInflater.from(getContext()).inflate(R.layout.view_item_coupons, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, CouponsBindEntity entity, int position) {
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
                expiry.setText(entity.getExpiry(getContext()));
                mark.setVisibility(View.VISIBLE);
                use.setVisibility(View.GONE);
                switch (mold) {
                    case USEABLE:
                        amount.setBackgroundResource(R.color.colorPrimary);
                        if (nearOverDueIds.contains(entity.getId())) {
                            mark.setImageResource(R.mipmap.icon_coupons_near_overdue);
                        } else {
                            mark.setVisibility(View.GONE);
                        }
                        break;
                    case USED:
                        amount.setBackgroundResource(R.color.lighter_gray);
                        mark.setImageResource(R.mipmap.icon_coupons_used);
                        break;
                    case OVERDUE:
                        amount.setBackgroundResource(R.color.lighter_gray);
                        mark.setImageResource(R.mipmap.icon_coupons_overdue);
                        break;
                    case NEAROVERDUE:
                        amount.setBackgroundResource(R.color.colorPrimary);
                        mark.setImageResource(R.mipmap.icon_coupons_near_overdue);
                        if (TextUtils.isEmpty(memberId)) {
                            member.setText(entity.getBindName());
                            member.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });
    }

    private void onClickUse(View view, CouponsBindEntity entity) {
        if (view == null || entity == null) {
            return;
        }
        if (this.mold != CouponsMold.USEABLE && this.mold != CouponsMold.NEAROVERDUE) {
            return;
        }
        view.setOnClickListener(v -> ICouponsManager.getInstance().useCoupons(getContext(), entity));
    }

    @Override
    public void onLoadCouponsSuccess() {
        this.reload();
    }
}

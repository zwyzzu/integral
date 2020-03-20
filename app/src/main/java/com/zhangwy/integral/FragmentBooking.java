package com.zhangwy.integral;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.entity.MemberEntity;

import java.util.ArrayList;

import yixia.lib.core.base.BaseFragment;

/**
 * Created by zhangwy on 2020-03-19.
 * Updated by zhangwy on 2020-03-19.
 * Description 预订列表
 */
public class FragmentBooking extends BaseFragment {

    public static FragmentBooking newInstance() {
        return new FragmentBooking();
    }

    private VSRecyclerView<MemberEntity> recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_booking;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.recyclerView = view.findViewById(R.id.bookingRecycler);
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.recyclerView.loadData(new ArrayList<>(), new RecyclerAdapter.OnItemLoading<MemberEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onLoadView(View root, int viewType, MemberEntity entity, int position) {
            }
        });
        //TODO
        this.reloadData();
        this.recyclerView.setOnItemClickListener((view1, viewType, entity, position) -> {
            //TODO
        });
    }

    private void reloadData() {
        //TODO
    }
}

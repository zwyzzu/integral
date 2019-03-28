package com.zhangwy.integral;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.RecyclerAdapter.OnItemClickListener;
import com.yixia.widget.recycler.VSRecyclerView;

import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/12/21 下午8:13.
 * Updated by zhangwy on 2018/12/21 下午8:13.
 * Description
 */
@SuppressWarnings("unused")
public class FragmentMine extends BaseFragment implements OnItemClickListener<FragmentMine.MineItem> {

    public static FragmentMine newInstance() {
        return new FragmentMine();
    }

    private SimpleDraweeView icon;
    private TextView nickName;
    private TextView message;
    private Button button;
    private VSRecyclerView<MineItem> recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.recyclerView = view.findViewById(R.id.mineRecyclerView);
        this.icon = view.findViewById(R.id.mineIcon);
        this.nickName = view.findViewById(R.id.mineNickname);
        this.message = view.findViewById(R.id.mineMessage);
        this.button = view.findViewById(R.id.mineLogin);
        this.icon.setImageResource(R.mipmap.icon_avatar);
        this.reloadItem();
        this.refreshLogin();
    }

    private void refreshLogin() {
        if (this.isLogin()) {
            this.nickName.setVisibility(View.VISIBLE);
            this.message.setVisibility(View.VISIBLE);
            this.button.setVisibility(View.INVISIBLE);
        } else {
            this.nickName.setVisibility(View.INVISIBLE);
            this.message.setVisibility(View.INVISIBLE);
            this.button.setVisibility(View.VISIBLE);
        }
    }

    private void reloadItem() {
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        List<MineItem> array = Util.array2List(MineItem.values());
        this.recyclerView.loadData(array, new RecyclerAdapter.OnItemLoading<MineItem>() {

            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return LayoutInflater.from(getContext()).inflate(R.layout.view_item_mine, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, MineItem entity, int position) {
                TextView textView = root.findViewById(R.id.mineItemTitle);
                textView.setText(entity.res);
            }
        });
        this.recyclerView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int viewType, MineItem entity, int position) {
        if (!this.isLogin()) {
            this.showMessage(true, R.string.error_message_logout);
            return;
        }
        switch (entity) {
            case Address:
                break;
            case Integral:
                IntegralElementActivity.start(getContext());
                break;
        }
    }

    public enum MineItem {
        Integral("integral", "积分项", R.string.mine_integral),
        Address("address", "导出地址", R.string.mine_address);
        public String code;
        public String name;
        public int res;

        MineItem(String code, String name, int res) {
            this.code = code;
            this.name = name;
            this.res = res;
        }
    }

    private boolean isLogin() {
        return true;
    }
}

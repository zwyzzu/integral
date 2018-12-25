package com.zhangwy.integral;

import android.os.Bundle;
import android.view.View;

import yixia.lib.core.base.BaseFragment;

/**
 * Created by zhangwy on 2018/12/21 下午8:13.
 * Updated by zhangwy on 2018/12/21 下午8:13.
 * Description
 */
public class FragmentMine extends BaseFragment {

    public static FragmentMine newInstance() {
        return new FragmentMine();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {

    }
}

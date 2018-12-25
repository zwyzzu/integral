package com.zhangwy.integral;

import android.os.Bundle;
import android.view.View;

import yixia.lib.core.base.BaseFragment;

/**
 * Created by zhangwy on 2018/12/21 下午8:10.
 * Updated by zhangwy on 2018/12/21 下午8:10.
 * Description TODO
 */
public class FragmentMain extends BaseFragment {

    public static FragmentMain newInstance() {
        return new FragmentMain();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
    }
}

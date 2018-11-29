package yixia.lib.core.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.ReflectUtils;


/**
 * Created by zhaoliangtai on 2018/4/28.
 */

public abstract class BaseMVPFragment<P extends BasePresenter> extends BaseFragment {

    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPresenter = ReflectUtils.getParameterizeTypeInstance(this, 0);
        if (this.mPresenter != null && this instanceof BaseView) {
            this.mPresenter.setView(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }
    }
}

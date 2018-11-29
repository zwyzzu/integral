package yixia.lib.core.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.ReflectUtils;

/**
 * Created by zhaoliangtai on 2018/7/31.
 */

public class BaseMVPActivity<P extends BasePresenter> extends BaseActivity {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = ReflectUtils.getParameterizeTypeInstance(this, 0);
        if (this instanceof BaseView && this.mPresenter != null)
            mPresenter.setView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }
}

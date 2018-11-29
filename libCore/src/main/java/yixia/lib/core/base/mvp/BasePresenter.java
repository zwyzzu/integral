package yixia.lib.core.base.mvp;

/**
 * Created by zhaoliangtai on 2018/4/28.
 */
public abstract class BasePresenter<V> {

    protected V mView;

    public void setView(V v) {
        mView = v;
    }

    public void onDestroy() {
        mView = null;
    }
}

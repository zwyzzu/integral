package yixia.lib.core.base.mvp;

/**
 * Created by zhaoliangtai on 2018/4/28.
 */

public interface SettableView<T> extends BaseView {

    void setPresenter(T presenter);
}

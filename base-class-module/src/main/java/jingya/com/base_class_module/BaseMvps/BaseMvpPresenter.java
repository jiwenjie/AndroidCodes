package jingya.com.base_class_module.BaseMvps;

/**
 * @author kuky
 * @description presenter 基类
 */
public abstract class BaseMvpPresenter<V extends BaseMvpViewImpl> {
    protected V mView;

    protected void attach(V view) {
        this.mView = view;
    }

    protected void detach() {
        this.mView = null;
    }

    protected void onResume() {
    }

    protected void onStop() {
    }

    protected void onPause() {
    }
}

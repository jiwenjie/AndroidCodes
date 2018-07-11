package jingya.com.base_class_module.BaseMvps;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author kuky
 * @description
 */
public abstract class BaseMvpFragment<V extends BaseMvpViewImpl, P extends BaseMvpPresenter<V>,
        VB extends ViewDataBinding> extends Fragment {

    protected P mPresenter;
    protected VB mViewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initFragment(savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attach((V) this);
        presenterActions();
        setListener();
        handleRxBus();
        return mViewBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    protected abstract int getLayoutId();

    protected abstract void initFragment(Bundle savedInstanceState);

    protected abstract P initPresenter();

    protected void presenterActions() {
    }

    protected void setListener() {
    }

    protected void handleRxBus() {
    }
}

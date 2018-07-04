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

import org.greenrobot.eventbus.EventBus;

/**
 * @author kuky
 * @description MVP 下懒加载 fragment 基类
 */
public abstract class BaseMvpLazyLoadingFragment<V extends BaseMvpViewImpl, P extends BaseMvpPresenter<V>,
        VB extends ViewDataBinding> extends Fragment {

    protected VB mViewBinding;
    protected P mPresenter;
    protected boolean isPageVisible;
    protected boolean isPageCreated;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPageCreated = true;
        tryLoad();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (enabledEventBus())
            EventBus.getDefault().register(this);

        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initFragment(savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attach((V) this);
        presenterActions();
        setListener();
        return mViewBinding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isPageVisible = true;
            tryLoad();
        } else {
            isPageVisible = false;
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (enabledEventBus())
            EventBus.getDefault().unregister(this);
        mPresenter.detach();
    }

    private void tryLoad() {
        if (isPageVisible && isPageCreated) {
            lazyLoading();
            isPageVisible = false;
            isPageCreated = false;
        }
    }

    protected abstract P initPresenter();

    protected abstract int getLayoutId();

    protected abstract void initFragment(Bundle savedInstanceState);

    protected abstract void lazyLoading();

    protected boolean enabledEventBus() {
        return false;
    }

    protected void presenterActions() {

    }

    protected void setListener() {

    }
}

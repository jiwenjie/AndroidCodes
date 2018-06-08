package jingya.com.base_class_module.BaseViews;

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
 * @author Kuky
 */
public abstract class BaseLazyLoadingFragment<VB extends ViewDataBinding> extends Fragment {
    protected VB mViewBinding;
    protected boolean isPageCreated;
    protected boolean isPageVisible;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPageCreated = true;
        tryLoad();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        if (enabledEventBus()) EventBus.getDefault().register(this);
        initFragment(savedInstanceState);
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
    public void onDestroy() {
        super.onDestroy();
        if (enabledEventBus()) EventBus.getDefault().unregister(this);
    }

    private void tryLoad() {
        if (isPageVisible && isPageCreated) {
            lazyLoading();
            isPageVisible = false;
            isPageCreated = false;
        }
    }

    protected abstract int getLayoutId();

    protected abstract boolean enabledEventBus();

    protected abstract void initFragment(Bundle savedInstanceState);

    protected abstract void lazyLoading();

    protected abstract void setListener();
}

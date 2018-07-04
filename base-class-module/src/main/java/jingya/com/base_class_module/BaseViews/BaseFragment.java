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
 * @author kuky
 * @description
 */
public abstract class BaseFragment<VB extends ViewDataBinding> extends Fragment {
    protected VB mViewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (enabledEventBus()) EventBus.getDefault().register(this);
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initFragment(savedInstanceState);
        setListener();
        return mViewBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (enabledEventBus()) EventBus.getDefault().unregister(this);
    }

    protected abstract boolean enabledEventBus();

    protected abstract int getLayoutId();

    protected abstract void initFragment(Bundle savedInstanceState);

    protected abstract void setListener();
}

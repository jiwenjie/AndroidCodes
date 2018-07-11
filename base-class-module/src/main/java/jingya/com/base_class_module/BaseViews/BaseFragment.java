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

/**
 * @author kuky
 * @description fragment 基类
 */
public abstract class BaseFragment<VB extends ViewDataBinding> extends Fragment {
    protected VB mViewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initFragment(savedInstanceState);
        setListener();
        handleRxBus();
        return mViewBinding.getRoot();
    }

    protected abstract int getLayoutId();

    protected abstract void initFragment(Bundle savedInstanceState);

    protected void setListener() {
    }

    protected void handleRxBus() {
    }
}

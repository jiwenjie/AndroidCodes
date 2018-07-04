package jingya.com.base_class_module.BaseViews;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author kuky
 * @description DialogFragment 基类
 */
public abstract class BaseDialogFragment<VB extends ViewDataBinding> extends DialogFragment {
    protected VB mViewBinding;
    protected int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected boolean mUseDefaultWidth = true;

    public void setWidth(int width, boolean useDefaultWidth) {
        this.mWidth = width;
        this.mUseDefaultWidth = useDefaultWidth;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        initDialogFragment();
        return mViewBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (mUseDefaultWidth) mWidth = (int) (dm.widthPixels * 0.8);
        getDialog().getWindow().setLayout(mWidth, mHeight);
    }

    protected abstract int getLayoutId();

    protected void initDialogFragment() {

    }
}

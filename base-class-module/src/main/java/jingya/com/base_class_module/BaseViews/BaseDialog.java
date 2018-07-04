package jingya.com.base_class_module.BaseViews;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import jingya.com.base_class_module.R;


/**
 * @author kuky
 * @description dialog 基类
 */
public abstract class BaseDialog<VB extends ViewDataBinding> extends Dialog {
    protected VB mViewBinding;
    protected Context mContext;

    public BaseDialog(Context context) {
        this(context, R.style.AlertDialogStyle);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    protected void init(Context context) {
        this.mContext = context;
        mViewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutId(), null, false);
        initDialogView();
        setContentView(mViewBinding.getRoot());
        setDialogWidth((int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8));
        setDialogHeight(WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setDialogHeight(int height) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }

    public void setDialogWidth(int width) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.width = width;
        getWindow().setAttributes(layoutParams);
    }

    public void setInputMode() {
        Window dialogWindow = getWindow();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected abstract int getLayoutId();

    protected void initDialogView() {

    }
}

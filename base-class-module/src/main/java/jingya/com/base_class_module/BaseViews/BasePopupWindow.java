package jingya.com.base_class_module.BaseViews;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * @author Kuky
 */
public abstract class BasePopupWindow<VB extends ViewDataBinding> extends PopupWindow {
    protected Context mContext;
    protected VB mViewBinding;

    public BasePopupWindow(Context context, final Window window, int animStyle) {
        this(context, window, animStyle, 0);
    }

    public BasePopupWindow(Context context, final Window window, int animStyle, int width) {
        this(context, window, animStyle, width, 0);
    }

    public BasePopupWindow(Context context, final Window window, int animStyle, int width, int height) {
        super(context);
        init(context, window, animStyle, width, height);
    }

    private void init(Context context, final Window window, int animStyle, int width, int height) {
        this.mContext = context;
        this.mViewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutId(), null, false);
        setContentView(mViewBinding.getRoot());
        initPopupView();
        setFocusable(true);
        setAnimationStyle(animStyle);
        setBackgroundDrawable(new ColorDrawable(0));
        setWidth(width == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : width);
        setHeight(height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : height);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }

    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = alpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    protected abstract int getLayoutId();

    protected abstract void initPopupView();
}

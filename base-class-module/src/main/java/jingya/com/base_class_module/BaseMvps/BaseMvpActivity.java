package jingya.com.base_class_module.BaseMvps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import jingya.com.base_class_module.ActivityController;
import jingya.com.base_class_module.PermissionListener;

/**
 * @author kuky
 * @description
 */
public abstract class BaseMvpActivity<V extends BaseMvpViewImpl, P extends BaseMvpPresenter<V>,
        VB extends ViewDataBinding> extends AppCompatActivity {

    private PermissionListener permissionListener;
    protected P mPresenter;
    protected VB mViewBinding;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (enableTransparentStatus()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                WindowManager.LayoutParams winParams = getWindow().getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                winParams.flags |= bits;
                getWindow().setAttributes(winParams);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                if (getSupportActionBar() != null) getSupportActionBar().hide();
            }
        }

        ActivityController.addActivity(this);
        mViewBinding = DataBindingUtil.setContentView(this, getLayoutId());
        initActivity(savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attach((V) this);
        presenterActions();
        setListener();
        handleRxBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
        ActivityController.removeActivity(this);
    }

    protected abstract int getLayoutId();

    protected abstract void initActivity(Bundle savedInstanceState);

    protected abstract P initPresenter();

    protected boolean enableTransparentStatus() {
        return false;
    }

    protected void presenterActions() {
    }

    protected void setListener() {
    }

    protected void handleRxBus() {
    }

    /**
     * 动态获取权限
     *
     * @param permissions
     * @param listener
     */
    protected void onRuntimePermissionsAsk(String[] permissions, PermissionListener listener) {
        Activity topActivity = ActivityController.getTopActivity();
        List<String> deniedPermissions = new ArrayList<>();
        permissionListener = listener;

        if (topActivity == null) {
            return;
        } else {
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(topActivity, p) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(p);
                }
            }

            /**
             * 全部同意则处理下一步
             */
            if (deniedPermissions.isEmpty()) {
                listener.onGranted();
            } else {
                ActivityCompat.requestPermissions(topActivity,
                        deniedPermissions.toArray(new String[deniedPermissions.size()]), 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                List<String> deniedPermissionList = new ArrayList<>();
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissionList.add(permissions[i]);
                        }
                    }

                    if (deniedPermissionList.isEmpty()) {
                        permissionListener.onGranted();
                    } else {
                        permissionListener.onDenied(deniedPermissionList);
                    }
                }
                break;
            default:
                break;
        }
    }
}

package com.kuky.restframework;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuky
 * @description
 */
public abstract class BaseActivity extends AppCompatActivity {
    private PermissionListener permissionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && enableTransparentStatus()) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            if (getSupportActionBar() != null) getSupportActionBar().hide();
        }
        ActivitiesManager.addActivity(this);
        setContentView(getLayoutId());
        initActivity(savedInstanceState);
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitiesManager.removeActivity(this);
    }

    protected abstract boolean enableTransparentStatus();

    protected abstract int getLayoutId();

    protected abstract void initActivity(Bundle savedInstanceState);

    protected abstract void setListener();

    /**
     * 动态获取权限
     *
     * @param permissions
     * @param listener
     */
    public void onRuntimePermissionsAsk(String[] permissions, PermissionListener listener) {
        Activity topActivity = ActivitiesManager.getTopActivity();
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

    public interface PermissionListener {
        void onGranted();

        void onDenied(List<String> deniedPermissions);
    }
}

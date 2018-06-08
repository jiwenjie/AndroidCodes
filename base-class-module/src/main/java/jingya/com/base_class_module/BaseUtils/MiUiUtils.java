package jingya.com.base_class_module.BaseUtils;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Kuky
 */
public class MiUiUtils {
    private static final String KEY_MI_UI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MI_UI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MI_UI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    /**
     * @param context
     * @param op
     * @return
     */
    public static boolean checkAppOps(Context context, String op) {
        if (isMiUi(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int checkOp = appOpsManager.checkOp(op, Binder.getCallingUid(), context.getPackageName());
                if (checkOp == AppOpsManager.MODE_IGNORED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否小米系统
     *
     * @param context
     * @return
     */
    public static boolean isMiUi(Context context) {
        Properties properties = new Properties();
        boolean isMiUi;

        String miUi = SharePreferenceUtils.getString(context, "is_mi_ui");
        if (miUi != null) {
            if (TextUtils.equals("1", miUi)) {
                return true;
            } else if (TextUtils.equals("2", miUi)) {
                return false;
            }
        }

        try {
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        isMiUi = properties.getProperty(KEY_MI_UI_VERSION_CODE, null) != null
                || properties.getProperty(KEY_MI_UI_VERSION_NAME, null) != null
                || properties.getProperty(KEY_MI_UI_INTERNAL_STORAGE, null) != null;
        /**
         * 保存系统类型
         */
        SharePreferenceUtils.saveString(context, "is_mi_ui", isMiUi ? "1" : "2");
        return isMiUi;
    }
}

package jingya.com.base_class_module.BaseUtils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Kuky
 */
public class ToastUtils {
    private static Toast mToast;

    public static void showToast(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String msg, int duration) {
        if (mToast == null)
            mToast = Toast.makeText(context, msg, duration);
        else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void showToastCenter(Context context, String msg) {
        showToastCenter(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToastCenter(Context context, String msg, int duration) {
        if (mToast == null)
            mToast = Toast.makeText(context, msg, duration);
        else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}

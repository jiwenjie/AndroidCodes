package jingya.com.base_class_module.BaseUtils;

import android.annotation.SuppressLint;
import android.util.Log;

import jingya.com.base_class_module.BuildConfig;


/**
 * @author Kuky
 */
public class LogUtils {
    private static String className;
    private static String methodName;
    private static int lineNumber;

    private static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    @SuppressLint("DefaultLocale")
    private static String createLog(String msg) {
        return String.format("%s(%s:%d)%s", methodName, className, lineNumber, msg);
    }

    private static void getMethodName(Throwable throwable) {
        className = throwable.getStackTrace()[1].getFileName();
        methodName = throwable.getStackTrace()[1].getMethodName();
        lineNumber = throwable.getStackTrace()[1].getLineNumber();
    }

    public static void e(String msg) {
        if (!isDebuggable())
            return;
        getMethodName(new Throwable());
        Log.e(className, createLog(msg));
    }

    public static void w(String msg) {
        if (!isDebuggable())
            return;
        getMethodName(new Throwable());
        Log.w(className, createLog(msg));
    }

    public static void i(String msg) {
        if (!isDebuggable())
            return;
        getMethodName(new Throwable());
        Log.i(className, createLog(msg));
    }

    public static void d(String msg) {
        if (!isDebuggable())
            return;
        getMethodName(new Throwable());
        Log.d(className, createLog(msg));
    }

    public static void v(String msg) {
        if (!isDebuggable())
            return;
        getMethodName(new Throwable());
        Log.v(className, createLog(msg));
    }
}

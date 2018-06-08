package jingya.com.floatingview;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;

/**
 * @author kuky.
 */
public class FloatingApplication extends Application {
    private static Context mContext;
    private MediaProjectionManager mMediaProjectionManager;
    private Intent mCaptureIntent;
    private int mCaptureCode;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        }
    }

    public static Context getContext() {
        return mContext;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MediaProjectionManager getMediaProjectionManager() {
        return mMediaProjectionManager;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Intent getCaptureIntent() {
        return mCaptureIntent;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setCaptureIntent(Intent captureIntent) {
        this.mCaptureIntent = captureIntent;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public int getCaptureCode() {
        return mCaptureCode;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setCaptureCode(int captureCode) {
        this.mCaptureCode = captureCode;
    }
}

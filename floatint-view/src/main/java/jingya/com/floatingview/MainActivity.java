package jingya.com.floatingview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import jingya.com.base_class_module.BaseUtils.SharePreferenceUtils;
import jingya.com.floatingview.window.FloatWindow;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private static final String SLIDE_TYPE = "slide_type";
    private static final int REQUEST_STORAGE = 1001;
    private static final int REQUEST_MEDIA_PROJECTION = 1002;
    private static final int OVERLAY_PERMISSION_REQUEST = 1003;
    private AlertDialog mOverlayAskDialog;
    private RadioGroup radioContainer;
    private int mCaptureCode = 0;
    private Intent mCaptureIntent;
    private FloatingApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApplication = (FloatingApplication) getApplication();
        radioContainer = findViewById(R.id.radio_container);

        if (!hasOverlayPermission(MainActivity.this))
            overlayPermissionRequest();
        else
            showFloating();
    }

    private void overlayPermissionRequest() {
        mOverlayAskDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Overlay Permission Request")
                .setMessage("Need Overlay Permission")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST);
                        }
                        mOverlayAskDialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        mOverlayAskDialog.show();
    }


    private void showFloating() {
        int type = SharePreferenceUtils.getInt(MainActivity.this, SLIDE_TYPE, FloatWindow.SLIDE_LEFT_TO_RIGHT);

        FloatWindow.getInstance()
                .setSlideType(type)
                .initFloatView()
                .show();

        switch (type) {
            case FloatWindow.SLIDE_LEFT_TO_RIGHT:
                ((RadioButton) findViewById(R.id.left)).setChecked(true);
                break;
            case FloatWindow.SLIDE_RIGHT_TO_LEFT:
                ((RadioButton) findViewById(R.id.right)).setChecked(true);
                break;
            case FloatWindow.SLIDE_TOP_TO_BOTTOM:
                ((RadioButton) findViewById(R.id.top)).setChecked(true);
                break;
            case FloatWindow.SLIDE_BOTTOM_TO_TOP:
                ((RadioButton) findViewById(R.id.bottom)).setChecked(true);
                break;
        }
        radioContainer.setOnCheckedChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestStoragePermission();
        } else {
            mediaSetting();
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        } else {
            mediaSetting();
        }
    }

    private void mediaSetting() {
        if (mCaptureIntent != null && mCaptureCode != 0) {
            mApplication.setCaptureCode(mCaptureCode);
            mApplication.setCaptureIntent(mCaptureIntent);
            startService(new Intent(MainActivity.this, BackgroundService.class));
        } else {
            startActivityForResult(mApplication.getMediaProjectionManager().createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaSetting();
                } else {
                    Toast.makeText(MainActivity.this, "Need EXTRA_STORAGE Permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode != 0 && data != null) {
                    mCaptureCode = resultCode;
                    mCaptureIntent = data;
                    mApplication.setCaptureCode(mCaptureCode);
                    mApplication.setCaptureIntent(mCaptureIntent);
                    startService(new Intent(MainActivity.this, BackgroundService.class));
                }
                break;

            case OVERLAY_PERMISSION_REQUEST:
                if (hasOverlayPermission(MainActivity.this))
                    showFloating();
                else
                    overlayPermissionRequest();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.left:
                FloatWindow.getInstance().killManager();
                FloatWindow.getInstance()
                        .setSlideType(FloatWindow.SLIDE_LEFT_TO_RIGHT)
                        .initFloatView().show();
                SharePreferenceUtils.saveInt(MainActivity.this, SLIDE_TYPE, FloatWindow.SLIDE_LEFT_TO_RIGHT);
                break;

            case R.id.top:
                FloatWindow.getInstance().killManager();
                FloatWindow.getInstance()
                        .setSlideType(FloatWindow.SLIDE_TOP_TO_BOTTOM)
                        .initFloatView().show();
                SharePreferenceUtils.saveInt(MainActivity.this, SLIDE_TYPE, FloatWindow.SLIDE_TOP_TO_BOTTOM);
                break;

            case R.id.right:
                FloatWindow.getInstance().killManager();
                FloatWindow.getInstance()
                        .setSlideType(FloatWindow.SLIDE_RIGHT_TO_LEFT)
                        .initFloatView().show();
                SharePreferenceUtils.saveInt(MainActivity.this, SLIDE_TYPE, FloatWindow.SLIDE_RIGHT_TO_LEFT);
                break;

            case R.id.bottom:
                FloatWindow.getInstance().killManager();
                FloatWindow.getInstance()
                        .setSlideType(FloatWindow.SLIDE_BOTTOM_TO_TOP)
                        .initFloatView().show();
                SharePreferenceUtils.saveInt(MainActivity.this, SLIDE_TYPE, FloatWindow.SLIDE_BOTTOM_TO_TOP);
                break;
        }
    }

    public static boolean hasOverlayPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }
}

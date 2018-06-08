package jingya.com.floatingview;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jingya.com.base_class_module.BaseUtils.ScreenUtils;
import jingya.com.floatingview.utils.BlurUtils;
import jingya.com.floatingview.window.FloatWindow;

public class BackgroundService extends IntentService {
    public static final String CAPTURE_ACTION = "com.kuky.floatingview.action.CaptureBackground";
    private static final int FOREGROUND_ID = 1001;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private int mScreenWidth, mScreenHeight, mScreenDensity;
    private VirtualDisplay mVirtualDisplay;
    private FloatingApplication mFloatingApplication;
    private volatile boolean running;
    private Drawable blurBg;
    private CaptureBackgroundReceiver mReceiver;
    private IntentFilter intentFilter;
    private Runnable mCaptureBackground = new Runnable() {
        @Override
        public void run() {
            mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2);

            mVirtualDisplay = mMediaProjection
                    .createVirtualDisplay("SCREEN_CAPTURE", mScreenWidth, mScreenHeight, mScreenDensity,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);

            /**
             * 需要延时，否则不能截到内容
             */
            Observable.timer(50, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            Bitmap bitmap = getBitmap(mImageReader.acquireLatestImage());

                            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/FloatingView/background.jpeg");
                            if (!imageFile.getParentFile().exists()) {
                                imageFile.getParentFile().mkdirs();
                            }

                            if (!imageFile.exists()) {
                                imageFile.createNewFile();
                            }

                            /**
                             * bitmap 不为空则重新写入文件中，否则调用之前的文件
                             */
                            if (bitmap != null) {
                                FileOutputStream out = new FileOutputStream(imageFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                                out.flush();
                                out.close();

                                if (mVirtualDisplay != null) {
                                    mVirtualDisplay.release();
                                    mVirtualDisplay = null;
                                }
                            }

                            FileInputStream in = new FileInputStream(imageFile);
                            Bitmap background = BitmapFactory.decodeStream(in);
                            blurBg = BlurUtils.doBlur(BackgroundService.this, background, 20, 10);
                            FloatWindow.getInstance().setOnContentScrollListener(new FloatWindow.OnContentScrollListener() {
                                @Override
                                public void onContentScroll(View contentView) {
                                    contentView.setBackground(blurBg);
                                }
                            });

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("BackgroundCapture", "accept: ", throwable);
                        }
                    });
        }
    };

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BackgroundService() {
        super(BackgroundService.class.getSimpleName());
    }

    private Bitmap getBitmap(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        return bitmap;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingApplication = (FloatingApplication) getApplication();
        mMediaProjectionManager = mFloatingApplication.getMediaProjectionManager();
        mScreenWidth = ScreenUtils.getScreenWidth(BackgroundService.this);
        mScreenHeight = ScreenUtils.getScreenHeight(BackgroundService.this);
        mScreenDensity = (int) ScreenUtils.getScreenDensity(BackgroundService.this);
        if (mMediaProjection == null) {
            mMediaProjection = mMediaProjectionManager
                    .getMediaProjection(mFloatingApplication.getCaptureCode(), mFloatingApplication.getCaptureIntent());
        }
        running = true;

        mReceiver = new CaptureBackgroundReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(CAPTURE_ACTION);
        registerReceiver(mReceiver, intentFilter);

        FloatWindow.getInstance().setOnFingerDownListener(new FloatWindow.OnFingerDownListener() {
            @Override
            public void onFingerDown() {
                sendBroadcast(new Intent(CAPTURE_ACTION));
            }
        });
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        startForeground(FOREGROUND_ID, getForegroundNotification(BackgroundService.this));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        running = false;
        unregisterReceiver(mReceiver);
        stopForeground(true);
    }

    public class CaptureBackgroundReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), CAPTURE_ACTION)) {
                new Thread(mCaptureBackground).start();
            }
        }
    }

    private Notification getForegroundNotification(Context context) {
        Notification notification;
        Notification.Builder builder;

        Intent nIntent = new Intent(context, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = getChannel(context, "main_service",
                    context.getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            builder = new Notification.Builder(FloatingApplication.getContext(), "main_service");
        } else {
            builder = new Notification.Builder(FloatingApplication.getContext());
        }

        notification = builder.setContentIntent(PendingIntent.getActivity(context, 0, nIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Floating is running")
                .setShowWhen(false)
                .build();

        notification.flags = Notification.FLAG_NO_CLEAR;

        return notification;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private NotificationChannel getChannel(Context context, String id, CharSequence name, int importance) {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.enableLights(true);
        channel.setLightColor(context.getResources().getColor(R.color.colorRed));
        channel.setShowBadge(false);
        return channel;
    }
}

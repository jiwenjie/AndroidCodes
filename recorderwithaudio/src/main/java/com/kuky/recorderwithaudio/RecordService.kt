package com.kuky.recorderwithaudio

import android.annotation.TargetApi
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.projection.MediaProjection
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.text.format.DateUtils
import com.kuky.baselib.baseUtils.ScreenUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RecordService : Service() {
    private lateinit var mApplication: MediaApplication
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mStopReceiver: StopRecordReceiver
    private lateinit var mFilter: IntentFilter
    private var mDisposable: Disposable? = null
    private var mMediaProjection: MediaProjection? = null
    private var mScreenRecorder: ScreenRecorder? = null
    private var mWidth = 0
    private var mHeight = 0
    private var mDpi = 0
    private var start = 0L

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = application as MediaApplication
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mMediaProjection = mApplication.getMediaProjectManager().getMediaProjection(mApplication.getCaptureCode(), mApplication.getCaptureIntent())
        mStopReceiver = StopRecordReceiver()
        mFilter = IntentFilter()
        mFilter.addAction(STOP_RECORD)
        registerReceiver(mStopReceiver, mFilter)
        mWidth = ScreenUtils.getScreenWidth(this@RecordService)
        mHeight = ScreenUtils.getScreenHeight(this@RecordService)
        mDpi = ScreenUtils.getScreenDensity(this@RecordService).toInt()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mScreenRecorder != null) mScreenRecorder!!.quit()
        mScreenRecorder = ScreenRecorder(mWidth, mHeight, BITRATE, mDpi, mMediaProjection!!, generateFileName())

        Thread(mScreenRecorder).start()
        start = System.currentTimeMillis()
        mDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (mDisposable != null && !mDisposable!!.isDisposed) {
                        val n = generateBuilder(this@RecordService)
                                .setContentText("Record time ${DateUtils.formatElapsedTime((System.currentTimeMillis() - start) / 1000)}")
                                .build()
                        n.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_AUTO_CANCEL
                        mNotificationManager.notify(1, n)
                    }
                })
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }

        unregisterReceiver(mStopReceiver)

        if (mDisposable != null) {
            mDisposable!!.dispose()
            mDisposable = null
        }
    }

    private fun generateFileName(): String {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val videoName = sdf.format(Date()) + ".mp4"
        val file = File(Environment.getExternalStorageDirectory(), "/ScreenRecord/$videoName")

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        return file.absolutePath
    }

    private fun generateBuilder(context: Context): NotificationCompat.Builder {
        val intent = Intent(STOP_RECORD)

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = generateChannel(this@RecordService, "record", "record", NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(this@RecordService, "record")
        } else {
            NotificationCompat.Builder(this@RecordService, "")
        }
        return builder
                .setContentIntent(PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Recording....")
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setLocalOnly(true)
                .setOnlyAlertOnce(true)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun generateChannel(context: Context, id: String, name: CharSequence, importance: Int): NotificationChannel {
        val channel = NotificationChannel(id, name, importance)
        channel.enableLights(true)
        channel.setShowBadge(true)
        channel.lightColor = context.resources.getColor(R.color.colorAccent, context.theme)
        return channel
    }

    inner class StopRecordReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && TextUtils.equals(intent.action, STOP_RECORD)) {
                mScreenRecorder!!.quit()

                if (mDisposable != null) {
                    mDisposable!!.dispose()
                    mDisposable = null
                }

                mNotificationManager.cancel(1)
                stopSelf()
            }
        }
    }

    companion object {
        private const val STOP_RECORD = "com.kuky.media.action.STOP_RECORD"
        private const val BITRATE = 2000000
    }
}

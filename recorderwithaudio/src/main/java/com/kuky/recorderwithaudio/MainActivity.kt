package com.kuky.recorderwithaudio

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.os.Bundle
import com.kuky.base_kt_module.BaseUtils.LogUtils
import com.kuky.base_kt_module.BaseUtils.ToastUtils
import com.kuky.base_kt_module.BaseViews.BaseActivity
import com.kuky.base_kt_module.PermissionListener
import com.kuky.recorderwithaudio.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var mApplication: MediaApplication
    private var mIntent: Intent? = null
    private var mCode = 0
    private lateinit var test: LifecycleTest

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {
        mApplication = application as MediaApplication
        mIntent = mApplication.getCaptureIntent()
        mCode = mApplication.getCaptureCode()

        test = LifecycleTest()
        lifecycle.addObserver(test)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(test)
    }

    override fun setListener() {
        record.setOnClickListener {
            onRuntimePermissionsAsk(arrayOf(android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    object : PermissionListener {
                        override fun onGranted() {
                            mediaSetting()
                        }

                        override fun onDenied(deniedPermissions: List<String>) {
                            ToastUtils.showToast(this@MainActivity, "permission was denied")
                        }
                    })
        }

        another.setOnClickListener { startActivity(Intent(this@MainActivity, Main2Activity::class.java)) }
    }

    private fun mediaSetting() {
        if (mIntent != null && mCode != 0) {
            startService(Intent(this@MainActivity, RecordService::class.java))
        } else {
            startActivityForResult(mApplication.getMediaProjectManager().createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION)
            if (data != null && resultCode != 0) {
                mApplication.setCaptureCode(resultCode)
                mApplication.setCaptureIntent(data)
                startService(Intent(this@MainActivity, RecordService::class.java))
            }
    }

    companion object {
        private const val REQUEST_MEDIA_PROJECTION = 1001
    }

    /**
     * Test Lifecycle
     * need implement LifecycleObserver and add observer when create
     */
    inner class LifecycleTest : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate(lifecycleOwner: LifecycleOwner) {
            LogUtils.e("..CycleOnCreate..")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart(lifecycleOwner: LifecycleOwner) {
            LogUtils.e("..CycleOnStart..")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume(lifecycleOwner: LifecycleOwner) {
            LogUtils.e("..CycleOnResume..")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause(lifecycleOwner: LifecycleOwner) {
            LogUtils.e("..CycleOnPause..")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop(lifecycleOwner: LifecycleOwner) {
            LogUtils.e("..CycleOnStop..")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(lifecycleOwner: LifecycleOwner) {
            LogUtils.e("..CycleOnDestroy..")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        fun onLifeChange(lifecycleOwner: LifecycleOwner, event: Lifecycle.Event) {
            LogUtils.e("..CycleOnAny.. currentEvent:$event, state:${lifecycleOwner.lifecycle.currentState}")
        }
    }
}

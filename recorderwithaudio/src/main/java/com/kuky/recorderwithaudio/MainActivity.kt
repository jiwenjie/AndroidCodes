package com.kuky.recorderwithaudio

import android.content.Intent
import android.os.Bundle
import com.kuky.base_kt_module.PermissionListener
import com.kuky.baselib.baseClass.BaseActivity
import com.kuky.baselib.baseUtils.ToastUtils
import com.kuky.recorderwithaudio.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var mApplication: MediaApplication
    private var mIntent: Intent? = null
    private var mCode = 0

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {
        mApplication = application as MediaApplication
        mIntent = mApplication.getCaptureIntent()
        mCode = mApplication.getCaptureCode()
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
}

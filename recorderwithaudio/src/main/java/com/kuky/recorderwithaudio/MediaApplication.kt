package com.kuky.recorderwithaudio

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager

/**
 * @author kuky.
 * @description
 */
class MediaApplication : Application() {
    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private var mCaptureIntent: Intent? = null
    private var mCaptureCode = 0

    override fun onCreate() {
        super.onCreate()
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    fun getMediaProjectManager(): MediaProjectionManager = mMediaProjectionManager

    fun getCaptureCode(): Int = mCaptureCode

    fun setCaptureCode(code: Int) {
        this.mCaptureCode = code
    }

    fun getCaptureIntent(): Intent? = mCaptureIntent

    fun setCaptureIntent(intent: Intent) {
        this.mCaptureIntent = intent
    }
}
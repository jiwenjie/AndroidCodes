package com.kuky.recorderwithaudio

import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.*
import android.media.projection.MediaProjection
import android.view.Surface
import com.kuky.base_kt_module.BaseUtils.LogUtils
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author kuky.
 * @description
 */
class ScreenRecorder constructor(width: Int = 640, height: Int = 480, bitrate: Int = 2000000, dpi: Int = 1, mp: MediaProjection, path: String) : Runnable {
    private var mWidth = width
    private var mHeight = height
    private var mBitrate = bitrate
    private var mDpi = dpi
    private var mMediaProjection: MediaProjection? = mp
    private var mPath = path
        set(value) {
            if (value.endsWith(".mp4"))
                field = value
            else
                throw IllegalArgumentException("need mp4 file")
        }
    private var mVideoEncoder: MediaCodec? = null
    private var mAudioEncoder: MediaCodec? = null
    private var mAudioRecord: AudioRecord? = null
    private var mMediaMuxer: MediaMuxer? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private lateinit var mSurface: Surface

    private var mMuxerStared = false
    private var mVideoTrackIndex = -1
    private var mAudioTrackIndex = -1
    private var total = 0

    private var mQuit = AtomicBoolean(false)
    private var mVideoBufferInfo = MediaCodec.BufferInfo()
    private var mAudioBufferInfo = MediaCodec.BufferInfo()

    fun quit() = mQuit.set(true)

    override fun run() {
        try {
            prepareEncoder()
            mMediaMuxer = MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            LogUtils.e("MediaMuxer init...")

            mVirtualDisplay = mMediaProjection!!
                    .createVirtualDisplay("ScreenRecorder-display", mWidth, mHeight, mDpi,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mSurface, null, null)
            recordVirtualDisplay()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e("Muxer exception $e")
        } finally {
            release()
        }
    }

    private fun prepareEncoder() {
        /**
         * 视频 format
         */
        val videoFormat = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, mWidth, mHeight)
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate)
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
        mVideoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE)
        mVideoEncoder!!.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        LogUtils.e("video format: $videoFormat")

        /**
         * 音频 format
         */
        val audioFormat = MediaFormat.createAudioFormat(AUDIO_MIME_TYPE, SAMPLE_RATE, 1)
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate)
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectHE)
        mAudioEncoder = MediaCodec.createEncoderByType(AUDIO_MIME_TYPE)
        mAudioEncoder!!.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        LogUtils.e("audio format: $audioFormat")

        mSurface = mVideoEncoder!!.createInputSurface()
        LogUtils.e("surface: $mSurface")
        mVideoEncoder!!.start()
        mAudioEncoder!!.start()
    }

    private fun recordVirtualDisplay() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
        val audioSource = MediaRecorder.AudioSource.MIC // 音频源
        val channelConfig = AudioFormat.CHANNEL_IN_MONO // 音频通道
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT // 音频格式
        val bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, channelConfig, audioFormat) // 缓冲区
        mAudioRecord = AudioRecord(audioSource, SAMPLE_RATE, channelConfig, audioFormat, bufferSizeInBytes)
        mAudioRecord!!.startRecording()

        var bufferReadResult = 0
        val byteSize = if (bufferSizeInBytes > SAMPLES_PER_FRAME) SAMPLES_PER_FRAME * 2 else SAMPLES_PER_FRAME

        while (!mQuit.get()) {
            val buffer = ByteArray(byteSize)
            bufferReadResult = mAudioRecord!!.read(buffer, 0, byteSize)
            if (bufferReadResult == AudioRecord.ERROR_BAD_VALUE || bufferReadResult == AudioRecord.ERROR_INVALID_OPERATION)
                LogUtils.e("Read Error")

            /**
             * 视频处理
             */
            val outputVideoBufferId = mVideoEncoder!!.dequeueOutputBuffer(mVideoBufferInfo, TIME_OUT)
            LogUtils.e("dequeue output video buffer id $outputVideoBufferId")

            if (outputVideoBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetVideoOutputFormat()
            } else if (outputVideoBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            } else if (outputVideoBufferId >= 0 /*&& total == 2*/) {
                if (!mMuxerStared)
                    throw IllegalStateException("MediaMuxer dose not call addTrack(format)")

                encodeToVideoTrack(outputVideoBufferId)
                mVideoEncoder!!.releaseOutputBuffer(outputVideoBufferId, false)
            }

            /**
             * 音频处理
             */
            val outputAudioBufferId = mAudioEncoder!!.dequeueOutputBuffer(mAudioBufferInfo, TIME_OUT)
            LogUtils.e("dequeue output audio buffer id $outputAudioBufferId")

            if (outputAudioBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetAudioOutputFormat()
            } else if (outputAudioBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            } else if (outputAudioBufferId >= 0 && total == 2) {
                if (!mMuxerStared)
                    throw IllegalStateException("MediaMuxer dose not call addTrack(format)")

                val index = mAudioEncoder!!.dequeueInputBuffer(TIME_OUT)

                if (index > 0) {
                    val inputBuffer = mAudioEncoder!!.getInputBuffer(index)
                    inputBuffer.clear()
                    inputBuffer.put(buffer)
                    val size = inputBuffer.limit()
                    mAudioEncoder!!.queueInputBuffer(index, 0, size, mVideoBufferInfo.presentationTimeUs, 0)
                }

                encodeToAudioTrack(outputAudioBufferId)
                mAudioEncoder!!.releaseOutputBuffer(outputAudioBufferId, false)
            }
        }
    }

    private fun resetVideoOutputFormat() {
        if (mMuxerStared)
            throw IllegalStateException("output format already changed!")

        val mediaFormat = mVideoEncoder!!.outputFormat
        mediaFormat.getByteBuffer("csd-0")
        mediaFormat.getByteBuffer("csd-1")
        LogUtils.e("output format changed. format: $mediaFormat")

        mVideoTrackIndex = mMediaMuxer!!.addTrack(mediaFormat)
        total++

        if (total == 2) {
            mMediaMuxer!!.start()
            mMuxerStared = true
            LogUtils.e("muxer start and video track index is $mVideoTrackIndex")
        }
    }

    private fun resetAudioOutputFormat() {
        if (mMuxerStared)
            throw IllegalStateException("output format already changed!")

        val mediaFormat = mAudioEncoder!!.outputFormat
        mAudioTrackIndex = mMediaMuxer!!.addTrack(mediaFormat)
        total++

        if (total == 2) {
            mMediaMuxer!!.start()
            mMuxerStared = true
            LogUtils.e("muxer start and audio track index is $mVideoTrackIndex")
        }
    }

    private fun encodeToVideoTrack(outputVideoBufferId: Int) {
        var encodeData: ByteBuffer? = mVideoEncoder!!.getOutputBuffer(outputVideoBufferId)

        if (mVideoBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0)
            mVideoBufferInfo.size = 0

        if (mVideoBufferInfo.size == 0)
            encodeData = null
        else
            LogUtils.e("got buffer, info: size = ${mVideoBufferInfo.size}, " +
                    "video presentationTimeUs ${mVideoBufferInfo.presentationTimeUs}, " +
                    "offset = ${mVideoBufferInfo.offset}")

        if (encodeData != null) {
            encodeData.position(mVideoBufferInfo.offset)
            encodeData.limit(mVideoBufferInfo.offset + mVideoBufferInfo.size)
            val bytes = ByteArray(encodeData.remaining())
            LogUtils.e("Video bytes length: ${bytes.size}")
            mMediaMuxer!!.writeSampleData(mVideoTrackIndex, encodeData, mVideoBufferInfo)
        }

    }

    private fun encodeToAudioTrack(outputAudioBufferId: Int) {
        var encodeData: ByteBuffer? = mAudioEncoder!!.getOutputBuffer(outputAudioBufferId)

        if (mAudioBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0)
            mAudioBufferInfo.size = 0

        if (mAudioBufferInfo.size == 0)
            encodeData = null
        else
            LogUtils.e("got buffer, info: size = ${mAudioBufferInfo.size}, " +
                    "video presentationTimeUs ${mAudioBufferInfo.presentationTimeUs}, " +
                    "offset = ${mAudioBufferInfo.offset}")

        if (encodeData != null) {
            encodeData.position(mAudioBufferInfo.offset)
            encodeData.limit(mAudioBufferInfo.offset + mAudioBufferInfo.size)
            val bytes = ByteArray(encodeData.remaining())
            LogUtils.e("Audio bytes length: ${bytes.size}")
            mMediaMuxer!!.writeSampleData(mVideoTrackIndex, encodeData, mAudioBufferInfo)
        }
    }

    private fun release() {
        if (mVideoEncoder != null) {
            mVideoEncoder!!.stop()
            mVideoEncoder!!.release()
            mVideoEncoder = null
        }

        if (mAudioEncoder != null) {
            mAudioEncoder!!.stop()
            mAudioEncoder!!.release()
            mAudioEncoder = null
        }

        if (mAudioRecord != null) {
            mAudioRecord!!.stop()
            mAudioRecord!!.release()
            mAudioRecord = null
        }

        if (mVirtualDisplay != null) {
            mVirtualDisplay!!.release()
            mVirtualDisplay = null
        }

        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }

        if (mMediaMuxer != null) {
            val mediaMuxerClass = mMediaMuxer!!.javaClass
            try {
                val state = mediaMuxerClass.getDeclaredField("mState")
                state.isAccessible = true
                val s = state.getInt(mMediaMuxer)
                LogUtils.e("state: $s")
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            mMediaMuxer!!.stop()
            mMediaMuxer!!.release()
            mMediaMuxer = null
        }
    }

    companion object {
        private const val VIDEO_MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC
        private const val AUDIO_MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC
        private const val FRAME_RATE = 30
        private const val I_FRAME_INTERVAL = 10
        private const val SAMPLE_RATE = 44100
        private const val TIME_OUT = 10000L
        private const val SAMPLES_PER_FRAME = 2048
    }
}
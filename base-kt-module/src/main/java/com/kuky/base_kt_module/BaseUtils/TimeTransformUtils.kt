package com.kuky.baselib.baseUtils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author kuky
 * @description
 */
object TimeTransformUtils {

    fun timeStamp2Date(seconds: String, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return if (TextUtils.isEmpty(seconds)) ""
        else sdf.format(Date(java.lang.Long.valueOf("${seconds}000")))
    }

    fun millsTimeStamp2Date(mills: String, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return if (TextUtils.isEmpty(mills)) ""
        else sdf.format(Date(java.lang.Long.valueOf(mills)))
    }

    fun date2TimeStamp(date: String, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return (sdf.parse(date).time / 1000).toString()
    }

    fun date2String(date: Date, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }
}
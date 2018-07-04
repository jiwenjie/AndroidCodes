package com.kuky.baselib.baseUtils

import android.content.Context
import android.os.Environment
import java.io.File
import java.math.BigDecimal

/**
 * @author kuky
 * @description
 */
object CleanMemoryCacheUtils {

    fun cleanAllCaches(context: Context) {
        deleteDir(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteDir(context.externalCacheDir)
        }
    }

    fun getTotalCacheSize(context: Context): String {
        var cacheSize = getFolderSize(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheSize += getFolderSize(context.externalCacheDir)
        }
        return getFormatSize(cacheSize.toDouble())
    }

    private fun deleteDir(cacheDir: File): Boolean {
        if (cacheDir.isDirectory) {
            val children = cacheDir.list()
            children?.map { deleteDir(File(cacheDir, it)) }?.filterNot { it }?.forEach { return false }
        }
        return cacheDir.delete()
    }

    private fun getFolderSize(cacheDir: File): Long {
        var size = 0L
        val fileList = cacheDir.listFiles()

        if (fileList != null) {
            for (f in fileList) {
                size += if (f.isDirectory)
                    getFolderSize(f)
                else
                    f.length()
            }
        }
        return size
    }

    fun getFormatSize(cacheSize: Double): String {
        val kilobyte = cacheSize / 1024
        if (kilobyte < 1) {
            return "0.00KB"
        }

        val megabyte = kilobyte / 1024
        if (megabyte < 1) {
            val decimal = BigDecimal(java.lang.Double.toString(kilobyte))
            return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }

        val gigabyte = megabyte / 1024
        if (gigabyte < 1) {
            val decimal = BigDecimal(java.lang.Double.toString(megabyte))
            return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }

        return BigDecimal(gigabyte).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
    }
}
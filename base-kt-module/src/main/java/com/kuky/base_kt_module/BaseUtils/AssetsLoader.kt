package com.kuky.base_kt_module.BaseUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @author kuky
 * @description asserts 文件加载工具类
 */
object AssetsLoader {

    fun getTextFromAssets(context: Context, fileName: String): String {
        var inputStream: InputStream? = null
        try {
            inputStream = context.resources.assets.open(fileName)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return getText(inputStream)
    }

    private fun getText(inputStream: InputStream?): String {
        var inputReader: InputStreamReader? = null
        var bufferedReader: BufferedReader? = null
        val result = StringBuilder()
        try {
            inputReader = InputStreamReader(inputStream!!)
            bufferedReader = BufferedReader(inputReader)

            var line: String
            do {
                line = bufferedReader.readLine()
                if (line != null) result.append(line) else break
            } while (true)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            if (inputReader != null) {
                try {
                    inputReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result.toString()
    }


    fun getImageFromAssets(context: Context, fileName: String): Bitmap? {
        var image: Bitmap? = null
        val am = context.resources.assets
        try {
            val `is` = am.open(fileName)
            image = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }
}

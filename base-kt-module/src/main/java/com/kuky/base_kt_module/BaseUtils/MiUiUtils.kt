package com.kuky.base_kt_module.BaseUtils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * @author kuky
 * @description
 */
object MiUiUtils {
    private const val KEY_MI_UI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MI_UI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_MI_UI_INTERNAL_STORAGE = "ro.miui.internal.storage"

    @SuppressLint("ObsoleteSdkInt")
    fun checkAppOps(context: Context, op: String): Boolean {
        if (isMiUi(context))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val checkOp = appOpsManager.checkOp(op, Binder.getCallingUid(), context.packageName)
                if (checkOp == AppOpsManager.MODE_IGNORED) return false
            }
        return true
    }

    private fun isMiUi(context: Context): Boolean {
        val properties = Properties()
        val isMiUi: Boolean

        val miUi: String? = SharePreferencesUtils.getString(context, "is_mi_ui")

        if (miUi != null) {
            return TextUtils.equals("1", miUi)
        }

        try {
            properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isMiUi = properties.getProperty(KEY_MI_UI_VERSION_CODE, null) != null
                || properties.getProperty(KEY_MI_UI_VERSION_NAME, null) != null
                || properties.getProperty(KEY_MI_UI_INTERNAL_STORAGE, null) != null

        SharePreferencesUtils.saveString(context, "is_mi_ui", if (isMiUi) "1" else "2")
        return isMiUi
    }
}
package com.kuky.base_kt_module.BaseViews

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.kuky.base_kt_module.ActivityController
import com.kuky.base_kt_module.PermissionListener

/**
 * @author kuky
 * @description
 */
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var mViewBinding: VB
    private var mPermissionListener: PermissionListener? = null

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (enableTransparentStatus()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT &&
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                val winParam: WindowManager.LayoutParams = window.attributes
                val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                winParam.flags = winParam.flags or bits
                window.attributes = winParam
            } else {
                val decorView = window.decorView
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.navigationBarColor = Color.TRANSPARENT
                window.statusBarColor = Color.TRANSPARENT
                if (supportActionBar != null) supportActionBar!!.hide()
            }
        }

        ActivityController.addActivity(this)
        mViewBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initActivity(savedInstanceState)
        setListener()
        handleRxBus()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.removeActivity(this)
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initActivity(savedInstanceState: Bundle?)

    protected open fun enableTransparentStatus(): Boolean = false

    protected open fun setListener() {}

    protected open fun handleRxBus() {}

    protected fun onRuntimePermissionsAsk(permissions: kotlin.Array<String>, listener: PermissionListener) {
        this.mPermissionListener = listener
        val activity = ActivityController.getTopActivity()
        val deniedPermissions: MutableList<String> = mutableListOf()

        permissions
                .filterNot { ContextCompat.checkSelfPermission(activity!!, it) == PackageManager.PERMISSION_GRANTED }
                .forEach { deniedPermissions.add(it) }

        if (deniedPermissions.isEmpty())
            mPermissionListener!!.onGranted()
        else
            ActivityCompat.requestPermissions(activity!!, deniedPermissions.toTypedArray(), 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            val deniedPermissions: MutableList<String> = mutableListOf()
            if (grantResults.isNotEmpty()) {
                grantResults
                        .filter { it != PackageManager.PERMISSION_GRANTED }
                        .mapTo(deniedPermissions) { permissions[it] }

                if (deniedPermissions.isEmpty())
                    mPermissionListener!!.onGranted()
                else
                    mPermissionListener!!.onDenied(deniedPermissions)
            }
        }
    }
}

package com.kuky.baselib.baseMvpClass

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
abstract class BaseMvpActivity<in V : BaseMvpViewImpl, P : BaseMvpPresenter<V>, VB : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var mViewBinding: VB
    protected lateinit var mPresenter: P
    private var mOnPermissionLister: PermissionListener? = null

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (enableTransparentStatus()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
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
        mPresenter = initPresenter()
        mPresenter.attachView(this as V)
        presenterActions()
        setListener()
        handleRxBus()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
        ActivityController.removeActivity(this)
    }

    protected abstract fun initPresenter(): P

    protected abstract fun getLayoutId(): Int

    protected abstract fun initActivity(savedInstanceState: Bundle?)

    protected open fun enableTransparentStatus(): Boolean = false

    protected open fun presenterActions() {}

    protected open fun setListener() {}

    protected open fun handleRxBus() {}

    protected fun onRuntimePermissionsAsk(permissions: Array<String>, permissionListener: PermissionListener) {
        this.mOnPermissionLister = permissionListener
        val activity = ActivityController.getTopActivity()
        val deniedPermissions: MutableList<String> = mutableListOf()

        permissions
                .filterNot { ContextCompat.checkSelfPermission(activity!!, it) == PackageManager.PERMISSION_GRANTED }
                .forEach { deniedPermissions.add(it) }

        if (deniedPermissions.isEmpty())
            mOnPermissionLister!!.onGranted()
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
                        .mapTo(deniedPermissions) { deniedPermissions[it] }

                if (deniedPermissions.isEmpty()) {
                    mOnPermissionLister!!.onGranted()
                } else {
                    mOnPermissionLister!!.onDenied(deniedPermissions)
                }
            }
        }
    }
}

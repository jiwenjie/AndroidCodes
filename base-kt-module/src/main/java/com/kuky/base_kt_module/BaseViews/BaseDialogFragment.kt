package com.kuky.baselib.baseClass

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author kuky
 * @description
 */
abstract class BaseDialogFragment<VB : ViewDataBinding> : DialogFragment() {
    protected lateinit var mViewBinding: VB
    protected var mWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    protected var mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    protected var mUseDefaultWidth = true

    fun setWidth(width: Int, useDefaultWidth: Boolean = true) {
        this.mWidth = width
        this.mUseDefaultWidth = useDefaultWidth
    }

    fun setHeight(height: Int) {
        this.mHeight = height
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        initDialogFragment()
        return mViewBinding.root
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        if (mUseDefaultWidth) mWidth = (dm.widthPixels * 0.8).toInt()
        dialog.window.setLayout(mWidth, mHeight)
    }

    abstract fun getLayoutId(): Int

    fun initDialogFragment() {}
}
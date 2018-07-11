package com.kuky.baselib.baseClass

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.WindowManager
import com.kuky.base_kt_module.R

/**
 * @author kuky
 * @description
 */
abstract class BaseDialog<VB : ViewDataBinding> : Dialog {
    protected var mContext: Context? = null
    protected lateinit var mViewBinding: VB

    constructor(context: Context, themeResId: Int = R.style.AlertDialogStyle)
            : super(context, themeResId) {
        initDialog(context)
    }

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener)
            : super(context, cancelable, cancelListener) {
        initDialog(context)
    }

    private fun initDialog(context: Context) {
        mContext = context
        mViewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutId(), null, false)
        initDialogView()
        setContentView(mViewBinding.root)
        setDialogWidth((mContext!!.resources.displayMetrics.widthPixels * 0.8).toInt())
        setDialogHeight(WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun setDialogHeight(height: Int) {
        val lp = window.attributes
        lp.height = height
        window.attributes = lp
    }

    fun setDialogWidth(width: Int) {
        val lp = window.attributes
        lp.width = width
        window.attributes = lp
    }

    fun setInputMode() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    protected abstract fun getLayoutId(): Int

    protected open fun initDialogView(){}
}
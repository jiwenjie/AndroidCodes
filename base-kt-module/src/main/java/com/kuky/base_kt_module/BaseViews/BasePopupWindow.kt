package com.kuky.baselib.baseClass

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import com.kuky.base_kt_module.R

/**
 * @author kuky
 * @description
 */
abstract class BasePopupWindow<VB : ViewDataBinding>(context: Context, animStyle: Int = R.style.animationForBottomAndBottom,
                                                     width: Int = WindowManager.LayoutParams.MATCH_PARENT,
                                                     height: Int = WindowManager.LayoutParams.WRAP_CONTENT) : PopupWindow(context) {
    protected var mContext: Context? = null
    protected lateinit var mViewBinding: VB

    init {
        initPopupWindow(context, animStyle, width, height)
    }

    private fun initPopupWindow(context: Context, animStyle: Int, width: Int, height: Int) {
        this.mContext = context
        this.mViewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext!!), getLayoutId(), null, false)
        contentView = mViewBinding.root
        initPopupView()
        setBackgroundDrawable(ColorDrawable(0))
        animationStyle = animStyle
        setWidth(width)
        setHeight(height)
        isFocusable = true
        setOnDismissListener({ setBackgroundAlpha(1.0f) })
    }

    fun setBackgroundAlpha(alpha: Float) {
        val lp = (mContext as Activity).window.attributes
        lp.alpha = alpha
        (mContext as Activity).window.attributes = lp
    }

    abstract fun getLayoutId(): Int

    fun initPopupView(){}
}
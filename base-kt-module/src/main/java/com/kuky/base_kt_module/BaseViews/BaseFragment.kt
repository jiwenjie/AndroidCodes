package com.kuky.base_kt_module.BaseViews

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author kuky
 * @description
 */
abstract class BaseFragment<VB : ViewDataBinding> : Fragment() {
    protected lateinit var mViewBinding: VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        initFragment(savedInstanceState)
        setListener()
        handleRxBus()
        return mViewBinding.root
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initFragment(savedInstanceState: Bundle?)

    protected open fun setListener() {}

    protected open fun handleRxBus() {}
}
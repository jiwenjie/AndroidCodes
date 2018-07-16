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
abstract class BaseLazyLoadingFragment<VB : ViewDataBinding> : Fragment() {
    protected lateinit var mViewBinding: VB
    protected var isPageCreated = false
    protected var isPageVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPageCreated = true
        tryLoad()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        initFragment(savedInstanceState)
        setListener()
        handleRxBus()
        return mViewBinding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            isPageVisible = true
            tryLoad()
        } else {
            isPageVisible = false
        }
    }

    private fun tryLoad() {
        if (isPageCreated && isPageVisible) {
            lazyLoading()
            isPageCreated = false
            isPageVisible = false
        }
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initFragment(savedInstanceState: Bundle?)

    protected abstract fun lazyLoading()

    protected open fun setListener() {}

    protected open fun handleRxBus() {}
}

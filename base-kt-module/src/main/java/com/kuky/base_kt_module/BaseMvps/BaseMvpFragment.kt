package com.kuky.base_kt_module.BaseMvps

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author Kuky
 * @description
 */
abstract class BaseMvpFragment<in V : BaseMvpViewImpl, P : BaseMvpPresenter<V>, VB : ViewDataBinding> : Fragment() {
    protected lateinit var mViewBinding: VB
    protected lateinit var mPresenter: P

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        initFragment(savedInstanceState)
        mPresenter = initPresenter()
        mPresenter.attachView(this as V)
        presenterActions()
        setListener()
        handleRxBus()
        return mViewBinding.root
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
    }

    protected abstract fun initPresenter(): P

    protected abstract fun getLayoutId(): Int

    protected abstract fun initFragment(savedInstanceState: Bundle?)

    protected open fun presenterActions(){}

    protected open fun setListener(){}

    protected open fun handleRxBus(){}
}
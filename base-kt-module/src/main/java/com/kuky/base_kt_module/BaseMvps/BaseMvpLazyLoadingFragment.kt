package com.kuky.baselib.baseMvpClass

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus

/**
 * @author kuky
 * @description
 */
abstract class BaseMvpLazyLoadingFragment<in V : BaseMvpViewImpl, P : BaseMvpPresenter<V>, VB : ViewDataBinding> : Fragment() {
    protected lateinit var mViewBinding: VB
    protected lateinit var mPresenter: P
    protected var isPageCreated: Boolean = false
    protected var isPageVisible: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPageCreated = true
        tryLoad()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (enabledEventBus()) EventBus.getDefault().register(this)

        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        initFragment(savedInstanceState)
        mPresenter = initPresenter()
        mPresenter.attachView(this as V)
        presenterActions()
        setListener()
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
        if (enabledEventBus()) EventBus.getDefault().unregister(this)
        mPresenter.detachView()
    }

    private fun tryLoad() {
        if (isPageCreated && isPageVisible) {
            lazyLoading()
            isPageCreated = false
            isPageVisible = false
        }
    }

    abstract fun getLayoutId(): Int

    abstract fun initFragment(savedInstanceState: Bundle?)

    abstract fun initPresenter(): P

    abstract fun lazyLoading()

    fun enabledEventBus(): Boolean = false

    fun presenterActions(){}

    fun setListener(){}
}

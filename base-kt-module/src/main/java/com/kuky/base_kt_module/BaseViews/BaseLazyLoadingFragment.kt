package com.kuky.baselib.baseClass

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
        if (enabledEventBus()) EventBus.getDefault().register(this)
        initFragment(savedInstanceState)
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

    override fun onDestroy() {
        super.onDestroy()
        if (enabledEventBus()) EventBus.getDefault().unregister(this)
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

    abstract fun lazyLoading()

    fun enabledEventBus(): Boolean = false

    fun setListener() {}
}

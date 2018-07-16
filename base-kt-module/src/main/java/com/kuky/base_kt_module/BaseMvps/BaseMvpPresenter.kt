package com.kuky.base_kt_module.BaseMvps


/**
 * @author kuky
 * @description
 */
abstract class BaseMvpPresenter<in V : BaseMvpViewImpl> {
    private var mView: V? = null

    fun attachView(view: V) {
        this.mView = view
    }

    fun detachView() {
        this.mView = null
    }

    fun onResume() {

    }

    fun onStop() {

    }

    fun onPause() {

    }
}
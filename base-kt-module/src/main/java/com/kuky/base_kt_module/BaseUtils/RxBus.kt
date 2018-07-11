package com.kuky.base_kt_module.BaseUtils

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * @author kuky.
 * @description
 */
class RxBus private constructor() {
    private var mSubscriptionMap: HashMap<String, CompositeDisposable>? = null
    // toSerialized 用于保证线程安全
    private val mSubject: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    companion object {
        val mBus: RxBus by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RxBus() }
        private val TAG = RxBus::class.java.simpleName
    }

    /**
     * 发送事件
     *
     * @param any
     */
    fun post(any: Any) = mSubject.onNext(any)

    /**
     * 默认的订阅方法
     *
     * @param type
     * @param next
     * @param <T>
     * @return
     */
    fun <T> doSubscribe(type: Class<T>, next: Consumer<T>): Disposable = doSubscribe(type, next, Consumer { t -> Log.e(TAG, "Exception", t) })

    fun <T> doSubscribe(type: Class<T>, next: Consumer<T>, error: Consumer<Throwable>):
            Disposable = getObservable(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(next, error)

    fun <T> doSubscribe(type: Class<T>, next: Consumer<T>, error: Consumer<Throwable>, complete: Action)
            : Disposable = getObservable(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(next, error, complete)

    /**
     * 是否有观察者
     *
     * @return
     */
    fun hasObservable(): Boolean = mSubject.hasObservers()

    /**
     * 返回指定类型的 Flowable
     *
     * @param type
     * @param <T>
     * @return
     */
    fun <T> getObservable(type: Class<T>): Flowable<T> = mSubject.toFlowable(BackpressureStrategy.BUFFER).ofType(type)

    /**
     * 保存订阅的 disposable
     *
     * @param o
     * @param disposable
     */
    fun addSubscription(any: Any, disposable: Disposable) {
        if (mSubscriptionMap == null) mSubscriptionMap = HashMap()
        val key = any.javaClass.simpleName

        if (mSubscriptionMap!![key] != null) mSubscriptionMap!![key]!!.add(disposable)
        else {
            val disposables = CompositeDisposable()
            disposables.add(disposable)
            mSubscriptionMap!!.plus(Pair(key, disposables))
        }
    }

    /**
     * 默认事件订阅
     *
     * @param any
     * @param type
     * @param next
     * @param <T>
     */
    fun <T> register(any: Any, type: Class<T>, next: Consumer<T>) = addSubscription(any, doSubscribe(type, next))

    fun <T> register(any: Any, type: Class<T>, next: Consumer<T>, error: Consumer<Throwable>) = addSubscription(any, doSubscribe(type, next, error))

    fun <T> register(any: Any, type: Class<T>, next: Consumer<T>, error: Consumer<Throwable>, complete: Action) = addSubscription(any, doSubscribe(type, next, error, complete))

    /**
     * 取消订阅
     *
     * @param any
     */
    fun unregister(any: Any) {
        if (mSubscriptionMap == null) return

        val key = any.javaClass.simpleName

        if (!mSubscriptionMap!!.contains(key)) return

        if (mSubscriptionMap!![key] != null) {
            mSubscriptionMap!![key]!!.dispose()
            mSubscriptionMap!!.remove(key)
        }
    }
}
package jingya.com.base_class_module.BaseUtils;

import android.util.Log;

import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author kuky.
 * @description
 */
public class RxBus {
    private static final String TAG = "RxBus";
    private static volatile RxBus mBus;
    private HashMap<String, CompositeDisposable> mSubscriptionMap;
    private Subject<Object> mSubject;

    private RxBus() {
        // toSerialized 用于保证线程安全
        mSubject = PublishSubject.create().toSerialized();
    }

    public static RxBus getDefault() {
        if (mBus == null)
            synchronized (RxBus.class) {
                if (mBus == null)
                    mBus = new RxBus();
            }
        return mBus;
    }

    /**
     * 发送事件
     *
     * @param o
     */
    public void post(Object o) {
        mSubject.onNext(o);
    }

    /**
     * 默认的订阅方法
     *
     * @param type
     * @param next
     * @param <T>
     * @return
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next) {
        return doSubscribe(type, next, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, "Exception: ", throwable);
            }
        });
    }

    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error, Action complete) {
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error, complete);
    }

    /**
     * 是否有观察者
     *
     * @return
     */
    public boolean hasObservable() {
        return mSubject.hasObservers();
    }

    /**
     * 返回指定类型的 Flowable
     *
     * @param type
     * @param <T>
     * @return
     */
    public <T> Flowable<T> getObservable(Class<T> type) {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER).ofType(type);
    }

    /**
     * 保存订阅的 disposable
     *
     * @param o
     * @param disposable
     */
    public void addSubscription(Object o, Disposable disposable) {
        if (mSubscriptionMap == null) mSubscriptionMap = new HashMap<>();

        String key = o.getClass().getSimpleName();

        if (mSubscriptionMap.get(key) != null)
            mSubscriptionMap.get(key).add(disposable);
        else {
            // 用于保存多个 disposable
            CompositeDisposable disposables = new CompositeDisposable();
            disposables.add(disposable);
            mSubscriptionMap.put(key, disposables);
        }
    }

    /**
     * 默认事件订阅
     * 如果不使用默认方法，通过 {@link #getObservable(Class)} 获取 Flowable 对象，自动切换线程
     * 并将生成的 disposable 对象通过调用 {@link #addSubscription(Object, Disposable)} 添加到 map 中
     *
     * @param o
     * @param type
     * @param next
     * @param <T>
     */
    public <T> void register(Object o, Class<T> type, Consumer<T> next) {
        addSubscription(o, doSubscribe(type, next));
    }

    public <T> void register(Object o, Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        addSubscription(o, doSubscribe(type, next, error));
    }

    public <T> void register(Object o, Class<T> type, Consumer<T> next, Consumer<Throwable> error, Action complete) {
        addSubscription(o, doSubscribe(type, next, error, complete));
    }

    /**
     * 取消订阅
     *
     * @param o
     */
    public void unregister(Object o) {
        if (mSubscriptionMap == null) return;

        String key = o.getClass().getSimpleName();

        if (!mSubscriptionMap.containsKey(key)) return;

        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).dispose();
            mSubscriptionMap.remove(key);
        }
    }
}

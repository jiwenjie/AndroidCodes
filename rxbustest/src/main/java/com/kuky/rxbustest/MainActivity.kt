package com.kuky.rxbustest

import android.content.Intent
import android.os.Bundle
import com.kuky.base_kt_module.BaseUtils.RxBus
import com.kuky.baselib.baseClass.BaseActivity
import com.kuky.baselib.baseUtils.LogUtils
import com.kuky.rxbustest.databinding.ActivityMainBinding
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {
        to_another.setOnClickListener({
            startActivity(Intent(this@MainActivity, Main2Activity::class.java))
        })
    }

    override fun handleRxBus() {
        RxBus.mBus.register(this@MainActivity, String::class.java, Consumer { t ->
            LogUtils.e(t)
            result_tv.text = "String Back"
        })

        RxBus.mBus.register(this@MainActivity, Number::class.java, Consumer { t ->
            LogUtils.e("Number $t")
            result_tv.text = "Number Back"
        })

        RxBus.mBus.register(this@MainActivity, Main2Activity.NormalEvent::class.java, Consumer { t ->
            LogUtils.e(t.toString())
            result_tv.text = "Event Back"
        })

        val disposable = RxBus.mBus.getObservable(Main2Activity.ThreadEvent::class.java)
                .subscribeOn(Schedulers.io())
                .doOnNext({ LogUtils.e("doOnNext01 Thread ${Thread.currentThread().name}") })
                .observeOn(Schedulers.newThread())
                .doOnNext({ LogUtils.e("doOnNext02 Thread ${Thread.currentThread().name}") })
                .observeOn(Schedulers.io())
                .subscribe({ t ->
                    LogUtils.e(t.toString())
                    LogUtils.e("CurrentThread ${Thread.currentThread().name}")
                })
        RxBus.mBus.addSubscription(this@MainActivity, disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.mBus.unregister(this@MainActivity)
    }
}

package com.kuky.rxbustest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kuky.base_kt_module.BaseUtils.RxBus
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        string_back.setOnClickListener({
            RxBus.mBus.post("1")
            RxBus.mBus.post("2")
            finish()
        })

        number_back.setOnClickListener({
            RxBus.mBus.post(Int.MAX_VALUE)
            RxBus.mBus.post(10f)
            finish()
        })

        event_back.setOnClickListener({
            RxBus.mBus.post(NormalEvent(20, "Sam"))
            RxBus.mBus.post(NormalEvent(30, "Jim"))
            finish()
        })

        thread_back.setOnClickListener({
            RxBus.mBus.post(ThreadEvent("Thread 01"))
            finish()
        })
    }

    class NormalEvent constructor(var age: Int, var name: String) {

        override fun toString(): String {
            return "NormalEvent(age=$age, name='$name')"
        }
    }

    class ThreadEvent constructor(var name: String) {
        override fun toString(): String {
            return "ThreadEvent(name='$name')"
        }
    }
}

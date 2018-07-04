package com.kuky.baselib.baseUtils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.Toast

/**
 * @author kuky
 * @description
 */
object ToastUtils {
    private var mToast: Toast? = null

    @SuppressLint("ShowToast")
    fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, length)
        } else {
            mToast!!.setText(msg)
        }

        mToast!!.show()
    }

    @SuppressLint("ShowToast")
    fun showCenterToast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, length)
        } else {
            mToast!!.setText(msg)
        }

        mToast!!.setGravity(Gravity.CENTER, 0, 0)
        mToast!!.show()
    }

    fun cancelToast() {
        if (mToast != null) {
            mToast!!.cancel()
            mToast = null
        }
    }
}
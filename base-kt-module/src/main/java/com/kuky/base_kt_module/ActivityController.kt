package com.kuky.base_kt_module

import android.app.Activity

/**
 * @author kuky.
 * @description
 */
object ActivityController {

    private val activities = ArrayList<Activity>()

    /**
     * onCreate 调用
     */
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    /**
     * onDestroy 调用
     */
    fun removeActivity(activity: Activity) {
        if (activities.contains(activity)) {
            activities.remove(activity)
            activity.finish()
        }
    }

    /**
     * 获取栈顶 Activity
     */
    fun getTopActivity(): Activity? = if (activities.isEmpty()) null else activities[activities.size - 1]

    /**
     * 关闭所有 Activity
     */
    fun finishAll() {
        for (a in activities)
            if (!a.isFinishing) a.finish()
    }
}
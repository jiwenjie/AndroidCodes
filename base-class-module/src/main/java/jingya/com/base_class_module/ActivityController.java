package jingya.com.base_class_module;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuky
 * @description Activity 管理类
 */
public class ActivityController {
    private static List<Activity> activities = new ArrayList<>();

    /**
     * onCreate 调用
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * onDestroy 调用
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
        activity.finish();
    }

    /**
     * 获取栈顶 Activity
     *
     * @return
     */
    public static Activity getTopActivity() {
        if (!activities.isEmpty()) {
            return activities.get(activities.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * 关闭所有 Activity
     */
    public static void finishAll() {
        if (activities.isEmpty()) {
            return;
        }

        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}

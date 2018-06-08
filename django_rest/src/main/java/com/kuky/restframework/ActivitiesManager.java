package com.kuky.restframework;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuky
 */
public class ActivitiesManager {
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
        activity.finish();
    }

    public static Activity getTopActivity() {
        if (activities != null && !activities.isEmpty()) {
            return activities.get(activities.size() - 1);
        } else {
            return null;
        }
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}

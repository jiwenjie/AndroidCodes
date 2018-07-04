package com.kuky.ios12_notification;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author kuky
 * @description
 */
public class NotificationHelper {
    private static long getTodayBeginTime() {
        Calendar todayBeginCalendar = Calendar.getInstance();
        todayBeginCalendar.set(
                todayBeginCalendar.get(Calendar.YEAR),
                todayBeginCalendar.get(Calendar.MONTH),
                todayBeginCalendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        return todayBeginCalendar.getTimeInMillis();
    }

    public static String formatPostTime(Context context, long postTime) {
        String time = "";

        long todayBeginTime = getTodayBeginTime();
        if (todayBeginTime - postTime < 0) {

            long pastTime = System.currentTimeMillis() - postTime;
            try {
                int minutes = (int) (pastTime / 60000);
                int days = (int) (pastTime / 86400000);
                int hours = (int) (pastTime / 3600000);

                if (days > 0) {
                    time = days + context.getResources().getString(R.string.notification_time_days);
                } else if (hours > 0) {
                    time = hours + context.getResources().getString(R.string.notification_time_hours);
                } else if (minutes > 0) {
                    time = minutes + context.getResources().getString(R.string.notification_time_minutes);
                } else {
                    time = context.getResources().getString(R.string.notification_time_now);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            DateFormat dateFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
            time = dateFormat.format(new Date(postTime));
        }

        return time;
    }

    private static String getWeek(Context context, long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        int weekId = calendar.get(Calendar.DAY_OF_WEEK);
        weekId = weekId - 1;
        String week = "";

        try {
            String[] weeks = context.getResources().getStringArray(R.array.week_names);

            if (weekId < 0) {
                weekId = 0;
            }

            if (weeks.length != 7 || weekId < 0 || weekId >= 7) {
                return week;
            }
            week = weeks[weekId];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return week;
    }
}

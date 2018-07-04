package com.kuky.ios12_notification.entity;


import android.content.Context;

import com.kuky.ios12_notification.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification {

    private String packageName = "";
    private String appName = "";
    private String content = "";
    private String title = "";
    private long postTime = 0;
    private boolean clearable = false;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        if (packageName == null) {
            return;
        }
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        if (appName == null) {
            return;
        }
        this.appName = appName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null) {
            return;
        }
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) {
            return;
        }
        this.title = title;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public boolean isClearable() {
        return clearable;
    }

    public void setClearable(boolean clearable) {
        this.clearable = clearable;
    }

    public Notification(String packageName, String appName, String content, String title, long postTime, boolean clearable) {
        this.packageName = packageName;
        this.appName = appName;
        this.content = content;
        this.title = title;
        this.postTime = postTime;
        this.clearable = clearable;
    }

    public Notification() {
    }

    @Override
    public String toString() {
        return "Notification{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", postTime=" + postTime +
                ", clearable=" + clearable +
                '}';
    }
}

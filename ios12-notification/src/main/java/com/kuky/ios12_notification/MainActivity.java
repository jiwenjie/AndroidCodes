package com.kuky.ios12_notification;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.kuky.ios12_notification.adapter.ExpandNotificationAdapter;
import com.kuky.ios12_notification.databinding.ActivityMainBinding;
import com.kuky.ios12_notification.entity.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jingya.com.base_class_module.BaseUtils.AssetsLoader;
import jingya.com.base_class_module.BaseUtils.LogUtils;
import jingya.com.base_class_module.BaseUtils.ToastUtils;
import jingya.com.base_class_module.BaseViews.BaseActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    public static final String BLANK_APPS = "com.expand.expand_blank_type";
    private ExpandNotificationAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        mAdapter = new ExpandNotificationAdapter(MainActivity.this);
        mViewBinding.notificationList.setBackground(new BitmapDrawable(getResources(),
                AssetsLoader.getImageFromAssets(MainActivity.this, "background.jpg")));

        mViewBinding.notificationList.setAdapter(mAdapter);
        mViewBinding.notificationList
                .addHeaderView(LayoutInflater.from(this)
                        .inflate(R.layout.header_layout, mViewBinding.notificationList, false));

        mAdapter.setOnClearAllOutmodedListener(new ExpandNotificationAdapter.OnClearAllOutmodedListener() {
            @Override
            public void onClearOutmoded() {
                ToastUtils.showToast(MainActivity.this, "Clear Outmoded Notifications");
            }
        });

        mAdapter.setOnClearGroupsListener(new ExpandNotificationAdapter.OnClearGroupsListener() {
            @Override
            public void onClearGroups(String appName) {
                ToastUtils.showToast(MainActivity.this, "Clear " + appName + " Groups Notifications");
            }
        });

        updateNotifications();
    }

    /**
     * 模拟数据，实际数据根据 NotificationService 获取
     * 获取通知存入数据库，根据 post_time 进行降序排序
     */
    private void updateNotifications() {
        List<String> apps = new ArrayList<>();
        apps.add("WeChat");
        apps.add("QQ");

        List<String> outmodedApps = new ArrayList<>();
        outmodedApps.add("FaceBook");
        outmodedApps.add("Twitter");
        outmodedApps.add("WhatsApp");

        List<List<Notification>> nGroups = new ArrayList<>();
        for (int i = 0; i < apps.size(); i++) {
            nGroups.add(notificationGen());
        }

        List<List<Notification>> oGroups = new ArrayList<>();
        for (int i = 0; i < outmodedApps.size(); i++) {
            oGroups.add(notificationGen());
        }

        /**
         * 空的列表，为了显示 通知中心 作为新旧通知的分割线
         */
        List<List<Notification>> blankGroups = new ArrayList<>();
        blankGroups.add(new ArrayList<Notification>());

        /**
         * 整体列表
         */
        List<List<Notification>> allGroups = new ArrayList<>();
        List<String> allApps = new ArrayList<>();

        /**
         * 新通知分组
         */
        allApps.addAll(apps);
        allGroups.addAll(nGroups);

        /**
         * 有旧通知才添加
         */
        if (!oGroups.isEmpty()) {
            // 通知中心 --> 新旧通知的分割线
            allGroups.addAll(blankGroups);
            allApps.add(BLANK_APPS);

            allApps.addAll(outmodedApps);
            allGroups.addAll(oGroups);
        }

        mAdapter.setGroups(allApps, allGroups);
    }

    private List<Notification> notificationGen() {
        List<Notification> ns = new ArrayList<>();

        int count = new Random().nextInt(3) + 4;

        for (int i = 0; i < count; i++) {
            Notification n = new Notification();
            n.setPostTime(System.currentTimeMillis());
            n.setTitle("Fourth of July BBQ Summer Bash");
            n.setContent("Invitation From Smeeta Singpuri \nJuly 4, 2018 at 2:00 PM");
            n.setClearable(true);
            ns.add(n);
        }
        return ns;
    }
}

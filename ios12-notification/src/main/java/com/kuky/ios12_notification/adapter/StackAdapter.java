package com.kuky.ios12_notification.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kuky.ios12_notification.NotificationHelper;
import com.kuky.ios12_notification.R;
import com.kuky.ios12_notification.entity.Notification;

import java.util.List;

/**
 * @author kuky.
 * @description 堆叠视图适配器
 */
public class StackAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Notification> notifications;

    public StackAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications == null ? 0 : notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExpandNotificationAdapter.NotificationParentHolder parentHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.stack_notification_parent, parent, false);
            parentHolder = new ExpandNotificationAdapter.NotificationParentHolder();
            parentHolder.parentIcon = convertView.findViewById(R.id.stack_app_icon);
            parentHolder.parentAppName = convertView.findViewById(R.id.stack_app_name);
            parentHolder.parentPostTime = convertView.findViewById(R.id.stack_post_time);
            parentHolder.parentTitle = convertView.findViewById(R.id.stack_notification_title);
            parentHolder.parentContent = convertView.findViewById(R.id.stack_notification_content);
            parentHolder.parentCount = convertView.findViewById(R.id.stack_more_count);
            convertView.setTag(parentHolder);
        } else {
            parentHolder = (ExpandNotificationAdapter.NotificationParentHolder) convertView.getTag();
        }

        /**
         * 始终设置为列表第一个值
         */
        Notification n = notifications.get(0);
        parentHolder.parentAppName.setText(n.getAppName());
        parentHolder.parentPostTime.setText(NotificationHelper.formatPostTime(context, n.getPostTime()));
        parentHolder.parentTitle.setText(n.getTitle());
        parentHolder.parentContent.setText(n.getContent());

        if (getCount() > 1) {
            parentHolder.parentCount.setVisibility(View.VISIBLE);
            parentHolder.parentCount.setText((getCount() - 1) + " more notifications");
        } else {
            parentHolder.parentCount.setVisibility(View.GONE);
        }

        return convertView;
    }
}

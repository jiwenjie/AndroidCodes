package com.kuky.ios12_notification.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.kuky.ios12_notification.NotificationHelper;
import com.kuky.ios12_notification.R;
import com.kuky.ios12_notification.entity.Notification;
import com.kuky.ios12_notification.widget.StackLayout;

import java.util.List;

import jingya.com.base_class_module.BaseUtils.LogUtils;


/**
 * @author kuky.
 * @description 通知分组列表适配器
 */
public class ExpandNotificationAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> appNames;
    private List<List<Notification>> notificationGroups;
    private OnClearAllOutmodedListener onClearAllOutmodedListener;
    private OnClearGroupsListener onClearGroupsListener;

    public ExpandNotificationAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
    }

    public void setGroups(List<String> appNames, List<List<Notification>> notificationGroups) {
        this.appNames = appNames;
        this.notificationGroups = notificationGroups;
        notifyDataSetChanged();
    }

    /**
     * 清理全部旧通知回调接口
     */
    public interface OnClearAllOutmodedListener {
        void onClearOutmoded();
    }

    /**
     * 清理通知组回调接口
     */
    public interface OnClearGroupsListener {
        void onClearGroups(String appName);
    }

    public void setOnClearAllOutmodedListener(OnClearAllOutmodedListener listener) {
        this.onClearAllOutmodedListener = listener;
    }

    public void setOnClearGroupsListener(OnClearGroupsListener listener) {
        this.onClearGroupsListener = listener;
    }

    @Override
    public int getGroupCount() {
        return appNames == null ? 0 : appNames.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Notification> notifications = notificationGroups.get(groupPosition);
        return notifications == null ? 0 : notifications.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return appNames.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return notificationGroups.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        NotificationParentHolder parentHolder = null;

        if (convertView == null) {
            parentHolder = new NotificationParentHolder();
            convertView = inflater.inflate(R.layout.list_notification_parent, parent, false);
            parentHolder.topCollapseItem = convertView.findViewById(R.id.top_collapse_item);
            parentHolder.topAppNames = convertView.findViewById(R.id.top_app_name);
            parentHolder.topCollapse = convertView.findViewById(R.id.top_collapse);
            parentHolder.topGroupClear = convertView.findViewById(R.id.top_group_clear);

            parentHolder.stacks = convertView.findViewById(R.id.stacks);
            parentHolder.linear = convertView.findViewById(R.id.linear);
            parentHolder.parentIcon = convertView.findViewById(R.id.parent_app_icon);
            parentHolder.parentAppName = convertView.findViewById(R.id.parent_app_name);
            parentHolder.parentPostTime = convertView.findViewById(R.id.parent_post_time);
            parentHolder.parentTitle = convertView.findViewById(R.id.parent_notification_title);
            parentHolder.parentContent = convertView.findViewById(R.id.parent_notification_content);
            parentHolder.parentCount = convertView.findViewById(R.id.parent_more_count);

            parentHolder.blankItem = convertView.findViewById(R.id.blank_item);
            parentHolder.notificationCenter = convertView.findViewById(R.id.notification_center);
            parentHolder.clearOutmoded = convertView.findViewById(R.id.clear_outmoded);

            convertView.setTag(parentHolder);
        } else {
            parentHolder = (NotificationParentHolder) convertView.getTag();
        }

        List<Notification> ns = notificationGroups.get(groupPosition);
        int count = ns.size();

        if (count > 1) {
            StackAdapter sa = new StackAdapter(context, ns);
            parentHolder.stacks.setVisibility(View.VISIBLE);
            parentHolder.stacks.setAdapter(sa);
            parentHolder.linear.setVisibility(View.GONE);
            parentHolder.blankItem.setVisibility(View.GONE);
        } else if (count == 1) {
            parentHolder.stacks.setVisibility(View.GONE);
            parentHolder.linear.setVisibility(View.VISIBLE);
            parentHolder.blankItem.setVisibility(View.GONE);
            /**
             * 单条消息关闭分组功能
             */
            ((ExpandableListView) parent).collapseGroup(groupPosition);
        } else {
            parentHolder.blankItem.setVisibility(View.VISIBLE);
            parentHolder.stacks.setVisibility(View.GONE);
            parentHolder.linear.setVisibility(View.GONE);
            ((ExpandableListView) parent).collapseGroup(groupPosition);
        }

        if (count > 0) {
            Notification topNotification = ns.get(0);
            parentHolder.topAppNames.setText(appNames.get(groupPosition));
            parentHolder.parentAppName.setText(appNames.get(groupPosition));
            parentHolder.parentPostTime.setText(NotificationHelper.formatPostTime(context, topNotification.getPostTime()));
            parentHolder.parentTitle.setText(topNotification.getTitle());
            parentHolder.parentContent.setText(topNotification.getContent());
        } else {
            parentHolder.notificationCenter.setText("Notifications Center");
        }

        /**
         * 单条不显示更多的数量，否则显示更多的通知数量
         */
        if (count > 0) {
            if (!isExpanded) {
                parentHolder.topCollapseItem.setVisibility(View.GONE);
                parentHolder.stacks.setVisibility(count > 1 ? View.VISIBLE : View.GONE);
                parentHolder.linear.setVisibility(count > 1 ? View.GONE : View.VISIBLE);
                parentHolder.parentCount.setVisibility(count > 1 ? View.VISIBLE : View.GONE);
                parentHolder.parentCount.setText((count - 1) + " more notifications");
            } else {
                parentHolder.topCollapseItem.setVisibility(count > 1 ? View.VISIBLE : View.GONE);
                parentHolder.linear.setVisibility(View.VISIBLE);
                parentHolder.stacks.setVisibility(View.GONE);
                parentHolder.parentCount.setVisibility(View.GONE);
            }
        }

        /**
         * 折叠通知
         */
        parentHolder.topCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExpandableListView) parent).collapseGroup(groupPosition);
            }
        });

        /**
         * 清理组
         */
        if (onClearGroupsListener != null) {
            parentHolder.topGroupClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClearGroupsListener.onClearGroups(appNames.get(groupPosition));
                }
            });
        }

        /**
         * 清理全部旧通知
         */
        if (onClearAllOutmodedListener != null) {
            parentHolder.clearOutmoded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClearAllOutmodedListener.onClearOutmoded();
                }
            });
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        NotificationSubHolder subHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_notification_sub, parent, false);
            subHolder = new NotificationSubHolder();
            subHolder.subContainer = convertView.findViewById(R.id.sub_container);
            subHolder.subIcon = convertView.findViewById(R.id.sub_app_icon);
            subHolder.subAppName = convertView.findViewById(R.id.sub_app_name);
            subHolder.subPostTime = convertView.findViewById(R.id.sub_post_time);
            subHolder.subTitle = convertView.findViewById(R.id.sub_notification_title);
            subHolder.subContent = convertView.findViewById(R.id.sub_notification_content);
            convertView.setTag(subHolder);
        } else {
            subHolder = (NotificationSubHolder) convertView.getTag();
        }

        Notification notification = notificationGroups.get(groupPosition).get(childPosition);
        subHolder.subAppName.setText(appNames.get(groupPosition));
        subHolder.subPostTime.setText(NotificationHelper.formatPostTime(context, notification.getPostTime()));
        subHolder.subTitle.setText(notification.getTitle());
        subHolder.subContent.setText(notification.getContent());

        subHolder.subContainer.setVisibility(childPosition > 0 ? View.VISIBLE : View.GONE);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public static class NotificationParentHolder {
        StackLayout stacks;
        LinearLayout linear, topCollapseItem, blankItem;
        ImageView parentIcon, topGroupClear, clearOutmoded;
        TextView topAppNames, topCollapse, parentAppName, parentPostTime, parentTitle, parentContent, parentCount, notificationCenter;
    }

    public static class NotificationSubHolder {
        LinearLayout subContainer;
        ImageView subIcon;
        TextView subAppName, subPostTime, subTitle, subContent;
    }
}

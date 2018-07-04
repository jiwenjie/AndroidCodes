package jingya.com.iosnotificationapplication;

/**
 * @author kuky
 * @description
 */
public class NotificationBean {
    private String notificationContent;
    private boolean isNewNotification;

    public NotificationBean() {
    }

    public NotificationBean(String notificationContent, boolean isNewNotification) {
        this.notificationContent = notificationContent;
        this.isNewNotification = isNewNotification;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public boolean isNewNotification() {
        return isNewNotification;
    }

    public void setNewNotification(boolean newNotification) {
        isNewNotification = newNotification;
    }
}

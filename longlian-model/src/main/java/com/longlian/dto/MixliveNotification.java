package com.longlian.dto;

/**
 * Created by liuhan on 2017-11-18.
 */
public class MixliveNotification {
    private String NotificationType;
    private String NotificationId;
//    private MixliveNotificationData mixliveNotificationData;

    public String getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(String notificationType) {
        NotificationType = notificationType;
    }

    public String getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(String notificationId) {
        NotificationId = notificationId;
    }

//    public MixliveNotificationData getMixliveNotificationData() {
//        return mixliveNotificationData;
//    }
//
//    public void setMixliveNotificationData(MixliveNotificationData mixliveNotificationData) {
//        this.mixliveNotificationData = mixliveNotificationData;
//    }
}

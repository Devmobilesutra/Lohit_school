package com.mobilesutra.SMS_Lohit.model;

/**
 * Created by Ganesh Borse on 25/04/2017.
 */
public class DTONotification {
    String notification_id, notification_title, notification_text, expiry_date;

    public DTONotification(String notification_id, String notification_title, String notification_text, String expiry_date) {
        this.notification_id = notification_id;
        this.notification_title = notification_title;
        this.notification_text = notification_text;
        this.expiry_date = expiry_date;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public String getNotification_title() {
        return notification_title;
    }

    public String getNotification_text() {
        return notification_text;
    }

    public String getExpiry_date() {
        return expiry_date;
    }
}

package com.mobilesutra.SMS_Lohit.model;

/**
 * Created by Ganesh Borse on 04/05/2017.
 */
public class DTOEvents {

    String event_id = "", school_id = "", teacher_id = "", event_date = "", event_description = "",
            event_title = "", image_url = "";

    public DTOEvents(String event_id, String school_id, String teacher_id, String event_date,
                               String event_description, String event_title, String image_url) {
        this.event_id = event_id;
        this.school_id = school_id;
        this.teacher_id = teacher_id;
        this.event_date = event_date;
        this.event_description = event_description;
        this.event_title = event_title;
        this.image_url = image_url;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getEvent_description() {
        return event_description;
    }

    public String getEvent_title() {
        return event_title;
    }

    public String getImage_url() {
        return image_url;
    }
}

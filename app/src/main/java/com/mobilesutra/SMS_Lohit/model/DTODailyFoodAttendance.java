package com.mobilesutra.SMS_Lohit.model;

/**
 * Created by Ganesh Borse on 19/04/2017.
 */
public class DTODailyFoodAttendance {
    String teacher_id = "", school_id = "", food_id = "", other_text = "", boys_count = "", girls_count = "", latitude = "",
            longitude = "", date = "", time = "", student_img_url = "", food_image_url = "";

    public DTODailyFoodAttendance(String teacher_id, String school_id, String food_id, String other_text, String boys_count, String girls_count, String latitude, String longitude, String date, String time, String student_img_url, String food_image_url) {
        this.teacher_id = teacher_id;
        this.school_id = school_id;
        this.food_id = food_id;
        this.other_text = other_text;
        this.boys_count = boys_count;
        this.girls_count = girls_count;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.time = time;
        this.student_img_url = student_img_url;
        this.food_image_url = food_image_url;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public String getOther_text() {
        return other_text;
    }

    public String getBoys_count() {
        return boys_count;
    }

    public String getGirls_count() {
        return girls_count;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStudent_img_url() {
        return student_img_url;
    }

    public String getFood_image_url() {
        return food_image_url;
    }
}

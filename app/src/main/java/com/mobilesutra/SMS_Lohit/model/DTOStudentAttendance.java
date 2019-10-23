package com.mobilesutra.SMS_Lohit.model;

/**
 * Created by Pramod Kale on 10/04/2017.
 */
public class DTOStudentAttendance {
    String standard = "", standard_id = "", boys_count = "", boys_present_count = "", girls_count = "",
            girls_present_count = "", total_count = "", total_present_count = "", image_url = "", school_id = "",
            teacher_id = "", current_date = "", current_time = "", latitude = "", longitude = "", term = "", subject = "";

    public DTOStudentAttendance(String teacher_id, String school_id, String standard_id, String boys_present_count, String girls_present_count,
                                String total_present_count, String image_url, String date, String time, String latitude,
                                String longitude) {
        this.teacher_id = teacher_id;
        this.school_id = school_id;
        this.standard_id = standard_id;
        this.boys_present_count = boys_present_count;
        this.girls_present_count = girls_present_count;
        this.total_present_count = total_present_count;
        this.image_url = image_url;
        this.current_date = date;
        this.current_time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public DTOStudentAttendance(String teacher_id, String school_id, String standard_id, String boys_present_count, String girls_present_count,
                                String total_present_count, String image_url, String date, String time, String latitude,
                                String longitude, String term, String subject) {
        this.teacher_id = teacher_id;
        this.school_id = school_id;
        this.standard_id = standard_id;
        this.boys_present_count = boys_present_count;
        this.girls_present_count = girls_present_count;
        this.total_present_count = total_present_count;
        this.image_url = image_url;
        this.current_date = date;
        this.current_time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.term = term;
        this.subject = subject;
    }

    public DTOStudentAttendance() {
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getStandard_id() {
        return standard_id;
    }

    public void setStandard_id(String standard_id) {
        this.standard_id = standard_id;
    }

    public String getBoys_count() {
        return boys_count;
    }

    public void setBoys_count(String boys_count) {
        this.boys_count = boys_count;
    }

    public String getBoys_present_count() {
        return boys_present_count;
    }

    public void setBoys_present_count(String boys_present_count) {
        this.boys_present_count = boys_present_count;
    }

    public String getGirls_count() {
        return girls_count;
    }

    public void setGirls_count(String girls_count) {
        this.girls_count = girls_count;
    }

    public String getGirls_present_count() {
        return girls_present_count;
    }

    public void setGirls_present_count(String girls_present_count) {
        this.girls_present_count = girls_present_count;
    }

    public String getTotal_present_count() {
        return total_present_count;
    }

    public void setTotal_present_count(String total_present_count) {
        this.total_present_count = total_present_count;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

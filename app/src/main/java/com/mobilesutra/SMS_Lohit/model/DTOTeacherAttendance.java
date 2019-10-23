package com.mobilesutra.SMS_Lohit.model;

/**
 * Created by Ganesh Borse on 03/04/2017.
 */
public class DTOTeacherAttendance {
    String teacher_id = "", school_id = "", in_latitude = "", in_longitude = "", in_time = "", in_image_url = "",
            attendance_date = "", out_latitude = "", out_longitude = "", out_time = "", out_image_url = "";

    // For Staff Attendance
    /* teacher_id & teacher_name */
    String teacher_name = "";
    boolean is_checked;

    public DTOTeacherAttendance(String teacher_id, String school_id, String in_latitude, String in_longitude, String in_time, String in_image_url, String attendance_date, String out_latitude, String out_longitude, String out_time, String out_image_url) {
        this.teacher_id = teacher_id;
        this.school_id = school_id;
        this.in_latitude = in_latitude;
        this.in_longitude = in_longitude;
        this.in_time = in_time;
        this.in_image_url = in_image_url;
        this.attendance_date = attendance_date;
        this.out_latitude = out_latitude;
        this.out_longitude = out_longitude;
        this.out_time = out_time;
        this.out_image_url = out_image_url;
    }

    public DTOTeacherAttendance(String teacher_id, String teacher_name, boolean is_checked) {
        this.teacher_id = teacher_id;
        this.teacher_name = teacher_name;
        this.is_checked = is_checked;
    }

    public boolean is_checked() {
        return is_checked;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public String getIn_latitude() {
        return in_latitude;
    }

    public String getIn_longitude() {
        return in_longitude;
    }

    public String getIn_time() {
        return in_time;
    }

    public String getIn_image_url() {
        return in_image_url;
    }

    public String getAttendance_date() {
        return attendance_date;
    }

    public String getOut_latitude() {
        return out_latitude;
    }

    public String getOut_longitude() {
        return out_longitude;
    }

    public String getOut_time() {
        return out_time;
    }

    public String getOut_image_url() {
        return out_image_url;
    }
}

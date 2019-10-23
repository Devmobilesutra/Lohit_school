package com.mobilesutra.SMS_Lohit.model;

/**
 * Created by Ganesh Borse on 04/05/2017.
 */
public class DTOLeaveApplication {
    String leave_id = "", school_id = "", teacher_id = "", leave_from_date = "", leave_to_date = "", leave_title = "", image_url = "";

    public DTOLeaveApplication(String leave_id, String school_id, String teacher_id, String leave_from_date,
                               String leave_to_date, String leave_title, String image_url) {
        this.leave_id = leave_id;
        this.school_id = school_id;
        this.teacher_id = teacher_id;
        this.leave_from_date = leave_from_date;
        this.leave_to_date = leave_to_date;
        this.leave_title = leave_title;
        this.image_url = image_url;
    }

    public String getLeave_id() {
        return leave_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public String getLeave_from_date() {
        return leave_from_date;
    }

    public String getLeave_to_date() {
        return leave_to_date;
    }

    public String getLeave_title() {
        return leave_title;
    }

    public String getImage_url() {
        return image_url;
    }
}

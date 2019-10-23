package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTOStudentAttendance;

import java.util.ArrayList;

/**
 * Created by Ganesh Borse on 04/04/2017.
 */
public class TABLE_SCHOOL_STANDARDS {
    private static String LOG_TAG = TABLE_SCHOOL_STANDARDS.class.getSimpleName();
    public static String NAME = "tbl_school_standards_master";

    public static String
            COL_ID = "id",
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_STANDARD_ID = "standard_id",
            COL_STANDARD_NAME = "standard_name",
            COL_BOYS = "boys",
            COL_GIRLS = "girls",
            COL_TOTAL_STUDENTS = "total_students";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEACHER_ID + " TEXT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_STANDARD_ID + " TEXT, "
            + COL_STANDARD_NAME + " TEXT, "
            + COL_BOYS + " TEXT, "
            + COL_GIRLS + " TEXT, "
            + COL_TOTAL_STUDENTS + " TEXT);";

    public static void deleteAllData() {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        try {
            long deleted_count = db.delete(NAME, COL_SCHOOL_ID + "=?", new String[]{MyApp.get_session(MyApp.SESSION_SCHOOL_ID)});
            MyApp.log(LOG_TAG, "Deleted count is " + deleted_count);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Delete standards Exception is " + e.getMessage());
        }
    }

    public static void insert_standard(String school_id, String teacher_id, String standard_id, String standard, String boys,
                                       String girls, String total_students) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_SCHOOL_ID, school_id);
        cv.put(COL_TEACHER_ID, teacher_id);
        cv.put(COL_STANDARD_ID, standard_id);
        cv.put(COL_STANDARD_NAME, standard);
        cv.put(COL_BOYS, boys);
        cv.put(COL_GIRLS, girls);
        cv.put(COL_TOTAL_STUDENTS, total_students);

        try {
            long insert_id = db.insert(NAME, null, cv);
            MyApp.log(LOG_TAG, "Standard Insert id is " + insert_id);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Insert standard exception is " + e.getMessage());
        }

    }

    public static ArrayList<DTOStudentAttendance> getAllStandardStudents(boolean exam_flag) {
        ArrayList<DTOStudentAttendance> studentAttendanceArrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT * FROM " + NAME + " WHERE " + COL_SCHOOL_ID + "=?";
        Cursor c = db.rawQuery(sql, new String[]{MyApp.get_session(MyApp.SESSION_SCHOOL_ID)});
        if (c.getCount() > 0) {
            c.moveToFirst();
            studentAttendanceArrayList = new ArrayList<>();
            do {
                DTOStudentAttendance dtoStudentAttendance = new DTOStudentAttendance();
                String school_id = c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_ID));
                String teacher_id = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID));
                String standard_id = c.getString(c.getColumnIndexOrThrow(COL_STANDARD_ID));

                dtoStudentAttendance.setSchool_id(c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_ID)));
                dtoStudentAttendance.setTeacher_id(c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID)));
                dtoStudentAttendance.setStandard_id(c.getString(c.getColumnIndexOrThrow(COL_STANDARD_ID)));
                dtoStudentAttendance.setStandard(c.getString(c.getColumnIndexOrThrow(COL_STANDARD_NAME)));

                dtoStudentAttendance.setBoys_count(c.getString(c.getColumnIndexOrThrow(COL_BOYS)));
                dtoStudentAttendance.setBoys_present_count("0");

                dtoStudentAttendance.setGirls_count(c.getString(c.getColumnIndexOrThrow(COL_GIRLS)));
                dtoStudentAttendance.setGirls_present_count("0");

                dtoStudentAttendance.setTotal_count(c.getString(c.getColumnIndexOrThrow(COL_TOTAL_STUDENTS)));
                dtoStudentAttendance.setTotal_present_count("0");

                dtoStudentAttendance.setCurrent_date(MyApp.getCurrentDate());
                dtoStudentAttendance.setCurrent_time(MyApp.getCurrentTime());
                dtoStudentAttendance.setLatitude("0.0");
                dtoStudentAttendance.setLongitude("0.0");
                dtoStudentAttendance.setImage_url("");
                MyApp.log(LOG_TAG, "Image url 1 is " + dtoStudentAttendance.getImage_url());

                if (exam_flag) {
                    dtoStudentAttendance.setTerm("");
                    dtoStudentAttendance.setSubject("");
                }

                String current_date = MyApp.getCurrentDate();

                if (exam_flag) {
                    MyApp.log(LOG_TAG,"In if and flag is " + exam_flag);
                    String sql2 = "SELECT * FROM " + TABLE_EXAMINATION_ATTENDANCE.NAME
                            + " WHERE " + TABLE_EXAMINATION_ATTENDANCE.COL_STANDARD_ID + "=? AND "
                            + TABLE_EXAMINATION_ATTENDANCE.COL_TEACHER_ID + "=? AND "
                            + TABLE_EXAMINATION_ATTENDANCE.COL_SCHOOL_ID + "=? AND "
                            + TABLE_EXAMINATION_ATTENDANCE.COL_DATE + "=?";

                    Cursor c_student = db.rawQuery(sql2, new String[]{standard_id, teacher_id, school_id, current_date});
                    if (c_student.getCount() > 0) {
                        c_student.moveToFirst();
                        dtoStudentAttendance.setBoys_present_count(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_BOYS_COUNT)));
                        dtoStudentAttendance.setGirls_present_count(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_GIRLS_COUNT)));
                        dtoStudentAttendance.setTotal_present_count(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_TOTAL_PRESENT_COUNT)));
                        dtoStudentAttendance.setCurrent_date(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_DATE)));
                        dtoStudentAttendance.setCurrent_time(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_TIME)));
                        dtoStudentAttendance.setLatitude(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_LATITUDE)));
                        dtoStudentAttendance.setLongitude(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_LONGITUDE)));
                        dtoStudentAttendance.setImage_url(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_IMAGE_URL)));
                        dtoStudentAttendance.setTerm(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_TERM)));
                        dtoStudentAttendance.setSubject(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_EXAMINATION_ATTENDANCE.COL_SUBJECT)));
                        MyApp.log(LOG_TAG, "if true Image url is " + dtoStudentAttendance.getImage_url());
                    }
                    c_student.close();
                } else {
                    MyApp.log(LOG_TAG,"In else and flag is " + exam_flag);
                    String sql3 = "SELECT * FROM " + TABLE_STUDENT_ATTENDANCE.NAME
                            + " WHERE " + TABLE_STUDENT_ATTENDANCE.COL_STANDARD_ID + "=? AND "
                            + TABLE_STUDENT_ATTENDANCE.COL_TEACHER_ID + "=? AND "
                            + TABLE_STUDENT_ATTENDANCE.COL_SCHOOL_ID + "=? AND "
                            + TABLE_STUDENT_ATTENDANCE.COL_DATE + "=?";

                    Cursor c_student = db.rawQuery(sql3, new String[]{standard_id, teacher_id, school_id, current_date});
                    if (c_student.getCount() > 0) {
                        c_student.moveToFirst();
                        dtoStudentAttendance.setBoys_present_count(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_BOYS_COUNT)));
                        dtoStudentAttendance.setGirls_present_count(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_GIRLS_COUNT)));
                        dtoStudentAttendance.setTotal_present_count(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_TOTAL_PRESENT_COUNT)));
                        dtoStudentAttendance.setCurrent_date(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_DATE)));
                        dtoStudentAttendance.setCurrent_time(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_TIME)));
                        dtoStudentAttendance.setLatitude(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_LATITUDE)));
                        dtoStudentAttendance.setLongitude(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_LONGITUDE)));
                        dtoStudentAttendance.setImage_url(c_student.getString(c_student.getColumnIndexOrThrow(TABLE_STUDENT_ATTENDANCE.COL_IMAGE_URL)));
                        MyApp.log(LOG_TAG, "else false Image url is " + dtoStudentAttendance.getImage_url());
                    }
                    c_student.close();
                }

                studentAttendanceArrayList.add(dtoStudentAttendance);
            } while (c.moveToNext());
        }
        c.close();
        return studentAttendanceArrayList;
    }
}


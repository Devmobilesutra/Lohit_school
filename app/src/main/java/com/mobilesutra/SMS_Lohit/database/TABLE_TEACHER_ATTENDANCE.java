package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTOTeacherAttendance;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Ganesh Borse on 31/03/2017.
 */
public class TABLE_TEACHER_ATTENDANCE {
    private static String LOG_TAG = TABLE_TEACHER_ATTENDANCE.class.getSimpleName();
    public static String NAME = "tbl_teacher_attendance";

    public static String
            COL_ID = "id",
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_IN_LATITUDE = "in_latitude",
            COL_IN_LONGITUDE = "in_longitude",
            COL_IN_IMAGE_URL = "in_image_url",
            COL_IN_TIME = "in_time",
            COL_DATE = "attendance_date",
            COL_OUT_LATITUDE = "out_latitude",
            COL_OUT_LONGITUDE = "out_longitude",
            COL_OUT_IMAGE_URL = "out_image_url",
            COL_OUT_TIME = "out_time",
            COL_IS_SYNCED = "is_synced";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEACHER_ID + " TEXT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_IN_LATITUDE + " TEXT, "
            + COL_IN_LONGITUDE + " TEXT, "
            + COL_IN_IMAGE_URL + " TEXT, "
            + COL_IN_TIME + " TEXT, "
            + COL_DATE + " TEXT, "
            + COL_OUT_LATITUDE + " TEXT, "
            + COL_OUT_LONGITUDE + " TEXT, "
            + COL_OUT_IMAGE_URL + " TEXT, "
            + COL_OUT_TIME + " TEXT, "
            + COL_IS_SYNCED + " TEXT);";

    public static void insertCheckIn(String in_latitude, String in_longitude, String in_img_path, String current_date,
                                     String current_time) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SCHOOL_ID, MyApp.get_session(MyApp.SESSION_SCHOOL_ID));
        cv.put(COL_TEACHER_ID, MyApp.get_session(MyApp.SESSION_TEACHER_ID));
        cv.put(COL_IN_LATITUDE, in_latitude);
        cv.put(COL_IN_LONGITUDE, in_longitude);
        cv.put(COL_IN_IMAGE_URL, in_img_path);
        cv.put(COL_DATE, current_date);
        cv.put(COL_IN_TIME, current_time);

        try {
            long insert_id = db.insert(NAME, null, cv);
            MyApp.log(LOG_TAG, "Teacher attendance Insert Id is " + insert_id);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Teacher insert exception is " + e.getMessage());
        }
    }

    public static void insertCheckOut(String out_latitude, String out_longitude, String out_img_path, String current_date,
                                      String current_time) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SCHOOL_ID, MyApp.get_session(MyApp.SESSION_SCHOOL_ID));
        cv.put(COL_TEACHER_ID, MyApp.get_session(MyApp.SESSION_TEACHER_ID));
        cv.put(COL_OUT_LATITUDE, out_latitude);
        cv.put(COL_OUT_LONGITUDE, out_longitude);
        cv.put(COL_OUT_IMAGE_URL, out_img_path);
        cv.put(COL_DATE, current_date);
        cv.put(COL_OUT_TIME, current_time);
        cv.put(COL_IS_SYNCED, "N");

        try {
            long updated_count = db.update(NAME, cv, COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=? AND " + COL_DATE + "=?", new String[]{MyApp.get_session(MyApp.SESSION_TEACHER_ID), MyApp.get_session(MyApp.SESSION_SCHOOL_ID), current_date});
            MyApp.log(LOG_TAG, "Teacher attendance Updated count is " + updated_count);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Teacher check out exception is " + e.getMessage());
        }
    }

    public static ArrayList<DTOTeacherAttendance> getUnSyncedAttendance() {
        ArrayList<DTOTeacherAttendance> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT * FROM " + NAME
                + " WHERE " + COL_IS_SYNCED + "=? AND " + COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=?";
        Cursor c = db.rawQuery(sql, new String[]{"N", MyApp.get_session(MyApp.SESSION_TEACHER_ID), MyApp.get_session(MyApp.SESSION_SCHOOL_ID)});
        if (c.getCount() > 0) {
            c.moveToFirst();
            arrayList = new ArrayList<>();
            do {

                String teacher_id = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID));
                String school_id = c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_ID));
                String in_latitude = c.getString(c.getColumnIndexOrThrow(COL_IN_LATITUDE));
                String in_longitude = c.getString(c.getColumnIndexOrThrow(COL_IN_LONGITUDE));
                String in_time = c.getString(c.getColumnIndexOrThrow(COL_IN_TIME));
                String in_image_url = c.getString(c.getColumnIndexOrThrow(COL_IN_IMAGE_URL));
                String attendance_date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                String out_latitude = c.getString(c.getColumnIndexOrThrow(COL_OUT_LATITUDE));
                String out_longitude = c.getString(c.getColumnIndexOrThrow(COL_OUT_LONGITUDE));
                String out_time = c.getString(c.getColumnIndexOrThrow(COL_OUT_TIME));
                String out_image_url = c.getString(c.getColumnIndexOrThrow(COL_OUT_IMAGE_URL));

                DTOTeacherAttendance dtoTeacherAttendance = new DTOTeacherAttendance(teacher_id, school_id, in_latitude, in_longitude, in_time, in_image_url, attendance_date, out_latitude, out_longitude, out_time, out_image_url);
                arrayList.add(dtoTeacherAttendance);

            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    public static void change_synced_flag(String teacher_id, String school_id, String attendance_date) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_SYNCED, "Y");

        try {
            long updated_count = db.update(NAME, cv, COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=? AND " + COL_DATE + "=?",
                    new String[]{teacher_id, school_id, attendance_date});
            MyApp.log(LOG_TAG, "Synced flag updated count is " + updated_count);
        } catch (SQLiteException e){
            MyApp.log(LOG_TAG,"Change Synced Flag Exception is " + e.getMessage());
        }
    }

    public static ArrayList<LinkedHashMap<String,String>> getSmsAttendance() {
        ArrayList<LinkedHashMap<String,String>> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();

        String sql = "SELECT * FROM " + NAME + " WHERE " + COL_TEACHER_ID + "=? AND " + COL_IS_SYNCED + "=?";
        Cursor c = db.rawQuery(sql, new String[]{MyApp.get_session(MyApp.SESSION_TEACHER_ID),"N"});

        if (c.getCount()>0){
            arrayList = new ArrayList<>();
            c.moveToFirst();
            do{
                String message = "ALERT LSTA ";

                String id = c.getString(c.getColumnIndexOrThrow(COL_ID));
                String school_id = MyApp.get_session(MyApp.SESSION_SCHOOL_ID);
                String teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID);
                String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                String in_time = c.getString(c.getColumnIndexOrThrow(COL_IN_TIME));
                String out_time = c.getString(c.getColumnIndexOrThrow(COL_OUT_TIME));
                /*String in_latitude = c.getString(c.getColumnIndexOrThrow(COL_IN_LATITUDE));
                String in_longitude = c.getString(c.getColumnIndexOrThrow(COL_IN_LONGITUDE));
                String out_latitude = c.getString(c.getColumnIndexOrThrow(COL_OUT_LATITUDE));
                String out_longitude = c.getString(c.getColumnIndexOrThrow(COL_OUT_LONGITUDE));*/

                message += school_id + "&" + teacher_id + "&" + date + "&" + in_time + "&" + out_time;

                /*message += date + "&" + in_time + "&" + out_time + "&"
                        + in_latitude + "&" + in_longitude + "&" + out_latitude + "&" + out_longitude;*/

                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("id",id);
                linkedHashMap.put("message",message);

                arrayList.add(linkedHashMap);

            } while (c.moveToNext());
        }
        c.close();

        return arrayList;
    }

    public static void change_synced_flag_sms(String id) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_SYNCED, "Y");

        try {
            long updated_count = db.update(NAME, cv, COL_ID + "=?",
                    new String[]{id});
            MyApp.log(LOG_TAG, "Synced flag updated count is " + updated_count);
        } catch (SQLiteException e){
            MyApp.log(LOG_TAG,"Change Synced Flag Exception is " + e.getMessage());
        }
    }
}

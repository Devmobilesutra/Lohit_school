package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTOStudentAttendance;
import com.mobilesutra.SMS_Lohit.model.DTOTeacherAttendance;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Ganesh Borse on 13/04/2017.
 */
public class TABLE_STUDENT_ATTENDANCE {
    private static String LOG_TAG = TABLE_STUDENT_ATTENDANCE.class.getSimpleName();
    public static String NAME = "tbl_student_attendance";

    public static String
            COL_ID = "id",
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_STANDARD_ID = "standard_id",
            COL_BOYS_COUNT = "boys_count",
            COL_GIRLS_COUNT = "girls_count",
            COL_TOTAL_PRESENT_COUNT = "total_present_count",
            COL_LATITUDE = "latitude",
            COL_LONGITUDE = "longitude",
            COL_DATE = "date",
            COL_TIME = "time",
            COL_IMAGE_URL = "image_url",
            COL_IS_SYNCED = "is_synced";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEACHER_ID + " TEXT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_STANDARD_ID + " TEXT, "
            + COL_BOYS_COUNT + " TEXT, "
            + COL_GIRLS_COUNT + " TEXT, "
            + COL_TOTAL_PRESENT_COUNT + " TEXT, "
            + COL_LATITUDE + " TEXT, "
            + COL_LONGITUDE + " TEXT, "
            + COL_DATE + " TEXT, "
            + COL_TIME + " TEXT, "
            + COL_IMAGE_URL + " TEXT, "
            + COL_IS_SYNCED + " TEXT);";

    public static void save_student_attendance(String teacher_id, String school_id, String standard_id, String boys_present_count,
                                               String girls_present_count, String total_present_count, String image_url,
                                               String current_date, String current_time, String latitude, String longitude) {

        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEACHER_ID, teacher_id.trim());
        cv.put(COL_SCHOOL_ID, school_id.trim());
        cv.put(COL_STANDARD_ID, standard_id.trim());
        cv.put(COL_BOYS_COUNT, boys_present_count.trim());
        cv.put(COL_GIRLS_COUNT, girls_present_count.trim());
        cv.put(COL_TOTAL_PRESENT_COUNT, total_present_count.trim());
        cv.put(COL_IMAGE_URL, image_url.trim());
        cv.put(COL_DATE, current_date.trim());
        cv.put(COL_TIME, current_time.trim());
        cv.put(COL_LATITUDE, latitude.trim());
        cv.put(COL_LONGITUDE, longitude.trim());
        cv.put(COL_IS_SYNCED, "N");

        MyApp.log(LOG_TAG,"Cursor to insert students attendance is " + cv);

        String sql = "SELECT " + COL_ID + " FROM " + NAME + " WHERE " + COL_SCHOOL_ID + "=? AND " + COL_TEACHER_ID + "=? AND "
                + COL_STANDARD_ID + "=? AND " + COL_DATE + "=?";
        Cursor c = db.rawQuery(sql, new String[]{school_id, teacher_id, standard_id, current_date});
        if (c.getCount() > 0) {
            long updated_count = db.update(NAME, cv, COL_SCHOOL_ID + "=? AND " + COL_TEACHER_ID + "=? AND "
                    + COL_STANDARD_ID + "=? AND " + COL_DATE + "=?", new String[]{school_id, teacher_id, standard_id, current_date});
            MyApp.log(LOG_TAG, "Updated count is " + updated_count);
        } else {
            long insert_id = db.insert(NAME, null, cv);
            MyApp.log(LOG_TAG, "Inserted id is " + insert_id);
        }
        c.close();
    }

    public static ArrayList<DTOStudentAttendance> getUnSyncedAttendance() {
        ArrayList<DTOStudentAttendance> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT * FROM " + NAME
                + " WHERE " + COL_IS_SYNCED + "=? AND " + COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=?";
        Cursor c = db.rawQuery(sql, new String[]{"N", MyApp.get_session(MyApp.SESSION_TEACHER_ID), MyApp.get_session(MyApp.SESSION_SCHOOL_ID)});
        MyApp.log(LOG_TAG,"Get getUnSyncedAttendance cursor count is " + c.getCount());
        if (c.getCount() > 0) {
            c.moveToFirst();
            arrayList = new ArrayList<>();
            do {
                String teacher_id = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID));
                String school_id = c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_ID));
                String standard_id = c.getString(c.getColumnIndexOrThrow(COL_STANDARD_ID));
                String boys_count = c.getString(c.getColumnIndexOrThrow(COL_BOYS_COUNT));
                String girls_count = c.getString(c.getColumnIndexOrThrow(COL_GIRLS_COUNT));
                String total_count = c.getString(c.getColumnIndexOrThrow(COL_TOTAL_PRESENT_COUNT));
                String image_url = c.getString(c.getColumnIndexOrThrow(COL_IMAGE_URL));
                String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                String time = c.getString(c.getColumnIndexOrThrow(COL_TIME));
                String latitude = c.getString(c.getColumnIndexOrThrow(COL_LATITUDE));
                String longitude = c.getString(c.getColumnIndexOrThrow(COL_LONGITUDE));

                DTOStudentAttendance studentAttendance = new DTOStudentAttendance(teacher_id, school_id,standard_id,boys_count,
                        girls_count,total_count,image_url,date,time,latitude,longitude);
                arrayList.add(studentAttendance);

            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    public static void change_synced_flag(String teacher_id, String school_id, String standard_id, String attendance_date) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_SYNCED, "Y");

        try {
            long updated_count = db.update(NAME, cv, COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=? AND " + COL_DATE + "=? AND " + COL_STANDARD_ID + "=?",
                    new String[]{teacher_id, school_id, attendance_date,standard_id});
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
                String message = "ALERT LSSA ";

                String id = c.getString(c.getColumnIndexOrThrow(COL_ID));
                String school_id = MyApp.get_session(MyApp.SESSION_SCHOOL_ID);
                String teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID);
                String standard_id = c.getString(c.getColumnIndexOrThrow(COL_STANDARD_ID));
                //String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                String time = c.getString(c.getColumnIndexOrThrow(COL_TIME));
                String boys_count = c.getString(c.getColumnIndexOrThrow(COL_BOYS_COUNT));
                String girls_count = c.getString(c.getColumnIndexOrThrow(COL_GIRLS_COUNT));
                String total_count = c.getString(c.getColumnIndexOrThrow(COL_TOTAL_PRESENT_COUNT));
                //String latitude = c.getString(c.getColumnIndexOrThrow(COL_LATITUDE));
                //String longitude = c.getString(c.getColumnIndexOrThrow(COL_LONGITUDE));

                message += school_id + "&" + teacher_id + "&" + standard_id + "&" + time + "&" + boys_count + "&"
                        + girls_count + "&"  + total_count;

                /*message += school_id + "&" + teacher_id + "&" + standard_id + "&" + date + "&" + time + "&" + boys_count + "&"
                        + girls_count + "&"  + total_count + "&" + latitude + "&" + longitude ;*/

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

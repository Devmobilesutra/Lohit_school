package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTODailyFoodAttendance;
import com.mobilesutra.SMS_Lohit.model.DTOStudentAttendance;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Ganesh Borse on 14/04/2017.
 */
public class TABLE_DAILY_FOOD {

    private static String LOG_TAG = TABLE_DAILY_FOOD.class.getSimpleName();
    public static String NAME = "tbl_daily_food";

    public static String
            COL_ID = "id",
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_FOOD_ID = "food_id",
            COL_OTHER_TEXT = "food_other_text",
            COL_BOYS_COUNT = "boys_count",
            COL_GIRLS_COUNT = "girls_count",
            COL_LATITUDE = "latitude",
            COL_LONGITUDE = "longitude",
            COL_DATE = "date",
            COL_TIME = "time",
            COL_STUDENT_IMAGE_URL = "student_image_url",
            COL_FOOD_IMAGE_URL = "food_image_url",
            COL_IS_SYNCED = "is_synced";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEACHER_ID + " TEXT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_FOOD_ID + " TEXT, "
            + COL_OTHER_TEXT + " TEXT, "
            + COL_BOYS_COUNT + " TEXT, "
            + COL_GIRLS_COUNT + " TEXT, "
            + COL_LATITUDE + " TEXT, "
            + COL_LONGITUDE + " TEXT, "
            + COL_DATE + " TEXT, "
            + COL_TIME + " TEXT, "
            + COL_STUDENT_IMAGE_URL + " TEXT, "
            + COL_FOOD_IMAGE_URL + " TEXT, "
            + COL_IS_SYNCED + " TEXT);";

    public static void insertDailyReport(String food_id, String other_text, String boys_count, String girls_count,
                                         String latitude, String longitude, String current_date, String current_time,
                                         String student_img_url, String food_img_url) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEACHER_ID, MyApp.get_session(MyApp.SESSION_TEACHER_ID).trim());
        cv.put(COL_SCHOOL_ID, MyApp.get_session(MyApp.SESSION_SCHOOL_ID).trim());
        cv.put(COL_FOOD_ID, food_id.trim());
        cv.put(COL_OTHER_TEXT, other_text.trim());
        cv.put(COL_BOYS_COUNT, boys_count.trim());
        cv.put(COL_GIRLS_COUNT, girls_count.trim());
        cv.put(COL_LATITUDE, latitude.trim());
        cv.put(COL_LONGITUDE, longitude.trim());
        cv.put(COL_DATE, current_date.trim());
        cv.put(COL_TIME, current_time.trim());
        cv.put(COL_STUDENT_IMAGE_URL, student_img_url.trim());
        cv.put(COL_FOOD_IMAGE_URL, food_img_url.trim());
        cv.put(COL_IS_SYNCED, "N");

        String sql = "SELECT " + COL_ID + " FROM " + NAME + " WHERE "
                + COL_TEACHER_ID + "=? AND "
                + COL_SCHOOL_ID + "=? AND "
                + COL_DATE + "=?";
        Cursor c = db.rawQuery(sql, new String[]{MyApp.get_session(MyApp.SESSION_TEACHER_ID), MyApp.get_session(MyApp.SESSION_SCHOOL_ID), current_date});
        if (c.getCount() > 0) {
            long updated_count = db.update(NAME, cv, COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=? AND " + COL_DATE + "=?", new String[]{MyApp.get_session(MyApp.SESSION_TEACHER_ID), MyApp.get_session(MyApp.SESSION_SCHOOL_ID), current_date});
            MyApp.log(LOG_TAG, "updated count is " + updated_count);
        } else {
            long inserted_id = db.insert(NAME, null, cv);
            MyApp.log(LOG_TAG, "Inserted id is " + inserted_id);
        }
        c.close();
    }

    public static ArrayList<DTODailyFoodAttendance> getUnSyncedDailyFoodAttendance() {
        ArrayList<DTODailyFoodAttendance> arrayList = null;
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
                String food_id = c.getString(c.getColumnIndexOrThrow(COL_FOOD_ID));
                String other_text = c.getString(c.getColumnIndexOrThrow(COL_OTHER_TEXT));
                String boys_count = c.getString(c.getColumnIndexOrThrow(COL_BOYS_COUNT));
                String girls_count = c.getString(c.getColumnIndexOrThrow(COL_GIRLS_COUNT));
                String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                String time = c.getString(c.getColumnIndexOrThrow(COL_TIME));
                String latitude = c.getString(c.getColumnIndexOrThrow(COL_LATITUDE));
                String longitude = c.getString(c.getColumnIndexOrThrow(COL_LONGITUDE));
                String student_img_url = c.getString(c.getColumnIndexOrThrow(COL_STUDENT_IMAGE_URL));
                String food_image_url = c.getString(c.getColumnIndexOrThrow(COL_FOOD_IMAGE_URL));

                DTODailyFoodAttendance dtoDailyFoodAttendance = new DTODailyFoodAttendance(teacher_id, school_id, food_id, other_text, boys_count, girls_count, latitude, longitude, date, time, student_img_url, food_image_url);
                arrayList.add(dtoDailyFoodAttendance);

            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    public static void change_synced_flag(String teacher_id, String school_id, String food_id, String date) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_SYNCED, "Y");

        try {
            long updated_count = db.update(NAME, cv, COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=? AND " + COL_DATE + "=? AND "
                            + COL_FOOD_ID + "=?",
                    new String[]{teacher_id, school_id, date, food_id});
            MyApp.log(LOG_TAG, "Synced flag updated count is " + updated_count);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Change Synced Flag Exception is " + e.getMessage());
        }
    }

    public static ArrayList<LinkedHashMap<String,String>> getSmsAttendance() {
        ArrayList<LinkedHashMap<String,String>> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();

        String sql = "SELECT * FROM " + NAME + " WHERE " + COL_TEACHER_ID + "=? AND " + COL_IS_SYNCED + "=?";
        Cursor c = db.rawQuery(sql, new String[]{MyApp.get_session(MyApp.SESSION_TEACHER_ID), "N"});

        if (c.getCount() > 0) {
            arrayList = new ArrayList<>();
            c.moveToFirst();
            do {
                String message = "ALERT LSDF ";

                String id = c.getString(c.getColumnIndexOrThrow(COL_ID));
                String school_id = MyApp.get_session(MyApp.SESSION_SCHOOL_ID);
                String teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID);
                String food_id = c.getString(c.getColumnIndexOrThrow(COL_FOOD_ID));
                //String other_text = c.getString(c.getColumnIndexOrThrow(COL_OTHER_TEXT));
                String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                String time = c.getString(c.getColumnIndexOrThrow(COL_TIME));
                String boys_count = c.getString(c.getColumnIndexOrThrow(COL_BOYS_COUNT));
                String girls_count = c.getString(c.getColumnIndexOrThrow(COL_GIRLS_COUNT));
                //String latitude = c.getString(c.getColumnIndexOrThrow(COL_LATITUDE));
                //String longitude = c.getString(c.getColumnIndexOrThrow(COL_LONGITUDE));

                message += school_id + "&" + teacher_id + "&" + food_id + "&" + date
                        + "&" + time + "&" + boys_count + "&" + girls_count;

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

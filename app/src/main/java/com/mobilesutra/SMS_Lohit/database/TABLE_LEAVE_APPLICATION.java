package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTOLeaveApplication;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Ganesh Borse on 03/05/2017.
 */
public class TABLE_LEAVE_APPLICATION {
    public static String LOG_TAG = TABLE_LEAVE_APPLICATION.class.getSimpleName();
    public static String NAME = "tbl_leave_application";

    public static String
            COL_ID = "id",
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_LEAVE_FROM_DATE = "leave_from_date",
            COL_LEAVE_TO_DATE = "leave_to_date",
            COL_LEAVE_TITLE = "leave_title",
            COL_IMAGE_URL = "image_url",
            COL_IS_SYNCED = "is_synced";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEACHER_ID + " TEXT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_LEAVE_FROM_DATE + " TEXT, "
            + COL_LEAVE_TO_DATE + " TEXT, "
            + COL_LEAVE_TITLE + " TEXT, "
            + COL_IMAGE_URL + " TEXT, "
            + COL_IS_SYNCED + " TEXT);";

    public static void insertLeaveApplication(String teacher_id, String school_id, String leave_from_date,
                                              String leave_to_date, String leave_title, String image_url) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEACHER_ID, teacher_id.trim());
        cv.put(COL_SCHOOL_ID, school_id.trim());
        cv.put(COL_LEAVE_FROM_DATE, leave_from_date.trim());
        cv.put(COL_LEAVE_TO_DATE, leave_to_date.trim());
        cv.put(COL_LEAVE_TITLE, leave_title.trim());
        cv.put(COL_IMAGE_URL, image_url.trim());
        cv.put(COL_IS_SYNCED, "N");

        long insert_id = db.insert(NAME, null, cv);
        MyApp.log(LOG_TAG, "Insert id is " + insert_id);
    }

    public static ArrayList<DTOLeaveApplication> getUnSyncedLeaveApplications() {
        ArrayList<DTOLeaveApplication> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT * FROM " + NAME + " WHERE " + COL_IS_SYNCED + "=?";
        Cursor c = db.rawQuery(sql, new String[]{"N"});

        if (c.getCount() > 0) {
            c.moveToFirst();
            arrayList = new ArrayList<>();
            do {
                String leave_id = c.getString(c.getColumnIndexOrThrow(COL_ID));
                String school_id = c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_ID));
                String teacher_id = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID));
                String leave_from_date = c.getString(c.getColumnIndexOrThrow(COL_LEAVE_FROM_DATE));
                String leave_to_date = c.getString(c.getColumnIndexOrThrow(COL_LEAVE_TO_DATE));
                String leave_title = c.getString(c.getColumnIndexOrThrow(COL_LEAVE_TITLE));
                String image_url = c.getString(c.getColumnIndexOrThrow(COL_IMAGE_URL));

                DTOLeaveApplication dtoLeaveApplication = new DTOLeaveApplication(leave_id, school_id, teacher_id, leave_from_date, leave_to_date, leave_title, image_url);
                arrayList.add(dtoLeaveApplication);
            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    public static void change_synced_flag(String leave_id, String teacher_id, String school_id) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_SYNCED, "Y");

        try {
            long updated_count = db.update(NAME, cv, COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=? AND " + COL_ID + "=?",
                    new String[]{teacher_id, school_id, leave_id});
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
                String message = "ALERT LSLA ";

                String id = c.getString(c.getColumnIndexOrThrow(COL_ID));
                String school_id = MyApp.get_session(MyApp.SESSION_SCHOOL_ID).trim();
                String teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID).trim();
                String from_date = c.getString(c.getColumnIndexOrThrow(COL_LEAVE_FROM_DATE));
                String to_date = c.getString(c.getColumnIndexOrThrow(COL_LEAVE_TO_DATE));
                //String title = c.getString(c.getColumnIndexOrThrow(COL_LEAVE_TITLE));

                message += school_id + "&" + teacher_id + "&" + from_date + "&" + to_date ;

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

package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTOEvents;
import com.mobilesutra.SMS_Lohit.model.DTOLeaveApplication;

import java.util.ArrayList;

/**
 * Created by Ganesh Borse on 03/05/2017.
 */
public class TABLE_EVENTS {
    public static String LOG_TAG = TABLE_EVENTS.class.getSimpleName();
    public static String NAME = "tbl_events";

    public static String
            COL_ID = "id",
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_EVENT_DATE = "event_date",
            COL_EVENT_TITLE = "event_title",
            COL_EVENT_DESCRIPTION = "event_description",
            COL_IMAGE_URL = "image_url",
            COL_IS_SYNCED = "is_synced";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEACHER_ID + " TEXT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_EVENT_DATE + " TEXT, "
            + COL_EVENT_TITLE + " TEXT, "
            + COL_EVENT_DESCRIPTION + " TEXT, "
            + COL_IMAGE_URL + " TEXT, "
            + COL_IS_SYNCED + " TEXT);";

    public static void insertEvent(String teacher_id, String school_id, String event_date, String event_title, String event_description, String image_url) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEACHER_ID, teacher_id);
        cv.put(COL_SCHOOL_ID, school_id);
        cv.put(COL_EVENT_DATE, event_date);
        cv.put(COL_EVENT_TITLE, event_title);
        cv.put(COL_EVENT_DESCRIPTION, event_description);
        cv.put(COL_IMAGE_URL, image_url);
        cv.put(COL_IS_SYNCED, "N");

        long insert_id = db.insert(NAME, null, cv);
        MyApp.log(LOG_TAG, "Insert id is " + insert_id);
    }

    public static ArrayList<DTOEvents> getUnSyncedEvents() {
        ArrayList<DTOEvents> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT * FROM " + NAME + " WHERE " + COL_IS_SYNCED + "=?";
        Cursor c = db.rawQuery(sql, new String[]{"N"});

        if (c.getCount()>0){
            c.moveToFirst();
            arrayList = new ArrayList<>();
            do {
                String event_id = c.getString(c.getColumnIndexOrThrow(COL_ID));
                String school_id = c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_ID));
                String teacher_id = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID));
                String event_date = c.getString(c.getColumnIndexOrThrow(COL_EVENT_DATE));
                String event_description = c.getString(c.getColumnIndexOrThrow(COL_EVENT_DESCRIPTION));
                String event_title = c.getString(c.getColumnIndexOrThrow(COL_EVENT_TITLE));
                String image_url = c.getString(c.getColumnIndexOrThrow(COL_IMAGE_URL));

                DTOEvents dtoEvents = new DTOEvents(event_id,school_id,teacher_id,event_date,event_description,event_title,image_url);
                arrayList.add(dtoEvents);
            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    public static void change_synced_flag(String event_id, String teacher_id, String school_id) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_SYNCED, "Y");

        try {
            long updated_count = db.update(NAME, cv, COL_TEACHER_ID + "=? AND " + COL_SCHOOL_ID + "=? AND " + COL_ID + "=?",
                    new String[]{teacher_id, school_id, event_id});
            MyApp.log(LOG_TAG, "Synced flag updated count is " + updated_count);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Change Synced Flag Exception is " + e.getMessage());
        }
    }

    public static ArrayList<String> getSmsAttendance() {
        ArrayList<String> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();

        String sql = "SELECT * FROM " + NAME + " WHERE " + COL_TEACHER_ID + "=? AND " + COL_IS_SYNCED + "=?";
        Cursor c = db.rawQuery(sql, new String[]{MyApp.get_session(MyApp.SESSION_TEACHER_ID),"N"});

        if (c.getCount()>0){
            arrayList = new ArrayList<>();
            c.moveToFirst();
            do{
                String message = "ALERT LSE ";

                String school_id = MyApp.get_session(MyApp.SESSION_SCHOOL_ID);
                String teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID);
                String event_date = c.getString(c.getColumnIndexOrThrow(COL_EVENT_DATE));
                String event_title = c.getString(c.getColumnIndexOrThrow(COL_EVENT_TITLE));
                String description = c.getString(c.getColumnIndexOrThrow(COL_EVENT_DESCRIPTION));

                message += school_id + "&" + teacher_id + "&" + event_date + "&" + event_title + "&" + description ;

                arrayList.add(message);

            } while (c.moveToNext());
        }
        c.close();

        return arrayList;
    }
}

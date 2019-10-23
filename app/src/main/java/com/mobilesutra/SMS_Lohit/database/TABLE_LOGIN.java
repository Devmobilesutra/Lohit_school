package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;

/**
 * Created by Ganesh Borse on 22/03/2017.
 */
public class TABLE_LOGIN {
    private static String LOG_TAG = TABLE_LOGIN.class.getSimpleName();
    public static String NAME = "tbl_login";

    public static String
            COL_ID = "id",
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_TEACHER_NAME = "teacher_name",
            COL_SCHOOL_NAME = "school_name",
            COL_TEACHER_DESIGNATION = "teacher_designation",
            COL_USERNAME = "username",
            COL_PASSWORD = "password",
            COL_SCHOOL_LATITUDE = "school_latitude",
            COL_SCHOOL_LONGITUDE = "school_longitude",
            COL_IS_TEACHER = "is_teacher",
            COL_SCHOOL_IMAGE = "school_image";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TEACHER_ID + " TEXT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_TEACHER_NAME + " TEXT, "
            + COL_SCHOOL_NAME + " TEXT, "
            + COL_TEACHER_DESIGNATION + " TEXT, "
            + COL_USERNAME + " TEXT, "
            + COL_PASSWORD + " TEXT, "
            + COL_SCHOOL_LATITUDE + " TEXT, "
            + COL_SCHOOL_LONGITUDE + " TEXT, "
            + COL_IS_TEACHER + " TEXT, "
            + COL_SCHOOL_IMAGE + " TEXT);";


    public static boolean checkUser(String str_username, String str_password) {

        SQLiteDatabase db = MyApp.db.getReadableDatabase();

        String sql = "SELECT " + COL_TEACHER_ID + "," + COL_SCHOOL_LATITUDE + "," + COL_SCHOOL_LONGITUDE + ","
                + COL_TEACHER_NAME + "," + COL_TEACHER_DESIGNATION + "," + COL_SCHOOL_NAME + "," + COL_IS_TEACHER + ","
                + COL_SCHOOL_IMAGE + ","+ COL_SCHOOL_ID + " FROM " + NAME
                + " WHERE " + COL_USERNAME + "=? AND "
                + COL_PASSWORD + "=?";
        Cursor c = db.rawQuery(sql, new String[]{str_username, str_password});
        if (c.getCount() > 0) {
            c.moveToFirst();
            MyApp.set_session(MyApp.SESSION_TEACHER_ID, c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID)));
            MyApp.set_session(MyApp.SESSION_SCHOOL_ID, c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_ID)));
            MyApp.set_session(MyApp.SESSION_USERNAME, str_username);
            MyApp.set_session(MyApp.SESSION_PASSWORD, str_password);
            MyApp.set_session(MyApp.SESSION_SCHOOL_NAME, c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_NAME)));
            MyApp.set_session(MyApp.SESSION_TEACHER_NAME, c.getString(c.getColumnIndexOrThrow(COL_TEACHER_NAME)));
            MyApp.set_session(MyApp.SESSION_TEACHER_DESIGNATION, c.getString(c.getColumnIndexOrThrow(COL_TEACHER_DESIGNATION)));
            MyApp.set_session(MyApp.SESSION_SCHOOL_LATITUDE, c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_LATITUDE)));
            MyApp.set_session(MyApp.SESSION_SCHOOL_LONGITUDE, c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_LONGITUDE)));
            MyApp.set_session(MyApp.SESSION_IS_TEACHER, c.getString(c.getColumnIndexOrThrow(COL_IS_TEACHER)));
            MyApp.set_session(MyApp.SESSION_SCHOOL_IMAGE_URL, c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_IMAGE)));
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public static void saveTeacherData(String teacher_id, String school_id, String teacher_name, String designation,
                                       String latitude, String longitude, String school_name, String str_username,
                                       String str_password, String is_teacher, String school_image) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEACHER_ID, teacher_id);
        cv.put(COL_SCHOOL_ID, school_id);
        cv.put(COL_TEACHER_NAME, teacher_name);
        cv.put(COL_TEACHER_DESIGNATION, designation);
        cv.put(COL_SCHOOL_LATITUDE, latitude);
        cv.put(COL_SCHOOL_LONGITUDE, longitude);
        cv.put(COL_SCHOOL_NAME, school_name);
        cv.put(COL_USERNAME, str_username);
        cv.put(COL_PASSWORD, str_password);
        cv.put(COL_IS_TEACHER, is_teacher);
        cv.put(COL_SCHOOL_IMAGE, school_image);

        try {
            long insert_id = db.insert(NAME, null, cv);
            MyApp.log(LOG_TAG, "Insert id is " + insert_id);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Insert Teacher data exception is " + e.getMessage());
        }

    }

    public static String getTeacherInfo(String teacher_id) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String teacher_info = "";
        String sql = "SELECT " + COL_TEACHER_NAME + ", " + COL_TEACHER_DESIGNATION + ", " + COL_SCHOOL_NAME
                + " FROM " + NAME
                + " WHERE " + COL_TEACHER_ID + "=?";
        Cursor c = db.rawQuery(sql, new String[]{teacher_id});
        if (c.getCount() > 0) {
            c.moveToFirst();
            teacher_info = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_NAME)) + ", "
                    + c.getString(c.getColumnIndexOrThrow(COL_TEACHER_DESIGNATION)) + ", "
                    + c.getString(c.getColumnIndexOrThrow(COL_SCHOOL_NAME));
        }
        c.close();
        return teacher_info;

    }

    public static void updatePassword(String teacher_id, String new_pass) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PASSWORD, new_pass);

        long updated_count = db.update(NAME,cv, COL_TEACHER_ID + "=?", new String[]{teacher_id});
        MyApp.log(LOG_TAG,"Reset password updated count is " + updated_count);
    }
}

package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTOTeacherAttendance;

import java.util.ArrayList;

/**
 * Created by Ganesh Borse on 02/05/2017.
 */
public class TABLE_TEACHER_MASTER {
    private static String LOG_TAG = TABLE_TEACHER_MASTER.class.getSimpleName();
    public static String NAME = "tbl_teacher_master";

    public static String
            COL_TEACHER_ID = "teacher_id",
            COL_SCHOOL_ID = "school_id",
            COL_TEACHER_NAME = "teacher_name",
            COL_DESIGNATION = "designation",
            COL_MOBILE = "mobile",
            COL_EMAIL = "email",
            COL_GENDER = "gender",
            COL_IS_HEADMASTER = "is_headmaster";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_SCHOOL_ID + " TEXT, "
            + COL_TEACHER_NAME + " TEXT, "
            + COL_DESIGNATION + " TEXT, "
            + COL_MOBILE + " TEXT, "
            + COL_EMAIL + " TEXT, "
            + COL_GENDER + " TEXT, "
            + COL_IS_HEADMASTER + " TEXT);";

    public static void deleteTeacherList() {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        try {
            long deleted_count = db.delete(NAME, COL_SCHOOL_ID + "=?", new String[]{MyApp.get_session(MyApp.SESSION_SCHOOL_ID)});
            MyApp.log(LOG_TAG, "Deleted count is " + deleted_count);
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Delete standards Exception is " + e.getMessage());
        }
    }

    public static void insert_teacher(String teacher_id, String teacher_name, String designation, String mobile, String email, String gender, String is_headmaster) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_SCHOOL_ID, MyApp.get_session(MyApp.SESSION_SCHOOL_ID));
        cv.put(COL_TEACHER_ID, teacher_id);
        cv.put(COL_TEACHER_NAME, teacher_name);
        cv.put(COL_DESIGNATION, designation);
        cv.put(COL_MOBILE, mobile);
        cv.put(COL_EMAIL, email);
        cv.put(COL_GENDER, gender);
        cv.put(COL_IS_HEADMASTER, is_headmaster);

        try {
            long insert_id = db.insert(NAME, null, cv);
            MyApp.log(LOG_TAG, "Inserted id is " + insert_id);
            if (insert_id == -1) {
                long updated_count = db.update(NAME, cv, COL_SCHOOL_ID + "=? AND " + COL_TEACHER_ID + "=?", new String[]{MyApp.get_session(MyApp.SESSION_SCHOOL_ID), teacher_id});
                MyApp.log(LOG_TAG, "Updated count is " + updated_count);
            }
        } catch (SQLiteException e) {
            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
        }
    }

    public static ArrayList<DTOTeacherAttendance> getTeacherList(String school_id) {
        ArrayList<DTOTeacherAttendance> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT " + COL_TEACHER_ID + ", " + COL_TEACHER_NAME + " FROM " + NAME
                + " WHERE " + COL_SCHOOL_ID + "=? AND " + COL_TEACHER_ID + "!=?";
        Cursor c = db.rawQuery(sql, new String[]{school_id, MyApp.get_session(MyApp.SESSION_TEACHER_ID)});
        if (c.getCount() > 0) {
            c.moveToFirst();
            arrayList = new ArrayList<>();
            do {
                String teacher_id = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_ID));
                String teacher_name = c.getString(c.getColumnIndexOrThrow(COL_TEACHER_NAME));

                DTOTeacherAttendance dtoTeacherAttendance = new DTOTeacherAttendance(teacher_id,teacher_name, false);
                arrayList.add(dtoTeacherAttendance);
            } while (c.moveToNext());
        }
        return arrayList;
    }
}

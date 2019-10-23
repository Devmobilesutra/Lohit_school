package com.mobilesutra.SMS_Lohit.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobilesutra.SMS_Lohit.config.MyApp;

import java.util.ArrayList;

/**
 * Created by Ganesh Borse on 22/03/2017.
 */
public class Database extends SQLiteOpenHelper{

    public static int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "SMS_Lohit.db";
    public static String LOG_TAG = Database.class.getSimpleName();

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_LOGIN.CREATE_TABLE);
            db.execSQL(TABLE_FOOD_MASTER.CREATE_TABLE);
            db.execSQL(TABLE_TEACHER_ATTENDANCE.CREATE_TABLE);
            db.execSQL(TABLE_SCHOOL_STANDARDS.CREATE_TABLE);
            db.execSQL(TABLE_STUDENT_ATTENDANCE.CREATE_TABLE);
            db.execSQL(TABLE_DAILY_FOOD.CREATE_TABLE);
            db.execSQL(TABLE_EXAMINATION_ATTENDANCE.CREATE_TABLE);
            db.execSQL(TABLE_NOTIFICATION.CREATE_TABLE);
            db.execSQL(TABLE_TEACHER_MASTER.CREATE_TABLE);
            db.execSQL(TABLE_EVENTS.CREATE_TABLE);
            db.execSQL(TABLE_LEAVE_APPLICATION.CREATE_TABLE);
        } catch (SQLiteException e){
            MyApp.log(LOG_TAG,"Database exception is " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

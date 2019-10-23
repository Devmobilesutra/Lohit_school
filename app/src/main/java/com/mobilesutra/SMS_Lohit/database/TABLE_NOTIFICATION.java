package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.model.DTONotification;

import java.util.ArrayList;

/**
 * Created by Ganesh Borse on 25/04/2017.
 */
public class TABLE_NOTIFICATION {
    private static final String LOG_TAG = TABLE_NOTIFICATION.class.getSimpleName();
    public static String NAME = "tbl_notification";

    public static String
                COL_ID = "id",
                COL_NOTIFICATION_TITLE = "notification_title",
                COL_NOTIFICATION_TEXT = "notification_text",
                COL_EXPIRY_DATE = "expiry_date";

    public static String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOTIFICATION_TITLE + " TEXT, "
            + COL_NOTIFICATION_TEXT + " TEXT, "
            + COL_EXPIRY_DATE + " TEXT ); ";

    public static void insertNotification(Bundle extras) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();

        if (extras.containsKey("notification_title"))
            cv.put(COL_NOTIFICATION_TITLE,extras.getString("notification_title"));
        if (extras.containsKey("notification_text"))
            cv.put(COL_NOTIFICATION_TEXT,extras.getString("notification_text"));
        if (extras.containsKey("expiry_date"))
            cv.put(COL_EXPIRY_DATE,extras.getString("expiry_date"));

        try {
            long insert_id = db.insert(NAME,null,cv);
            MyApp.log(LOG_TAG,"Notification Insert id is " + insert_id);
        } catch (SQLException e){
            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
        }
    }

    public static ArrayList<DTONotification> getNotifications() {
        ArrayList<DTONotification> notificationArrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT * FROM " + NAME + " ORDER BY " + COL_ID + " DESC ";
        Cursor c = db.rawQuery(sql,null);
        if (c.getCount()>0){
            c.moveToFirst();
            notificationArrayList = new ArrayList<>();
            String today_date = MyApp.get_today_date();
            do {
                String expiry_date = c.getString(c.getColumnIndexOrThrow(COL_EXPIRY_DATE));
                if(MyApp.compare_date(expiry_date, today_date)>=0){
                    String notification_id = c.getString(c.getColumnIndexOrThrow(COL_ID));
                    String title = c.getString(c.getColumnIndexOrThrow(COL_NOTIFICATION_TITLE));
                    String text = c.getString(c.getColumnIndexOrThrow(COL_NOTIFICATION_TEXT));

                    DTONotification dtoNotification = new DTONotification(notification_id,title,text,expiry_date);
                    notificationArrayList.add(dtoNotification);
                }
            } while (c.moveToNext());
        }

        return notificationArrayList;
    }
}

package com.mobilesutra.SMS_Lohit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobilesutra.SMS_Lohit.config.MyApp;

import java.util.ArrayList;

/**
 * Created by Ganesh Borse on 30/03/2017.
 */
public class TABLE_FOOD_MASTER {
    private static String LOG_TAG = TABLE_FOOD_MASTER.class.getSimpleName();
    public static String NAME = "tbl_food_master";

    public static String
            COL_ID = "id",
            COL_FOOD_NAME = "food_name",
            COL_LAST_UPDATE = "last_update";

    public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FOOD_NAME + " TEXT, "
            + COL_LAST_UPDATE + " TEXT);";


    public static boolean hasData() {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT " + COL_ID + " FROM " + NAME;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public static void deleteAllData() {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        long deleted_count = db.delete(NAME, null, null);
        MyApp.log(LOG_TAG, "Deleted count is " + deleted_count);
    }

    public static void insertFoodMasterList(String id, String food_name, String last_update) {
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, id);
        cv.put(COL_FOOD_NAME, food_name);
        cv.put(COL_LAST_UPDATE, last_update);

        long insert_id = db.insert(NAME, null, cv);
        MyApp.log(LOG_TAG,"Inserted id is " + insert_id);
    }

    public static ArrayList<String> getMealList() {
        ArrayList<String> arrayList = null;
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT " + COL_FOOD_NAME + " FROM " + NAME;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount()>0){
            c.moveToFirst();
            arrayList = new ArrayList<>();
            do {
                String meal_text = c.getString(c.getColumnIndexOrThrow(COL_FOOD_NAME));
                arrayList.add(meal_text);
            } while (c.moveToNext());
        }
        c.close();
        return arrayList;
    }

    public static String getFoodId(String food_name) {
        String selected_food_id = "";
        SQLiteDatabase db = MyApp.db.getReadableDatabase();
        String sql = "SELECT " + COL_ID + " FROM " + NAME
                + " WHERE " + COL_FOOD_NAME + "=?";
        Cursor c = db.rawQuery(sql, new String[]{food_name});
        if (c.getCount()>0){
            c.moveToFirst();
            selected_food_id = c.getString(c.getColumnIndexOrThrow(COL_ID));
        }
        c.close();
        return selected_food_id;
    }
}

package com.mobilesutra.SMS_Lohit.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobilesutra.SMS_Lohit.database.Database;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okio.Buffer;

/**
 * Created by Ganesh Borse on 22/03/2017.
 */
public class MyApp extends Application {


    private static String TAG = "Lohit_School";
    private static String LOG_TAG = MyApp.class.getSimpleName();

    public static Database db = null;
    private static Context context = null;

    //Shared Preferences Variables
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;

    String PREFS_NAME = "dsfwr34r334_r4#23e2e";
    public static String SessionKey = "j5aD9uweHEAncWdj";//Must have 16 character session key
    public static AESAlgorithm aes;

    //GCM Variables
    public static String regid = "";
    public static final String GCM_SENDER_ID = "1040835999809";//"15229033406";//"744141448913";//"570827506529";
    public static final String SESSION_NOTIFICATION_ID = "1";
    public static final String PREFS_PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    GoogleCloudMessaging gcm;

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;

    //session variables
    public static String SESSION_TEACHER_ID = "session_teacher_id";
    public static String SESSION_SCHOOL_ID = "session_school_id";
    public static String SESSION_SCHOOL_NAME = "session_school_name";
    public static String SESSION_TEACHER_NAME = "session_teacher_name";
    public static String SESSION_TEACHER_DESIGNATION = "session_teacher_designation";
    public static String SESSION_USERNAME = "session_username";
    public static String SESSION_PASSWORD = "session_password";
    public static String SESSION_SCHOOL_LATITUDE = "session_school_latitude";
    public static String SESSION_SCHOOL_LONGITUDE = "session_school_longitude";
    public static String SESSION_SCHOOL_IMAGE_URL = "school_image_url";
    public static String SESSION_IS_TEACHER = "session_is_teacher";
    public static String SESSION_USER_LATITUDE = "session_user_latitude";
    public static String SESSION_USER_LONGITUDE = "session_user_longitude";
    public static String SESSION_USER_AREA = "session_user_area";
    public static String SESSION_GPS_CITY = "session_user_city";
    public static String SESSION_CHECK_IN_BUTTON_TEXT = "session_check_in_button_text";
    public static String SESSION_SERVER_MOBILE_NUMBER = "session_server_mobile_no";
    public static String SESSION_IS_RECEIVER = "session_is_receiver";

    //Server URLs
    public static String
            local_url = "http://192.168.0.246/Lohit_School_Webservices/index.php/",
            server_url = "http://mobilesutra.com/Lohit_School_Webservices/index.php/",

    base_url = server_url,
            url_login_teacher = base_url + "Teacher/login_teacher",
            url_food_master = base_url + "Food/get_master_food",
            url_school_standards = base_url + "School/get_school_standards",
            url_staff_attendance = base_url + "Teacher/teacher_attendance",
            url_students_attendance = base_url + "Students/students_attendance",
            url_examination_attendance = base_url + "Students/examination_attendance",
            url_daily_food_attendance = base_url + "Food/daily_food_attendance",
            url_get_teacher_list = base_url + "Teacher/get_teacher_list",
            url_leave_application = base_url + "Teacher/leave_application",
            url_school_events = base_url + "School/school_events",
            url_reset_password = base_url + "Teacher/reset_password";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        if (db == null) {
            db = new Database(context);
            db.getWritableDatabase();

            sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            aes = new AESAlgorithm();
            gcm = GoogleCloudMessaging.getInstance(this);
            getRegistrationId(context);

        }
    }

    public static void set_session(String key, String value) {
        Log.i("SetSession", "Key=" + key + "__value=" + value);
        String temp_key = aes.Encrypt(key);
        String temp_value = aes.Encrypt(value);
        MyApp.editor.putString(temp_key, temp_value);
        MyApp.editor.commit();
    }

    public static String get_session(String key) {
        String temp_key = aes.Encrypt(key);
        if (sharedPref.contains(temp_key)) {
            return aes.Decrypt(sharedPref.getString(temp_key, ""));
        } else
            return "";
    }

    public static long get_Longsession(String key) {
        String temp_key = aes.Encrypt(key);
        if (sharedPref.contains(temp_key)) {
            String str = aes.Decrypt(sharedPref.getString(temp_key, ""));
            return Long.parseLong(str);
        } else
            return 0;
    }

    public static int get_Intsession(String key) {
        String temp_key = aes.Encrypt(key);
        int temp = 0;
        try {
            if (sharedPref.contains(temp_key)) {
                String str = aes.Decrypt(sharedPref.getString(temp_key, ""));
                temp = Integer.parseInt(str);
            }
        } catch (NumberFormatException e) {
        }
        log(TAG, "get_Intsession Key=" + key + "__value=" + temp);
        return temp;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable());
    }

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static boolean compare_lat_long(double lat, double log, double ex_lat, double ex_log) {

        //calculate distance between given two co-ordinates

        Location mylocation = new Location("");
        Location dest_location = new Location("");
        if (ex_lat > 0 && lat > 0) {
            dest_location.setLatitude(lat);
            dest_location.setLongitude(log);
            Double my_loc = 0.00;
            mylocation.setLatitude(ex_lat);
            mylocation.setLongitude(ex_log);
            double distance = mylocation.distanceTo(dest_location) / 1000; //in meters
            MyApp.log(TAG, "distance: direct" + distance);
            distance = (double) Math.round(distance * 10) / 10;
            MyApp.log(TAG, "distance: round " + distance);
            distance = distance * 1000;
            MyApp.log(TAG, "distance in meters:" + distance);
            MyApp.log(TAG, "Distance: " + distance + " FromLat:" + lat + " FromLon:" + log + " ToLat:" + ex_lat + " ToLon:" + ex_log);
            if (distance <= Double.parseDouble("100")) {
                MyApp.log(TAG, " in if distance:" + distance);
                return true;
            } else {
                MyApp.log(TAG, " in else distance:" + distance);
                return false;
            }
        } else {
            //return 0.0;
            return false;
        }
    }

    public static String getCurrentDate() {
        DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
        final String current_date = df_date.format(Calendar.getInstance().getTime());
        MyApp.log(LOG_TAG, "Current Date is " + current_date);

        return current_date;
    }

    public static String getCurrentDateTime() {
        DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String current_date = df_date.format(Calendar.getInstance().getTime());
        MyApp.log(LOG_TAG, "Current Date is " + current_date);

        return current_date;
    }

    public static String getCurrentTime() {
        DateFormat df_time = new SimpleDateFormat("HH:mm:ss");
        final String current_time = df_time.format(Calendar.getInstance().getTime());
        MyApp.log(LOG_TAG, "Current Time is " + current_time);

        return current_time;
    }

    public static String get_today_date() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static int compare_date(String date1,String date2) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateNew = null,dateOld = null;
        int j = 0;
        try {
            dateOld = dateFormat.parse(date1);//dateFormat.format(date)
            dateNew = dateFormat.parse(date2);//dateFormat.format(date)
            log("CDdateOld", dateFormat.format(dateOld));
            log("CDdateNew", dateFormat.format(dateNew));
            log("CDdateOld.dateNew", dateOld.compareTo(dateNew) + "");
            long diff = dateOld.getTime() - dateNew.getTime();
            log("dateOld.getTime()", dateOld.getTime() + "");
            log("dateNew.getTime()", dateNew.getTime() + "");
            log("diff", diff + "");
            String days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+"";
            String hrs = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)+"";
            log("hrs", hrs + "");
            log("days", diff + "");

            if (dateOld.compareTo(dateNew) > 0) {
                log("dateOld is after dateNew");
            } else if (dateOld.compareTo(dateNew) < 0) {
                log("dateOld is before dateNew");
            } else if (dateOld.compareTo(dateNew) == 0) {
                log("dateOld is equal to dateNew");
            } else {
                log("How to get here?");
            }


            j = Integer.parseInt(days);
            System.out.println("Days: " + j);
            /*if(dateOld.compareTo(dateNew) < 0) {
                return true;
            }
            else {
                return false;
            }*/


        } catch (ParseException e) {
            Log.i("compare_date","Exception:"+e.getMessage());
        }
        return j;
    }

    ////////////////////////////// GCM FUNCTIONS//////////////////////////////////////////////////////////////////////

    public void getRegistrationGCMID() {
        if (checkPlayServices()) {
            // Retrieve registration id from local storage
            regid = getRegistrationId(context);

            if (TextUtils.isEmpty(regid)) {
//		    	Log.i("Empty",regid);
                registerInBackground();

            } else {
//		    	Log.i("Not empty",regid);

            }
            Log.i("Store in database", regid);
        } else {
//		    Log.i(Globals.TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //		GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //	    	Log.i(Globals.TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public String getRegistrationId(Context context) {
        String registrationId = get_session(PREFS_PROPERTY_REG_ID);
        if (registrationId == null || registrationId.equals("")) {
//		    Log.i(Globals.TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = get_Intsession(PROPERTY_APP_VERSION);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
//		    Log.i(Globals.TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the regID in your app is up to you.
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    log(TAG, msg);
                    // You should send the registration ID to your server over
                    // HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
//		    sendRegistrationIdToBackend();
//		    postData(regid);
                    // For this demo: we use upstream GCM messages to send the
                    // registration ID to the 3rd party server

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            private void storeRegistrationId(Context context, String regId) {
                int appVersion = getAppVersion(context);
                log(TAG, "Saving regId on app version " + appVersion);
                set_session(PREFS_PROPERTY_REG_ID, regId);
                set_session(PROPERTY_APP_VERSION, appVersion + "");
                log(TAG, "Saving regId on app version " + regId);
            }

            @Override
            protected void onPostExecute(String msg) {
//	    	Log.e("","Device Registered");
//		((EditText) findViewById(R.id.txtPin)).setText(regid);
            }
        }.execute(null, null, null);
    }

    public static void log(String LOG_TAG, String str) {
        if (str.length() > 4000) {
            Log.i(TAG, LOG_TAG + "->" + str.substring(0, 4000));
            log(TAG, str.substring(4000));
        } else
            Log.i(TAG, LOG_TAG + "->" + str);
    }

    public static void log(String str) {
        if (str.length() > 4000) {
            Log.i(TAG, str.substring(0, 4000));
            log(str.substring(4000));
        } else
            Log.i(TAG, str);
    }

    public static void log_bundle(Bundle extras) {
        log(TAG, "In log_bundle");
        if (extras != null) {
            for (String key : extras.keySet()) {
                log(TAG, key + " = " + extras.get(key));
            }
        } else {
            log(TAG, "Bundle->" + extras);
        }
    }

    public static String post_server_call(String url, RequestBody formBody) {
        long REQ_TIMEOUT = 600;
        log("post_server_callUrl:" + url);

        try {
            Buffer buffer = new Buffer();
            formBody.writeTo(buffer);
            //log("post_for_body:" + buffer.readUtf8().toString());

        } catch (IOException e) {

        }

        try {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(REQ_TIMEOUT, TimeUnit.SECONDS);
            client.setReadTimeout(REQ_TIMEOUT, TimeUnit.SECONDS);
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();

            int status_code = response.code();
            boolean status = response.isSuccessful();
            log("logtime:StatusCode:" + status_code);
            log("logtime:ResponseStatus:" + status);
            String res = response.body().string();
            log("logtime:ResponseSize:" + res.length());
            //log("logtime:Response:" + res);
            log("post_server_callResponseStatus:" + response.isSuccessful());
            return res;
        } catch (Exception e) {

            log("logtime:E1->" + e + "");
            log("logtime:E2->" + e.getMessage());
            log("logtime:E3->" + e.getLocalizedMessage());
            return "-0";
        }
    }

    public static String teacher_login(String str_username, String str_password, String deviceId) {
        RequestBody formBody = new FormEncodingBuilder()
                .add("device_id", deviceId)
                .add("gcm_id", regid)
                .add("username", str_username)
                .add("password", str_password)
                .build();

        MyApp.log(LOG_TAG, "Form body to login user contains " + formBody.toString());

        return post_server_call(url_login_teacher, formBody);
    }

    public static String get_food_master() {
        RequestBody formBody = new FormEncodingBuilder()
                .build();
        return post_server_call(url_food_master, formBody);
    }

    public static String get_school_standards() {

        RequestBody formBody = new FormEncodingBuilder()
                .add("school_id", MyApp.get_session(MyApp.SESSION_SCHOOL_ID))
                .build();

        MyApp.log(LOG_TAG, "Form body to get_school_standards contains " + formBody.toString());

        return post_server_call(url_school_standards, formBody);
    }

    public static String get_teacher_list() {

        RequestBody formBody = new FormEncodingBuilder()
                .add("school_id", MyApp.get_session(MyApp.SESSION_SCHOOL_ID))
                .build();

        MyApp.log(LOG_TAG, "Form body to url_get_teacher_list contains " + formBody.toString());

        return post_server_call(url_get_teacher_list, formBody);
    }

    public static String send_staff_attendance(String teacher_id, String school_id, String in_latitude, String in_longitude,
                                               String in_time, String in_image_url, String attendance_date, String out_latitude,
                                               String out_longitude, String out_time, String out_image_url) {

        MultipartBuilder obj = new MultipartBuilder().type(MultipartBuilder.FORM);

        if (in_image_url != null) {
            log(LOG_TAG, "image_file teacher in: " + in_image_url);
            File in_image_file = new File(in_image_url);
            log(LOG_TAG, "IsD FIle->" + in_image_file.exists());
            try {
                if (in_image_file.exists())
                    obj.addFormDataPart("in_image", in_image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), in_image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "in_image exception is " + e.getMessage());
                obj.addFormDataPart("in_image", "");
            }
        } else {
            obj.addFormDataPart("in_image", "");
        }

        if (out_image_url != null) {
            log(LOG_TAG, "image_file teacher out: " + out_image_url);
            File out_image_file = new File(out_image_url);
            log(LOG_TAG, "IsD FIle->" + out_image_file.exists());
            try {
                if (out_image_file.exists())
                    obj.addFormDataPart("out_image", out_image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), out_image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "out_image exception is " + e.getMessage());
                obj.addFormDataPart("out_image", "");
            }
        } else {
            obj.addFormDataPart("out_image", "");
        }

        obj
                .addFormDataPart("teacher_id", teacher_id)
                .addFormDataPart("school_id", school_id)
                .addFormDataPart("in_latitude", in_latitude)
                .addFormDataPart("in_longitude", in_longitude)
                .addFormDataPart("in_time", in_time)
                .addFormDataPart("date", attendance_date)
                .addFormDataPart("out_latitude", out_latitude)
                .addFormDataPart("out_longitude", out_longitude)
                .addFormDataPart("out_time", out_time);

        RequestBody formBody = obj
                .build();
        return post_server_call(url_staff_attendance, formBody);
    }

    public static String send_students_attendance(String teacher_id, String school_id, String standard_id,
                                                  String boys_present_count,
                                                  String girls_present_count, String total_present_count, String image_url,
                                                  String current_date,
                                                  String current_time, String latitude, String longitude) {

        MultipartBuilder obj = new MultipartBuilder().type(MultipartBuilder.FORM);

        if (image_url != null) {
            log(LOG_TAG, "image_file send_students_attendance: " + image_url);
            File image_file = new File(image_url);
            log(LOG_TAG, "IsD FIle->" + image_file.exists());
            try {
                if (image_file.exists())
                    obj.addFormDataPart("image_url", image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "in_image exception is " + e.getMessage());
                obj.addFormDataPart("image_url", "");
            }
        } else {
            obj.addFormDataPart("image_url", "");
        }

        obj
                .addFormDataPart("teacher_id", teacher_id)
                .addFormDataPart("school_id", school_id)
                .addFormDataPart("standard_id", standard_id)
                .addFormDataPart("present_boys", boys_present_count)
                .addFormDataPart("present_girls", girls_present_count)
                .addFormDataPart("present_students", total_present_count)
                .addFormDataPart("time", current_time)
                .addFormDataPart("date", current_date)
                .addFormDataPart("latitude", latitude)
                .addFormDataPart("longitude", longitude);

        RequestBody formBody = obj
                .build();
        return post_server_call(url_students_attendance, formBody);
    }

    public static String send_examination_attendance(String teacher_id, String school_id, String standard_id,
                                                     String boys_present_count,
                                                     String girls_present_count, String total_present_count, String image_url,
                                                     String current_date,
                                                     String current_time, String latitude, String longitude, String term, String subject) {

        MultipartBuilder obj = new MultipartBuilder().type(MultipartBuilder.FORM);

        if (image_url != null) {
            log(LOG_TAG, "image_file send_examination_attendance: " + image_url);
            File image_file = new File(image_url);
            log(LOG_TAG, "IsD FIle->" + image_file.exists());
            try {
                if (image_file.exists())
                    obj.addFormDataPart("image_url", image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "in_image exception is " + e.getMessage());
                obj.addFormDataPart("image_url", "");
            }
        } else {
            obj.addFormDataPart("image_url", "");
        }

        obj
                .addFormDataPart("teacher_id", teacher_id)
                .addFormDataPart("school_id", school_id)
                .addFormDataPart("standard_id", standard_id)
                .addFormDataPart("present_boys", boys_present_count)
                .addFormDataPart("present_girls", girls_present_count)
                .addFormDataPart("present_students", total_present_count)
                .addFormDataPart("time", current_time)
                .addFormDataPart("date", current_date)
                .addFormDataPart("latitude", latitude)
                .addFormDataPart("longitude", longitude)
                .addFormDataPart("term", term)
                .addFormDataPart("subject", subject);

        RequestBody formBody = obj
                .build();
        return post_server_call(url_examination_attendance, formBody);
    }

    public static String send_daily_food_attendance(String teacher_id, String school_id, String food_id, String other_text,
                                                    String boys_count, String girls_count, String latitude, String longitude,
                                                    String date, String time, String student_img_url, String food_image_url) {
        MultipartBuilder obj = new MultipartBuilder().type(MultipartBuilder.FORM);

        if (student_img_url != null) {
            log(LOG_TAG, "image_file student_img_url: " + student_img_url);
            File image_file = new File(student_img_url);
            log(LOG_TAG, "IsD FIle->" + image_file.exists());
            try {
                if (image_file.exists())
                    obj.addFormDataPart("students_image_url", image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "in_image exception is " + e.getMessage());
                obj.addFormDataPart("students_image_url", "");
            }
        } else {
            obj.addFormDataPart("students_image_url", "");
        }

        if (food_image_url != null) {
            log(LOG_TAG, "image_file food_image_url: " + food_image_url);
            File image_file = new File(food_image_url);
            log(LOG_TAG, "IsD FIle->" + image_file.exists());
            try {
                if (image_file.exists())
                    obj.addFormDataPart("food_image_url", image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "in_image exception is " + e.getMessage());
                obj.addFormDataPart("food_image_url", "");
            }
        } else {
            obj.addFormDataPart("food_image_url", "");
        }

        obj
                .addFormDataPart("teacher_id", teacher_id)
                .addFormDataPart("school_id", school_id)
                .addFormDataPart("food_id", food_id)
                .addFormDataPart("boys_count", boys_count)
                .addFormDataPart("girls_count", girls_count)
                .addFormDataPart("other_text", other_text)
                .addFormDataPart("time", time)
                .addFormDataPart("date", date)
                .addFormDataPart("latitude", latitude)
                .addFormDataPart("longitude", longitude);

        RequestBody formBody = obj
                .build();
        return post_server_call(url_daily_food_attendance, formBody);
    }

    public static String send_leave_application(String teacher_id, String school_id, String leave_from_date,
                                                String leave_to_date, String leave_title, String image_url) {
        MultipartBuilder obj = new MultipartBuilder().type(MultipartBuilder.FORM);

        if (image_url != null) {
            log(LOG_TAG, "image_file send_leave_application: " + image_url);
            File image_file = new File(image_url);
            log(LOG_TAG, "IsD FIle->" + image_file.exists());
            try {
                if (image_file.exists())
                    obj.addFormDataPart("image_url", image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "in_image exception is " + e.getMessage());
                obj.addFormDataPart("image_url", "");
            }
        } else {
            obj.addFormDataPart("image_url", "");
        }

        obj
                .addFormDataPart("teacher_id", teacher_id)
                .addFormDataPart("school_id", school_id)
                .addFormDataPart("leave_title", leave_title)
                .addFormDataPart("leave_from_date", leave_from_date)
                .addFormDataPart("leave_to_date", leave_to_date);

        RequestBody formBody = obj
                .build();
        return post_server_call(url_leave_application, formBody);
    }

    public static String send_events(String teacher_id, String school_id, String event_date,
                                                String event_description, String event_title, String image_url) {
        MultipartBuilder obj = new MultipartBuilder().type(MultipartBuilder.FORM);

        if (image_url != null) {
            log(LOG_TAG, "image_file send_events: " + image_url);
            File image_file = new File(image_url);
            log(LOG_TAG, "IsD FIle->" + image_file.exists());
            try {
                if (image_file.exists())
                    obj.addFormDataPart("image_url", image_file.getName(), RequestBody.create(MediaType.parse("image/jpg"), image_file));//"application/pdf"
            } catch (Exception e) {
                MyApp.log(TAG, "in_image exception is " + e.getMessage());
                obj.addFormDataPart("image_url", "");
            }
        } else {
            obj.addFormDataPart("image_url", "");
        }

        obj
                .addFormDataPart("teacher_id", teacher_id)
                .addFormDataPart("school_id", school_id)
                .addFormDataPart("event_title", event_title)
                .addFormDataPart("event_date", event_date)
                .addFormDataPart("description", event_description);

        RequestBody formBody = obj
                .build();
        return post_server_call(url_school_events, formBody);
    }

    public static String get_change_pass(String username, String old_pass, String new_pass, String teacher_id) {
        RequestBody formBody = new FormEncodingBuilder()
                .add("teacher_id", teacher_id)
                .add("username", username)
                .add("old_password", old_pass)
                .add("new_password", new_pass)
                .build();

        MyApp.log(LOG_TAG, "Form body to get_school_standards contains " + formBody.toString());

        return post_server_call(url_reset_password, formBody);
    }
}

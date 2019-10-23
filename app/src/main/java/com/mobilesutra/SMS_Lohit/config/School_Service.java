package com.mobilesutra.SMS_Lohit.config;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.activities.Activity_Dashboard;
import com.mobilesutra.SMS_Lohit.activities.Activity_Notification;
import com.mobilesutra.SMS_Lohit.database.TABLE_DAILY_FOOD;
import com.mobilesutra.SMS_Lohit.database.TABLE_EVENTS;
import com.mobilesutra.SMS_Lohit.database.TABLE_EXAMINATION_ATTENDANCE;
import com.mobilesutra.SMS_Lohit.database.TABLE_FOOD_MASTER;
import com.mobilesutra.SMS_Lohit.database.TABLE_LEAVE_APPLICATION;
import com.mobilesutra.SMS_Lohit.database.TABLE_NOTIFICATION;
import com.mobilesutra.SMS_Lohit.database.TABLE_SCHOOL_STANDARDS;
import com.mobilesutra.SMS_Lohit.database.TABLE_STUDENT_ATTENDANCE;
import com.mobilesutra.SMS_Lohit.database.TABLE_TEACHER_ATTENDANCE;
import com.mobilesutra.SMS_Lohit.database.TABLE_TEACHER_MASTER;
import com.mobilesutra.SMS_Lohit.model.DTODailyFoodAttendance;
import com.mobilesutra.SMS_Lohit.model.DTOEvents;
import com.mobilesutra.SMS_Lohit.model.DTOLeaveApplication;
import com.mobilesutra.SMS_Lohit.model.DTOStudentAttendance;
import com.mobilesutra.SMS_Lohit.model.DTOTeacherAttendance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by  Ganesh Borse on 04/04/2017.
 */
public class School_Service extends IntentService {

    Context context = null;
    public static int NOTIFICATION_ID = 1;
    public static String LOG_TAG = School_Service.class.getSimpleName();

    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;

    public School_Service() {
        super("School_Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = this;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (!extras.isEmpty()) {
                MyApp.log("School_Service_onHandleIntent");
                MyApp.log("Bundle->" + extras);
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
                String messageType = gcm.getMessageType(intent);
                MyApp.log("MessageType->" + messageType);

                for (String key : extras.keySet()) {
                    MyApp.log("Bundle Debug" + key + " = \"" + extras.get(key) + "\"");
                }

                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    //sendNotification("Received: " + extras.toString());
                    appProcessGCM(extras);
                } else {
                    appProcess(extras);
                }

                BroadcastReceiver.completeWakefulIntent(intent);
            }
        }
    }

    private void appProcessGCM(Bundle bundle) {
        if (bundle != null) {
            TABLE_NOTIFICATION.insertNotification(bundle);
            generateNotification(context, "", bundle);
            updateMyActivity(context, "1", "", "Activity_Notification");
        }
    }

    private void appProcess(Bundle extras) {
        if (extras != null) {
            if (extras.containsKey("Flag")) {
                if (extras.get("Flag").equals("Sync_staff_attendance")) {
                    send_staff_attendance();
                } else if (extras.get("Flag").equals("Sync_students_attendance")) {
                    send_students_attendance();
                } else if (extras.get("Flag").equals("Sync_examination_attendance")) {
                    send_examination_attendance();
                } else if (extras.get("Flag").equals("Sync_daily_food_attendance")) {
                    send_daily_food_attendance();
                } else if (extras.get("Flag").equals("Sync_leave_application")) {
                    send_leave_application();
                } else if (extras.get("Flag").equals("Sync_events")) {
                    send_events();
                } else if (extras.get("Flag").equals("Connection")) {
                    if (extras.containsKey("Status")) {
                        if (extras.get("Status").equals("ON")) {
                            send_staff_attendance();
                            send_students_attendance();
                            send_examination_attendance();
                            send_daily_food_attendance();
                            send_leave_application();
                            send_events();
                        }
                    }
                } else if (extras.get("Flag").equals("get_all_master_data")) {
                    getMasterData();
                } else if (extras.get("Flag").equals("SMS_data")) {
                    sendDataSMS();
                }
            }
        }
    }

    private void generateNotification(Context context, String message1, Bundle extras) {
        String title = "", subtitle = "", message = "";

        if (extras != null) {
            if (extras.containsKey("notification_title"))
                title = extras.getString("notification_title");
            if (extras.containsKey("notification_subtitle"))
                subtitle = extras.getString("notification_subtitle");
            if (extras.containsKey("notification_text"))
                message = extras.getString("notification_text");
            subtitle = message;

            if (extras.containsKey("notification_type")) {

            }

            Intent intent;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)

                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            NOTIFICATION_ID = MyApp.get_Intsession(MyApp.SESSION_NOTIFICATION_ID);
            MyApp.set_session(MyApp.SESSION_NOTIFICATION_ID, (NOTIFICATION_ID + 1) + "");
            intent = new Intent(context, Activity_Notification.class).setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            intent.putExtra("message", message);
            mBuilder.setTicker(subtitle);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(subtitle));
            mBuilder.setContentText(subtitle);
            mBuilder.setSmallIcon(R.drawable.ic_notification);
            mBuilder.setColor(getResources().getColor(R.color.red));
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            PendingIntent contentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    static void broadcast(Context context, String flag, Bundle bundle) {
        MyApp.log(LOG_TAG, "in update activity->" + flag);
        Intent intent = new Intent(flag);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);

    }

    static void updateMyActivity(Context context, String response_code, String response_message, String flag) {

        Intent intent = new Intent(flag);
        //put whatever data you want to send, if any
        intent.putExtra("response_code", response_code);
        intent.putExtra("response_message", response_message);
        //send broadcast
        context.sendBroadcast(intent);

    }

    private void getMasterData() {
        String response = "-0";

        updateMyActivity(context, "2", "Fetching Master Food data", "Activity_Login");
        // Get Food Master Data
        response = MyApp.get_food_master();
        if (!response.equalsIgnoreCase("-0")) {
            MyApp.log(LOG_TAG, "get_food_master Response is " + response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null && jsonObject.length() > 0) {
                    if (jsonObject.has("response_status")) {
                        String response_status = jsonObject.getString("response_status");
                        if (response_status.equalsIgnoreCase("success")) {
                            if (jsonObject.has("food_list")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("food_list");
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    TABLE_FOOD_MASTER.deleteAllData();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String id = jsonObject1.getString("id");
                                        String food_name = jsonObject1.getString("food_name");
                                        String last_update = jsonObject1.getString("last_update");
                                        TABLE_FOOD_MASTER.insertFoodMasterList(id, food_name, last_update);
                                    }

                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                MyApp.log(LOG_TAG, "Food Master List Exception is " + e.getMessage());
            }
        }
        updateMyActivity(context, "2", "Fetching School Standards data", "Activity_Login");

        //Get School Standards
        response = MyApp.get_school_standards();
        if (!response.equalsIgnoreCase("-0")) {
            MyApp.log(LOG_TAG, "get_school_standards Response is " + response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null && jsonObject.length() > 0) {
                    if (jsonObject.has("response_status")) {
                        String response_status = jsonObject.getString("response_status");
                        if (response_status.equalsIgnoreCase("success")) {
                            if (jsonObject.has("standards_list")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("standards_list");
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    TABLE_SCHOOL_STANDARDS.deleteAllData();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String school_id = jsonObject1.getString("school_id");
                                        //String teacher_id = jsonObject1.getString("teacher_id");
                                        String teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID);
                                        String standard_id = jsonObject1.getString("standard_id");
                                        String standard = jsonObject1.getString("standard");
                                        String boys = jsonObject1.getString("boys");
                                        String girls = jsonObject1.getString("girls");
                                        String total_students = jsonObject1.getString("total_students");

                                        TABLE_SCHOOL_STANDARDS.insert_standard(school_id, teacher_id, standard_id, standard, boys, girls, total_students);
                                    }

                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                MyApp.log(LOG_TAG, "School Standards List Exception is " + e.getMessage());
            }
        }
        updateMyActivity(context, "2", "Fetching School Teacher List", "Activity_Login");

        //Get teacher Master List
        response = MyApp.get_teacher_list();
        if (!response.equalsIgnoreCase("-0")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null && jsonObject.length() > 0) {
                    if (jsonObject.has("response_status")) {
                        String response_status = jsonObject.getString("response_status");
                        if (response_status.equalsIgnoreCase("success")) {
                            if (jsonObject.has("details")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("details");
                                if (jsonArray != null && jsonArray.length() > 0) {
                                    TABLE_TEACHER_MASTER.deleteTeacherList();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String teacher_id = jsonObject1.getString("id");
                                        String teacher_name = jsonObject1.getString("teacher_name");
                                        String designation = jsonObject1.getString("designation");
                                        String mobile = jsonObject1.getString("mobile");
                                        String email = jsonObject1.getString("email");
                                        String gender = jsonObject1.getString("gender");
                                        String is_headmaster = jsonObject1.getString("is_headmaster");

                                        TABLE_TEACHER_MASTER.insert_teacher(teacher_id, teacher_name, designation, mobile, email, gender, is_headmaster);
                                    }

                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                MyApp.log(LOG_TAG, "Teacher Master List Exception is " + e.getMessage());
            }
        }
        updateMyActivity(context, "1", "", "Activity_Login");
    }

    private void send_staff_attendance() {
        ArrayList<DTOTeacherAttendance> arrayList = TABLE_TEACHER_ATTENDANCE.getUnSyncedAttendance();
        if (arrayList != null && arrayList.size() > 0) {

            for (int i = 0; i < arrayList.size(); i++) {
                DTOTeacherAttendance dtoTeacherAttendance = arrayList.get(i);

                String teacher_id = dtoTeacherAttendance.getTeacher_id();
                String school_id = dtoTeacherAttendance.getSchool_id();
                String in_latitude = dtoTeacherAttendance.getIn_latitude();
                String in_longitude = dtoTeacherAttendance.getIn_longitude();
                String in_time = dtoTeacherAttendance.getIn_time();
                String in_image_url = dtoTeacherAttendance.getIn_image_url();
                String attendance_date = dtoTeacherAttendance.getAttendance_date();
                String out_latitude = dtoTeacherAttendance.getOut_latitude();
                String out_longitude = dtoTeacherAttendance.getOut_longitude();
                String out_time = dtoTeacherAttendance.getOut_time();
                String out_image_url = dtoTeacherAttendance.getOut_image_url();

                String response = MyApp.send_staff_attendance(teacher_id, school_id, in_latitude, in_longitude, in_time, in_image_url, attendance_date,
                        out_latitude, out_longitude, out_time, out_image_url);

                MyApp.log(LOG_TAG, "Response is " + response);
                if (response != null) {
                    if (!response.equalsIgnoreCase("-0")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response_status")) {
                                String response_status = jsonObject.getString("response_status");
                                if (response_status.equalsIgnoreCase("success")) {
                                    TABLE_TEACHER_ATTENDANCE.change_synced_flag(teacher_id, school_id, attendance_date);
                                }
                            }
                        } catch (JSONException e) {
                            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    private void send_students_attendance() {
        ArrayList<DTOStudentAttendance> arrayList = TABLE_STUDENT_ATTENDANCE.getUnSyncedAttendance();
        if (arrayList != null && arrayList.size() > 0) {
            MyApp.log(LOG_TAG, "Send students attendance count is " + arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                DTOStudentAttendance dtoStudentAttendance = arrayList.get(i);

                String teacher_id = dtoStudentAttendance.getTeacher_id();
                String school_id = dtoStudentAttendance.getSchool_id();
                String standard_id = dtoStudentAttendance.getStandard_id();
                String boys_present_count = dtoStudentAttendance.getBoys_present_count();
                String girls_present_count = dtoStudentAttendance.getGirls_present_count();
                String total_present_count = dtoStudentAttendance.getTotal_present_count();
                String image_url = dtoStudentAttendance.getImage_url();
                String current_date = dtoStudentAttendance.getCurrent_date();
                String current_time = dtoStudentAttendance.getCurrent_time();
                String latitude = dtoStudentAttendance.getLatitude();
                String longitude = dtoStudentAttendance.getLongitude();

                String response = MyApp.send_students_attendance(teacher_id, school_id, standard_id, boys_present_count,
                        girls_present_count, total_present_count, image_url, current_date, current_time, latitude, longitude);

                MyApp.log(LOG_TAG, "Response is " + response);
                if (response != null) {
                    if (!response.equalsIgnoreCase("-0")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response_status")) {
                                String response_status = jsonObject.getString("response_status");
                                if (response_status.equalsIgnoreCase("success")) {
                                    TABLE_STUDENT_ATTENDANCE.change_synced_flag(teacher_id, school_id, standard_id, current_date);
                                }
                            }
                        } catch (JSONException e) {
                            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void send_examination_attendance() {
        ArrayList<DTOStudentAttendance> arrayList = TABLE_EXAMINATION_ATTENDANCE.getUnSyncedAttendance();
        if (arrayList != null && arrayList.size() > 0) {
            MyApp.log(LOG_TAG, "Send examination attendance count is " + arrayList.size());

            for (int i = 0; i < arrayList.size(); i++) {
                DTOStudentAttendance dtoStudentAttendance = arrayList.get(i);

                String teacher_id = dtoStudentAttendance.getTeacher_id();
                String school_id = dtoStudentAttendance.getSchool_id();
                String standard_id = dtoStudentAttendance.getStandard_id();
                String boys_present_count = dtoStudentAttendance.getBoys_present_count();
                String girls_present_count = dtoStudentAttendance.getGirls_present_count();
                String total_present_count = dtoStudentAttendance.getTotal_present_count();
                String image_url = dtoStudentAttendance.getImage_url();
                String current_date = dtoStudentAttendance.getCurrent_date();
                String current_time = dtoStudentAttendance.getCurrent_time();
                String latitude = dtoStudentAttendance.getLatitude();
                String longitude = dtoStudentAttendance.getLongitude();
                String term = dtoStudentAttendance.getTerm();
                String subject = dtoStudentAttendance.getSubject();

                String response = MyApp.send_examination_attendance(teacher_id, school_id, standard_id, boys_present_count,
                        girls_present_count, total_present_count, image_url, current_date, current_time, latitude, longitude, term, subject);

                MyApp.log(LOG_TAG, "Response is " + response);
                if (response != null) {
                    if (!response.equalsIgnoreCase("-0")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response_status")) {
                                String response_status = jsonObject.getString("response_status");
                                if (response_status.equalsIgnoreCase("success")) {
                                    TABLE_EXAMINATION_ATTENDANCE.change_synced_flag(teacher_id, school_id, standard_id, current_date, term, subject);
                                }
                            }
                        } catch (JSONException e) {
                            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    private void send_daily_food_attendance() {
        ArrayList<DTODailyFoodAttendance> arrayList = TABLE_DAILY_FOOD.getUnSyncedDailyFoodAttendance();
        if (arrayList != null && arrayList.size() > 0) {
            MyApp.log(LOG_TAG, "Send examination attendance count is " + arrayList.size());

            for (int i = 0; i < arrayList.size(); i++) {
                DTODailyFoodAttendance dtoDailyFoodAttendance = arrayList.get(i);

                String teacher_id = dtoDailyFoodAttendance.getTeacher_id();
                String school_id = dtoDailyFoodAttendance.getSchool_id();
                String food_id = dtoDailyFoodAttendance.getFood_id();
                String other_text = dtoDailyFoodAttendance.getOther_text();
                String boys_count = dtoDailyFoodAttendance.getBoys_count();
                String girls_count = dtoDailyFoodAttendance.getGirls_count();
                String latitude = dtoDailyFoodAttendance.getLatitude();
                String longitude = dtoDailyFoodAttendance.getLongitude();
                String date = dtoDailyFoodAttendance.getDate();
                String time = dtoDailyFoodAttendance.getTime();
                String student_img_url = dtoDailyFoodAttendance.getStudent_img_url();
                String food_image_url = dtoDailyFoodAttendance.getFood_image_url();

                String response = MyApp.send_daily_food_attendance(teacher_id, school_id, food_id, other_text,
                        boys_count, girls_count, latitude, longitude, date, time, student_img_url, food_image_url);

                MyApp.log(LOG_TAG, "Response is " + response);
                if (response != null) {
                    if (!response.equalsIgnoreCase("-0")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response_status")) {
                                String response_status = jsonObject.getString("response_status");
                                if (response_status.equalsIgnoreCase("success")) {
                                    TABLE_DAILY_FOOD.change_synced_flag(teacher_id, school_id, food_id, date);
                                }
                            }
                        } catch (JSONException e) {
                            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    private void send_leave_application() {
        ArrayList<DTOLeaveApplication> arrayList = TABLE_LEAVE_APPLICATION.getUnSyncedLeaveApplications();
        if (arrayList != null && arrayList.size() > 0) {
            MyApp.log(LOG_TAG, "Send leave application count is " + arrayList.size());

            for (int i = 0; i < arrayList.size(); i++) {
                DTOLeaveApplication dtoLeaveApplication = arrayList.get(i);

                String leave_id = dtoLeaveApplication.getLeave_id();
                String teacher_id = dtoLeaveApplication.getTeacher_id();
                String school_id = dtoLeaveApplication.getSchool_id();
                String leave_from_date = dtoLeaveApplication.getLeave_from_date();
                String leave_to_date = dtoLeaveApplication.getLeave_to_date();
                String leave_title = dtoLeaveApplication.getLeave_title();
                String image_url = dtoLeaveApplication.getImage_url();

                String response = MyApp.send_leave_application(teacher_id, school_id, leave_from_date, leave_to_date, leave_title, image_url);

                MyApp.log(LOG_TAG, "Response is " + response);
                if (response != null) {
                    if (!response.equalsIgnoreCase("-0")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response_status")) {
                                String response_status = jsonObject.getString("response_status");
                                if (response_status.equalsIgnoreCase("success")) {
                                    TABLE_LEAVE_APPLICATION.change_synced_flag(leave_id, teacher_id, school_id);
                                }
                            }
                        } catch (JSONException e) {
                            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    private void send_events() {
        ArrayList<DTOEvents> arrayList = TABLE_EVENTS.getUnSyncedEvents();
        if (arrayList != null && arrayList.size() > 0) {
            MyApp.log(LOG_TAG, "Send school events count is " + arrayList.size());

            for (int i = 0; i < arrayList.size(); i++) {
                DTOEvents dtoEvents = arrayList.get(i);

                String event_id = dtoEvents.getEvent_id();
                String teacher_id = dtoEvents.getTeacher_id();
                String school_id = dtoEvents.getSchool_id();
                String event_date = dtoEvents.getEvent_date();
                String event_description = dtoEvents.getEvent_description();
                String event_title = dtoEvents.getEvent_title();
                String image_url = dtoEvents.getImage_url();

                String response = MyApp.send_events(teacher_id, school_id, event_date, event_description, event_title, image_url);

                MyApp.log(LOG_TAG, "Response is " + response);
                if (response != null) {
                    if (!response.equalsIgnoreCase("-0")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("response_status")) {
                                String response_status = jsonObject.getString("response_status");
                                if (response_status.equalsIgnoreCase("success")) {
                                    TABLE_EVENTS.change_synced_flag(event_id, teacher_id, school_id);
                                }
                            }
                        } catch (JSONException e) {
                            MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    private void sendDataSMS() {
        MyApp.log(LOG_TAG, "In sendDataSMS");

        ArrayList<LinkedHashMap<String, String>> arrayListTA = TABLE_TEACHER_ATTENDANCE.getSmsAttendance();

        if (arrayListTA != null && arrayListTA.size() > 0) {
            String server_mobile = MyApp.get_session(MyApp.SESSION_SERVER_MOBILE_NUMBER);
            for (int i = 0; i < arrayListTA.size(); i++) {
                LinkedHashMap lhm = arrayListTA.get(i);
                final String id = lhm.get("id").toString();
                String message = lhm.get("message").toString();
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(SENT), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                sendBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS sent");
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                //Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Generic failure");
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                //Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "No service");
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                //Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Null PDU");
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                //Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Radio off");
                                break;
                        }
                    }
                };

                //---when the SMS has been delivered---
                deliveryBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                //MyApp.log(LOG_TAG, "SMS delivered");
                                TABLE_TEACHER_ATTENDANCE.change_synced_flag_sms(id);
                                break;
                            case Activity.RESULT_CANCELED:
                                //Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS not delivered");
                                break;
                        }
                    }
                };


                registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
                registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(server_mobile, null, message, sentPI, deliveredPI);

                TABLE_TEACHER_ATTENDANCE.change_synced_flag_sms(id);

                unregisterReceiver(sendBroadcastReceiver);
                unregisterReceiver(deliveryBroadcastReceiver);
            }
        }

        ArrayList<LinkedHashMap<String, String>> arrayListTL = TABLE_LEAVE_APPLICATION.getSmsAttendance();

        if (arrayListTL != null && arrayListTL.size() > 0) {
            String server_mobile = MyApp.get_session(MyApp.SESSION_SERVER_MOBILE_NUMBER);
            for (int i = 0; i < arrayListTL.size(); i++) {
                LinkedHashMap lhm = arrayListTL.get(i);
                final String id = lhm.get("id").toString();
                String message = lhm.get("message").toString();
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(SENT), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                sendBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS sent");
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                //Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Generic failure");
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                //Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "No service");
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                //Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Null PDU");
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                //Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Radio off");
                                break;
                        }
                    }
                };

                //---when the SMS has been delivered---
                deliveryBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS delivered");
                                TABLE_LEAVE_APPLICATION.change_synced_flag_sms(id);
                                break;
                            case Activity.RESULT_CANCELED:
                                //Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS not delivered");
                                break;
                        }
                    }
                };


                registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
                registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(server_mobile, null, message, sentPI, deliveredPI);

                TABLE_LEAVE_APPLICATION.change_synced_flag_sms(id);

                unregisterReceiver(sendBroadcastReceiver);
                unregisterReceiver(deliveryBroadcastReceiver);
            }
        }

        ArrayList<LinkedHashMap<String, String>> arrayListSA = TABLE_STUDENT_ATTENDANCE.getSmsAttendance();

        if (arrayListSA != null && arrayListSA.size() > 0) {
            String server_mobile = MyApp.get_session(MyApp.SESSION_SERVER_MOBILE_NUMBER);
            for (int i = 0; i < arrayListSA.size(); i++) {
                LinkedHashMap lhm = arrayListSA.get(i);
                final String id = lhm.get("id").toString();
                String message = lhm.get("message").toString();
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(SENT), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                sendBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS sent");
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                //Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Generic failure");
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                //Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "No service");
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                //Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Null PDU");
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                //Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Radio off");
                                break;
                        }
                    }
                };

                //---when the SMS has been delivered---
                deliveryBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS delivered");
                                TABLE_STUDENT_ATTENDANCE.change_synced_flag_sms(id);
                                break;
                            case Activity.RESULT_CANCELED:
                                //Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS not delivered");
                                break;
                        }
                    }
                };


                registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
                registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(server_mobile, null, message, sentPI, deliveredPI);

                TABLE_STUDENT_ATTENDANCE.change_synced_flag_sms(id);

                unregisterReceiver(sendBroadcastReceiver);
                unregisterReceiver(deliveryBroadcastReceiver);
            }
        }

        ArrayList<LinkedHashMap<String, String>> arrayListEA = TABLE_EXAMINATION_ATTENDANCE.getSmsAttendance();

        if (arrayListEA != null && arrayListEA.size() > 0) {
            String server_mobile = MyApp.get_session(MyApp.SESSION_SERVER_MOBILE_NUMBER);
            for (int i = 0; i < arrayListEA.size(); i++) {
                LinkedHashMap lhm = arrayListEA.get(i);
                final String id = lhm.get("id").toString();
                String message = lhm.get("message").toString();
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(SENT), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                sendBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS sent");
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                //Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Generic failure");
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                //Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "No service");
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                //Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Null PDU");
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                //Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Radio off");
                                break;
                        }
                    }
                };

                //---when the SMS has been delivered---
                deliveryBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS delivered");
                                TABLE_EXAMINATION_ATTENDANCE.change_synced_flag_sms(id);
                                break;
                            case Activity.RESULT_CANCELED:
                                //Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS not delivered");
                                break;
                        }
                    }
                };


                registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
                registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(server_mobile, null, message, sentPI, deliveredPI);

                TABLE_EXAMINATION_ATTENDANCE.change_synced_flag_sms(id);

                unregisterReceiver(sendBroadcastReceiver);
                unregisterReceiver(deliveryBroadcastReceiver);
            }
        }

        ArrayList<LinkedHashMap<String, String>> arrayListDF = TABLE_DAILY_FOOD.getSmsAttendance();

        if (arrayListDF != null && arrayListDF.size() > 0) {
            String server_mobile = MyApp.get_session(MyApp.SESSION_SERVER_MOBILE_NUMBER);
            for (int i = 0; i < arrayListDF.size(); i++) {
                LinkedHashMap lhm = arrayListDF.get(i);
                final String id = lhm.get("id").toString();
                String message = lhm.get("message").toString();
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(SENT), 0);

                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                sendBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS sent");
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                //Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Generic failure");
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                //Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "No service");
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                //Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Null PDU");
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                //Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "Radio off");
                                break;
                        }
                    }
                };

                //---when the SMS has been delivered---
                deliveryBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                //Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS delivered");
                                TABLE_DAILY_FOOD.change_synced_flag_sms(id);
                                break;
                            case Activity.RESULT_CANCELED:
                                //Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                MyApp.log(LOG_TAG, "SMS not delivered");
                                break;
                        }
                    }
                };


                registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
                registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(server_mobile, null, message, sentPI, deliveredPI);

                TABLE_DAILY_FOOD.change_synced_flag_sms(id);

                unregisterReceiver(sendBroadcastReceiver);
                unregisterReceiver(deliveryBroadcastReceiver);
            }
        }
    }

}

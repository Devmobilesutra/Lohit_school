package com.mobilesutra.SMS_Lohit.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.config.MyReceiver;
import com.mobilesutra.SMS_Lohit.config.School_Service;
import com.mobilesutra.SMS_Lohit.database.TABLE_FOOD_MASTER;
import com.mobilesutra.SMS_Lohit.database.TABLE_LOGIN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Activity_Login extends AppCompatActivity {

    private static String LOG_TAG = Activity_Login.class.getSimpleName();
    Context context = null;
    EditText edit_username = null, edit_password = null;
    Button btn_login = null;
    TextView txt_forget_password = null;
    private String deviceId = "", str_username = "", str_password = "";
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__login);
        context = this;
        context.registerReceiver(mMessageReceiver, new IntentFilter(LOG_TAG));

        if (((MyApp) getApplicationContext()).getRegistrationId(context).equalsIgnoreCase("")) {
            ((MyApp) getApplicationContext()).getRegistrationGCMID();
        }
        //Getting device id
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        initComponents();
        initComponentListener();
        bindComponentData();

        if (!MyApp.get_session(MyApp.SESSION_IS_RECEIVER).equalsIgnoreCase("Y")) {
            MyApp.set_session(MyApp.SESSION_IS_RECEIVER, "Y");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);

            Intent intent = new Intent(Activity_Login.this, MyReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        context.registerReceiver(mMessageReceiver, new IntentFilter(LOG_TAG));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.registerReceiver(mMessageReceiver, new IntentFilter(LOG_TAG));
        context.unregisterReceiver(mMessageReceiver);
    }

    private void initComponents() {
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        txt_forget_password = (TextView) findViewById(R.id.txt_forget_password);
        /*String mystring = new String("RESET PASSWORD");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        txt_forget_password.setText(content);*/
    }

    private void initComponentListener() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((MyApp) getApplicationContext()).getRegistrationId(context).equalsIgnoreCase("")) {
                    ((MyApp) getApplicationContext()).getRegistrationGCMID();
                }

                str_username = edit_username.getText().toString().trim();
                str_password = edit_password.getText().toString().trim();

                if (str_username.equals("") || str_password.equals("")) {
                    String empty_input = getResources().getString(R.string.Login_validations);
                    getdialog_showResponse(context, empty_input);
                } else {
                    if (TABLE_LOGIN.checkUser(str_username, str_password)) {
                        Intent intent = new Intent(Activity_Login.this, Activity_Dashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
                    } else {
                        if (((MyApp) getApplicationContext()).isInternetAvailable()) {
                            if (((MyApp) getApplicationContext()).getRegistrationId(
                                    context).equalsIgnoreCase("")) {
                                String invalid_regid = getResources().getString(R.string.Gcm_error_response);
                                getdialog_showResponse(context, invalid_regid);
                            } else {
                                HTTPLogin httpLogin = new HTTPLogin();
                                httpLogin.execute();
                            }
                        } else {
                            String internet_response = getResources().getString(R.string.no_internet_message);
                            getdialog_showResponse(context, internet_response);
                        }
                    }
                }
            }
        });

        txt_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "RESET PASSWORD Pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindComponentData() {

        if (!MyApp.get_session(MyApp.SESSION_USERNAME).equalsIgnoreCase("")) {
            edit_username.setText(MyApp.get_session(MyApp.SESSION_USERNAME).toString().trim());
            edit_username.setSelection(MyApp.get_session(MyApp.SESSION_USERNAME).toString().length());
        }

        if (!MyApp.get_session(MyApp.SESSION_PASSWORD).equalsIgnoreCase("")) {
            edit_password.setText(MyApp.get_session(MyApp.SESSION_PASSWORD).toString().trim());
            edit_password.setSelection(MyApp.get_session(MyApp.SESSION_PASSWORD).toString().length());
        }
    }

    public class HTTPLogin extends AsyncTask<Void, String, Void> {

        String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String title = getResources().getString(R.string.app_name);
            SpannableString ss1 = new SpannableString(title);
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss1.length(), 0);
            progressDialog = ProgressDialog.show(context, ss1, "Please wait.. Validating login credentials.", false, false);

            Drawable drawable = new ProgressBar(Activity_Login.this).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(Activity_Login.this, R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN);
            progressDialog.setIndeterminateDrawable(drawable);
            //progressDialog = ProgressDialog.show(context, getResources().getString(R.string.app_name), "Please wait.. Checking teacher login.", false, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = MyApp.teacher_login(str_username, str_password, deviceId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!response.equalsIgnoreCase("-0")) {
                MyApp.log(LOG_TAG, "Response is " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.length() > 0) {
                        if (jsonObject.has("response_status")) {
                            String response_status = jsonObject.getString("response_status");
                            if (response_status.equalsIgnoreCase("success")) {
                                if (jsonObject.has("details")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("details");
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                        String teacher_id = jsonObject1.getString("teacher_id");
                                        String school_id = jsonObject1.getString("school_id");
                                        String teacher_name = jsonObject1.getString("teacher_name");
                                        String designation = jsonObject1.getString("designation");
                                        String latitude = jsonObject1.getString("latitude");
                                        String longitude = jsonObject1.getString("longitude");
                                        String school_name = jsonObject1.getString("school_name");
                                        String is_teacher = jsonObject1.getString("is_teacher");
                                        String image_url = "";
                                        if (jsonObject1.has("image_url")) {
                                            image_url = jsonObject1.getString("image_url");
                                        }

                                        MyApp.set_session(MyApp.SESSION_USERNAME, str_username);
                                        MyApp.set_session(MyApp.SESSION_PASSWORD, str_password);
                                        MyApp.set_session(MyApp.SESSION_TEACHER_ID, teacher_id);
                                        MyApp.set_session(MyApp.SESSION_SCHOOL_ID, school_id);
                                        MyApp.set_session(MyApp.SESSION_SCHOOL_NAME, school_name);
                                        MyApp.set_session(MyApp.SESSION_TEACHER_NAME, teacher_name);
                                        MyApp.set_session(MyApp.SESSION_TEACHER_DESIGNATION, designation);
                                        MyApp.set_session(MyApp.SESSION_SCHOOL_LATITUDE, latitude);
                                        MyApp.set_session(MyApp.SESSION_SCHOOL_LONGITUDE, longitude);
                                        MyApp.set_session(MyApp.SESSION_IS_TEACHER, is_teacher);
                                        MyApp.set_session(MyApp.SESSION_SCHOOL_IMAGE_URL, image_url);

                                        TABLE_LOGIN.saveTeacherData(teacher_id, school_id, teacher_name, designation, latitude, longitude, school_name, str_username, str_password, is_teacher, image_url);

                                        /*if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }

                                        if (!TABLE_FOOD_MASTER.hasData()){
                                            HTTPFoodMaster httpFoodMaster = new HTTPFoodMaster();
                                            httpFoodMaster.execute();
                                        }
                                        showSuccessResponse(context, jsonObject.getString("response_message"));*/

                                        Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
                                        intent1.putExtra("Flag", "get_all_master_data");
                                        startService(intent1);

                                    }
                                }
                            } else {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                getdialog_showResponse(context, jsonObject.getString("response_message"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    MyApp.log(LOG_TAG, "Login Teacher Exception is " + e.getMessage());
                }
            }
        }
    }

    public class HTTPFoodMaster extends AsyncTask<Void, String, Void> {

        String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, getResources().getString(R.string.app_name), "Fetching Daily Food Master Data.", false, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = MyApp.get_food_master();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!response.equalsIgnoreCase("-0")) {
                MyApp.log(LOG_TAG, "Response is " + response);

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
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    MyApp.log(LOG_TAG, "Food Master List Exception is " + e.getMessage());
                }
            }
        }
    }

    private void getdialog_showResponse(Context context, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.app_name)).setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((MyApp) getApplicationContext()).getRegistrationGCMID();

                    }
                }).show();

    }

    private void showSuccessResponse(Context context, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.app_name)).setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Activity_Login.this, Activity_Dashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
                    }
                }).show();

    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            //String service = intent.getStringExtra("service");
            String status = intent.getStringExtra("response_code");
            String message = intent.getStringExtra("response_message");
            MyApp.log("CheckSyncUp", "In broadcast receiver of Login member activity");

            // Toast.makeText(context,"Status:"+status+"\nMessage:"+message,Toast.LENGTH_SHORT).show();
            if (status != null) {
                if (status.equals("0")) {
                    MyApp.log("CheckSyncUp", "In if status = 0 of broadcast receiver of Login member activity");
                    if (progressDialog != null)
                        if (progressDialog.isShowing())
                            progressDialog.hide();
                }
                if (status.equals("1") && message.equals("")) {
                    MyApp.log("CheckSyncUp", "In if status = 1 and message = BannerMaster of broadcast receiver of Login member activity");
                    if (progressDialog != null)
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    showSuccessResponse(context, "Teacher logged in successfully.");
                } else {
                    MyApp.log("CheckSyncUp", "In else status != 1 of broadcast receiver of Login member activity");
                    if (progressDialog != null)
                        if (progressDialog.isShowing())
                            progressDialog.setMessage(message);
                }
            }
        }
    };
}

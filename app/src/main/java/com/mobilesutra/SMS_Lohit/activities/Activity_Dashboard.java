package com.mobilesutra.SMS_Lohit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.BroadcastReceiver;
import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.database.TABLE_LOGIN;
import com.mobilesutra.SMS_Lohit.database.TABLE_TEACHER_ATTENDANCE;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormat;

public class Activity_Dashboard extends AppCompatActivity {
    private static String LOG_TAG = Activity_Dashboard.class.getSimpleName();
    Context context = null;
    LinearLayout linear_teacher_attendance = null, linear_students_attendance = null, linear_mid_day_meal = null,
            linear_examination_attendance = null, linear_leave_application = null, linear_updates = null,
            linear_upload_event = null;

    CardView card_teacher_attendance = null, card_students_attendance = null, card_mid_day_meal = null,
            card_examination_attendance = null;

    TextView txt_teacher_attendance = null, txt_student_attendance = null, txt_examination_attendance = null,
            txt_meal_report = null, txt_updates = null, txt_upload_event = null, txt_leave = null;

    TextView txt_teacher_attendance_hindi = null, txt_students_attendance_hindi = null, txt_mid_day_meal_hindi = null,
            txt_examination_attendance_hindi = null, txt_leave_hindi = null, txt_updates_hindi = null, txt_upload_hindi;

    ImageButton btn_back = null, btn_setting = null;
    /*TextView txt_toolbar_title = null;*/
    int text_ids[] = {R.id.txt_teacher_attendance, R.id.txt_student_attendance,
            R.id.txt_meal_report, R.id.txt_examination_attendance, R.id.txt_updates, R.id.txt_upload_event, R.id.txt_leave};
    int icon_ids[] = {R.id.img_staff, R.id.img_student, R.id.img_meal,
            R.id.img_examination, R.id.img_alerts, R.id.img_upload, R.id.img_leave};

    ImageView img_staff, img_student, img_meal, img_examination, img_alerts, img_upload, img_leave;
    ImageView img_school = null;
    TextView txt_school_name = null, txt_teacher_name = null; //txt_teacher_designation = null, txt_current_date_time = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);

        context = this;
        initComponents();
        initComponentListener();
        bindComponentData();

        if (MyApp.get_session(MyApp.SESSION_IS_TEACHER).equalsIgnoreCase("yes")) {
            card_students_attendance.setVisibility(View.VISIBLE);
            card_mid_day_meal.setVisibility(View.VISIBLE);
            card_examination_attendance.setVisibility(View.VISIBLE);

            txt_teacher_attendance.setText(getResources().getString(R.string.text_teacher_attendance));
            txt_teacher_attendance_hindi.setText(getResources().getString(R.string.text_teacher_attendance_hindi));
        } else {
            card_students_attendance.setVisibility(View.GONE);
            card_mid_day_meal.setVisibility(View.GONE);
            card_examination_attendance.setVisibility(View.GONE);

            txt_teacher_attendance.setText(getResources().getString(R.string.text_staff_attendance));
            txt_teacher_attendance_hindi.setText(getResources().getString(R.string.text_staff_attendance_hindi));
        }
    }

    private void initComponents() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_setting = (ImageButton) findViewById(R.id.btn_setting);
        btn_setting.setVisibility(View.VISIBLE);

        /* txt_toolbar_title = (TextView) findViewById(R.id.txt_toolbar_title);
        txt_toolbar_title.setText("");*/

        txt_teacher_attendance = (TextView) findViewById(R.id.txt_teacher_attendance);
        txt_student_attendance = (TextView) findViewById(R.id.txt_student_attendance);
        txt_examination_attendance = (TextView) findViewById(R.id.txt_examination_attendance);
        txt_meal_report = (TextView) findViewById(R.id.txt_meal_report);
        txt_updates = (TextView) findViewById(R.id.txt_updates);
        txt_upload_event = (TextView) findViewById(R.id.txt_upload_event);
        txt_leave = (TextView) findViewById(R.id.txt_leave);

        img_staff = (ImageView) findViewById(R.id.img_staff);
        img_student = (ImageView) findViewById(R.id.img_student);
        img_meal = (ImageView) findViewById(R.id.img_meal);
        img_examination = (ImageView) findViewById(R.id.img_examination);
        img_alerts = (ImageView) findViewById(R.id.img_alerts);
        img_upload = (ImageView) findViewById(R.id.img_upload);
        img_leave = (ImageView) findViewById(R.id.img_leave);

        // txt_teacher_info = (TextView) findViewById(R.id.txt_teacher_info);

        txt_school_name = (TextView) findViewById(R.id.txt_school_name_dashboard);
        txt_teacher_name = (TextView) findViewById(R.id.txt_teacher_name);
        /*txt_teacher_designation = (TextView) findViewById(R.id.txt_teacher_designation);
        txt_current_date_time = (TextView) findViewById(R.id.txt_current_date_time);*/

        linear_teacher_attendance = (LinearLayout) findViewById(R.id.linear_teacher_attendance);
        linear_students_attendance = (LinearLayout) findViewById(R.id.linear_students_attendance);
        linear_mid_day_meal = (LinearLayout) findViewById(R.id.linear_mid_day_meal);
        linear_examination_attendance = (LinearLayout) findViewById(R.id.linear_examination_attendance);
        linear_leave_application = (LinearLayout) findViewById(R.id.linear_leave_application);
        linear_updates = (LinearLayout) findViewById(R.id.linear_updates);
        linear_upload_event = (LinearLayout) findViewById(R.id.linear_upload_event);

        card_teacher_attendance = (CardView) findViewById(R.id.card_teacher_attendance);
        card_students_attendance = (CardView) findViewById(R.id.card_students_attendance);
        card_mid_day_meal = (CardView) findViewById(R.id.card_mid_day_meal);
        card_examination_attendance = (CardView) findViewById(R.id.card_examination_attendance);


        txt_teacher_attendance_hindi = (TextView) findViewById(R.id.txt_teacher_attendance_hindi);
        txt_students_attendance_hindi = (TextView) findViewById(R.id.txt_students_attendance_hindi);
        txt_mid_day_meal_hindi = (TextView) findViewById(R.id.txt_mid_day_meal_hindi);
        txt_examination_attendance_hindi = (TextView) findViewById(R.id.txt_examination_attendance_hindi);
        txt_leave_hindi = (TextView) findViewById(R.id.txt_leave_hindi);
        txt_updates_hindi = (TextView) findViewById(R.id.txt_updates_hindi);
        txt_upload_hindi = (TextView) findViewById(R.id.txt_upload_hindi);

        img_school = (ImageView) findViewById(R.id.img_school);
    }

    private void initComponentListener() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v1 = btn_setting;
                PopupMenu popup = new PopupMenu(context, v1);
                popup.getMenuInflater().inflate(R.menu.menu_setting, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == (R.id.acknowledgment)) {
                            Intent intent = new Intent(Activity_Dashboard.this, Activity_Acknowledgment.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
                        } else if (item.getItemId() == (R.id.terms_privacy)) {
                            Intent intent = new Intent(Activity_Dashboard.this, Activity_Terms_Policy.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
                        } else if (item.getItemId() == (R.id.developer)) {
                            /*Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse("http://www.mobilesutra.com");
                            intent.setData(uri);
                            startActivity(intent);*/
                            Intent intent = new Intent(Activity_Dashboard.this, Activity_developer.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
                        } else if (item.getItemId() == (R.id.reset_password)) {

                            if (((MyApp) getApplication()).isInternetAvailable()) {

                                final Dialog dialog = new Dialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.getWindow().setBackgroundDrawableResource(R.color.white);
                                dialog.setContentView(R.layout.change_password_dialog);

                                final EditText edt_old_pass = (EditText) dialog.findViewById(R.id.edt_old_password);
                                final EditText edt_new_pass = (EditText) dialog.findViewById(R.id.edt_new_password);
                                final EditText edt_reenter_pass = (EditText) dialog.findViewById(R.id.edt_re_password);
                                final Button btn_done = (Button) dialog.findViewById(R.id.btn_done);
                                GradientDrawable bgShape = (GradientDrawable) btn_done.getBackground();
                                bgShape.setColor(getResources().getColor(R.color.dark_blue));
                                dialog.show();

                                final String current_password = MyApp.get_session(MyApp.SESSION_PASSWORD);

                                btn_done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (edt_old_pass.getText().length() == 0) {
                                            Snackbar.make(btn_done, "Please enter current password", Snackbar.LENGTH_SHORT).show();
                                        } else if (!current_password.equals(edt_old_pass.getText().toString())) {
                                            Snackbar.make(btn_done, "Please enter valid current password", Snackbar.LENGTH_SHORT).show();
                                        } else if (edt_new_pass.getText().length() == 0) {
                                            Snackbar.make(btn_done, "Please enter new password", Snackbar.LENGTH_SHORT).show();
                                        } else if (current_password.equalsIgnoreCase(edt_new_pass.getText().toString())) {
                                            Snackbar.make(btn_done, "Current and New password should not be same", Snackbar.LENGTH_SHORT).show();
                                        } else if (edt_reenter_pass.getText().length() == 0) {
                                            Snackbar.make(btn_done, "Please re-enter new password", Snackbar.LENGTH_SHORT).show();
                                        } else if (!edt_new_pass.getText().toString().equals(edt_reenter_pass.getText().toString())) {
                                            Snackbar.make(btn_done, "Please re-enter same password", Snackbar.LENGTH_SHORT).show();
                                        } else if (((MyApp) getApplication()).isInternetAvailable()) {
                                            dialog.dismiss();
                                            String old_pass = edt_old_pass.getText().toString();
                                            String new_pass = edt_new_pass.getText().toString();
                                            String re_pass = edt_reenter_pass.getText().toString();
                                            AsyncChangePassword asyncChangePassword = new AsyncChangePassword();
                                            asyncChangePassword.execute(old_pass, new_pass, re_pass);
                                        } else {
                                            showInternetDialog("Reset Password");
                                        }
                                    }
                                });
                            } else {
                                showResponseDialog("Reset Password", "You cannot reset password without data or internet.");
                            }
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });

        linear_teacher_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_teacher_attendance.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Teacher_Attendance.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        txt_teacher_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_teacher_attendance.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Teacher_Attendance.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        linear_students_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_student_attendance.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Student_Attendance.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        txt_student_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_student_attendance.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Student_Attendance.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        linear_examination_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_examination_attendance.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Examination_Attendance.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        txt_examination_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_examination_attendance.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Examination_Attendance.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        linear_mid_day_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_meal_report.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Mid_Day_Meal.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        txt_meal_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_meal_report.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Mid_Day_Meal.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        linear_updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_updates.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Notification.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        txt_updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_updates.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Notification.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        linear_upload_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_upload_event.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Upload_Events.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        txt_upload_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_upload_event.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Upload_Events.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        linear_leave_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_leave.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Leave_Application.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });

        txt_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextColor(txt_leave.getId());
                Intent intent = new Intent(Activity_Dashboard.this, Activity_Leave_Application.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
            }
        });
    }

    private void bindComponentData() {
        /*String teacher_info = TABLE_LOGIN.getTeacherInfo(MyApp.get_session(MyApp.SESSION_TEACHER_ID));
        txt_teacher_info.setText(teacher_info);*/

        txt_school_name.setText(MyApp.get_session(MyApp.SESSION_SCHOOL_NAME));
        txt_teacher_name.setText(MyApp.get_session(MyApp.SESSION_TEACHER_NAME) + ", " + MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));
        //txt_teacher_designation.setText(MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));

        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        //txt_current_date_time.setText(currentDateTimeString);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        //txt_current_date_time.setText(date);

        if (!MyApp.get_session(MyApp.SESSION_SCHOOL_IMAGE_URL).equalsIgnoreCase("")) {
            Picasso.with(context)
                    .load(MyApp.get_session(MyApp.SESSION_SCHOOL_IMAGE_URL))
                    .placeholder(R.drawable.school_img1)
                    .error(R.drawable.school_img1)
                    .resize(img_school.getWidth(), (int) getResources().getDimension(R.dimen.image_height))
                    .into(img_school);
        }
    }

    public void changeTextColor(int text_id) {
        if (text_ids != null && text_ids.length > 0) {
            for (int i = 0; i < text_ids.length; i++) {
                if (text_id == text_ids[i]) {
                    TextView textView = (TextView) findViewById(text_id);
                    textView.setTextColor(getResources().getColor(R.color.teacher_details_background));
                    /*if(i == 0){
                        img_staff.setImageDrawable(getResources().getDrawable(R.drawable.staff_green));
                    } else if(i == 1){
                        img_student.setImageDrawable(getResources().getDrawable(R.drawable.student));
                    } else if(i == 2){
                        img_meal.setImageDrawable(getResources().getDrawable(R.drawable.meal));
                    } else if(i == 3){
                        img_examination.setImageDrawable(getResources().getDrawable(R.drawable.examination_attendance));
                    } else if(i == 4){
                        img_alerts.setImageDrawable(getResources().getDrawable(R.drawable.alert));
                    }*/

                    switch (i) {
                        case 0:
                            img_staff.setImageDrawable(getResources().getDrawable(R.drawable.staff_attendance_green));
                            txt_teacher_attendance_hindi.setTextColor(getResources().getColor(R.color.teacher_details_background));
                            break;

                        case 1:
                            img_student.setImageDrawable(getResources().getDrawable(R.drawable.student_green));
                            txt_students_attendance_hindi.setTextColor(getResources().getColor(R.color.teacher_details_background));
                            break;

                        case 2:
                            img_meal.setImageDrawable(getResources().getDrawable(R.drawable.meal_green));
                            txt_mid_day_meal_hindi.setTextColor(getResources().getColor(R.color.teacher_details_background));
                            break;

                        case 3:
                            img_examination.setImageDrawable(getResources().getDrawable(R.drawable.examination_green));
                            txt_examination_attendance_hindi.setTextColor(getResources().getColor(R.color.teacher_details_background));
                            break;

                        case 4:
                            img_alerts.setImageDrawable(getResources().getDrawable(R.drawable.alert_green));
                            txt_updates_hindi.setTextColor(getResources().getColor(R.color.teacher_details_background));
                            break;

                        case 5:
                            img_upload.setImageDrawable(getResources().getDrawable(R.drawable.event_green));
                            txt_upload_hindi.setTextColor(getResources().getColor(R.color.teacher_details_background));
                            break;

                        case 6:
                            img_leave.setImageDrawable(getResources().getDrawable(R.drawable.leave_green));
                            txt_leave_hindi.setTextColor(getResources().getColor(R.color.teacher_details_background));
                            break;

                        default:
                            break;
                    }


                } else {
                    TextView textView = (TextView) findViewById(text_ids[i]);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    /*if(i == 0){
                        img_staff.setImageDrawable(getResources().getDrawable(R.drawable.staff_attendance));
                    } else if(i == 1){
                        img_student.setImageDrawable(getResources().getDrawable(R.drawable.student));
                    } else if(i == 2){
                        img_meal.setImageDrawable(getResources().getDrawable(R.drawable.meal));
                    } else if(i == 3){
                        img_examination.setImageDrawable(getResources().getDrawable(R.drawable.examination_attendance));
                    } else if(i == 4){
                        img_alerts.setImageDrawable(getResources().getDrawable(R.drawable.alert));
                    }*/

                    switch (i) {
                        case 0:
                            img_staff.setImageDrawable(getResources().getDrawable(R.drawable.staff_attendance_white));
                            txt_teacher_attendance_hindi.setTextColor(getResources().getColor(R.color.white));
                            break;

                        case 1:
                            img_student.setImageDrawable(getResources().getDrawable(R.drawable.student_white));
                            txt_students_attendance_hindi.setTextColor(getResources().getColor(R.color.white));
                            break;

                        case 2:
                            img_meal.setImageDrawable(getResources().getDrawable(R.drawable.meal_white));
                            txt_mid_day_meal_hindi.setTextColor(getResources().getColor(R.color.white));
                            break;

                        case 3:
                            img_examination.setImageDrawable(getResources().getDrawable(R.drawable.examination_white));
                            txt_examination_attendance_hindi.setTextColor(getResources().getColor(R.color.white));
                            break;

                        case 4:
                            img_alerts.setImageDrawable(getResources().getDrawable(R.drawable.alert_white));
                            txt_updates_hindi.setTextColor(getResources().getColor(R.color.white));
                            break;

                        case 5:
                            img_upload.setImageDrawable(getResources().getDrawable(R.drawable.event_white));
                            txt_upload_hindi.setTextColor(getResources().getColor(R.color.white));
                            break;

                        case 6:
                            img_leave.setImageDrawable(getResources().getDrawable(R.drawable.leave_white));
                            txt_leave_hindi.setTextColor(getResources().getColor(R.color.white));
                            break;

                        default:
                            break;
                    }
                }
            }
        }
    }

    public void showInternetDialog(String title) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage("No Internet connection... Please check your internet connection");
        alertDialog.setIcon(R.mipmap.ic_launcher);

        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void showResponseDialog(String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.mipmap.ic_launcher);

        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public class AsyncChangePassword extends AsyncTask<String, Void, String> {
        String response = "";
        ProgressDialog progressDialog = null;
        String old_pass, new_pass, username, teacher_id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String title = getResources().getString(R.string.app_name);
            SpannableString ss1 = new SpannableString(title);
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss1.length(), 0);
            progressDialog = ProgressDialog.show(context, ss1, "Please wait changing password....", false, false);

            Drawable drawable = new ProgressBar(Activity_Dashboard.this).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(Activity_Dashboard.this, R.color.colorAccent),
                    PorterDuff.Mode.SRC_IN);
            progressDialog.setIndeterminateDrawable(drawable);
        }

        @Override
        protected String doInBackground(String... params) {
            old_pass = params[0];
            new_pass = params[1];
            username = MyApp.get_session(MyApp.SESSION_USERNAME);
            teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID);
            try {
                response = MyApp.get_change_pass(username, old_pass, new_pass, teacher_id);
                return response;
            } catch (Exception e) {
                return "-0";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MyApp.log(LOG_TAG, "Reset Password response is " + response);

            if (progressDialog != null)
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            if (!response.equalsIgnoreCase("-0")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String response_status = jsonObject.getString("response_status");
                    if (response_status.equalsIgnoreCase("success")) {

                        TABLE_LOGIN.updatePassword(teacher_id, new_pass);

                        String message = jsonObject.getString("response_message");
                        showResponseDialog("Reset Password", message);

                    } else if (response_status.equalsIgnoreCase("unsuccess")) {
                        String message = jsonObject.getString("response_message");
                        showResponseDialog("Reset Password", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in_return, R.anim.fade_out_return);
    }
}

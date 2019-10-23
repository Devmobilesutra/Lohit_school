package com.mobilesutra.SMS_Lohit.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.MyApp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Activity_developer extends AppCompatActivity {

    Context context = null;
    ImageButton btn_back = null;
    //Action bar
    TextView txt_school_name = null, txt_teacher_name = null, txt_current_date_time = null; //txt_teacher_designation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        context = this;

        initComponents();
        initComponentListenre();
        bindComponentData();
    }

    private void initComponents() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        txt_school_name = (TextView) findViewById(R.id.txt_school_name);
        txt_teacher_name = (TextView) findViewById(R.id.txt_teacher_name);
        txt_current_date_time = (TextView) findViewById(R.id.txt_current_date_time);
    }

    private void initComponentListenre() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void bindComponentData() {
        txt_school_name.setText(MyApp.get_session(MyApp.SESSION_SCHOOL_NAME));
        txt_teacher_name.setText(MyApp.get_session(MyApp.SESSION_TEACHER_NAME) + ", " + MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));
        //txt_teacher_designation.setText(MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        txt_current_date_time.setText(date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in_return, R.anim.fade_out_return);
    }
}

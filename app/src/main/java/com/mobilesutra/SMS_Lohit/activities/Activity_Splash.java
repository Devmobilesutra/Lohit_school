package com.mobilesutra.SMS_Lohit.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.MyApp;

public class Activity_Splash extends AppCompatActivity {

    private static String LOG_TAG = Activity_Splash.class.getSimpleName();
    Context context = null;
    boolean active = true;
    final int splashTime = 3000; // time to display the splash screen in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__splash);
        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (((MyApp) getApplicationContext()).getRegistrationId(context).equalsIgnoreCase("")) {
            MyApp.set_session(MyApp.SESSION_SERVER_MOBILE_NUMBER,"9223050607");
            ((MyApp) getApplicationContext()).getRegistrationGCMID();
        }

        initComponents();
        initComponentListener();
        bindComponentData();
    }

    private void initComponents() {

    }

    private void initComponentListener() {

    }

    private void bindComponentData() {

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (active && (waited < splashTime)) {
                        sleep(100);
                        if (active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {

                    Intent intent = new Intent(Activity_Splash.this, Activity_Login.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in_call, R.anim.fade_out_call);
                }
            }
        };
        splashTread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        active = false;

        return active;
    }

    public void onBackPressed() {
        active = false;
    }
}

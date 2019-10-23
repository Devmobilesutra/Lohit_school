package com.mobilesutra.SMS_Lohit.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.database.TABLE_NOTIFICATION;
import com.mobilesutra.SMS_Lohit.model.DTONotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Activity_Notification extends AppCompatActivity {

    Context context = null;
    static String LOG_TAG = Activity_Notification.class.getSimpleName();

    RecyclerView recycler_notification = null;
    ArrayList<DTONotification> DTONotificationArray = null;
    RecyclerAdapter mRecyclerAdapter = null;
    TextView txt_empty = null;

    //Action bar
    TextView txt_school_name = null, txt_teacher_name = null, txt_current_date_time = null; //txt_teacher_designation = null;
    ImageButton btn_back = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        context = this;

        initComponents();
        initComponentListener();
        bindComponentData();

        context.registerReceiver(mMessageReceiver, new IntentFilter(LOG_TAG));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        bindComponentData();
    }

    private void initComponents() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        txt_school_name = (TextView) findViewById(R.id.txt_school_name);
        txt_teacher_name = (TextView) findViewById(R.id.txt_teacher_name);
        txt_current_date_time = (TextView) findViewById(R.id.txt_current_date_time);

        txt_school_name.setText(MyApp.get_session(MyApp.SESSION_SCHOOL_NAME));
        txt_teacher_name.setText(MyApp.get_session(MyApp.SESSION_TEACHER_NAME) + ", " + MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        txt_current_date_time.setText(date);

        txt_empty = (TextView) findViewById(R.id.txt_empty);
        recycler_notification = (RecyclerView) findViewById(R.id.recycler_notification);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recycler_notification.setHasFixedSize(true);
        recycler_notification.setLayoutManager(llm);
    }

    private void initComponentListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void bindComponentData() {
        DTONotificationArray = TABLE_NOTIFICATION.getNotifications();

        if (DTONotificationArray != null && DTONotificationArray.size() > 0) {
            recycler_notification.setVisibility(View.VISIBLE);
            txt_empty.setVisibility(View.GONE);
            if (mRecyclerAdapter == null) {
                mRecyclerAdapter = new RecyclerAdapter(DTONotificationArray);
                recycler_notification.setAdapter(mRecyclerAdapter);
            } else {
                mRecyclerAdapter.rowItems.clear();
                mRecyclerAdapter.rowItems.addAll(DTONotificationArray);
                mRecyclerAdapter.notifyDataSetChanged();
            }
        } else {
            recycler_notification.setVisibility(View.GONE);
            txt_empty.setVisibility(View.VISIBLE);
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<DTONotification> rowItems = null;

        public RecyclerAdapter(ArrayList<DTONotification> dtoNotificationArray) {
            rowItems = dtoNotificationArray;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_notification, parent, false);
            vh = new MyViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DTONotification dtoNotification = rowItems.get(position);

            ((MyViewHolder) holder).txt_title.setText(dtoNotification.getNotification_title());
            ((MyViewHolder) holder).txt_notification_text.setText(dtoNotification.getNotification_text());
        }

        @Override
        public int getItemCount() {
            return rowItems == null ? 0 : rowItems.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txt_title = null, txt_notification_text = null;

            public MyViewHolder(View v) {
                super(v);
                txt_title = (TextView) v.findViewById(R.id.txt_title);
                txt_notification_text = (TextView) v.findViewById(R.id.txt_notification_text);
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in_return, R.anim.fade_out_return);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            //String service = intent.getStringExtra("service");
            String status = intent.getStringExtra("response_code");
            String message = intent.getStringExtra("response_message");
            MyApp.log(LOG_TAG, "In broadcast receiver of " + LOG_TAG);

            // Toast.makeText(context,"Status:"+status+"\nMessage:"+message,Toast.LENGTH_SHORT).show();
            if (status != null) {
                if (status.equals("0")) {
                    MyApp.log(LOG_TAG, "In if status = 0 of broadcast receiver of Council members activity");

                }
                if (status.equals("1") && message.equals("")) {
                    MyApp.log(LOG_TAG, "In if status = 1 and message = BannerMaster of broadcast receiver of Council members activity");

                    bindComponentData();

                } else {
                    MyApp.log(LOG_TAG, "In else status != 1 of broadcast receiver of Council members activity");

                }
            }
        }
    };

}

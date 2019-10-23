package com.mobilesutra.SMS_Lohit.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.database.TABLE_TEACHER_MASTER;
import com.mobilesutra.SMS_Lohit.model.DTOTeacherAttendance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Activity_Staff_Attendance extends AppCompatActivity {

    private static String LOG_TAG = Activity_Staff_Attendance.class.getSimpleName();
    Context context = null;

    ImageButton btn_back = null;
    Button btn_submit = null;

    //Action bar
    TextView txt_school_name = null, txt_teacher_name = null, txt_current_date_time = null; //txt_teacher_designation = null;

    ListView list_teacher = null;
    ArrayList<DTOTeacherAttendance> arrayList = null;
    ArrayList<DTOTeacherAttendance> arrayTemp = new ArrayList<>();
    CustomAdapter customAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_attendance);
        context = this;

        initComponents();
        initComponentListener();
        bindComponentData();
    }

    private void initComponents() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        //Change drawable background color
        GradientDrawable bgShape = (GradientDrawable) btn_submit.getBackground();
        bgShape.setColor(getResources().getColor(R.color.teacher_attendance_background));

        txt_school_name = (TextView) findViewById(R.id.txt_school_name_dashboard);
        txt_teacher_name = (TextView) findViewById(R.id.txt_teacher_name);
        txt_current_date_time = (TextView) findViewById(R.id.txt_current_date_time);

        list_teacher = (ListView) findViewById(R.id.list_teacher);

        txt_school_name.setText(MyApp.get_session(MyApp.SESSION_SCHOOL_NAME));
        txt_teacher_name.setText(MyApp.get_session(MyApp.SESSION_TEACHER_NAME) + ", " + MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));
        //txt_teacher_designation.setText(MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        txt_current_date_time.setText(date);
    }

    private void initComponentListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.log(LOG_TAG,"Temp Array size is " + arrayTemp.size());
            }
        });
    }

    private void bindComponentData() {
        arrayList = TABLE_TEACHER_MASTER.getTeacherList(MyApp.get_session(MyApp.SESSION_SCHOOL_ID));


        if (arrayList != null && arrayList.size() > 0) {

            customAdapter = new CustomAdapter(Activity_Staff_Attendance.this, arrayList);
            list_teacher.setAdapter(customAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!MyApp.isTimeAutomatic(context)) {
            showDateTimeDialog(context, "Please Enable Automatic time and date to get exact date and time.");
        }
    }

    public void showDateTimeDialog(final Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.app_name)).setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                        //dialog1.setCanceledOnTouchOutside(true);
                        dialog.dismiss();
                        dialog.cancel();
                        context.startActivity(intent);
                    }
                }).show();
    }

    public class CustomAdapter  extends BaseAdapter implements View.OnClickListener{

        Context context;
        ArrayList<DTOTeacherAttendance> rowItems;

        private LayoutInflater inflater = null;

        public CustomAdapter(Context context, ArrayList rowItems){
            this.context = context;
            this.rowItems = rowItems;
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return rowItems == null ? 0 : rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rowItems.indexOf(getItem(position));
        }

        /*********
         * Create a holder Class to contain inflated xml file elements
         *********/
        public class ViewHolder {
            TextView txt_teacher_name = null, txt_school_id = null, txt_teacher_id = null;
            CheckBox chk_teacher = null;
            boolean flag;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            final ViewHolder holder;

            convertView = null;
            if (convertView == null) {
                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.list_row_teacher, null);
                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();

                holder.txt_teacher_name = (TextView) vi.findViewById(R.id.txt_teacher_name);
                holder.txt_school_id = (TextView) vi.findViewById(R.id.txt_school_id);
                holder.txt_teacher_id = (TextView) vi.findViewById(R.id.txt_teacher_id);
                holder.chk_teacher = (CheckBox) vi.findViewById(R.id.chk_teacher);

                /************ Set holder with LayoutInflater ************/
                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();

            final DTOTeacherAttendance row_pos = rowItems.get(position);

            holder.txt_teacher_name.setText(row_pos.getTeacher_name());
            holder.txt_teacher_id.setText(row_pos.getTeacher_id());
            holder.flag = row_pos.is_checked();

            if (holder.flag == true) {
                holder.chk_teacher.setChecked(true);
            } else {
                holder.chk_teacher.setChecked(false);
            }

            holder.chk_teacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO Auto-generated method stub

                    if (holder.chk_teacher.isChecked()) {
                        row_pos.setIs_checked(true);
                        arrayTemp.add(row_pos);

                    } else {
                        row_pos.setIs_checked(false);
                        for (int i = 0; i < arrayTemp.size(); i++) {
                            DTOTeacherAttendance dtoTeacherAttendance = arrayTemp.get(i);
                            if (dtoTeacherAttendance.getTeacher_id().equalsIgnoreCase(row_pos.getTeacher_id())) {
                                arrayTemp.remove(i);
                                break;
                            }
                        }

                    }
                }
            });

            return vi;
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in_return, R.anim.fade_out_return);
    }

}

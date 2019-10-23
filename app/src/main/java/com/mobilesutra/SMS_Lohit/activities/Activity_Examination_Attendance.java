package com.mobilesutra.SMS_Lohit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.GPSTracker1;
import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.config.School_Service;
import com.mobilesutra.SMS_Lohit.database.TABLE_EXAMINATION_ATTENDANCE;
import com.mobilesutra.SMS_Lohit.database.TABLE_SCHOOL_STANDARDS;
import com.mobilesutra.SMS_Lohit.database.TABLE_STUDENT_ATTENDANCE;
import com.mobilesutra.SMS_Lohit.model.DTOStudentAttendance;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Activity_Examination_Attendance extends AppCompatActivity {

    private static String LOG_TAG = Activity_Examination_Attendance.class.getSimpleName();
    Context context = null;
    ImageButton btn_back = null;
    TextView txt_toolbar_title = null;
    RecyclerView recycler_view_standards = null;
    Button btn_submit_student_attendance = null;
    ArrayList<DTOStudentAttendance> arrayListStudents = null;
    RecyclerAdapter recyclerAdapter = null;

    //Image Parameters
    Intent intent;
    File fileImage = null;
    String str_path = "";
    int TAKE_IMAGE = 100, PICK_IMAGE = 200;
    public static String new_str_path = "";
    public int permission_count = 4;
    DTOStudentAttendance dtoStudentAttendance = null;

    //Action bar
    TextView txt_school_name = null, txt_teacher_name = null, txt_current_date_time = null; //txt_teacher_designation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination_attendance);
        context = this;

        initComponents();
        initComponentListener();
        bindComponentData();
    }

    private void initComponents() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        txt_toolbar_title = (TextView) findViewById(R.id.txt_toolbar_title);
        txt_toolbar_title.setText("Student Attendance");

        recycler_view_standards = (RecyclerView) findViewById(R.id.recycler_view_standards);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recycler_view_standards.setLayoutManager(llm);
        recycler_view_standards.setHasFixedSize(true);

        btn_submit_student_attendance = (Button) findViewById(R.id.btn_submit_student_attendance);

        //Change drawable background color
        GradientDrawable bgShape = (GradientDrawable) btn_submit_student_attendance.getBackground();
        bgShape.setColor(getResources().getColor(R.color.examination_attendance_background));

        txt_school_name = (TextView) findViewById(R.id.txt_school_name);
        txt_teacher_name = (TextView) findViewById(R.id.txt_teacher_name);
        txt_current_date_time = (TextView) findViewById(R.id.txt_current_date_time);
        /*txt_teacher_designation = (TextView) findViewById(R.id.txt_teacher_designation);*/
    }

    private void initComponentListener() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_submit_student_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerAdapter.rowItems != null && recyclerAdapter.rowItems.size() > 0) {
                    boolean flag = false;
                    for (int i = 0; i < recyclerAdapter.rowItems.size(); i++) {
                        DTOStudentAttendance dtoStudentAttendance = recyclerAdapter.rowItems.get(i);

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

                        /*if (term.equalsIgnoreCase("")) {
                            flag = true;
                            getShowErrorMessage(context, "Please enter Term/Test name for " + dtoStudentAttendance.getStandard());
                            break;
                        } else if (subject.equalsIgnoreCase("")) {
                            flag = true;
                            getShowErrorMessage(context, "Please enter Subject for " + dtoStudentAttendance.getStandard());
                            break;
                        } else if (image_url.equalsIgnoreCase("")) {
                            flag = true;
                            getShowErrorMessage(context, "Please take image for " + dtoStudentAttendance.getStandard());
                            break;
                        } else if (latitude.equalsIgnoreCase("0.0") || latitude == null || longitude.equalsIgnoreCase("0.0") || longitude == null) {
                            flag = true;
                            getShowErrorMessage(context, "Gan not get your GPS coordinates. Please retake photo for " + dtoStudentAttendance.getStandard());
                            break;
                        } else {
                            flag = false;*/

                        if (!TextUtils.isEmpty(image_url)) {

                            TABLE_EXAMINATION_ATTENDANCE.save_student_examination_attendance(teacher_id, school_id, standard_id, boys_present_count, girls_present_count,
                                    total_present_count, image_url, current_date, current_time, latitude, longitude, term, subject);

                            if (((MyApp) getApplicationContext()).isInternetAvailable()) {
                                Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
                                intent1.putExtra("Flag", "Sync_examination_attendance");
                                startService(intent1);
                            }
                        }

                        showSuccessResponse(context, "Examination attendance saved successfully");
                        //}
                    }

                    /*if (!flag) {
                        if (((MyApp) getApplicationContext()).isInternetAvailable()) {
                            Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
                            intent1.putExtra("Flag", "Sync_examination_attendance");
                            startService(intent1);
                        }

                        showSuccessResponse(context, "Examination attendance saved successfully");
                    }*/
                }
            }
        });
    }

    private void getShowErrorMessage(Context context, String msg) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.app_name)).setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void showSuccessResponse(Context context, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.app_name)).setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).show();

    }

    private void bindComponentData() {

        txt_school_name.setText(MyApp.get_session(MyApp.SESSION_SCHOOL_NAME));
        txt_teacher_name.setText(MyApp.get_session(MyApp.SESSION_TEACHER_NAME) + ", " + MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));
        //txt_teacher_designation.setText(MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        txt_current_date_time.setText(date);

        arrayListStudents = TABLE_SCHOOL_STANDARDS.getAllStandardStudents(true);

        if (arrayListStudents != null && arrayListStudents.size() > 0) {
            recyclerAdapter = new RecyclerAdapter(arrayListStudents);
            recycler_view_standards.setAdapter(recyclerAdapter);
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        List<DTOStudentAttendance> rowItems;

        public RecyclerAdapter(List<DTOStudentAttendance> rowItems) {
            MyApp.log(LOG_TAG, "In RecyclerAdapter Constructor");
            this.rowItems = rowItems;
            MyApp.log(LOG_TAG, "In RecyclerAdapter rowItems = " + rowItems);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
            final RecyclerView.ViewHolder vh;
            MyApp.log(LOG_TAG, "In RecyclerAdapter onCreateViewHolder");

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_exam, viewGroup, false);
            vh = new MyViewHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            MyApp.log(LOG_TAG, "In RecyclerAdapter onBindViewHolder");
            if (holder instanceof MyViewHolder) {
                MyApp.log(LOG_TAG, "In RecyclerAdapter instanceof MyViewHolder");
                final DTOStudentAttendance item = rowItems.get(position);
                ((MyViewHolder) holder).edt_term_name.setTag(position);
                ((MyViewHolder) holder).edt_subject_name.setTag(position);
                ((MyViewHolder) holder).txt_standard.setText(item.getStandard());
                ((MyViewHolder) holder).txt_standard_id.setText(item.getStandard_id());
                ((MyViewHolder) holder).txt_school_id.setText(item.getSchool_id());
                ((MyViewHolder) holder).txt_teacher_id.setText(item.getTeacher_id());
                ((MyViewHolder) holder).txt_total_count.setText("Total : " + item.getTotal_present_count());

                if (!item.getTerm().equalsIgnoreCase(""))
                    ((MyViewHolder) holder).edt_term_name.setText(item.getTerm());
                else
                    ((MyViewHolder) holder).edt_term_name.setText("");
                if (!item.getSubject().equalsIgnoreCase(""))
                    ((MyViewHolder) holder).edt_subject_name.setText(item.getSubject());
                else
                    ((MyViewHolder) holder).edt_subject_name.setText("");

                MyApp.log(LOG_TAG, "Image url for " + item.getStandard() + " is " + item.getImage_url());
                MyApp.log(LOG_TAG, "Boys present count is " + item.getBoys_present_count());
                MyApp.log(LOG_TAG, "Girls present count is " + item.getGirls_present_count());
                MyApp.log(LOG_TAG,"Term for " + item.getStandard() + " is " + item.getTerm());
                MyApp.log(LOG_TAG,"Subject for " + item.getStandard() + " is " + item.getSubject());

                int boys_count;
                try {
                    boys_count = Integer.parseInt(item.getBoys_count());
                } catch (NumberFormatException e) {
                    MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                    boys_count = 0;
                }

                ArrayList<String> arrayBoys = null;
                if (boys_count != 0) {
                    arrayBoys = new ArrayList<>();
                    for (int i = 0; i <= boys_count; i++) {
                        arrayBoys.add(i, i + "");
                    }
                } else {
                    arrayBoys = new ArrayList<>();
                    arrayBoys.add(0, "0");
                }

                if (arrayBoys != null && arrayBoys.size() > 0) {
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Activity_Examination_Attendance.this,
                            android.R.layout.simple_spinner_item, arrayBoys);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((MyViewHolder) holder).spn_boys_count.setAdapter(dataAdapter);
                    ((MyViewHolder) holder).spn_boys_count.setSelection(Integer.parseInt(item.getBoys_present_count()));
                }

                int girls_count;
                try {
                    girls_count = Integer.parseInt(item.getGirls_count());
                } catch (NumberFormatException e) {
                    MyApp.log(LOG_TAG, "Exception is " + e.getMessage());
                    girls_count = 0;
                }

                ArrayList<String> arrayGirls = null;
                if (girls_count != 0) {
                    arrayGirls = new ArrayList<>();
                    for (int i = 0; i <= girls_count; i++) {
                        arrayGirls.add(i, i + "");
                    }
                } else {
                    arrayGirls = new ArrayList<>();
                    arrayGirls.add(0, "0");
                }

                if (arrayGirls != null && arrayGirls.size() > 0) {
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Activity_Examination_Attendance.this,
                            android.R.layout.simple_spinner_item, arrayGirls);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((MyViewHolder) holder).spn_girls_count.setAdapter(dataAdapter);
                    ((MyViewHolder) holder).spn_girls_count.setSelection(Integer.parseInt(item.getGirls_present_count()));
                }

                ((MyViewHolder) holder).spn_boys_count.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int boys_present_count = Integer.parseInt(((MyViewHolder) holder).spn_boys_count.getSelectedItem().toString());
                        int girls_present_count = Integer.parseInt(((MyViewHolder) holder).spn_girls_count.getSelectedItem().toString());
                        int total_present_count = boys_present_count + girls_present_count;
                        item.setBoys_present_count(boys_present_count + "");
                        item.setGirls_present_count(girls_present_count + "");
                        item.setTotal_present_count(total_present_count + "");
                        ((MyViewHolder) holder).txt_total_count.setText("Total : " + item.getTotal_present_count());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ((MyViewHolder) holder).spn_girls_count.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int boys_present_count = Integer.parseInt(((MyViewHolder) holder).spn_boys_count.getSelectedItem().toString());
                        int girls_present_count = Integer.parseInt(((MyViewHolder) holder).spn_girls_count.getSelectedItem().toString());
                        int total_present_count = boys_present_count + girls_present_count;
                        item.setBoys_present_count(boys_present_count + "");
                        item.setGirls_present_count(girls_present_count + "");
                        item.setTotal_present_count(total_present_count + "");
                        ((MyViewHolder) holder).txt_total_count.setText("Total : " + item.getTotal_present_count());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (item.getImage_url().toString().length() > 0) {
                    Picasso.with(context)
                            .load(new File(item.getImage_url()))
                            .placeholder(R.drawable.camera_gray)
                            .error(R.drawable.camera_gray)
                           /* .resize((int) getResources().getDimension(R.dimen.img_height), (int) getResources().getDimension(R.dimen.img_height))*/
                            .into(((MyViewHolder) holder).img_camera, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    MyApp.log(LOG_TAG, "Image loaded successfully");
                                }

                                @Override
                                public void onError() {
                                    MyApp.log(LOG_TAG, "Image can't be load");
                                }
                            });

                    /*Picasso.Builder builder = new Picasso.Builder(Activity_Examination_Attendance.this);
                    builder.listener(new Picasso.Listener()
                    {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                        {
                            exception.printStackTrace();
                            MyApp.log(LOG_TAG,"Exception is " + exception.getMessage());
                        }
                    });
                    builder.build()
                            .load(new File(item.getImage_url()))
                            .placeholder(R.drawable.camera_gray)
                            .error(R.drawable.camera_gray)
                            .resize((int) getResources().getDimension(R.dimen.img_height), (int) getResources().getDimension(R.dimen.img_height))
                            .into(((MyViewHolder) holder).img_camera);*/
                } else {
                    Picasso.with(context)
                            .load(R.drawable.camera_gray)
                            .into(((MyViewHolder) holder).img_camera);
                }

                ((MyViewHolder) holder).img_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            MyApp.log(LOG_TAG, "In try");
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "SMS_Lohit");
                            if (!imageStorageDir.exists()) {
                                imageStorageDir.mkdirs();
                            }
                            str_path = imageStorageDir + File.separator + "IMG-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                            fileImage = new File(str_path);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));
                            dtoStudentAttendance = item;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (getPermissionCount() > 0)
                                    check_app_permission();
                                else {
                                    GPSTracker1 gps = new GPSTracker1(context);
                                    if (!gps.canGetLocation()) {
                                        gps.showSettingsAlert(context);
                                    } else {
                                        gps.getLocation();
                                        startActivityForResult(intent, TAKE_IMAGE);
                                        //Toast.makeText(context,"Locations On Activity_Splash:\n"+gps.getLatitude()+"\n"+gps.getLongitude(),Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                GPSTracker1 gps = new GPSTracker1(context);
                                if (!gps.canGetLocation()) {
                                    gps.showSettingsAlert(context);
                                } else {
                                    gps.getLocation();
                                    startActivityForResult(intent, TAKE_IMAGE);
                                    //Toast.makeText(context,"Locations On Activity_Splash:\n"+gps.getLatitude()+"\n"+gps.getLongitude(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            MyApp.log(LOG_TAG, "In catch exception is " + e.getMessage());
                            Snackbar.make(((MyViewHolder) holder).img_camera, "Unable to get Camera, Please try again later!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

                ((MyViewHolder) holder).edt_term_name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int pos = (int)((MyViewHolder) holder).edt_term_name.getTag();
                        rowItems.get(pos).setTerm(s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                ((MyViewHolder) holder).edt_subject_name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int pos = (int)((MyViewHolder) holder).edt_subject_name.getTag();
                        rowItems.get(pos).setSubject(s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            MyApp.log(LOG_TAG, "In RecyclerAdapter getItemCount");
            return rowItems == null ? 0 : rowItems.size();
        }

        public void removeLastItem() {
            if (rowItems.get(rowItems.size() - 1) == null) {
                rowItems.remove(rowItems.size() - 1);
                notifyItemRemoved(rowItems.size());
            }
        }

        public void removeAllItems() {
            rowItems.clear();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txt_standard = null, txt_total_count = null, txt_school_id = null, txt_teacher_id = null, txt_standard_id = null;
            public ImageView img_camera = null;
            Spinner spn_boys_count = null, spn_girls_count = null;
            EditText edt_term_name = null, edt_subject_name = null;

            public MyViewHolder(View v) {
                super(v);
                MyApp.log(LOG_TAG, "In RecyclerAdapter MyViewHolder");
                txt_standard = (TextView) v.findViewById(R.id.txt_standard);
                txt_standard_id = (TextView) v.findViewById(R.id.txt_standard_id);
                txt_total_count = (TextView) v.findViewById(R.id.txt_total_count);
                txt_school_id = (TextView) v.findViewById(R.id.txt_school_id);
                txt_teacher_id = (TextView) v.findViewById(R.id.txt_teacher_id);
                img_camera = (ImageView) v.findViewById(R.id.img_camera);
                spn_boys_count = (Spinner) v.findViewById(R.id.spn_boys_count);
                spn_girls_count = (Spinner) v.findViewById(R.id.spn_girls_count);

                edt_term_name = (EditText) v.findViewById(R.id.edt_term_name);
                edt_subject_name = (EditText) v.findViewById(R.id.edt_subject_name);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyApp.log(LOG_TAG, "onActivityResult");
        MyApp.log(LOG_TAG, "RequestCode->" + requestCode);//profile_pic_1 = 501,profile_pic_2 = 502
        MyApp.log(LOG_TAG, "ResultCode->" + resultCode);

        if (data != null) {
            Bundle extras = data.getExtras();

            if (extras != null)
                MyApp.log_bundle(extras);
        }
        if (resultCode != 0) {

            if (requestCode == TAKE_IMAGE) {

                MyApp.log(LOG_TAG, "In request code = 100");
                File f = new File(Environment.getExternalStorageDirectory().toString());
                MyApp.log(LOG_TAG, "IsFileExists->" + fileImage.exists());
                if (fileImage.exists()) {
                    f = fileImage;

                    /*if (dtoStudentAttendance != null) {
                        MyApp.log(LOG_TAG, "Selected standard is " + dtoStudentAttendance.getStandard());

                        DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
                        final String current_date = df_date.format(Calendar.getInstance().getTime());
                        MyApp.log(LOG_TAG, "Current Date is " + current_date);

                        DateFormat df_time = new SimpleDateFormat("HH:mm:ss");
                        final String current_time = df_time.format(Calendar.getInstance().getTime());
                        MyApp.log(LOG_TAG, "Current Time is " + current_time);

                        final String latitude = MyApp.get_session(MyApp.SESSION_USER_LATITUDE);
                        final String longitude = MyApp.get_session(MyApp.SESSION_USER_LONGITUDE);

                        for (int i = 0; i < arrayListStudents.size(); i++) {
                            DTOStudentAttendance dtoStudent = arrayListStudents.get(i);
                            if (dtoStudent.getStandard_id().equalsIgnoreCase(dtoStudentAttendance.getStandard_id())) {
                                arrayListStudents.get(i).setImage_url(f.getAbsolutePath());
                                arrayListStudents.get(i).setCurrent_date(current_date);
                                arrayListStudents.get(i).setCurrent_time(current_time);
                                arrayListStudents.get(i).setLatitude(latitude);
                                arrayListStudents.get(i).setLongitude(longitude);
                                arrayListStudents.get(i).setBoys_present_count(dtoStudentAttendance.getBoys_present_count());
                                arrayListStudents.get(i).setGirls_present_count(dtoStudentAttendance.getGirls_present_count());
                                arrayListStudents.get(i).setTotal_present_count(dtoStudentAttendance.getTotal_present_count());
                                arrayListStudents.get(i).setTerm(dtoStudentAttendance.getTerm());
                                arrayListStudents.get(i).setSubject(dtoStudentAttendance.getSubject());

                                MyApp.log(LOG_TAG, "Image url is " + f.getAbsolutePath());
                                recyclerAdapter.rowItems.get(i).setImage_url(f.getAbsolutePath());
                                recyclerAdapter.rowItems.get(i).setCurrent_date(current_date);
                                recyclerAdapter.rowItems.get(i).setCurrent_time(current_time);
                                recyclerAdapter.rowItems.get(i).setLatitude(latitude);
                                recyclerAdapter.rowItems.get(i).setLongitude(longitude);
                                recyclerAdapter.rowItems.get(i).setBoys_present_count(dtoStudentAttendance.getBoys_present_count());
                                recyclerAdapter.rowItems.get(i).setGirls_present_count(dtoStudentAttendance.getGirls_present_count());
                                recyclerAdapter.rowItems.get(i).setTotal_present_count(dtoStudentAttendance.getTotal_present_count());
                                recyclerAdapter.rowItems.get(i).setTerm(dtoStudentAttendance.getTerm());
                                recyclerAdapter.rowItems.get(i).setSubject(dtoStudentAttendance.getSubject());
                                //recyclerAdapter.notifyItemChanged(i);
                                recyclerAdapter.notifyDataSetChanged();

                                MyApp.set_session(MyApp.SESSION_USER_LATITUDE, "0.0");
                                MyApp.set_session(MyApp.SESSION_USER_LONGITUDE, "0.0");

                                break;
                            }
                        }
                    }*/

                    //recyclerAdapter.removeAllItems();


                    /*Picasso.with(context)
                            .load(fileImage)
                            .placeholder(R.drawable.camera)
                            .error(R.drawable.camera)
                            .into(img_camera);*/

                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = compressImage(str_path);
                    //Bitmap bmp = BitmapFactory.decodeFile(str_path);
                    //img_photo_1.setImageBitmap(bitmap);
                    MyApp.log(LOG_TAG, "Image url is " + f.getAbsolutePath() + ", " + f.getAbsoluteFile());



                   /* String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = fileImage;//new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public Bitmap compressImage(String filePath) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        MyApp.log(LOG_TAG, "BactualWidth->" + actualWidth);
        MyApp.log(LOG_TAG, "BactualHeight->" + actualHeight);
//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 768.0f;
        float maxWidth = 1024.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        MyApp.log(LOG_TAG, "AactualWidth->" + actualWidth);
        MyApp.log(LOG_TAG, "AactualHeight->" + actualHeight);
//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            MyApp.log(LOG_TAG, "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                MyApp.log(LOG_TAG, "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                MyApp.log(LOG_TAG, "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                MyApp.log(LOG_TAG, "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        new_str_path = getFilename();
        MyApp.log(LOG_TAG," Compressed image url is " + new_str_path);
        try {
            out = new FileOutputStream(new_str_path);

            if (dtoStudentAttendance != null) {
                MyApp.log(LOG_TAG, "Selected standard is " + dtoStudentAttendance.getStandard());

                DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
                final String current_date = df_date.format(Calendar.getInstance().getTime());
                MyApp.log(LOG_TAG, "Current Date is " + current_date);

                DateFormat df_time = new SimpleDateFormat("HH:mm:ss");
                final String current_time = df_time.format(Calendar.getInstance().getTime());
                MyApp.log(LOG_TAG, "Current Time is " + current_time);

                final String latitude = MyApp.get_session(MyApp.SESSION_USER_LATITUDE);
                final String longitude = MyApp.get_session(MyApp.SESSION_USER_LONGITUDE);

                for (int i = 0; i < arrayListStudents.size(); i++) {
                    DTOStudentAttendance dtoStudent = arrayListStudents.get(i);
                    if (dtoStudent.getStandard_id().equalsIgnoreCase(dtoStudentAttendance.getStandard_id())) {
                        arrayListStudents.get(i).setImage_url(new_str_path);
                        arrayListStudents.get(i).setCurrent_date(current_date);
                        arrayListStudents.get(i).setCurrent_time(current_time);
                        arrayListStudents.get(i).setLatitude(latitude);
                        arrayListStudents.get(i).setLongitude(longitude);
                        arrayListStudents.get(i).setBoys_present_count(dtoStudentAttendance.getBoys_present_count());
                        arrayListStudents.get(i).setGirls_present_count(dtoStudentAttendance.getGirls_present_count());
                        arrayListStudents.get(i).setTotal_present_count(dtoStudentAttendance.getTotal_present_count());
                        //arrayListStudents.get(i).setTerm(dtoStudentAttendance.getTerm());
                        //arrayListStudents.get(i).setSubject(dtoStudentAttendance.getSubject());

                        MyApp.log(LOG_TAG, "Image url is " + new_str_path);
                        recyclerAdapter.rowItems.get(i).setImage_url(new_str_path);
                        recyclerAdapter.rowItems.get(i).setCurrent_date(current_date);
                        recyclerAdapter.rowItems.get(i).setCurrent_time(current_time);
                        recyclerAdapter.rowItems.get(i).setLatitude(latitude);
                        recyclerAdapter.rowItems.get(i).setLongitude(longitude);
                        recyclerAdapter.rowItems.get(i).setBoys_present_count(dtoStudentAttendance.getBoys_present_count());
                        recyclerAdapter.rowItems.get(i).setGirls_present_count(dtoStudentAttendance.getGirls_present_count());
                        recyclerAdapter.rowItems.get(i).setTotal_present_count(dtoStudentAttendance.getTotal_present_count());
                        //recyclerAdapter.rowItems.get(i).setTerm(dtoStudentAttendance.getTerm());
                        //recyclerAdapter.rowItems.get(i).setSubject(dtoStudentAttendance.getSubject());
                        //recyclerAdapter.notifyItemChanged(i);
                        recyclerAdapter.notifyDataSetChanged();

                        MyApp.set_session(MyApp.SESSION_USER_LATITUDE, "0.0");
                        MyApp.set_session(MyApp.SESSION_USER_LONGITUDE, "0.0");

                        break;
                    }
                }
            }

            if (fileImage.exists()) {
                fileImage.delete();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return scaledBitmap;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String getFilename() {
        File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "SMS_Lohit");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        String new_str_path = imageStorageDir + File.separator + "IMG-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        return new_str_path;
    }

    private void check_app_permission() {
        permission_count = 4;
        int permission_granted = PackageManager.PERMISSION_GRANTED;
        MyApp.log(LOG_TAG, "PersmissionGrantedCode->" + permission_granted);

        int storage_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        MyApp.log(LOG_TAG, "StoragePermission->" + storage_permission);
        if (storage_permission == permission_granted)
            permission_count -= 1;

        int camera_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        MyApp.log(LOG_TAG, "CameraPermission->" + camera_permission);
        if (camera_permission == permission_granted)
            permission_count -= 1;

        int location_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        MyApp.log(LOG_TAG, "location_permission->" + location_permission);
        if (location_permission == permission_granted)
            permission_count -= 1;

        int sms_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        MyApp.log(LOG_TAG, "sms_permission->" + sms_permission);
        if (sms_permission == permission_granted)
            permission_count -= 1;

       /* int location2_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        MyApp.log(LOG_TAG, "location_permission->" + location_permission);
        if(location2_permission == permission_granted)
            permission_count -= 1;*/


        MyApp.log(LOG_TAG, "check_app_permission PermissionCount->" + permission_count);

        if (permission_count > 0) {
            String permissionArray[] = new String[permission_count];

            for (int i = 0; i < permission_count; i++) {
                MyApp.log(LOG_TAG, "i->" + i);

                if (storage_permission != permission_granted) {
                    if (!Arrays.asList(permissionArray).contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        permissionArray[i] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        MyApp.log(LOG_TAG, "i->WRITE_EXTERNAL_STORAGE");
                        // break;
                    }
                }

                if (camera_permission != permission_granted) {
                    if (!Arrays.asList(permissionArray).contains(Manifest.permission.CAMERA)) {
                        permissionArray[i] = Manifest.permission.CAMERA;
                        MyApp.log(LOG_TAG, "i->CAMERA");
                        //break;
                    }
                }
                if (location_permission != permission_granted) {
                    if (!Arrays.asList(permissionArray).contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        permissionArray[i] = Manifest.permission.ACCESS_FINE_LOCATION;
                        MyApp.log(LOG_TAG, "i->ACCESS_FINE_LOCATION");
                        //break;
                    }
                }

                if (sms_permission != permission_granted) {
                    if (!Arrays.asList(permissionArray).contains(Manifest.permission.SEND_SMS)) {
                        permissionArray[i] = Manifest.permission.SEND_SMS;
                        MyApp.log(LOG_TAG, "i->SEND_SMS");
                        //break;
                    }
                }

                /*if (location2_permission != permission_granted) {
                    if(!Arrays.asList(permissionArray).contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        permissionArray[i] = Manifest.permission.ACCESS_COARSE_LOCATION;
                        MyApp.log(LOG_TAG, "i->ACCESS_COARSE_LOCATION");
                        //break;
                    }
                }*/


            }
            MyApp.log(LOG_TAG, "PermissionArray->" + Arrays.deepToString(permissionArray));

            ActivityCompat.requestPermissions(Activity_Examination_Attendance.this, permissionArray, permission_count);//requestPermissions(permissionArray, permission_count);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission_count = permissions.length;
        MyApp.log(LOG_TAG, "In onRequestPermissionsResult");
        MyApp.log(LOG_TAG, "requestCode->" + requestCode);
        MyApp.log(LOG_TAG, "permissions->" + Arrays.deepToString(permissions));
        int len = grantResults.length;
        MyApp.log(LOG_TAG, "permissionsLength->" + len);

        int permission_granted = PackageManager.PERMISSION_GRANTED;
        MyApp.log(LOG_TAG, "PermissionGrantedCode->" + permission_granted);
        String str = "";
        for (int i = 0; i < len; i++) {

            if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                int location_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                MyApp.log(LOG_TAG, "AccessCore->" + location_permission);
                if (location_permission == permission_granted) {
                    permission_count -= 1;
                } else {
                    str += "Location, ";
                }
            }


            if (permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA)) {
                int camera_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                MyApp.log(LOG_TAG, "AccessCore->" + camera_permission);
                if (camera_permission == permission_granted) {
                    permission_count -= 1;
                } else {
                    str += "Camera, ";
                }
            }

            if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                int storage_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                MyApp.log(LOG_TAG, "AccessCore->" + storage_permission);
                if (storage_permission == permission_granted) {
                    permission_count -= 1;
                } else {
                    str += "Storage, ";
                }
            }

            if (permissions[i].equalsIgnoreCase(Manifest.permission.SEND_SMS)) {
                int sms_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                MyApp.log(LOG_TAG, "AccessCore->" + sms_permission);
                if ( sms_permission== permission_granted) {
                    permission_count -= 1;
                } else {
                    str += "sms, ";
                }
            }

            MyApp.log(LOG_TAG, "onRequestPermissionsResult PermissionCount->" + permission_count);
        }

        if (permission_count > 0) {
            Snackbar.make(btn_submit_student_attendance, getResources().getString(R.string.app_name) + " needs permissions : " + str,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    String SCHEME = "package";
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts(SCHEME, getApplication().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }).show();
        } else {
            GPSTracker1 gps = new GPSTracker1(context);
            if (!gps.canGetLocation()) {
                gps.showSettingsAlert(context);
            } else {
                gps.getLocation();
                startActivityForResult(intent, TAKE_IMAGE);
            }
        }
    }

    public int getPermissionCount() {
        int count = 4;
        int permission_granted = PackageManager.PERMISSION_GRANTED;
        int camera_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (camera_permission == permission_granted)
            count -= 1;
        int storage_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (storage_permission == permission_granted)
            count -= 1;
        int access_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (access_permission == permission_granted)
            count -= 1;
        int sms_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        if (sms_permission == permission_granted)
            count -= 1;
        return count;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in_return, R.anim.fade_out_return);
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
}

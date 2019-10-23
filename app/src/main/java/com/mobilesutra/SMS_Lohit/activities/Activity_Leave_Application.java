package com.mobilesutra.SMS_Lohit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.config.School_Service;
import com.mobilesutra.SMS_Lohit.database.TABLE_EVENTS;
import com.mobilesutra.SMS_Lohit.database.TABLE_LEAVE_APPLICATION;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Activity_Leave_Application extends AppCompatActivity {
    Context context = null;
    static String LOG_TAG = Activity_Leave_Application.class.getSimpleName();
    //Action bar
    TextView txt_school_name = null, txt_teacher_name = null, txt_current_date_time = null; //txt_teacher_designation = null;
    ImageButton btn_back = null;

    ImageView img_leave_application = null;
    EditText edt_leave_title = null, edt_leave_description = null;
    Button btn_submit_event = null;
    TextView txt_leave_from_date = null, txt_leave_to_date = null;

    //Image Parameters
    Intent intent;
    File fileImage = null;
    String str_path = "";
    int TAKE_IMAGE = 100, PICK_IMAGE = 200;
    public static String new_str_path = "";
    public int permission_count = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);context = this;

        initComponents();
        initComponentListener();
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

        img_leave_application = (ImageView) findViewById(R.id.img_leave_application);
        edt_leave_title = (EditText) findViewById(R.id.edt_leave_title);
        edt_leave_description = (EditText) findViewById(R.id.edt_leave_description);
        btn_submit_event = (Button) findViewById(R.id.btn_submit_event);
        txt_leave_from_date = (TextView) findViewById(R.id.txt_leave_from_date);
        txt_leave_to_date = (TextView) findViewById(R.id.txt_leave_to_date);

        //Change drawable background color
        GradientDrawable bgShape = (GradientDrawable) btn_submit_event.getBackground();
        bgShape.setColor(getResources().getColor(R.color.leave_application_background));
    }

    private void initComponentListener() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txt_leave_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                mCurrentDate.add(Calendar.DATE, 0); // to set default date to current date
                int mYear = mCurrentDate.get(Calendar.YEAR);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(context, R.style.AppThemeDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker,
                                                  int selectedyear, int selectedmonth,
                                                  int selectedday) {
                                // TODO Auto-generated method stub
                                // Your code to get date and time
                                String d = null;
                                selectedmonth = selectedmonth + 1;
                                if (selectedmonth < 10) {
                                    d = "" + selectedyear + "-"
                                            + "0" + selectedmonth + "-" + selectedday;
                                }
                                if (selectedday < 10) {
                                    d = "" + selectedyear + "-"
                                            + selectedmonth + "-" + "0" + selectedday;
                                }
                                if (selectedday < 10 && selectedmonth < 10) {
                                    d = "" + selectedyear + "-"
                                            + "0" + selectedmonth + "-" + "0" + selectedday;
                                }
                                if (selectedday > 9 && selectedmonth > 9) {
                                    d = "" + selectedyear + "-"
                                            + selectedmonth + "-" + selectedday;
                                }
                                Log.i("date===", d);

                                SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

                                Date from_date = null;
                                Log.i(LOG_TAG, " Date is " + d);
                                try {
                                    from_date = targetFormat.parse(d);
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                String from_d = targetFormat.format(from_date);
                                Log.i("from_d===", from_d);

                                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                                Date currentLocalTime = cal.getTime();
                                DateFormat date = new SimpleDateFormat("HH:mm a"); // you can get seconds by adding  "...:ss" to it

                                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                                String localTime = date.format(currentLocalTime);

                                txt_leave_from_date.setText("Leave From Date : " + from_d);

                            }
                        }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Leave From Date");
                mDatePicker.show();
            }
        });

        txt_leave_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                mCurrentDate.add(Calendar.DATE, 0); // to set default date to current date
                int mYear = mCurrentDate.get(Calendar.YEAR);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(context, R.style.AppThemeDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker,
                                                  int selectedyear, int selectedmonth,
                                                  int selectedday) {
                                // TODO Auto-generated method stub
                                // Your code to get date and time
                                String d = null;
                                selectedmonth = selectedmonth + 1;
                                if (selectedmonth < 10) {
                                    d = "" + selectedyear + "-"
                                            + "0" + selectedmonth + "-" + selectedday;
                                }
                                if (selectedday < 10) {
                                    d = "" + selectedyear + "-"
                                            + selectedmonth + "-" + "0" + selectedday;
                                }
                                if (selectedday < 10 && selectedmonth < 10) {
                                    d = "" + selectedyear + "-"
                                            + "0" + selectedmonth + "-" + "0" + selectedday;
                                }
                                if (selectedday > 9 && selectedmonth > 9) {
                                    d = "" + selectedyear + "-"
                                            + selectedmonth + "-" + selectedday;
                                }
                                Log.i("date===", d);

                                SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

                                Date from_date = null;
                                Log.i(LOG_TAG, " Date is " + d);
                                try {
                                    from_date = targetFormat.parse(d);
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                String from_d = targetFormat.format(from_date);
                                Log.i("from_d===", from_d);

                                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                                Date currentLocalTime = cal.getTime();
                                DateFormat date = new SimpleDateFormat("HH:mm a"); // you can get seconds by adding  "...:ss" to it

                                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                                String localTime = date.format(currentLocalTime);

                                txt_leave_to_date.setText("Leave To Date : " + from_d);

                            }
                        }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Leave To Date");
                mDatePicker.show();
            }
        });

        img_leave_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyApp.log(LOG_TAG, "In try");
                    /*intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "SMS_Lohit");
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }
                    str_path = imageStorageDir + File.separator + "IMG-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    fileImage = new File(str_path);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));*/


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getPermissionCount() > 0)
                            check_app_permission();
                        else {
                            //startActivityForResult(intent, TAKE_IMAGE);
                            selectImage();
                        }
                    } else {
                        //startActivityForResult(intent, TAKE_IMAGE);
                        selectImage();
                    }
                } catch (Exception e) {
                    MyApp.log(LOG_TAG, "In catch");
                    Snackbar.make(img_leave_application, "Unable to get Camera, Please try again later!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btn_submit_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String leave_from_date = "", leave_to_date = "";
                String str_event_date = txt_leave_from_date.getText().toString().trim();
                String str_leave_to_date = txt_leave_to_date.getText().toString().trim();
                String date_array[] = str_event_date.split(":");

                if (date_array != null && date_array.length > 1) {
                    leave_from_date = date_array[1];
                    MyApp.log(LOG_TAG, "Leave From date is " + leave_from_date);
                }

                date_array = str_leave_to_date.split(":");

                if (date_array != null && date_array.length > 1) {
                    leave_to_date = date_array[1];
                    MyApp.log(LOG_TAG, "leave to date is " + leave_to_date);
                }

                String event_title = edt_leave_title.getText().toString().trim();
                //String event_description = edt_leave_description.getText().toString().trim();

                if (leave_from_date.equalsIgnoreCase("")) {
                    getShowErrorMessage(context, "Please select leave from date");
                } else if (leave_to_date.equalsIgnoreCase("")) {
                    getShowErrorMessage(context, "Please select leave to date");
                } else if (event_title.equalsIgnoreCase("")) {
                    getShowErrorMessage(context, "Please enter leave reason/title");
                } else if (new_str_path.equalsIgnoreCase("")) {
                    getShowErrorMessage(context, "Please take leave application picture");
                } else {
                    String teacher_id = MyApp.get_session(MyApp.SESSION_TEACHER_ID);
                    String school_id = MyApp.get_session(MyApp.SESSION_SCHOOL_ID);

                    TABLE_LEAVE_APPLICATION.insertLeaveApplication(teacher_id, school_id, leave_from_date, leave_to_date, event_title, new_str_path);

                    if (((MyApp) getApplicationContext()).isInternetAvailable()) {
                        Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
                        intent1.putExtra("Flag", "Sync_leave_application");
                        startService(intent1);
                    }

                    showSuccessResponse(context,"Leave application saved successfully");
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

    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Leave_Application.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "SMS_Lohit");
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }
                    str_path = imageStorageDir + File.separator + "IMG-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    fileImage = new File(str_path);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));

                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));*/
                    startActivityForResult(intent, TAKE_IMAGE);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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

                    /*Picasso.with(context)
                            .load(fileImage)
                            .placeholder(R.drawable.camera)
                            .error(R.drawable.camera)
                            .into(img_leave_application);*/

                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = compressImage(str_path);
                    //Bitmap bmp = BitmapFactory.decodeFile(str_path);
                    //img_photo_1.setImageBitmap(bitmap);
                    Picasso.with(context)
                            .load(new File(new_str_path))
                            .placeholder(R.drawable.camera)
                            .error(R.drawable.camera)
                            .into(img_leave_application);
                    MyApp.log(LOG_TAG, "Image url is " + f.getAbsolutePath() + ", compressed img url is " + new_str_path);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_IMAGE) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                //Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                MyApp.log(LOG_TAG, "path of image from gallery......******************........." + picturePath + "");

                compressImage(picturePath);
                Bitmap thumbnail = (BitmapFactory.decodeFile(new_str_path));
                MyApp.log(LOG_TAG, "Compressed image url is " + new_str_path);
                img_leave_application.setImageBitmap(thumbnail);
            }
        }
    }

    public static Bitmap compressImage(String filePath) {

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
        try {
            out = new FileOutputStream(new_str_path);
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
        permission_count = 3;
        int permission_granted = PackageManager.PERMISSION_GRANTED;
        MyApp.log(LOG_TAG, "PermissionGrantedCode->" + permission_granted);

        int storage_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        MyApp.log(LOG_TAG, "StoragePermission->" + storage_permission);
        if (storage_permission == permission_granted)
            permission_count -= 1;

        int camera_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        MyApp.log(LOG_TAG, "CameraPermission->" + camera_permission);
        if (camera_permission == permission_granted)
            permission_count -= 1;

        int sms_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        MyApp.log(LOG_TAG, "sms_permission->" + sms_permission);
        if (sms_permission == permission_granted)
            permission_count -= 1;

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

                if (sms_permission != permission_granted) {
                    if (!Arrays.asList(permissionArray).contains(Manifest.permission.SEND_SMS)) {
                        permissionArray[i] = Manifest.permission.SEND_SMS;
                        MyApp.log(LOG_TAG, "i->SEND_SMS");
                        //break;
                    }
                }
            }
            MyApp.log(LOG_TAG, "PermissionArray->" + Arrays.deepToString(permissionArray));

            ActivityCompat.requestPermissions(Activity_Leave_Application.this, permissionArray, permission_count);//requestPermissions(permissionArray, permission_count);
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

            if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                int storage_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                MyApp.log(LOG_TAG, "AccessCore->" + storage_permission);
                if (storage_permission == permission_granted) {
                    permission_count -= 1;
                } else {
                    str += "Storage, ";
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
            Snackbar.make(img_leave_application, getResources().getString(R.string.app_name) + " needs permissions : " + str,
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
            //startActivityForResult(intent, TAKE_IMAGE);
            selectImage();
        }
    }

    public int getPermissionCount() {
        int count = 3;
        int permission_granted = PackageManager.PERMISSION_GRANTED;
        int camera_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (camera_permission == permission_granted)
            count -= 1;
        int storage_permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (storage_permission == permission_granted)
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
}

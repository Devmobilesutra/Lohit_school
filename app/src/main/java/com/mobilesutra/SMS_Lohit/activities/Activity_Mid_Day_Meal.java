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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobilesutra.SMS_Lohit.R;
import com.mobilesutra.SMS_Lohit.config.GPSTracker1;
import com.mobilesutra.SMS_Lohit.config.MyApp;
import com.mobilesutra.SMS_Lohit.config.School_Service;
import com.mobilesutra.SMS_Lohit.database.TABLE_DAILY_FOOD;
import com.mobilesutra.SMS_Lohit.database.TABLE_FOOD_MASTER;
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

public class Activity_Mid_Day_Meal extends AppCompatActivity {

    private static String LOG_TAG = Activity_Student_Attendance.class.getSimpleName();
    Context context = null;
    ImageButton btn_back = null;
    Button btn_submit_mid_day_meal = null;

    Spinner spn_meal = null;
    EditText edt_boys_count = null, edt_girls_count = null, edt_other_text = null;
    ImageView img_student = null, img_food = null;

    //Action bar
    TextView txt_school_name = null, txt_teacher_name = null, txt_current_date_time = null; //txt_teacher_designation = null;

    //Image Parameters
    Intent intent;
    File fileImage = null;
    String str_path = "";
    int TAKE_IMAGE = 100, PICK_IMAGE = 200;
    public static String new_str_path = "";
    public int permission_count = 4;
    String selected_img = "", student_img_url = "", food_img_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_day_meal);
        context = this;

        initComponents();
        initComponentListener();
        setSpinnerData();
        bindComponentData();
    }

    private void initComponents() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        txt_school_name = (TextView) findViewById(R.id.txt_school_name);
        txt_teacher_name = (TextView) findViewById(R.id.txt_teacher_name);
        txt_current_date_time = (TextView) findViewById(R.id.txt_current_date_time);

        btn_submit_mid_day_meal = (Button) findViewById(R.id.btn_submit_mid_day_meal);
        //Change drawable background color
        GradientDrawable bgShape = (GradientDrawable) btn_submit_mid_day_meal.getBackground();
        bgShape.setColor(getResources().getColor(R.color.mid_day_meal_background));

        spn_meal = (Spinner) findViewById(R.id.spn_meal);

        edt_boys_count = (EditText) findViewById(R.id.edt_boys_count);
        edt_girls_count = (EditText) findViewById(R.id.edt_girls_count);
        edt_other_text = (EditText) findViewById(R.id.edt_other_text);

        img_student = (ImageView) findViewById(R.id.img_student);
        img_food = (ImageView) findViewById(R.id.img_food);
    }

    private void initComponentListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyApp.log(LOG_TAG, "In try");
                    selected_img = "students";
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "SMS_Lohit");
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }
                    str_path = imageStorageDir + File.separator + "IMG-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    fileImage = new File(str_path);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));


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
                    MyApp.log(LOG_TAG, "In catch");
                    Snackbar.make(img_student, "Unable to get Camera, Please try again later!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        img_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyApp.log(LOG_TAG, "In try");
                    selected_img = "food";
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "SMS_Lohit");
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }
                    str_path = imageStorageDir + File.separator + "IMG-" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    fileImage = new File(str_path);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));


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
                    MyApp.log(LOG_TAG, "In catch");
                    Snackbar.make(img_food, "Unable to get Camera, Please try again later!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btn_submit_mid_day_meal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String boys_count = edt_boys_count.getText().toString().trim();
                String girls_count = edt_girls_count.getText().toString().trim();
                String other_text = edt_other_text.getText().toString().trim();
                String food_selected = spn_meal.getSelectedItem().toString();
                String food_id = TABLE_FOOD_MASTER.getFoodId(food_selected);
                String latitude = MyApp.get_session(MyApp.SESSION_USER_LATITUDE);
                String longitude = MyApp.get_session(MyApp.SESSION_USER_LONGITUDE);
                String current_date = MyApp.getCurrentDate();
                String current_time = MyApp.getCurrentTime();

                if (latitude != null && longitude != null) {
                    if (!MyApp.isTimeAutomatic(context)) {
                        Snackbar.make(img_food, "Please Enable Automatic time and date to get exact date and time.", Snackbar.LENGTH_SHORT).show();
                    } else if (student_img_url.equalsIgnoreCase("")) {
                        Snackbar.make(img_food, "Please Take students image", Snackbar.LENGTH_SHORT).show();
                    } else if (food_img_url.equalsIgnoreCase("")) {
                        Snackbar.make(img_food, "Please Take food items image", Snackbar.LENGTH_SHORT).show();
                    } else if (latitude.equalsIgnoreCase("0.0") || longitude.equalsIgnoreCase("0.0")) {
                        Snackbar.make(img_food, "Can't get your GPS Location. Please Check your GPS Connection and try again.", Snackbar.LENGTH_SHORT).show();
                    } else if (boys_count.equalsIgnoreCase("")) {
                        Snackbar.make(img_food, "Please enter boys count", Snackbar.LENGTH_SHORT).show();
                    } else if (girls_count.equalsIgnoreCase("")) {
                        Snackbar.make(img_food, "Please enter girls count", Snackbar.LENGTH_SHORT).show();
                    } else {
                        TABLE_DAILY_FOOD.insertDailyReport(food_id, other_text, boys_count, girls_count, latitude, longitude, current_date, current_time, student_img_url, food_img_url);

                        if (((MyApp) getApplicationContext()).isInternetAvailable()) {
                            Intent intent1 = new Intent(Intent.ACTION_SYNC, null, context, School_Service.class);
                            intent1.putExtra("Flag", "Sync_daily_food_attendance");
                            startService(intent1);
                        }

                        showSuccessResponse(context, "Mid Day Food details saved successfully");
                    }
                } else {
                    Snackbar.make(img_food, "Can't get your GPS Location. Please Check your GPS Connection and try again.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setSpinnerData() {
        ArrayList<String> arrayBoys = TABLE_FOOD_MASTER.getMealList();

        if (arrayBoys != null && arrayBoys.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Activity_Mid_Day_Meal.this,
                    android.R.layout.simple_spinner_item, arrayBoys);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_meal.setAdapter(dataAdapter);
        }
    }

    private void bindComponentData() {
        txt_school_name.setText(MyApp.get_session(MyApp.SESSION_SCHOOL_NAME));
        txt_teacher_name.setText(MyApp.get_session(MyApp.SESSION_TEACHER_NAME) + ", " + MyApp.get_session(MyApp.SESSION_TEACHER_DESIGNATION));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        txt_current_date_time.setText(date);
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
                    if (!selected_img.equalsIgnoreCase("") && selected_img.equalsIgnoreCase("students")) {
                        student_img_url = f.getAbsolutePath().toString();
                        Picasso.with(context)
                                .load(fileImage)
                                .placeholder(R.drawable.camera_gray)
                                .error(R.drawable.camera_gray)
                                .into(img_student);
                    } else if (!selected_img.equalsIgnoreCase("") && selected_img.equalsIgnoreCase("food")) {
                        food_img_url = f.getAbsolutePath().toString();
                        Picasso.with(context)
                                .load(fileImage)
                                .placeholder(R.drawable.camera_gray)
                                .error(R.drawable.camera_gray)
                                .into(img_food);
                    }

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
        try {
            out = new FileOutputStream(new_str_path);
            MyApp.log(LOG_TAG, "in try and file path is " + new_str_path);

            if (!selected_img.equalsIgnoreCase("") && selected_img.equalsIgnoreCase("students")) {
                MyApp.log(LOG_TAG, "In if students");
                student_img_url = new_str_path;

                /*Picasso.with(context)
                        .load(new File(new_str_path))
                        .placeholder(R.drawable.camera_gray)
                        .error(R.drawable.camera_gray)
                        .into(img_student);*/

                /*Picasso.Builder builder = new Picasso.Builder(Activity_Mid_Day_Meal.this);
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                            MyApp.log(LOG_TAG, "Exception is " + exception.getMessage());
                        }
                    });
                    builder.build()
                            .load(new File(new_str_path))
                            .placeholder(R.drawable.camera_gray)
                            .error(R.drawable.camera_gray)
                            *//*.resize((int) getResources().getDimension(R.dimen.img_height), (int) getResources().getDimension(R.dimen.img_height))*//*
                            .into(img_student);*/

            } else if (!selected_img.equalsIgnoreCase("") && selected_img.equalsIgnoreCase("food")) {
                MyApp.log(LOG_TAG, "In if food");
                food_img_url = new_str_path;

               /* Picasso.with(context)
                        .load(new File(new_str_path))
                        .placeholder(R.drawable.camera_gray)
                        .error(R.drawable.camera_gray)
                        .into(img_food);*/
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

            ActivityCompat.requestPermissions(Activity_Mid_Day_Meal.this, permissionArray, permission_count);//requestPermissions(permissionArray, permission_count);
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
            Snackbar.make(img_food, getResources().getString(R.string.app_name) + " needs permissions : " + str,
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
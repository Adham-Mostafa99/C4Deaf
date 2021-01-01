package com.example.graduationproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    String[] gender = {"Select Gender", "male", "female"};
    private Spinner spinner_month, spinner_year, spinner_gender, spinner_state;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private EditText ed_birth_day, ed_birth_month, ed_birth_year;
    private int day, month, year;
    private TextView txt_birth;
    private CircleImageView upload_image, profile_image;
    private String userChoosenPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
//        /*spinner_month = findViewById(R.id.spinner_month);
//        spinner_year = findViewById(R.id.spinner_year);*/
//        spinner_gender = findViewById(R.id.spinner_select_gender);
//        spinner_state = findViewById(R.id.spinner_state);
//
//        ed_birth_day = findViewById(R.id.edit_day);

//        ed_birth_month = findViewById(R.id.edit_month);
//        ed_birth_year = findViewById(R.id.edit_year);
//        txt_birth = findViewById(R.id.birth_day);
//        profile_image = findViewById(R.id.profile_image);
//        upload_image = findViewById(R.id.upload_image);
//        upload_image.setOnClickListener(this);
//
//        if (ContextCompat.checkSelfPermission(SignUp.this,
//                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(SignUp.this,
//                    new String[]{
//                            Manifest.permission.CAMERA
//                    }, 100);
//
//        }
////        SetSpinnerMonth();
//        SetSpinnerGender();
//        SetSpinnerState();
//        SetBirthDay();
//
//
//    }
//
//    /*public void SetSpinnerMonth(){
//        ArrayAdapter<CharSequence> adapter_month=
//                ArrayAdapter.createFromResource(this,R.array.spinner_month,R.layout.style_spinner_text);
//        adapter_month.setDropDownViewResource(R.layout.style_spinner_text);
//        spinner_month.setAdapter(adapter_month);
//    }*/
//    public void SetSpinnerState() {
//        ArrayAdapter<CharSequence> adapter_state =
//                ArrayAdapter.createFromResource(this, R.array.spinner_state, R.layout.style_spinner_text);
//        adapter_state.setDropDownViewResource(R.layout.style_spinner_text);
//        spinner_state.setAdapter(adapter_state);
//
//    }
//
//    public void SetSpinnerGender() {
//        ArrayList<String> list_gender = new ArrayList<>(Arrays.asList(gender));
//        ArrayAdapter<String> adapter_gender =
//                new ArrayAdapter<>(this, R.layout.style_spinner_text, gender);
//        spinner_gender.setAdapter(adapter_gender);
//    }
//
//    public void SetBirthDay() {
//        txt_birth.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View v) {
//                Calendar cal = Calendar.getInstance();
//                year = cal.get(Calendar.YEAR);
//                month = cal.get(Calendar.MONTH);
//                day = cal.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this,
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        month = month + 1;
//                        ed_birth_day.setText(String.valueOf(dayOfMonth));
//                        ed_birth_month.setText(String.valueOf(month));
//                        ed_birth_year.setText(String.valueOf(year));
//                    }
//                }, year, month, day);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                datePickerDialog.show();
//            }
//        });
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.upload_image:
//                selectProfileImage();
//                break;
//        }
//    }
//
//    public void selectProfileImage() {
//        final CharSequence[] items = {"Take Photo", "Choose From Gallery", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
//        builder.setTitle("Add Photo!");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                if (items[i].equals("Take Photo")) {
//                    userChoosenPhoto = "Take Photo";
//                    //camera Intent
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, 100);
//
//                } else if (items[i].equals("Choose From Gallery")) {
//                    userChoosenPhoto = "Choose From Gallery";
//                    //gallery Intent
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                    startActivityForResult(intent, 100);
//
//
//                } else if (items[i].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (userChoosenPhoto.equals("Take Photo")) {
//            if (requestCode == 100 && resultCode == RESULT_OK) {
//                // get capture Image
//                Bitmap captureImage = (Bitmap) data.getExtras().get("data");
//                // set capture Image to ImageView
//                profile_image.setImageBitmap(captureImage);
//            }
//        } else if (userChoosenPhoto.equals("Choose From Gallery")) {
//            if (requestCode == 100 && resultCode == RESULT_OK) {
//                // get capture Image
//                Uri uri = data.getData();
//                // set capture Image to profileImage
//                profile_image.setImageURI(uri);
//            }
//        }
    }

    @Override
    public void onClick(View v) {

    }
}

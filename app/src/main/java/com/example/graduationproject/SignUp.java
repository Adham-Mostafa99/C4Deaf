package com.example.graduationproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.upload_image)
    ImageView uploadImage;
    @BindView(R.id.edit_first_name)
    EditText editFirstName;
    @BindView(R.id.edit_last_name)
    EditText editLastName;
    @BindView(R.id.edit_email)
    EditText editEmail;
    @BindView(R.id.edit_pass)
    EditText editPass;
    @BindView(R.id.code)
    CountryCodePicker code;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_day)
    EditText editDay;
    @BindView(R.id.edit_month)
    Spinner editMonth;
    @BindView(R.id.edit_year)
    Spinner editYear;
    @BindView(R.id.spinner_select_gender)
    Spinner spinnerSelectGender;
    @BindView(R.id.spinner_state)
    Spinner spinnerState;
    @BindView(R.id.check_box)
    CheckBox checkBox;
    @BindView(R.id.btn_sign_up)
    Button btnSignUp;

    //String[] gender = {"Select Gender", "male", "female"};
//    private DatePickerDialog.OnDateSetListener mDateSetListener;
//    private int day, month, year;
    private String userChosenPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        //get camera permission
        if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignUp.this,
                    new String[]{Manifest.permission.CAMERA},
                    100);

        }


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImage();
            }
        });
    }

    //        SetSpinnerMonth();
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

    public void selectProfileImage() {
        final String[] items = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (items[i]) {
                    case "Take Photo":
                        userChosenPhoto = "Take Photo";
                        //camera Intent
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 100);
                        break;
                    case "Choose From Gallery":
                        userChosenPhoto = "Choose From Gallery";
                        //gallery Intent
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 100);
                        break;
                    case "Cancel":
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (userChosenPhoto) {
            case "Take Photo":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                    // set capture Image to ImageView
                    profileImage.setImageBitmap(captureImage);
                }
                break;
            case "Choose From Gallery":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Uri uri = data.getData();
                    // set capture Image to profileImage
                    profileImage.setImageURI(uri);
                }
                break;
        }


    }
}


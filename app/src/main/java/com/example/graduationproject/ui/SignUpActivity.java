package com.example.graduationproject.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.graduationproject.DatePickerFragment;
import com.example.graduationproject.R;
import com.example.graduationproject.models.UserPrivateInfo;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements DatePickerFragment.OnFinish {

    @BindView(R.id.sign_up_profile_image)
    CircleImageView signUpProfileImage;
    @BindView(R.id.sign_up_upload_image)
    ImageView signUpUploadImage;
    @BindView(R.id.sign_up_edit_first_name)
    EditText signUpEditFirstName;
    @BindView(R.id.sign_up_edit_last_name)
    EditText signUpEditLastName;
    @BindView(R.id.sign_up_edit_email)
    EditText signUpEditEmail;
    @BindView(R.id.sign_up_edit_pass)
    EditText signUpEditPass;
    @BindView(R.id.sign_up_country_code)
    CountryCodePicker signUpCountryCode;
    @BindView(R.id.sign_up_edit_phone)
    EditText signUpEditPhone;
    @BindView(R.id.sign_up_spinner_select_gender)
    Spinner signUpSpinnerGender;
    @BindView(R.id.sign_up_spinner_state)
    Spinner signUpSpinnerState;
    @BindView(R.id.sign_up_check_accepted_rules)
    CheckBox signUpCheckAcceptedRules;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.sign_up_date_birth_day)
    Button signUpDateOfBirthDay;
    @BindView(R.id.error_no_date)
    TextView errorNoDate;
    @BindView(R.id.sign_up_have_account)
    TextView signUpHaveAccount;
    @BindView(R.id.user_date)
    TextView userDate;

    private static final String TAG = "SignUpActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE = 102;
    private static final int READ_FROM_STORAGE_PERMISSION_REQUEST_CODE = 103;
    public static final String USER_PRIVATE_INFO_INTENT_EXTRA = "userPrivateInfo";
    public static final String USER_PUBLIC_INFO_INTENT_EXTRA = "userPublicInfo";
    @BindView(R.id.sign_up_edit_display_name)
    EditText signUpEditDisplayName;

    private String[] gender, state;
    private String userChosenPhoto;
    private List<Integer> dateTime = new ArrayList<>();
    private String genderSelected, stateSelected;

    private String userPhotoPath;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        init();
        initializeFirebase();

        //upload photo form camera or gallery button
        signUpUploadImage.setOnClickListener(v -> {
            setCameraPermission();
            uploadProfileImage();
        });

        //define user birthDate
        signUpDateOfBirthDay.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        //choose user gender
        signUpSpinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //male=1
                //female=2
                genderSelected = gender[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //choose user state
        signUpSpinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //normal=1
                //deaf=2
                stateSelected = state[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //sign up to create new account
        signUpButton.setOnClickListener(v -> {
            String firstName = signUpEditFirstName.getText().toString().trim();
            String lastName = signUpEditLastName.getText().toString().trim();
            String displayName = signUpEditDisplayName.getText().toString().trim();
            String email = signUpEditEmail.getText().toString().trim();
            String pass = signUpEditPass.getText().toString().trim();
            String phone = signUpEditPhone.getText().toString().trim();
            String fullPhone = signUpCountryCode.getFullNumberWithPlus().trim();
            String date = dateTime.toString().trim();
            String currentGender = genderSelected;
            String currentState = stateSelected;


            //check validate of input user information
            if (isInputValid(userPhotoPath, firstName, lastName, displayName, email, pass, phone, currentGender, currentState)) {

                //creating account for user using his mail and password
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    currentUser = mAuth.getCurrentUser();

                                    UserPrivateInfo userPrivateInfo = new
                                            UserPrivateInfo(email, pass, fullPhone, date);
                                    UserPublicInfo userPublicInfo = new
                                            UserPublicInfo(currentUser.getUid()
                                            , firstName
                                            , lastName
                                            , displayName
                                            , currentState
                                            , currentGender
                                            , userPhotoPath);

                                    //confirming the uer email and phone number
                                    confirmEmailAndPhone(userPrivateInfo, userPublicInfo);

                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        //already have account
        //so sign in instead
        signUpHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });

    }

    public void init() {
        //requestFocus to firstName
        signUpEditFirstName.requestFocus();

        //declare StringArrays for Spinner
        gender = getResources().getStringArray(R.array.gender);
        state = getResources().getStringArray(R.array.state);

        //define default spinner
        genderSelected = gender[0];
        stateSelected = state[0];

        //declare spinners
        setSpinnerGender();
        setSpinnerState();

        //connect code to phone editView
        signUpCountryCode.registerPhoneNumberTextView(signUpEditPhone);

    }

    //initialize firebase objects
    public void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = null;


    }

    /**
     * check validate of input user information
     *
     * @param userPhotoPath  user photo passing in uri
     * @param firstName     user first name
     * @param lastName      user last name
     * @param displayName   user display name
     * @param email         user email
     * @param pass          user password
     * @param phone         user phone number without country code
     * @param currentGender user gender [male / female]
     * @param currentState  user state [normal / deaf]
     * @return true if data is valid and false otherwise
     */
    public boolean isInputValid(@NonNull String userPhotoPath, String firstName, String displayName, String lastName, String email, String pass, String phone, String currentGender, String currentState) {
        if (userPhotoPath == null) {
            Toast.makeText(this, "please choose your photo", Toast.LENGTH_SHORT).show();
        } else if (firstName.isEmpty()) {
            signUpEditFirstName.setError("Enter your first name");
            signUpEditFirstName.requestFocus();
        } else if (lastName.isEmpty()) {
            signUpEditLastName.setError("Enter your last name");
            signUpEditLastName.requestFocus();
        } else if (displayName.isEmpty()) {
            signUpEditDisplayName.setError("Enter your Display name");
            signUpEditDisplayName.requestFocus();
        } else if (email.isEmpty()) {
            signUpEditEmail.setError("Enter your email");
            signUpEditEmail.requestFocus();
        } else if (pass.isEmpty() || pass.length() < 6) {
            signUpEditPass.setError("Enter your password and larger 6 char");
            signUpEditPass.requestFocus();
        } else if (phone.isEmpty()) {
            signUpEditPhone.setError("Enter your phone");
            signUpEditPhone.requestFocus();
        } else if (dateTime.isEmpty()) {
            //show error message
            showErrorMsg();
        } else if (currentGender.equals(gender[0])) {
            Toast.makeText(getApplicationContext(), "Please select Gender", Toast.LENGTH_SHORT).show();
        } else if (currentState.equals(state[0])) {
            Toast.makeText(getApplicationContext(), "Please select State", Toast.LENGTH_SHORT).show();
        } else if (!signUpCheckAcceptedRules.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please Agree Rules", Toast.LENGTH_SHORT).show();
        } else {
            //hide error message of date if it showing
            removeErrorMsg();
            return true;
        }
        return false;
    }

    //confirm user email address
    public void confirmEmailAndPhone(UserPrivateInfo userPrivateInfo, UserPublicInfo userPublicInfo) {
        //send email verify
        currentUser.sendEmailVerification();
        //go to confirm activity
        Intent intent = new Intent(this, ConfirmEmailActivity.class);

        intent.putExtra(USER_PRIVATE_INFO_INTENT_EXTRA, userPrivateInfo);
        intent.putExtra(USER_PUBLIC_INFO_INTENT_EXTRA, userPublicInfo);
        startActivity(intent);

    }


    //set state array to spinner to show it
    public void setSpinnerState() {
        ArrayAdapter<String> adapter_state = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, state);
        //to specify the design of menu with items
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signUpSpinnerState.setAdapter(adapter_state);
    }

    //set gender array to spinner to show it
    public void setSpinnerGender() {
        ArrayAdapter<String> adapter_gender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        //to specify the design of menu with items
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signUpSpinnerGender.setAdapter(adapter_gender);
    }

    //upload image from camera or gallery
    public void uploadProfileImage() {
        final String[] items = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
    public void finish(List<Integer> dateTime) {
        //get BirthDate from DatePickerFragment
        this.dateTime = dateTime;

        //remove error msg and show the date
        removeErrorMsg();
    }


    public void removeErrorMsg() {
        errorNoDate.setVisibility(View.GONE);
        userDate.setVisibility(View.VISIBLE);
        userDate.setText(dateTime.toString());
    }

    public void showErrorMsg() {
        userDate.setVisibility(View.GONE);
        errorNoDate.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (userChosenPhoto) {
            case "Take Photo":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                    //set user photo uri
                    Uri captureImageUri = getImageUri(this, captureImage);
                    userPhotoPath = getRealPathFromURI(captureImageUri);
                    // set capture Image to ImageView
                    signUpProfileImage.setImageBitmap(captureImage);
                }
                break;
            case "Choose From Gallery":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Uri uri = data.getData();
                    //set user photo uri
                    userPhotoPath = getRealPathFromURI(uri);
                    // set capture Image to profileImage
                    signUpProfileImage.setImageURI(uri);
                }
                break;
        }
    }

    //get uri from bitmap image
    public Uri getImageUri(@NonNull Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "CapturedImage", null);
        return Uri.parse(path);
    }

    //get full path from uri
    public String getRealPathFromURI(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToCamera = false;
        boolean permissionToWrite = false;

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToCamera)
                    setReadStoragePermission();
                else
                    finish();
                break;
            case READ_FROM_STORAGE_PERMISSION_REQUEST_CODE:
                Toast.makeText(this, "Read permission granted", Toast.LENGTH_LONG).show();
                permissionToWrite = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToWrite)
                    setWriteStoragePermission();
                else
                    finish();
            case WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE:
                Toast.makeText(this, "Write permission granted", Toast.LENGTH_LONG).show();
                permissionToWrite = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!permissionToWrite)
                    finish();
        }
    }

    //get Permission from user to use the camera
    public void setCameraPermission() {
        //set camera permission
        ActivityCompat.requestPermissions(SignUpActivity.this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    //get Permission from user to use the write Image
    public void setReadStoragePermission() {
        //set write permission
        ActivityCompat.requestPermissions(SignUpActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_FROM_STORAGE_PERMISSION_REQUEST_CODE);
    }

    //get Permission from user to use the write Image
    public void setWriteStoragePermission() {
        //set write permission
        ActivityCompat.requestPermissions(SignUpActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE);
    }
}


package com.example.graduationproject.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.graduationproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

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

    private static final String TAG = "SignUpActivity";

    private String[] gender, state;
    private String userChosenPhoto;
    private static List<Integer> dateTime = new ArrayList<>();
    private String genderSelected, stateSelected;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        init();
        initializeFirebase();

        setPermissions();

        //upload photo form camera or gallery button
        signUpUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });

        //define user birthDate
        signUpDateOfBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                //hide error message
                errorNoDate.setVisibility(View.GONE);
            }
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
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = signUpEditFirstName.getText().toString().trim();
                String lastName = signUpEditLastName.getText().toString().trim();
                String email = signUpEditEmail.getText().toString().trim();
                String pass = signUpEditPass.getText().toString().trim();
                String phone = signUpEditPhone.getText().toString().trim();
                String fullPhone = signUpCountryCode.getFullNumberWithPlus().trim();
                String date = dateTime.toString().trim();
                String currentGender = genderSelected;
                String currentState = stateSelected;


                //check validate of input user information
                if (firstName.isEmpty()) {
                    signUpEditFirstName.setError("Enter your first name");
                    signUpEditFirstName.requestFocus();
                } else if (lastName.isEmpty()) {
                    signUpEditLastName.setError("Enter your last name");
                    signUpEditLastName.requestFocus();
                } else if (email.isEmpty()) {
                    signUpEditEmail.setError("Enter your email");
                    signUpEditEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    signUpEditPass.setError("Enter your password");
                    signUpEditPass.requestFocus();
                } else if (phone.isEmpty()) {
                    signUpEditPhone.setError("Enter your phone");
                    signUpEditPhone.requestFocus();
                } else if (dateTime.isEmpty()) {
                    //show error message
                    errorNoDate.setVisibility(View.VISIBLE);
                } else if (currentGender.equals(gender[0])) {
                    Toast.makeText(getApplicationContext(), "Please select Gender", Toast.LENGTH_SHORT).show();
                } else if (currentState.equals(state[0])) {
                    Toast.makeText(getApplicationContext(), "Please select State", Toast.LENGTH_SHORT).show();
                } else if (!signUpCheckAcceptedRules.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please Agree Rules", Toast.LENGTH_SHORT).show();
                } else {


                //add user
                Map<String, Object> user = new HashMap<>();
                user.put("first name", firstName);
                user.put("last name", lastName);
                user.put("email", email);
                user.put("password", pass);
                user.put("phone", fullPhone);
                user.put("birth date", date);
                user.put("gender", currentGender);
                user.put("state", currentState);

                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    currentUser = mAuth.getCurrentUser();
                                    db.collection("users")
                                            .add(user)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });

                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                //confirming the uer email and phone number
//                    confirmEmailAndPhone();
            }

            }
        });

        /**
         * already have account
         * so sign in instead
         */
        signUpHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        //hide error message
        errorNoDate.setVisibility(View.GONE);

        //connect code to phone editView
        signUpCountryCode.registerPhoneNumberTextView(signUpEditPhone);

    }

    public void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void confirmEmailAndPhone() {
        //find the constrain in sign_up layout
        // to be the parent o the popup Window
        ConstraintLayout constraintLayout = findViewById(R.id.sign_up_layout);

        //make the width and height for the pop Window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // lets taps outside the popup also dismiss it
        boolean focusable = false;

        //inflate new layout with specific layout(pop_layout) for the pop window
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popLayout = layoutInflater.inflate(R.layout.pop_layout, null);

        //create instance of popupWindow by specific view, width, and height
        PopupWindow popupWindow = new PopupWindow(popLayout, width, height, focusable);

        //show the created instance in specific location
        popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

        //declare the cancel button in popWindow
        Button cancelButton = popLayout.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button okButton = popLayout.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handel click
            }
        });

    }

    public void setPermissions() {
        //set camera permission
        if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        }
    }

    public void setSpinnerState() {
        ArrayAdapter<String> adapter_state = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, state);
        //to specify the design of menu with items
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signUpSpinnerState.setAdapter(adapter_state);
    }

    public void setSpinnerGender() {
        ArrayAdapter<String> adapter_gender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        //to specify the design of menu with items
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signUpSpinnerGender.setAdapter(adapter_gender);
    }

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (userChosenPhoto) {
            case "Take Photo":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                    // set capture Image to ImageView
                    signUpProfileImage.setImageBitmap(captureImage);
                }
                break;
            case "Choose From Gallery":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Uri uri = data.getData();
                    // set capture Image to profileImage
                    signUpProfileImage.setImageURI(uri);
                }
                break;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (dateTime.size() == 0) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, dateTime.get(2), dateTime.get(1), dateTime.get(0));
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            if (dateTime.size() == 0) {
                dateTime.add(0, day);
                dateTime.add(1, month);
                dateTime.add(2, year);
            } else {
                dateTime.set(0, day);
                dateTime.set(1, month);
                dateTime.set(2, year);
            }
        }

    }
}

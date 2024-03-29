package com.example.graduationproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.graduationproject.DatePickerFragment;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.SpinnerAdapter;
import com.example.graduationproject.models.CompleteInfo;
import com.example.graduationproject.models.SpinnerModel;
import com.example.graduationproject.models.UserPrivateInfo;
import com.example.graduationproject.models.UserPublicInfo;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements DatePickerFragment.OnFinish {

    public static final int FACEBOOK = 2;
    public static final int GOOGLE = 3;
    public static final String TYPE = "type";


    @BindView(R.id.sign_up_edit_first_name)
    EditText signUpEditFirstName;
    @BindView(R.id.sign_up_edit_last_name)
    EditText signUpEditLastName;
    @BindView(R.id.sign_up_edit_email)
    TextInputLayout signUpEditEmail;
    @BindView(R.id.sign_up_edit_pass)
    TextInputLayout signUpEditPass;

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

    @BindView(R.id.sign_up_have_account)
    TextView signUpHaveAccount;


    private static final String TAG = "SignUpActivity";
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE = 102;
    private static final int READ_FROM_STORAGE_PERMISSION_REQUEST_CODE = 103;
    public static final String USER_PRIVATE_INFO_INTENT_EXTRA = "userPrivateInfo";
    public static final String USER_PUBLIC_INFO_INTENT_EXTRA = "userPublicInfo";
    public static final String COMPLETE_USER_INFO_INTENT_EXTRA = "userInfo";
    @BindView(R.id.sign_up_edit_confirm_pass)
    TextInputLayout signUpEditConfirmPass;
    @BindView(R.id.date)
    TextView date;


    private List<Integer> dateTime = new ArrayList<>();
    private String genderSelected, stateSelected;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private int currentType;
    @BindView(R.id.btn_arrow_back)
    CircleImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        init();
        initializeFirebase();
        hideSystemUI();

        backButton.setOnClickListener(v -> onBackPressed());

        CompleteInfo completeInfo = getIntent().getParcelableExtra(COMPLETE_USER_INFO_INTENT_EXTRA);

        assert completeInfo != null;
        if (completeInfo.getUserEmail() != null) {
            setCompleteInfo(completeInfo);

            int t = getIntent().getIntExtra(TYPE, 0);
            if (t == GOOGLE)
                currentType = GOOGLE;
            else
                currentType = FACEBOOK;
        }

        //define user birthDate
        signUpDateOfBirthDay.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        //choose user gender
        signUpSpinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        genderSelected = "male";
                        break;
                    case 1:
                        genderSelected = "female";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //choose user state
        signUpSpinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        stateSelected = "normal";
                        break;
                    case 1:
                        stateSelected = "deaf";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //sign up to create new account
        signUpButton.setOnClickListener(v -> {
            String firstName = signUpEditFirstName.getText().toString().trim();
            String lastName = signUpEditLastName.getText().toString().trim();
            String email = signUpEditEmail.getEditText().getText().toString().trim();
            String pass = signUpEditPass.getEditText().getText().toString().trim();
            String confirmPass = signUpEditConfirmPass.getEditText().getText().toString().trim();
            String date = dateTime.toString().trim();
            String currentGender = genderSelected;
            String currentState = stateSelected;

            //check validate of input user information
            if (isInputValid(firstName, lastName, email, pass, confirmPass)) {

                //creating account for user using his mail and password
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                currentUser = mAuth.getCurrentUser();

                                UserPrivateInfo userPrivateInfo = new
                                        UserPrivateInfo(email, pass, null, date);
                                UserPublicInfo userPublicInfo = new
                                        UserPublicInfo(currentUser.getUid()
                                        , firstName
                                        , lastName
                                        , firstName
                                        , currentState
                                        , currentGender
                                        , null);

                                //confirming the uer email and phone number
                                confirmEmailAndPhone(userPrivateInfo, userPublicInfo);

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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


    public void setCompleteInfo(@NonNull CompleteInfo completeInfo) {
        String[] name = extractUserName(completeInfo.getUserName());

        signUpEditFirstName.setText(name[0]);
        signUpEditLastName.setText(name[1]);
        signUpEditEmail.getEditText().setText(completeInfo.getUserEmail());
        signUpEditEmail.getEditText().setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
        signUpEditEmail.setEnabled(false);
        signUpEditEmail.getEditText().setInputType(InputType.TYPE_NULL);


        GetToken getToken = new GetToken(this);
        getToken.execute();


    }

    public class GetToken extends AsyncTask<Void, Void, String> {

        private final Context context;

        public GetToken(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (currentType == GOOGLE) {
                try {
                    String scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE;
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    return GoogleAuthUtil.getToken(context, account.getAccount(), scope, new Bundle());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

            } else if (currentType == FACEBOOK) {
                return getFacebookTokenId();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            deleteAccount(s);
        }
    }

    public void deleteAccount(String token) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = null;
        if (currentType == FACEBOOK)
            credential = FacebookAuthProvider.getCredential(token);
        else if (currentType == GOOGLE)
            credential = GoogleAuthProvider.getCredential(token, null);
        if (credential != null) {
            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User account deleted.");
                                            }
                                        }
                                    });

                        }
                    });
        }
    }

    public String getFacebookTokenId() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return AccessToken.getCurrentAccessToken().getToken();
        }
        return null;
    }

    public String[] extractUserName(@NonNull String name) {
        return name.split(" ", 2);
    }

    public void init() {
        //requestFocus to firstName
        signUpEditFirstName.requestFocus();

        genderSelected = "male";
        genderSelected = "female";

        setSpinnerGender();
        setSpinnerState();

//        //connect code to phone editView
//        signUpCountryCode.registerPhoneNumberTextView(signUpEditPhone);

    }

    //initialize firebase objects
    public void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = null;
    }

    public boolean isInputValid(String firstName, String lastName, String email, String pass, String confirm) {
//        if (userPhotoPath == null) {
//            Toast.makeText(this, "please choose your photo", Toast.LENGTH_SHORT).show();
//        }
        if (firstName.isEmpty()) {
            signUpEditFirstName.setError("Enter your first name");
            signUpEditFirstName.requestFocus();
        } else if (lastName.isEmpty()) {
            signUpEditLastName.setError("Enter your last name");
            signUpEditLastName.requestFocus();
        } else if (email.isEmpty()) {
            signUpEditEmail.getEditText().setError("Enter your email");
            signUpEditEmail.getEditText().requestFocus();
        } else if (pass.isEmpty() || pass.length() < 6) {
            signUpEditPass.getEditText().setError("Enter your password and larger 6 char");
            signUpEditPass.getEditText().requestFocus();
        } else if (!confirm.equals(pass)) {
            signUpEditConfirmPass.getEditText().setError("DisMatched Password");
            signUpEditConfirmPass.getEditText().requestFocus();
        } else if (dateTime.isEmpty()) {
            //show error message
            showErrorMsg();
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
    public void confirmEmailAndPhone(UserPrivateInfo userPrivateInfo, UserPublicInfo
            userPublicInfo) {
        //send email verify
        currentUser.sendEmailVerification();
        //go to confirm activity
        Intent intent = new Intent(this, ConfirmEmailActivity.class);

        intent.putExtra(USER_PRIVATE_INFO_INTENT_EXTRA, userPrivateInfo);
        intent.putExtra(USER_PUBLIC_INFO_INTENT_EXTRA, userPublicInfo);
        startActivity(intent);

    }

//    @Override
//    protected void onStop() {
//        signOut();
//        super.onStop();
//    }

    @Override
    protected void onDestroy() {
        signOut();
        super.onDestroy();
    }

    public void signOut() {

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        GoogleSignIn.getClient(getApplicationContext(), gso).signOut();
////        mAuth.removeAuthStateListener(new FirebaseAuth.AuthStateListener() {
////            @Override
////            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
////
////            }
////        });

        //TODO delete google account from Auth
//        currentUser.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
//            @Override
//            public void onSuccess(GetTokenResult getTokenResult) {
//                String token = getTokenResult.getToken();
//                AuthCredential authCredential = GoogleAuthProvider.getCredential(token, null);
//                currentUser.reauthenticate(authCredential);
//            }
//        });

        LoginManager.getInstance().logOut();

    }

    //set state array to spinner to show it
    public void setSpinnerState() {
        ArrayList<SpinnerModel> list = new ArrayList<>();
        list.add(new SpinnerModel("Normal", R.drawable.sign_up_state_normal_icon));
        list.add(new SpinnerModel("Deaf", R.drawable.sign_up_state_deaf_icon));
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, list);
        signUpSpinnerState.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signUpSpinnerState.setSelection(0);
        signUpSpinnerState.setAdapter(spinnerAdapter);
    }

    //set gender array to spinner to show it
    public void setSpinnerGender() {
        ArrayList<SpinnerModel> list = new ArrayList<>();
        list.add(new SpinnerModel("Male", R.drawable.sign_up_gender_male_icon));
        list.add(new SpinnerModel("Female", R.drawable.sign_up_gender_female_icon));
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, list);
        signUpSpinnerGender.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signUpSpinnerGender.setSelection(0);
    }

    @Override
    public void finish(List<Integer> dateTime) {
        //get BirthDate from DatePickerFragment
        this.dateTime = dateTime;

        //remove error msg and show the date
        removeErrorMsg();
    }


    public void removeErrorMsg() {
        date.setTextColor(Color.WHITE);
        date.setText(dateTime.toString());
    }

    public void showErrorMsg() {
        date.setTextColor(Color.RED);
        date.setText("Empty Data");
    }

    @Override
    public void onBackPressed() {
        signOut();
        startActivity(new Intent(this, LogIn_or_SignUp.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

//    //get Permission from user to use the camera
//    public void setCameraPermission() {
//        //set camera permission
//        ActivityCompat.requestPermissions(SignUpActivity.this,
//                new String[]{Manifest.permission.CAMERA},
//                REQUEST_CAMERA_PERMISSION);
//    }

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

    private void hideSystemUI() {
        Window window = this.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30
            window.setDecorFitsSystemWindows(false);
        } else {

            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }


    }


}


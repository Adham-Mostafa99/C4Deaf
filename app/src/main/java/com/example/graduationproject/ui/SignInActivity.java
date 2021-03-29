package com.example.graduationproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.sign_up_profile_image)
    CircleImageView profileImage;
    @BindView(R.id.bottom_background)
    LinearLayout bottomBackground;
    @BindView(R.id.sign_in_button)
    Button signIn;
    @BindView(R.id.sign_in_email)
    EditText signInEmail;
    @BindView(R.id.sign_in_password)
    EditText signInPassword;
    @BindView(R.id.sign_in_forgot_pass)
    TextView signInForgotPass;
    @BindView(R.id.sign_in_by_google_button)
    Button signInByGoogleButton;
    @BindView(R.id.sign_in_by_fb_button)
    Button signInByFbButton;
    @BindView(R.id.sign_in_create_account)
    TextView signInCreateAccount;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        initializeFirebase();

        //sign in by email and password
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInEmail.getText().toString().trim();
                String pass = signInPassword.getText().toString().trim();

                //check validate of email and password
                if (isInputValid(email, pass)) {
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //get instance from current user
                                        currentUser = mAuth.getCurrentUser();
                                        signInToChat(currentUser);
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //forgetting password
        signInForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RestUserPasswordActivity.class));
            }
        });

        //sign in by google account
        signInByGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //sign in by facebook account
        signInByFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //not have an account and want to create one
        signInCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
    }

    //initialize Firebase objects
    public void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    /**
     * check validate of email and password
     *
     * @param email user email address to sign in
     * @param pass  user password to sign in
     * @return true if data is valid and false otherwise
     */
    public boolean isInputValid(@NonNull String email, String pass) {
        if (email.isEmpty()) {
            signInEmail.setError("Enter your Email");
            signInEmail.requestFocus();
        } else if (pass.isEmpty()) {
            signInPassword.setError("Enter your Password");
            signInPassword.requestFocus();
        } else {
            return true;
        }
        return false;
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        signInToChat(currentUser);
//    }
//
    public void signInToChat(FirebaseUser currentUser) {
        if (currentUser != null) {
            currentUser.reload();
            if (currentUser.isEmailVerified()) {
                Toast.makeText(getApplicationContext(), "Sign in success", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), ChatMenuActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            } else {
                Toast.makeText(getApplicationContext(), "please verify your email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
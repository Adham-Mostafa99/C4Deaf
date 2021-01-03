package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignIn extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);


        //sign in by email and password
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInEmail.getText().toString().trim();
                String pass = signInPassword.getText().toString().trim();

                //check validate of email and password
                if (email.isEmpty()) {
                    signInEmail.setError("Enter your Email");
                    signInEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    signInPassword.setError("Enter your Password");
                    signInPassword.requestFocus();
                } else {
                    startActivity(
                            new Intent(getApplicationContext(), SignUp.class));
                }

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
            }
        });
    }


}
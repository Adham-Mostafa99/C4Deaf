package com.example.graduationproject.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import com.example.graduationproject.R;
import com.example.graduationproject.models.CompleteInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LogIn_or_SignUp extends AppCompatActivity {

    Button btn_log, btn_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_or__sign_up);

        btn_log =(Button)findViewById(R.id.log_in);
        btn_sign =(Button)findViewById(R.id.button_sign_up);

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),SignInActivity.class));
            }
        });
       btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)
                        .putExtra(SignUpActivity.COMPLETE_USER_INFO_INTENT_EXTRA, new CompleteInfo()));
            }
        });

    }
}
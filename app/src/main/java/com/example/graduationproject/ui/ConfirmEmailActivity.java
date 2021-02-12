package com.example.graduationproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.models.UserPrivateInfo;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmEmailActivity extends AppCompatActivity {
    @BindView(R.id.change_button)
    Button changeButton;
    @BindView(R.id.resend_button)
    Button resendButton;
    @BindView(R.id.email_verify)
    TextView emailVerify;

    public static final String USER_PRIVATE_INFO_INTENT_EXTRA = "userPrivateInfo";
    public static final String USER_PUBLIC_INFO_INTENT_EXTRA = "userPublicInfo";
    private static final String TAG = "ConfirmEmail";

    private FirebaseUser currentUser;
    private boolean isChangeButtonClick = false;
    private Thread thread;

    private UserPublicInfo userPublicInfo;
    private UserPrivateInfo userPrivateInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confrim_email);
        ButterKnife.bind(this);

        initFirebase();
        init();


        //handling Resend Button
        //resend Verification
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), " resend done", Toast.LENGTH_SHORT).show();
                currentUser.sendEmailVerification();
            }
        });

        //handling Change Email Button
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete current user from FirebaseAuth
                currentUser.delete();

                //clicked ChangeEmailButton
                isChangeButtonClick = true;

                //back to sign up activity to change the email
                onBackPressed();
            }
        });

    }

    public void init() {
        userPublicInfo = getIntent().getParcelableExtra(SignUpActivity.USER_PUBLIC_INFO_INTENT_EXTRA);
        userPrivateInfo = getIntent().getParcelableExtra(SignUpActivity.USER_PRIVATE_INFO_INTENT_EXTRA);
    }

    public void initFirebase() {
        //create instance of user from FirebaseAuth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void refreshEmailVerify(int millSec) {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        //refresh every millSec
                        Thread.sleep(millSec);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkVerify();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    //end the thread from running
    public void endThread(Thread thread) {
        if (thread != null)
            thread.interrupt();
    }

    public void checkVerify() {
        if (currentUser != null) {
            //refresh user information
            currentUser.reload();

            if (currentUser.isEmailVerified()) {
                emailVerify.setText("Verify");
                Toast.makeText(getApplicationContext(), " verify", Toast.LENGTH_SHORT).show();

                //end the current thread from running
                endThread(thread);

                //go to chat menu activity
                finish();
                startActivity(new Intent(getApplicationContext(), WelcomeDeafChatActivity.class)
                        .putExtra(SignUpActivity.USER_PRIVATE_INFO_INTENT_EXTRA, userPrivateInfo)
                        .putExtra(SignUpActivity.USER_PUBLIC_INFO_INTENT_EXTRA, userPublicInfo));
            }
        }
    }

    @Override
    public void onBackPressed() {
        //handling BackPressed to enable only
        //if user want to change email
        if (isChangeButtonClick) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //ending the thread
        endThread(thread);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refresh activity every 1 sec
        //to check if email is verified
        refreshEmailVerify(1000);
    }


}
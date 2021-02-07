package com.example.graduationproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduationproject.ui.ChatMenuActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmEmail extends AppCompatActivity {
    @BindView(R.id.change_button)
    Button changeButton;
    @BindView(R.id.resend_button)
    Button resendButton;
    @BindView(R.id.email_verify)
    TextView emailVerify;

    private static final String TAG = "ConfirmEmail";

    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private boolean isChangeButtonClick = false;
    private Thread thread;
    Map<String, Object> user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confrim_email);
        ButterKnife.bind(this);


        //get user information
        user = (HashMap<String, Object>) getIntent().getSerializableExtra("user");

        //create instance of user from FirebaseAuth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
                                //create instance of user from FirebaseAuth
                                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (currentUser != null) {
                                    //refresh user information
                                    currentUser.reload();

                                    if (currentUser.isEmailVerified()) {
                                        emailVerify.setText("Verify");
                                        Toast.makeText(getApplicationContext(), " verify", Toast.LENGTH_SHORT).show();

                                        //store user in database
                                        insertUserToDatabase(user);

                                        //end the current thread from running
                                        endThread(thread);

                                        //go to chat menu activity
                                        startActivity(new Intent(getApplicationContext(), ChatMenuActivity.class));
                                    }
                                }
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

    /**
     * @param user map object which contain user information
     */
    public void insertUserToDatabase(Map<String, Object> user) {
        db = FirebaseFirestore.getInstance();
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
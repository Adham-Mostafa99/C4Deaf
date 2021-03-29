package com.example.graduationproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(() -> {
            signInToChat(currentUser);
        }, 2000);

    }


    public void signInToChat(FirebaseUser currentUser) {
        if (currentUser != null) {
            currentUser.reload();
            if (currentUser.isEmailVerified()) {
                Toast.makeText(getApplicationContext(), "Sign in success", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), ChatMenuActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        } else {
            Toast.makeText(getApplicationContext(), "please sign in", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

        }
    }
}
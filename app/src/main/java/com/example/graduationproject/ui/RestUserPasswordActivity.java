package com.example.graduationproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.ui.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestUserPasswordActivity extends AppCompatActivity {

    @BindView(R.id.email_for_rest_pass)
    com.google.android.material.textfield.TextInputLayout emailForRestPass;
    @BindView(R.id.rest_pass)
    Button restPass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_user_password);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        restPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailForRestPass.getEditText().getText().toString().trim();
                if (email != null) {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext()
                                                , task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });
    }
}
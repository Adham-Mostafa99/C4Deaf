package com.example.graduationproject;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class GraduationProject extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //offline realTime firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //offline fireStore firebase
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }
}

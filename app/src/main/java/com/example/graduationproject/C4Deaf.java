package com.example.graduationproject;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class C4Deaf extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(this);
        EmojiManager.install(new IosEmojiProvider());

        //offline realTime firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //offline fireStore firebase
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

    }
}

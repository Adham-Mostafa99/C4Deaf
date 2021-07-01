package com.example.graduationproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.UserPrivateInfo;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeDeafChatActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeDeafChatActivity";
    private static final String USERS_PATH = "users";

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;


    private UserPublicInfo userPublicInfo;
    private UserPrivateInfo userPrivateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        initializeFirebase();


        userPublicInfo = getIntent().getParcelableExtra(ConfirmEmailActivity.USER_PUBLIC_INFO_INTENT_EXTRA);
        userPrivateInfo = getIntent().getParcelableExtra(ConfirmEmailActivity.USER_PRIVATE_INFO_INTENT_EXTRA);

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(userPublicInfo.getUserDisplayName()).build();
        currentUser.updateProfile(request);

        insertUserPrivateInfoToDatabase(userPrivateInfo);


    }

    public void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users" + "/" + currentUser.getUid() + "/" + "menu-chat");
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReference("users_images" + "/" + currentUser.getUid() + "/" + "profile_photo.jpg");
    }


    /**
     * @param userPublicInfo which contain public user information
     */
    public void insertUserPublicInfoToDatabase(UserPublicInfo userPublicInfo) {
        //path of user document
        //like: "users/ID"
        String pathOfUserDocument = USERS_PATH + "/" + currentUser.getUid();

        //create map contain userPublicInfo object
        Map<String, Object> userPublicInfoMap = new HashMap<>();
        userPublicInfoMap.put("public-info", userPublicInfo);

        //store user public information
        db.document(pathOfUserDocument)
                .set(userPublicInfoMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        finish();
                        startActivity(new Intent(getApplicationContext(), ChatMenuActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * @param userPrivateInfo which contain private user information
     */
    public void insertUserPrivateInfoToDatabase(UserPrivateInfo userPrivateInfo) {
        //path of user document
        //like: "users/ID"
        String pathOfUserDocument = USERS_PATH + "/" + currentUser.getUid();

        //create map contain userPublicInfo object
        Map<String, Object> userPrivateInfoMap = new HashMap<>();
        userPrivateInfoMap.put("private-info", userPrivateInfo);

        //store user private information
        db.document(pathOfUserDocument)
                .set(userPrivateInfoMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        insertUserPublicInfoToDatabase(userPublicInfo);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    //get current time for the message
    public String getTimeNow() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }

    public void insertUserId(String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child("users ids")
                .child(id)
                .setValue(id);
    }
}
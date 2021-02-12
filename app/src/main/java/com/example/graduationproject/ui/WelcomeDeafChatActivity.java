package com.example.graduationproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.models.UserMenuChat;
import com.example.graduationproject.models.UserPrivateInfo;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeDeafChatActivity extends AppCompatActivity {

    @BindView(R.id.start_chat)
    Button startChat;

    private static final String TAG = "WelcomeDeafChatActivity";
    private static final String USERS_PATH = "users";

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;

    private boolean isPublicUserStored = false;
    private boolean isPrivateUserStored = false;
    private boolean isFriendsStored = false;
    private boolean isMenuChatStored = false;
    private boolean isUpdateUserProfile = false;

    private UserPublicInfo userPublicInfo;
    private UserPrivateInfo userPrivateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_deaf_chat);
        ButterKnife.bind(this);

        initializeFirebase();

        userPublicInfo = getIntent().getParcelableExtra(ConfirmEmailActivity.USER_PUBLIC_INFO_INTENT_EXTRA);
        userPrivateInfo = getIntent().getParcelableExtra(ConfirmEmailActivity.USER_PRIVATE_INFO_INTENT_EXTRA);


        try {
            insertUserProfileImageToDatabaseStorage(userPublicInfo.getUserPhotoPath());
            insertUserPrivateInfoToDatabase(userPrivateInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startChat.setOnClickListener(v -> {

            if (isUpdateUserProfile && isPublicUserStored && isPrivateUserStored && isFriendsStored && isMenuChatStored) {
                finish();
                startActivity(new Intent(getApplicationContext(), ChatMenuActivity.class));
            }

        });


    }

    public void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users" + "/" + currentUser.getUid() + "/" + "menu-chat");
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = firebaseStorage.getReference("users_images" + "/" + currentUser.getUid() + "/" + "profile_photo.jpg");
    }


    public void updateUserProfile(@NonNull UserPublicInfo userPublicInfo) {
        //create instance of UserProfileChangeRequest
        //holding display name and photoUrl
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(userPublicInfo.getUserDisplayName())
                .setPhotoUri(Uri.parse(userPublicInfo.getUserPhotoPath()))
                .build();

        //set UserProfileChangeRequest to current user
        currentUser.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "UpdateUserName:success");
                            isUpdateUserProfile = true;
                        }
                    }
                });
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
                        isPublicUserStored = true;
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
                        isPrivateUserStored = true;
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
     * @param userPublicInfo which contain public user information
     */
    public void insertUserDefaultFriendToDatabase(@NonNull UserPublicInfo userPublicInfo) {
        //path of user document
        //like: "users/ID"
        String pathOfFriendOfUserDocument = USERS_PATH + "/" + currentUser.getUid() + "/" + "Friends" + "/" + userPublicInfo.getUserId();

        //store user default friend (self)
        db.document(pathOfFriendOfUserDocument)
                .set(userPublicInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        isFriendsStored = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /**
     * @param userPublicInfo which contain public user information
     */
    public void insertUserDefaultMenuChatToDatabase(@NonNull UserPublicInfo userPublicInfo) {

        //create instance of default chat
        //which will be [self]
        UserMenuChat userDefaultMenuChat = new UserMenuChat(
                userPublicInfo.getUserId()
                , userPublicInfo.getUserFirstName() + " " + userPublicInfo.getUserLastName()
                , "hello"
                , userPublicInfo.getUserPhotoPath()
                , getTimeNow());

        //store instance of default chat in database
        //which myRef path :"users/userID/menu-chat"
        myRef
                .child(userPublicInfo.getUserId())
                .setValue(userDefaultMenuChat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        isMenuChatStored = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    //upload users data
    public void uploadUserDataToDatabase(String imagePath) {
        userPublicInfo.setUserPhotoPath(imagePath);
        updateUserProfile(userPublicInfo);
        insertUserPublicInfoToDatabase(userPublicInfo);
        insertUserDefaultFriendToDatabase(userPublicInfo);
        insertUserDefaultMenuChatToDatabase(userPublicInfo);
    }

    public void insertUserProfileImageToDatabaseStorage(String userPhotoPath) {
        Uri fileUri = Uri.fromFile(new File(userPhotoPath));
        mStorageRef.putFile(fileUri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        // Continue with the task to get the download URL
                        return mStorageRef.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            //get userPhoto Uri from FirebaseStorage
                            Uri imageUri = task.getResult();
                            assert imageUri != null;
                            Log.v(TAG, "path:" + imageUri.toString());
                            //upload user data
                            uploadUserDataToDatabase(imageUri.toString());
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

    }

    //get current time for the message
    public String getTimeNow() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
}
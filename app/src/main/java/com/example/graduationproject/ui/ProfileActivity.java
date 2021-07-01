package com.example.graduationproject.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static String TAG = "ProfileActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE = 102;
    private static final int READ_FROM_STORAGE_PERMISSION_REQUEST_CODE = 103;


    @BindView(R.id.photo)
    CircleImageView photo;
    @BindView(R.id.chose_photo)
    ImageButton chosePhoto;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.new_first_name)
    EditText newFirstName;
    @BindView(R.id.new_last_name)
    EditText newLastName;
    @BindView(R.id.user_state)
    TextView userState;
    @BindView(R.id.user_gender)
    TextView userGender;
    @BindView(R.id.user_email)
    TextView userEmail;
    @BindView(R.id.user_pass)
    TextView userPass;
    @BindView(R.id.btn_delete_account)
    Button deleteAccount;
    @BindView(R.id.btn_edit_name)
    ImageView editUserName;
    @BindView(R.id.btn_edit_name_done)
    ImageView editUserNameDone;
    @BindView(R.id.btn_edit_pass)
    ImageView editUserpass;
    @BindView(R.id.arrow_back)
    ImageView backButton;


    String userChosenPhoto, userPhotoPath;

    private UserPublicInfo userPublicInfo;

    private ProgressDialog dialog;
    private Activity activity = this;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Glide
                .with(this)
                .load(currentUser.getPhotoUrl())
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(photo);

        DatabaseQueries.getCurrentUserInfo(new DatabaseQueries.GetCurrentUserInfo() {
            @Override
            public void afterGetCurrentUserInfo(UserPublicInfo currentUserInfo, int id) {
                userPublicInfo = currentUserInfo;

                userName.setText(userPublicInfo.getUserFirstName() + " " + userPublicInfo.getUserLastName());
                userState.setText(userPublicInfo.getUserState());
                userGender.setText(userPublicInfo.getUserGender());
            }
        }, currentUser.getUid(), 0);

        chosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCameraPermission();
                uploadProfileImage();
            }
        });

        userEmail.setText(currentUser.getEmail());
        userPass.setText("*************");

        editUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName.setVisibility(View.INVISIBLE);
                editUserNameDone.setVisibility(View.VISIBLE);

                userName.setVisibility(View.INVISIBLE);
                newFirstName.setVisibility(View.VISIBLE);
                newLastName.setVisibility(View.VISIBLE);
            }
        });

        editUserNameDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = newFirstName.getText().toString().trim();
                String lastName = newLastName.getText().toString().trim();

                if (firstName.isEmpty())
                    newFirstName.requestFocus();
                else if (lastName.isEmpty())
                    newLastName.requestFocus();
                else {
                    userPublicInfo.setUserFirstName(firstName);
                    userPublicInfo.setUserLastName(lastName);
                    userPublicInfo.setUserDisplayName(firstName);

                    updateUserProfileUserName(userPublicInfo.getUserDisplayName());
                    updateUserPublicInfoToDatabase(userPublicInfo);
                }

                editUserName.setVisibility(View.VISIBLE);
                editUserNameDone.setVisibility(View.INVISIBLE);

                userName.setVisibility(View.VISIBLE);
                newFirstName.setVisibility(View.INVISIBLE);
                newLastName.setVisibility(View.INVISIBLE);
            }
        });

        editUserpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "This feature will work soon", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRefMenuChat = FirebaseDatabase.getInstance().
                        getReference("users/" + currentUser.getUid());

                myRefMenuChat.removeValue();
                FirebaseFirestore.getInstance().document("users/" + currentUser.getUid()).delete();

                currentUser.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), LogIn_or_SignUp.class));
                                } else {
                                    Log.d(TAG, task.getException().getMessage());
                                }
                            }
                        });

            }
        });
    }

    //get Permission from user to use the camera
    public void setCameraPermission() {
        //set camera permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    //upload image from camera or gallery
    public void uploadProfileImage() {
        final String[] items = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (items[i]) {
                    case "Take Photo":
                        userChosenPhoto = "Take Photo";
                        //camera Intent
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 100);
                        break;
                    case "Choose From Gallery":
                        userChosenPhoto = "Choose From Gallery";
                        //gallery Intent
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 100);
                        break;
                    case "Cancel":
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (userChosenPhoto) {
            case "Take Photo":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                    //set user photo uri
                    Uri captureImageUri = getImageUri(this, captureImage);
                    userPhotoPath = getRealPathFromURI(captureImageUri);
                    // set capture Image to ImageView
                    photo.setImageBitmap(captureImage);
                    uploadPhoto(userPhotoPath);
                }
                break;
            case "Choose From Gallery":
                if (requestCode == 100 && resultCode == RESULT_OK) {
                    // get capture Image
                    Uri uri = data.getData();
                    //set user photo uri
                    userPhotoPath = getRealPathFromURI(uri);
                    // set capture Image to profileImage
                    photo.setImageURI(uri);
                    uploadPhoto(userPhotoPath);
                }
                break;
        }
    }


    //get uri from bitmap image
    public Uri getImageUri(@NonNull Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "CapturedImage", null);
        return Uri.parse(path);
    }

    //get full path from uri
    public String getRealPathFromURI(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToCamera = false;
        boolean permissionToWrite = false;

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToCamera)
                    setReadStoragePermission();
                else
                    finish();
                break;
            case READ_FROM_STORAGE_PERMISSION_REQUEST_CODE:
                permissionToWrite = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToWrite)
                    setWriteStoragePermission();
                else
                    finish();
            case WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE:
                permissionToWrite = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!permissionToWrite)
                    finish();
        }
    }

    //get Permission from user to use the write Image
    public void setReadStoragePermission() {
        //set write permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_FROM_STORAGE_PERMISSION_REQUEST_CODE);
    }

    //get Permission from user to use the write Image
    public void setWriteStoragePermission() {
        //set write permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE);
    }

    public void uploadPhoto(String photoPath) {
        DatabaseQueries.insertPhotoToStorage(new DatabaseQueries.InsertPhotoToStorage() {
            @Override
            public void afterInsertPhotoToStorage(String downloadPhotoPath) {
                userPublicInfo.setUserPhotoPath(downloadPhotoPath);
                updateUserProfilePhoto(userPublicInfo.getUserPhotoPath());
            }
        }, new DatabaseQueries.PhotoProgress() {
            @Override
            public void progress(double progress) {

            }
        }, photoPath);
    }

    public void updateUserProfilePhoto(String photoPath) {
        //create instance of UserProfileChangeRequest
        //holding display name and photoUrl
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
                .Builder()
                .setPhotoUri(Uri.parse(photoPath))
                .build();

        //set UserProfileChangeRequest to current user
        currentUser.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "update profile photo:success");
                        }
                    }
                });

        updateUserPublicInfoToDatabase(userPublicInfo);

    }

    public void updateUserPublicInfoToDatabase(UserPublicInfo userPublicInfo) {
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Profile information being updated, please wait.");
        dialog.show();

        //path of user document
        //like: "users/ID"
        String pathOfUserDocument = "users" + "/" + currentUser.getUid();

        //create map contain userPublicInfo object
        Map<String, Object> userPublicInfoMap = new HashMap<>();
        userPublicInfoMap.put("public-info", userPublicInfo);

        //store user public information
        FirebaseFirestore.getInstance().document(pathOfUserDocument)
                .set(userPublicInfoMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(getApplicationContext(), "information updated", Toast.LENGTH_SHORT).show();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        userName.setText(userPublicInfo.getUserFirstName() + " " + userPublicInfo.getUserLastName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    public void updateUserProfileUserName(@NonNull String userName) {
        //create instance of UserProfileChangeRequest
        //holding display name and photoUrl
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(userName)
                .build();

        //set UserProfileChangeRequest to current user
        currentUser.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "update profile photo:success");
                        }
                    }
                });
    }
}
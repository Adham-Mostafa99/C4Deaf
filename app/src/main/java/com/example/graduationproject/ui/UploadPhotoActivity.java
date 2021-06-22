package com.example.graduationproject.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.graduationproject.R;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.UserPrivateInfo;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadPhotoActivity extends AppCompatActivity {

    @BindView(R.id.photo)
    de.hdodenhof.circleimageview.CircleImageView photo;
    @BindView(R.id.chose_photo)
    ImageView chosePhoto;
    @BindView(R.id.progress_photo)
    CircularProgressBar progressPhoto;

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE = 102;
    private static final int READ_FROM_STORAGE_PERMISSION_REQUEST_CODE = 103;

    String userChosenPhoto, userPhotoPath;

    private UserPublicInfo userPublicInfo;
    private UserPrivateInfo userPrivateInfo;


    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        ButterKnife.bind(this);


        initializeFirebase();

        userPublicInfo = getIntent().getParcelableExtra(ConfirmEmailActivity.USER_PUBLIC_INFO_INTENT_EXTRA);
        userPrivateInfo = getIntent().getParcelableExtra(ConfirmEmailActivity.USER_PRIVATE_INFO_INTENT_EXTRA);


        progressPhoto.setProgressWithAnimation(0);

        chosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCameraPermission();
                uploadProfileImage();
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

    //get Permission from user to use the camera
    public void setCameraPermission() {
        //set camera permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToCamera = false;
        boolean permissionToWrite = false;

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToCamera)
                    setReadStoragePermission();
                else
                    finish();
                break;
            case READ_FROM_STORAGE_PERMISSION_REQUEST_CODE:
                Toast.makeText(this, "Read permission granted", Toast.LENGTH_LONG).show();
                permissionToWrite = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permissionToWrite)
                    setWriteStoragePermission();
                else
                    finish();
            case WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE:
                Toast.makeText(this, "Write permission granted", Toast.LENGTH_LONG).show();
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
        progressPhoto.setProgress(0);
        DatabaseQueries.insertPhotoToStorage(new DatabaseQueries.InsertPhotoToStorage() {
            @Override
            public void afterInsertPhotoToStorage(String downloadPhotoPath) {
                userPublicInfo.setUserPhotoPath(downloadPhotoPath);
                updateUserProfile(userPublicInfo);
            }
        }, new DatabaseQueries.PhotoProgress() {
            @Override
            public void progress(double progress) {
                syncProgressBar((float) progress);
            }
        }, photoPath);
    }

    public void syncProgressBar(float progress) {

        Log.v("TAG", "progress:" + progress);

        progressPhoto.setProgressWithAnimation(progress);
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
                            Log.d("TAG", "UpdateUserName:success");

                            finish();
                            startActivity(new Intent(getApplicationContext(), WelcomeDeafChatActivity.class)
                                    .putExtra(SignUpActivity.USER_PRIVATE_INFO_INTENT_EXTRA, userPrivateInfo)
                                    .putExtra(SignUpActivity.USER_PUBLIC_INFO_INTENT_EXTRA, userPublicInfo));
                        }
                    }
                });
    }

}
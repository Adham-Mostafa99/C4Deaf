package com.example.graduationproject.models;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.Converter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.graduationproject.models.DatabasePaths.REALTIME_ADD_FRIEND_REQUEST_LIST;
import static com.example.graduationproject.models.DatabasePaths.REALTIME_FRIEND_REQUEST_LIST;

public class DatabaseQueries {

    private static FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void getUserFriends(GetUserFriends getUserFriends, int id) {
        ArrayList<String> friendsId = new ArrayList<>();
        String pathOfFriendOfUser =
                "users" + "/" + currentUser().getUid() + "/" + "friends";

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(pathOfFriendOfUser);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (((HashMap<String, Object>) dataSnapshot.getValue()).get("id").toString() != null) {
                        String friendId = ((HashMap<String, Object>) dataSnapshot.getValue()).get("id").toString();
                        friendsId.add(friendId);
                    }
                }
                getUserFriends.afterGetUserFriends(friendsId, id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void insertFriendToDatabase(InsertNewFriend insertNewFriend, String currentUserId, @NonNull String friendId, int id) {
        //path of new friend document
        //like: "users/ID/Friends/friendID"

        String friendsPath = "users" + "/" + currentUserId + "/" + "friends";

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(friendsPath);

        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("id", friendId);

        databaseReference
                .child(friendId)
                .setValue(friendMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        insertNewFriend.afterInsertNewFriend(true, friendId, id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        insertNewFriend.afterInsertNewFriend(false, null, id);
                    }
                });
    }

    public static void getUserMenuChat(GetUserMenuChat getUserMenuChat, int id) {
        DatabaseReference myRefMenuChat =
                FirebaseDatabase.getInstance().
                        getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRefMenuChat.keepSynced(true);

        myRefMenuChat
                .child("menu-chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        getUserMenuChat.afterGetUserMenuChat(snapshot, id);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void createNewChat(CreateNewChat createNewChat, int id, String userId, @NonNull UserMenuChat friendMenuChat) {
        Date date = new Date();
        DatabaseReference myRefMenuChatNewChat = FirebaseDatabase.getInstance()
                .getReference("users/" + userId + "/" + DatabasePaths.REALTIME_MENU_CHAT);
        myRefMenuChatNewChat
                .child(friendMenuChat.getUserId())
                .setValue(friendMenuChat, -date.getTime()) // -date.getTime() ==>add child in last of database
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        createNewChat.afterCreateNewChat(id);
                        // Write was successful!
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

    public static void sendMsg(SendMsg sendMsg, int id, HashMap<String, Object> msg, @NonNull String userId, String friendId) {
        Date date = new Date();
        //path  currentUserId/msg/receiverId/msgNumber/Msg
        DatabaseReference myRefMsgChatSend = FirebaseDatabase.getInstance()
                .getReference("users/" + userId + "/" + "chat-msg");
        //store send msg for my database only
        if (userId.equals(currentUser().getUid()))
            myRefMsgChatSend.keepSynced(true);
        myRefMsgChatSend
                .child(friendId)
                .push()
                .setValue(msg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendMsg.afterSendMsg(true, id, msg);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.w(TAG, "Error adding sender msg", e);
                        sendMsg.afterSendMsg(false, id, msg);
                    }
                });
    }

    public static void getFriendInfo(GetFriendInfo getFriendInfo, int id, String friendId) {
        String friendPath = "users" + "/" + friendId;
        db.document(friendPath)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        HashMap<String, Object> currentFriendInfo = (HashMap<String, Object>) documentSnapshot.get("public-info");
                        UserPublicInfo friendInfo = Converter.ConvertMapToUserPublicInfo(currentFriendInfo);
                        getFriendInfo.afterGetFriendInfo(friendInfo, id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting friend info", e);
                    }
                });
    }

    public static void readMsg(ReadMsg readMsg, int id, String friendId) {
        //path  currentUserId/msg/receiverId/msgNumber/Msg
        DatabaseReference myRefMsgChat =
                FirebaseDatabase.getInstance()
                        .getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/chat-msg");

        myRefMsgChat.keepSynced(true);

        myRefMsgChat.child(friendId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        HashMap<String, Object> currentMsg = (HashMap<String, Object>) snapshot.getValue();
                        readMsg.afterReadMsg(currentMsg, id);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void getFriendByDisplayName(GetFriendByDisplayName getFriendByDisplayName, int id, String displayName) {
        //path of users Collection
        //like: "users"
        String pathOfFriends = "users";
        db.collection(pathOfFriends)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //get Map(public-info) from every user
                            @SuppressWarnings("unchecked")
                            Map<String, Object> currentUser = (HashMap<String, Object>) document.get("public-info");

                            //convert map to UserPublicInfo
                            UserPublicInfo friendInfo = Converter.ConvertMapToUserPublicInfo(currentUser);

                            //check if matched display name
                            if (friendInfo.getUserDisplayName().equals(displayName)) {
                                getFriendByDisplayName.afterGetFriendByDisplayName(friendInfo, id);
                            }
                        }
                    }
                });
    }

    public static void checkClickedUserInFriendList(IsUserInFriendList isUserInFriendList, String searchedUserId) {
        DatabaseQueries.getUserFriends(new GetUserFriends() {
            @Override
            public void afterGetUserFriends(ArrayList<String> friendsId, int id) {
                for (String currentFriendId : friendsId) {
                    if (currentFriendId.equals(searchedUserId)) {
                        isUserInFriendList.isUserInFriendList(true);
                        return;
                    }
                }
                isUserInFriendList.isUserInFriendList(false);
            }
        }, 0);
    }

    private static void insertFriendToSentRequestList(InsertFriendToSentRequestList insertFriendToSentRequestList, @NonNull String friendId) {
        //insert in user [sent request list]
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_ADD_FRIEND_REQUEST_LIST);

        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("id", friendId);

        myRefUser
                .child(friendId)
                .setValue(friendMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        insertFriendToSentRequestList.afterInsertFriendToSentRequestList(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        insertFriendToSentRequestList.afterInsertFriendToSentRequestList(false);
                        Log.w(TAG, "Error adding friend request", e);
                    }
                });

    }

    private static void insertFriendToRequestList(InsertFriendToRequestList insertFriendToRequestList, String friendId) {
        //insert in friend [request list]
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + friendId + "/" + REALTIME_FRIEND_REQUEST_LIST);

        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("id", currentUser().getUid());

        myRefUser
                .child(currentUser().getUid())
                .setValue(friendMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        insertFriendToRequestList.afterInsertFriendToRequestList(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        insertFriendToRequestList.afterInsertFriendToRequestList(false);
                        Log.w(TAG, "Error adding friend request", e);
                    }
                });

    }

    public static void sendAddRequest(SendAddRequest sendAddRequest, @NonNull String friendId, int id) {
        DatabaseQueries.checkIfFriendRequestIsInRequestList(new CheckIfFriendRequestIsInRequestList() {
            @Override
            public void afterCheckIfFriendRequestIsInRequestList(boolean isFound) {
                if (isFound) {
                    sendAddRequest.afterSendAddRequest(false, id);
                    //you sent already request
                } else {
                    DatabaseQueries.insertFriendToSentRequestList(new InsertFriendToSentRequestList() {
                        @Override
                        public void afterInsertFriendToSentRequestList(boolean isSentRequest) {
                            if (isSentRequest) {
                                DatabaseQueries.insertFriendToRequestList(new InsertFriendToRequestList() {
                                    @Override
                                    public void afterInsertFriendToRequestList(boolean isAddToRequest) {
                                        if (isAddToRequest) {
                                            sendAddRequest.afterSendAddRequest(true, id);
                                        }
                                    }
                                }, friendId);
                            }
                        }
                    }, friendId);
                }
            }
        }, friendId);

    }

    public static void cancelRequest(String friendId) {
        DatabaseQueries.checkIfFriendRequestIsInRequestList(new CheckIfFriendRequestIsInRequestList() {
            @Override
            public void afterCheckIfFriendRequestIsInRequestList(boolean isFound) {
                if (isFound) {

                    DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                            .getReference("users/" + currentUser().getUid() + "/" + REALTIME_ADD_FRIEND_REQUEST_LIST);
                    myRefUser
                            .child(friendId)
                            .removeValue();

                    DatabaseReference myRefFriend = FirebaseDatabase.getInstance()
                            .getReference("users/" + friendId + "/" + REALTIME_FRIEND_REQUEST_LIST);

                    myRefFriend
                            .child(currentUser().getUid())
                            .removeValue();

                }
            }
        }, friendId);
    }

    public static void checkIfFriendRequestIsInRequestList(CheckIfFriendRequestIsInRequestList check, String friendId) {

        DatabaseReference myRef = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_ADD_FRIEND_REQUEST_LIST);


        myRef.get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (((HashMap<String, Object>) snapshot.getValue()).get("id").equals(friendId)) {
                                check.afterCheckIfFriendRequestIsInRequestList(true);
                                return;
                            }
                        }
                        check.afterCheckIfFriendRequestIsInRequestList(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public static void getFriendRequestList(GetFriendRequestList friendRequestList, int id) {
        ArrayList<String> requestListIds = new ArrayList<>();
        Log.v(TAG, currentUser().getUid());
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_FRIEND_REQUEST_LIST);

        myRefUser.get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String currentRequestId = ((HashMap<String, Object>) snapshot.getValue()).get("id").toString();
                            requestListIds.add(currentRequestId);
                        }
                        if (!requestListIds.isEmpty())
                            friendRequestList.afterGetFriendRequestList(requestListIds, id);
                        else
                            friendRequestList.afterGetFriendRequestList(null, id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public static void getCurrentUserInfo(GetCurrentUserInfo getCurrentUserInfo, String userId, int id) {

        String currentUserPath = "users" + "/" + userId;
        db.document(currentUserPath)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //get Map(public-info) from every user
                        @SuppressWarnings("unchecked")
                        Map<String, Object> currentUser = (HashMap<String, Object>) documentSnapshot.get("public-info");

                        //convert map to UserPublicInfo
                        UserPublicInfo currentUserInfo = Converter.ConvertMapToUserPublicInfo(currentUser);

                        getCurrentUserInfo.afterGetCurrentUserInfo(currentUserInfo, id);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting friend info", e);
                    }
                });

    }

    private static void deleteFriendRequestFromCurrentUser(DeleteFriendRequestFromCurrentUser deleteFriendRequestFromCurrentUser, @NonNull String friendId) {
        //delete  user [sent request list][current user here is yourFriend]
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_FRIEND_REQUEST_LIST);

        myRefUser
                .child(friendId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteFriendRequestFromCurrentUser.afterDeleteFriendRequestFromCurrentUser(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteFriendRequestFromCurrentUser.afterDeleteFriendRequestFromCurrentUser(false);
                        Log.w(TAG, "Error delete friend request", e);
                    }
                });
    }

    private static void deleteFriendRequestFromOtherUser(DeleteFriendRequestFromOtherUser deleteFriendRequestFromOtherUser, String friendId) {
        //delete  friend [request list] [friend here is adder account]
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + friendId + "/" + REALTIME_ADD_FRIEND_REQUEST_LIST);

        myRefUser
                .child(currentUser().getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteFriendRequestFromOtherUser.afterDeleteFriendRequestFromOtherUser(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteFriendRequestFromOtherUser.afterDeleteFriendRequestFromOtherUser(false);
                        Log.w(TAG, "Error delete friend request", e);
                    }
                });

    }

    public static void acceptFriendRequest(AcceptFriendRequest acceptedFriendRequest, String acceptedFriendId, int id) {


        DatabaseQueries.deleteFriendRequestFromCurrentUser(new DeleteFriendRequestFromCurrentUser() {
            @Override
            public void afterDeleteFriendRequestFromCurrentUser(boolean isDeleted) {
                if (isDeleted) {
                    DatabaseQueries.deleteFriendRequestFromOtherUser(new DeleteFriendRequestFromOtherUser() {
                        @Override
                        public void afterDeleteFriendRequestFromOtherUser(boolean isDeleted) {
                            if (isDeleted) {
                                DatabaseQueries.insertFriendToDatabase(new InsertNewFriend() {
                                    @Override
                                    public void afterInsertNewFriend(boolean isSuccess, String friendId, int id) {

                                    }
                                }, currentUser().getUid(), acceptedFriendId, 0);
                                DatabaseQueries.insertFriendToDatabase(new InsertNewFriend() {
                                    @Override
                                    public void afterInsertNewFriend(boolean isSuccess, String friendId, int id) {

                                    }
                                }, acceptedFriendId, currentUser().getUid(), 0);
                                acceptedFriendRequest.afterAcceptFriendRequest(id);
                            }
                        }
                    }, acceptedFriendId);
                }
            }
        }, acceptedFriendId);


    }

    public static void ignoreFriendRequest(IgnoreFriendRequest ignoreFriendRequest, String ignoredFriendId, int id) {
        DatabaseQueries.deleteFriendRequestFromCurrentUser(new DeleteFriendRequestFromCurrentUser() {
            @Override
            public void afterDeleteFriendRequestFromCurrentUser(boolean isDeleted) {
                if (isDeleted) {
                    DatabaseQueries.deleteFriendRequestFromOtherUser(new DeleteFriendRequestFromOtherUser() {
                        @Override
                        public void afterDeleteFriendRequestFromOtherUser(boolean isDeleted) {
                            if (isDeleted)
                                ignoreFriendRequest.afterIgnoreFriendRequest(id);
                        }
                    }, ignoredFriendId);
                }
            }
        }, ignoredFriendId);
    }

    public static void insertPhotoToStorage(InsertPhotoToStorage insertPhotoToStorage, PhotoProgress photoProgress, String photoUri) {
        StorageReference mStorageRef = FirebaseStorage.getInstance()
                .getReference("users_images" + "/" + currentUser().getUid() + "/" + "profile_photo.jpg");

        Uri fileUri = Uri.fromFile(new File(photoUri));
        mStorageRef
                .putFile(fileUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        photoProgress.progress(progress);
                    }
                })
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
                            //upload user data
                            insertPhotoToStorage.afterInsertPhotoToStorage(imageUri.toString());
                            photoProgress.progress(100);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
    }

    public static void insertRecordAudioToStorage(InsertRecordAudioToStorage insertRecordAudioToStorage, String recordAudioPath) {
        StorageReference mStorageRef = FirebaseStorage.getInstance()
                .getReference("users_record_audio" + "/" + currentUser().getUid());

        String uniqueID = UUID.randomUUID().toString();

        Uri fileUri = Uri.fromFile(new File(recordAudioPath));
        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();


        mStorageRef.child(uniqueID)
                .putFile(fileUri, metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.v("File Upload: ", "OK");

                        mStorageRef.child(uniqueID)
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri recordAudioUri) {


                                        Log.v("File Download: ", "OK");
                                        Log.v("File Download: ", recordAudioUri.toString());

                                        insertRecordAudioToStorage.afterInsertRecordAudioToStorage(uniqueID, recordAudioUri.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v("File Download: ", "fall");
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("File Upload: ", "fall");
                    }
                });
    }

    public static void insertRecordVideoToStorage(InsertRecordVideoToStorage insertRecordVideoToStorage, String recordVideoPath) {
        StorageReference mStorageRef = FirebaseStorage.getInstance()
                .getReference("users_record_video" + "/" + currentUser().getUid());

        String uniqueID = UUID.randomUUID().toString();

        Uri fileUri = Uri.fromFile(new File(recordVideoPath));

        mStorageRef.child(uniqueID)
                .putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.v("File Upload: ", "OK");

                        mStorageRef.child(uniqueID)
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri recordAudioUri) {


                                        Log.v("File Download: ", "OK");
                                        Log.v("File Download: ", recordAudioUri.toString());

                                        insertRecordVideoToStorage.afterInsertRecordVideoToStorage(uniqueID, recordAudioUri.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v("File Download: ", "fall");
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("File Upload: ", "fall");
                    }
                });
    }

    public static void downloadRecordFromUrl(DownloadRecordFromUrl downloadRecordFromUrl, @NonNull String recordType, String recordUrl, String uniqueID) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(recordUrl);
        File rootPath = null;
        File localFile;
        if (recordType.equals("audio")) {
            rootPath = new File(Environment.getExternalStorageDirectory() + "/" + "DeafChat", "records-audio");
            // Create directory if not exists
            if (rootPath != null && !rootPath.exists()) {
                rootPath.mkdirs();
            }
            localFile = new File(rootPath, uniqueID + ".m4a");
        } else {
            rootPath = new File(Environment.getExternalStorageDirectory() + "/" + "DeafChat", "records-video");
            // Create directory if not exists
            if (rootPath != null && !rootPath.exists()) {
                rootPath.mkdirs();
            }
            localFile = new File(rootPath, uniqueID + ".mp4");
        }

        if (localFile.exists()) {
            Log.v("File Download To Local:", "is Exist");
            downloadRecordFromUrl.afterDownloadRecordFromUrl(localFile.getPath());
        } else {
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.v("File Download To Local:", "OK");
                            downloadRecordFromUrl.afterDownloadRecordFromUrl(localFile.getPath());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("File Download To Local:", "fall");
                        }
                    });
        }
    }


    public static void downloadFramesOfWord(DownloadFramesOfWord downloadFramesOfWord, String word) {
        File rootPath = new File(Environment.getExternalStorageDirectory() + "/" + "DeafChat", "convertedVideos");
        // Create directory if not exists
        if (rootPath != null && !rootPath.exists()) {
            rootPath.mkdirs();
        }
        File localFile = new File(rootPath, word);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("sign_language_frames");


        if (localFile.exists() && localFile.listFiles().length != 0) {
            Log.v("File Download To Local:", "is Exist");
            downloadFramesOfWord.afterDownloadFramesOfWord(true, localFile.getPath());
        } else {
            storageReference
                    .child(word)
                    .listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {

                            if (!listResult.getItems().isEmpty()) {
                                Log.v(TAG, listResult.getItems().toString());
                                localFile.mkdirs();
                                int numberOfFrame = 0;
                                for (StorageReference fileRef : listResult.getItems()) {

                                    String currentImageName = fileRef.getName();

                                    File currentImagePath = new File(localFile, currentImageName + ".jpg");

                                    int finalNumberOfFrame = numberOfFrame;
                                    fileRef.getFile(currentImagePath)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    if (finalNumberOfFrame == listResult.getItems().size() - 1) {
                                                        Log.v("File Download To Local:", "OK");
                                                        downloadFramesOfWord.afterDownloadFramesOfWord(true, localFile.getPath());
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                    numberOfFrame++;
                                }

                            } else {
                                Log.v("File Download To Local:", "fall");
                                downloadFramesOfWord.afterDownloadFramesOfWord(false, null);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Log.v("File Download To Local:", "fall");
                            downloadFramesOfWord.afterDownloadFramesOfWord(false, null);
                        }
                    });
        }


    }


    public static void convertRecordToText() {
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("ConverTextToSpeek", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        // Download complete. Depending on your app, you could enable the ML
                        // feature, or switch from the local model to the remote model, etc.

                        // The CustomModel object contains the local path of the model file,
                        // which you can use to instantiate a TensorFlow Lite interpreter.
                        File modelFile = model.getFile();
                        if (modelFile != null) {

                            Interpreter interpreter = new Interpreter(modelFile);
                            Log.v("interpreter", interpreter.toString());

                        }
                    }
                });
    }

    public interface GetUserFriends {
        void afterGetUserFriends(ArrayList<String> friendsId, int id);
    }

    public interface InsertNewFriend {
        void afterInsertNewFriend(boolean isSuccess, String friendId, int id);
    }

    public interface GetUserMenuChat {
        void afterGetUserMenuChat(DataSnapshot snapshot, int id);
    }

    public interface CreateNewChat {
        void afterCreateNewChat(int id);
    }

    public interface SendMsg {
        void afterSendMsg(boolean isSent, int id, HashMap<String, Object> msg);
    }

    public interface GetFriendInfo {
        void afterGetFriendInfo(UserPublicInfo friendInfo, int id);
    }

    public interface ReadMsg {
        void afterReadMsg(HashMap<String, Object> currentMsg, int id);
    }

    public interface GetFriendByDisplayName {
        void afterGetFriendByDisplayName(UserPublicInfo friendInfo, int id);
    }

    public interface IsUserInFriendList {
        void isUserInFriendList(boolean isFound);
    }

    private interface InsertFriendToSentRequestList {
        void afterInsertFriendToSentRequestList(boolean isSentRequest);
    }

    private interface InsertFriendToRequestList {
        void afterInsertFriendToRequestList(boolean isAddToRequest);
    }

    public interface SendAddRequest {
        void afterSendAddRequest(boolean isSuccess, int id);
    }

    public interface GetCurrentUserInfo {
        void afterGetCurrentUserInfo(UserPublicInfo currentUserInfo, int id);
    }

    public interface CheckIfFriendRequestIsInRequestList {
        void afterCheckIfFriendRequestIsInRequestList(boolean isFound);
    }

    private interface DeleteFriendRequestFromCurrentUser {
        void afterDeleteFriendRequestFromCurrentUser(boolean isDeleted);
    }

    private interface DeleteFriendRequestFromOtherUser {
        void afterDeleteFriendRequestFromOtherUser(boolean isDeleted);
    }

    public interface AcceptFriendRequest {
        void afterAcceptFriendRequest(int id);
    }

    public interface IgnoreFriendRequest {
        void afterIgnoreFriendRequest(int id);
    }

    public interface GetFriendRequestList {
        void afterGetFriendRequestList(ArrayList<String> friendsListId, int id);
    }

    public interface InsertPhotoToStorage {
        void afterInsertPhotoToStorage(String downloadPhotoPath);
    }

    public interface InsertRecordAudioToStorage {
        void afterInsertRecordAudioToStorage(String recordName, String downloadRecordAudioUrl);
    }

    public interface InsertRecordVideoToStorage {
        void afterInsertRecordVideoToStorage(String recordName, String downloadRecordVideoUrl);
    }

    public interface DownloadRecordFromUrl {
        void afterDownloadRecordFromUrl(String recordPath);
    }

    public interface DownloadFramesOfWord {
        void afterDownloadFramesOfWord(boolean isFound, String framesFolderPath);
    }

    public interface PhotoProgress {
        void progress(double progress);
    }

}

package com.example.graduationproject.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.graduationproject.Converter;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.graduationproject.models.DatabasePaths.REALTIME_ACCEPTED_REQUEST_LIST;
import static com.example.graduationproject.models.DatabasePaths.REALTIME_ADD_FRIEND_REQUEST_LIST;
import static com.example.graduationproject.models.DatabasePaths.REALTIME_FRIEND_REQUEST_LIST;

public class DatabaseQueries {

    private static FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public static void getUserFriends(GetUserFriends getUserFriends, int id) {
        ArrayList<UserPublicInfo> friends = new ArrayList<>();
        String pathOfFriendOfUser =
                "users" + "/" + currentUser().getUid() + "/" + "Friends";
        db.collection(pathOfFriendOfUser)
                .get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documents : task.getResult())
                                friends.add(documents.toObject(UserPublicInfo.class));
                            getUserFriends.afterGetUserFriends(friends, id);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void insertFriendToDatabase(InsertNewFriend insertNewFriend, @NonNull UserPublicInfo currentUserInfo, @NonNull UserPublicInfo newFriendInfo, int id) {
        //path of new friend document
        //like: "users/ID/Friends/friendID"

        String pathOfNewFriend = "users" + "/" + currentUserInfo.getUserId() + "/" + "Friends" + "/" + newFriendInfo.getUserId();

        db.document(pathOfNewFriend)
                .set(newFriendInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        insertNewFriend.afterInsertNewFriend(true, newFriendInfo, id);
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
                        getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/menu-chat");
        myRefMenuChat.addValueEventListener(new ValueEventListener() {
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

    public static void sendMsgText(SendMsgText sendMsgText, int id, HashMap<String, Object> textMsg, String userId, String friendId) {
        //path  currentUserId/msg/receiverId/msgNumber/Msg
        DatabaseReference myRefMsgChatSend = FirebaseDatabase.getInstance()
                .getReference("users/" + userId + "/" + "chat-msg");
        myRefMsgChatSend
                .child(friendId)
                .push()
                .setValue(textMsg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendMsgText.afterSendMsgText(true, id, textMsg);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.w(TAG, "Error adding sender msg", e);
                        sendMsgText.afterSendMsgText(false, id, textMsg);
                    }
                });
    }

    public static void getFriendInfo(GetFriendInfo getFriendInfo, int id, String friendId) {
        String friendPath = "users" + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "Friends" + "/" + friendId;
        db.document(friendPath)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserPublicInfo friendInfo = documentSnapshot.toObject(UserPublicInfo.class);
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

    public static void checkClickedUserInFriendList(IsUserInFriendList isUserInFriendList, UserPublicInfo searchedUser) {
        DatabaseQueries.getUserFriends(new GetUserFriends() {
            @Override
            public void afterGetUserFriends(ArrayList<UserPublicInfo> friends, int id) {
                for (UserPublicInfo currentFriend : friends) {
                    if (currentFriend.getUserId().equals(searchedUser.getUserId())) {
                        isUserInFriendList.isUserInFriendList(true);
                        return;
                    }
                }
                isUserInFriendList.isUserInFriendList(false);
            }
        }, 0);
    }

    private static void insertFriendToSentRequestList(InsertFriendToSentRequestList insertFriendToSentRequestList, @NonNull UserPublicInfo friendInfo) {
        //insert in user [sent request list]
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_ADD_FRIEND_REQUEST_LIST);

        myRefUser
                .child(friendInfo.getUserId())
                .setValue(friendInfo)
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

    private static void insertFriendToRequestList(InsertFriendToRequestList insertFriendToRequestList, UserPublicInfo currentUserInfo, String friendId) {
        //insert in friend [request list]
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + friendId + "/" + REALTIME_FRIEND_REQUEST_LIST);


        myRefUser
                .child(currentUser().getUid())
                .setValue(currentUserInfo)
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

    public static void sendAddRequest(SendAddRequest sendAddRequest, @NonNull UserPublicInfo friendInfo, int id) {
        DatabaseQueries.checkIfFriendRequestIsInRequestList(new CheckIfFriendRequestIsInRequestList() {
            @Override
            public void afterCheckIfFriendRequestIsInRequestList(boolean isFound) {
                if (isFound) {
                    sendAddRequest.afterSendAddRequest(false, id);
                    //you sent already request
                } else {
                    DatabaseQueries.getCurrentUserInfo(new GetCurrentUserInfo() {
                        @Override
                        public void afterGetCurrentUserInfo(UserPublicInfo currentUserInfo, int afterGetCurrentUserInfoID) {
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
                                        }, currentUserInfo, friendInfo.getUserId());
                                    }
                                }
                            }, friendInfo);
                        }
                    }, currentUser().getUid(), 0);
                }
            }
        }, friendInfo.getUserId());

    }

    public static void checkIfFriendRequestIsInRequestList(CheckIfFriendRequestIsInRequestList check, String friendId) {

        DatabaseReference myRef = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_ADD_FRIEND_REQUEST_LIST);


        myRef.get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getValue(UserPublicInfo.class).getUserId().equals(friendId)) {
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
        ArrayList<UserPublicInfo> requestList = new ArrayList<>();
        Log.v(TAG, currentUser().getUid());
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_FRIEND_REQUEST_LIST);

        myRefUser.get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserPublicInfo currentRequest = snapshot.getValue(UserPublicInfo.class);
                            requestList.add(currentRequest);
                        }
                        if (!requestList.isEmpty())
                            friendRequestList.afterGetFriendRequestList(requestList, id);
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


    private static void deleteFriendRequestFromCurrentUser(DeleteFriendRequestFromCurrentUser deleteFriendRequestFromCurrentUser, @NonNull UserPublicInfo deletedFriendInfo) {
        //delete  user [sent request list][current user here is yourFriend]
        DatabaseReference myRefUser = FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser().getUid() + "/" + REALTIME_FRIEND_REQUEST_LIST);

        myRefUser
                .child(deletedFriendInfo.getUserId())
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

    public static void acceptFriendRequest(AcceptFriendRequest acceptedFriendRequest, UserPublicInfo acceptedFriend, int id) {

        DatabaseQueries.getCurrentUserInfo(new GetCurrentUserInfo() {
            @Override
            public void afterGetCurrentUserInfo(UserPublicInfo currentUserInfo, int id) {

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
                                            public void afterInsertNewFriend(boolean isSuccess, UserPublicInfo newFriendInfo, int id) {

                                            }
                                        }, currentUserInfo, acceptedFriend, 0);
                                        DatabaseQueries.insertFriendToDatabase(new InsertNewFriend() {
                                            @Override
                                            public void afterInsertNewFriend(boolean isSuccess, UserPublicInfo newFriendInfo, int id) {

                                            }
                                        }, acceptedFriend, currentUserInfo, 0);
                                        acceptedFriendRequest.afterAcceptFriendRequest(id);
                                    }
                                }
                            }, acceptedFriend.getUserId());
                        }
                    }
                }, acceptedFriend);
            }

        }, currentUser().getUid(), 0);


    }

    public static void ignoreFriendRequest(IgnoreFriendRequest ignoreFriendRequest, UserPublicInfo ignoredFriend, int id) {
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
                    }, ignoredFriend.getUserId());
                }
            }
        }, ignoredFriend);
    }

    public interface GetUserFriends {
        void afterGetUserFriends(ArrayList<UserPublicInfo> friends, int id);
    }

    public interface InsertNewFriend {
        void afterInsertNewFriend(boolean isSuccess, UserPublicInfo newFriendInfo, int id);
    }

    public interface GetUserMenuChat {
        void afterGetUserMenuChat(DataSnapshot snapshot, int id);
    }

    public interface CreateNewChat {
        void afterCreateNewChat(int id);
    }

    public interface SendMsgText {
        void afterSendMsgText(boolean isSent, int id, HashMap<String, Object> textMsg);
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
        void afterGetFriendRequestList(ArrayList<UserPublicInfo> friendsList, int id);
    }
}

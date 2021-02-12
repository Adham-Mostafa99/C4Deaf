package com.example.graduationproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.UserFriendsAdapter;
import com.example.graduationproject.models.UserMenuChat;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFriendsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, UserFriendsAdapter.OnItemClick {

    @BindView(R.id.recycler_view_user_friends)
    RecyclerView recyclerViewUserFriends;
    @BindView(R.id.user_friends_refresh_swipe)
    SwipeRefreshLayout userFriendsRefreshSwipe;

    private static final String TAG = "UserFriendsActivity";
    public static final String FRIEND_ID_INTENT = "friendId";
    public static final int ADD_FRIEND_REQUEST_CODE = 200;
    @BindView(R.id.add_friend_button)
    Button addFriendButton;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private ArrayList<UserPublicInfo> userFriends;
    private UserFriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);
        ButterKnife.bind(this);
        init();
        initializeFirebase();
        initializeAdapter();
        try {
            getUserFriends();
        } catch (Exception e) {
            e.printStackTrace();
        }


        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddFriendActivity.class)
                        , ADD_FRIEND_REQUEST_CODE);
            }
        });

    }

    public void init() {
        userFriends = new ArrayList<>();
        userFriendsRefreshSwipe.setOnRefreshListener(this);
    }

    public void initializeFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    public void initializeAdapter() {
        adapter = new UserFriendsAdapter(this, userFriends, this);
        recyclerViewUserFriends.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserFriends.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .build());
        recyclerViewUserFriends.setAdapter(adapter);
    }

    public void refreshAdapter(@NonNull ArrayList<UserPublicInfo> data) {
        userFriends.clear();
        userFriends.addAll(data);
        adapter.notifyDataSetChanged();
    }

    public void insertFriendToAdapter(UserPublicInfo userPublicInfo) {
        int firstItemInList = 0;
        userFriends.add(firstItemInList, userPublicInfo);
        adapter.notifyItemInserted(firstItemInList);
        recyclerViewUserFriends.smoothScrollToPosition(firstItemInList);
    }

    public void insertFriendToDatabase(@NonNull UserPublicInfo userPublicInfo) {
        //path of friend document
        //like: "users/ID/Friends/friendID"
        String pathOfFriendOfUserCollection = "users" + "/" + currentUser.getUid() + "/" + "Friends" + "/" + userPublicInfo.getUserId();
        db.document(pathOfFriendOfUserCollection)
                .set(userPublicInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        insertFriendToAdapter(userPublicInfo);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getUserFriends() {
        //path of Friends Collection
        //like: "users/ID/Friends"
        String pathOfFriendOfUserCollection = "users" + "/" + currentUser.getUid() + "/" + "Friends";

        ArrayList<UserPublicInfo> friends = new ArrayList<>();

        db.collection(pathOfFriendOfUserCollection)
                .get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documents : task.getResult())
                                friends.add(documents.toObject(UserPublicInfo.class));
                            refreshAdapter(friends);
                            userFriendsRefreshSwipe.setRefreshing(false);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null)
                    currentUser.reload();
                getUserFriends();
            }
        }, 1000);
    }

    @Override
    public void onItemClick(int position) {
        //open user
        String friendId = userFriends.get(position).getUserId();
        Toast.makeText(this, friendId, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ChatPageNormal.class)
                .putExtra(FRIEND_ID_INTENT, friendId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_FRIEND_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Log.v(TAG, data.getParcelableExtra(AddFriendActivity.ADDED_FRIEND_INTENT_EXTRA).toString());
                insertFriendToDatabase(data.getParcelableExtra(AddFriendActivity.ADDED_FRIEND_INTENT_EXTRA));
            }
        }
    }
}
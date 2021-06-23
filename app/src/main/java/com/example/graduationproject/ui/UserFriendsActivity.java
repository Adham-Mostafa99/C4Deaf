package com.example.graduationproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.UserFriendsAdapter;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.UserPublicInfo;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserFriendsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
        , UserFriendsAdapter.OnItemClick, DatabaseQueries.GetUserFriends, DatabaseQueries.InsertNewFriend {

    @BindView(R.id.recycler_view_user_friends)
    RecyclerView recyclerViewUserFriends;
    @BindView(R.id.user_friends_refresh_swipe)
    SwipeRefreshLayout userFriendsRefreshSwipe;

    private static final String TAG = "UserFriendsActivity";
    public static final String FRIEND_ID_INTENT = "friendId";
    public static final int ADD_FRIEND_REQUEST_CODE = 200;
    public static final int DB_GET_FRIENDS_ID = 1;
    public static final int DB_INSERT_FRIENDS_ID = 2;

    @BindView(R.id.fab_add_friend)
    FloatingActionButton addFriendButton;

    private FirebaseUser currentUser;
    private ArrayList<UserPublicInfo> userFriends;
    private UserFriendsAdapter adapter;
    private DatabaseQueries.GetUserFriends getUserFriends = this;
    private DatabaseQueries.InsertNewFriend insertNewFriend = this;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);
        ButterKnife.bind(this);
        init();
        initializeFirebase();
        initializeAdapter();
        try {
            DatabaseQueries.getUserFriends(getUserFriends, DB_GET_FRIENDS_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }


        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddFriendActivity.class));
            }
        });

    }

    public void init() {
        userFriends = new ArrayList<>();
        userFriendsRefreshSwipe.setOnRefreshListener(this);
    }

    public void initializeFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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

    public void insertFriendToAdapter(UserPublicInfo userPublicInfo) {
        int firstItemInList = 0;
        userFriends.add(firstItemInList, userPublicInfo);
        adapter.notifyItemInserted(firstItemInList);
        recyclerViewUserFriends.smoothScrollToPosition(firstItemInList);
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null)
                    currentUser.reload();
                DatabaseQueries.getUserFriends(getUserFriends, DB_GET_FRIENDS_ID);
            }
        }, 1000);
    }

    @Override
    public void onItemClick(int position) {
        //open user
        String friendDisplayName = userFriends.get(position).getUserDisplayName();
        DatabaseQueries.getFriendByDisplayName(new DatabaseQueries.GetFriendByDisplayName() {
            @Override
            public void afterGetFriendByDisplayName(UserPublicInfo friendInfo, int id) {
                popWindowCreation(friendInfo);
            }
        }, 0, friendDisplayName);

    }

    public void getFriendInfo(String friendId) {
        DatabaseQueries.getFriendInfo(new DatabaseQueries.GetFriendInfo() {
            @Override
            public void afterGetFriendInfo(UserPublicInfo friendInfo, int id) {
                insertFriendToAdapter(friendInfo);
            }
        }, 0, friendId);
    }


    @Override
    public void afterGetUserFriends(ArrayList<String> friendsId, int id) {
        switch (id) {
            case DB_GET_FRIENDS_ID:
                for (String friendId : friendsId) {
                    DatabaseQueries.getFriendInfo(new DatabaseQueries.GetFriendInfo() {
                        @Override
                        public void afterGetFriendInfo(UserPublicInfo friendInfo, int id) {
                            insertFriendToAdapter(friendInfo);
                        }
                    }, 0, friendId);
                }
                userFriendsRefreshSwipe.setRefreshing(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void afterInsertNewFriend(boolean isSuccess, String newFriendId, int id) {
        switch (id) {
            case DB_INSERT_FRIENDS_ID:
                if (isSuccess)
                    getFriendInfo(newFriendId);
        }

    }

    public void popWindowCreation(@NonNull UserPublicInfo friendInfo) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.friend_info_pop_up, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        CircleImageView friendPhoto = popupView.findViewById(R.id.friend_pop_photo);
        TextView friendDisplayName = popupView.findViewById(R.id.friend_pop_display_name);
        TextView friendFullName = popupView.findViewById(R.id.friend_pop_full_name);
        TextView friendGender = popupView.findViewById(R.id.friend_pop_gender);
        TextView friendState = popupView.findViewById(R.id.friend_pop_state);

        Glide
                .with(this)
                .load(friendInfo.getUserPhotoPath())
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(friendPhoto);

        friendDisplayName.setText(friendInfo.getUserDisplayName());
        friendFullName.setText(friendInfo.getUserFirstName() + " " + friendInfo.getUserLastName());
        friendGender.setText(friendInfo.getUserGender());
        friendState.setText(friendInfo.getUserState());

    }
}
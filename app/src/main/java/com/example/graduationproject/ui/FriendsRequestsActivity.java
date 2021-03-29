package com.example.graduationproject.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.FriendsRequestsAdapter;
import com.example.graduationproject.adapters.UserFriendsAdapter;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.UserPublicInfo;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRequestsActivity extends AppCompatActivity implements FriendsRequestsAdapter.OnItemClick
        , FriendsRequestsAdapter.OnAcceptFriend, FriendsRequestsAdapter.OnIgnoreFriend, DatabaseQueries.GetFriendRequestList
        , SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.friends_requests_list)
    RecyclerView friendsRequestsList;
    @BindView(R.id.request_friends_refresh_swipe)
    SwipeRefreshLayout requestFriendsRefreshSwipe;
    private ArrayList<UserPublicInfo> friendsRequestsArrayList;
    private FriendsRequestsAdapter adapter;
    private PopupWindow popupWindow;

    private static final int DB_ACCEPTED_FRIEND = 1;
    private static final int DB_IGNORED_FRIEND = 2;
    private static final int DB_GetFriendRequestList = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_requests);
        ButterKnife.bind(this);
        init();
        initAdapter();

        DatabaseQueries.getFriendRequestList(this, DB_GetFriendRequestList);
    }

    public void init() {
        friendsRequestsArrayList = new ArrayList<>();
        requestFriendsRefreshSwipe.setOnRefreshListener(this);
    }

    public void initAdapter() {
        adapter = new FriendsRequestsAdapter(this, friendsRequestsArrayList, this, this, this);
        friendsRequestsList.setLayoutManager(new LinearLayoutManager(this));
        friendsRequestsList.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .build());
        friendsRequestsList.setAdapter(adapter);
    }

    public void refreshAdapter(ArrayList<UserPublicInfo> friendsList) {
        friendsRequestsArrayList.clear();
        friendsRequestsArrayList.addAll(friendsList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        String friendDisplayName = friendsRequestsArrayList.get(position).getUserDisplayName();
        DatabaseQueries.getFriendByDisplayName(new DatabaseQueries.GetFriendByDisplayName() {
            @Override
            public void afterGetFriendByDisplayName(UserPublicInfo friendInfo, int id) {
                popWindowCreation(friendInfo);
            }
        }, 0, friendDisplayName);
    }

    @Override
    public void onAcceptFriend(int position) {
        UserPublicInfo acceptedRequest = friendsRequestsArrayList.get(position);
        DatabaseQueries.acceptFriendRequest(new DatabaseQueries.AcceptFriendRequest() {
            @Override
            public void afterAcceptFriendRequest(int id) {
                friendsRequestsArrayList.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), " accepted", Toast.LENGTH_SHORT).show();
            }
        }, acceptedRequest, DB_ACCEPTED_FRIEND);
    }

    @Override
    public void onIgnoreFriend(int position) {
        UserPublicInfo ignoredRequest = friendsRequestsArrayList.get(position);
        DatabaseQueries.ignoreFriendRequest(new DatabaseQueries.IgnoreFriendRequest() {
            @Override
            public void afterIgnoreFriendRequest(int id) {
                friendsRequestsArrayList.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), " ignored", Toast.LENGTH_SHORT).show();
            }
        }, ignoredRequest, DB_IGNORED_FRIEND);
    }

    @Override
    public void afterGetFriendRequestList(ArrayList<UserPublicInfo> friendsList, int id) {
        switch (id) {
            case DB_GetFriendRequestList:
                if (friendsList != null) {
                    refreshAdapter(friendsList);
                }
                requestFriendsRefreshSwipe.setRefreshing(false);

                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        friendsRequestsArrayList.clear();
        adapter.notifyDataSetChanged();
        DatabaseQueries.getFriendRequestList(this, DB_GetFriendRequestList);
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
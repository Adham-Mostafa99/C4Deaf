package com.example.graduationproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.FriendsSentAdapter;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.graduationproject.models.DatabasePaths.REALTIME_ADD_FRIEND_REQUEST_LIST;
import static com.example.graduationproject.models.DatabasePaths.REALTIME_FRIEND_REQUEST_LIST;

public class SentRequestsActivity extends AppCompatActivity implements FriendsSentAdapter.OnItemClick,
        FriendsSentAdapter.CancelFriend, SwipeRefreshLayout.OnRefreshListener, DatabaseQueries.GetFriendSentList {


    @BindView(R.id.arrow_back)
    ImageView backButton;
    @BindView(R.id.friends_sent_requests_list)
    RecyclerView friendsSentList;
    @BindView(R.id.sent_request_friends_refresh_swipe)
    SwipeRefreshLayout requestFriendsRefreshSwipe;
    private ArrayList<UserPublicInfo> friendsSent;
    private FriendsSentAdapter adapter;
    private PopupWindow popupWindow;

    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_requests);
        ButterKnife.bind(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        init();
        initAdapter();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DatabaseQueries.getFriendSentList(this);
    }

    public void init() {
        friendsSent = new ArrayList<>();
        requestFriendsRefreshSwipe.setOnRefreshListener(this);
    }

    public void initAdapter() {
        adapter = new FriendsSentAdapter(this, friendsSent, this, this);
        friendsSentList.setLayoutManager(new LinearLayoutManager(this));
        friendsSentList.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .build());
        friendsSentList.setAdapter(adapter);
    }

    public void insertFriendToAdapter(UserPublicInfo userPublicInfo) {
        int firstItemInList = 0;
        friendsSent.add(firstItemInList, userPublicInfo);
        adapter.notifyItemInserted(firstItemInList);
        friendsSentList.smoothScrollToPosition(firstItemInList);
    }


    @Override
    public void onCancelFriend(int position) {
        FirebaseDatabase.getInstance()
                .getReference("users/" + currentUser.getUid() + "/" + REALTIME_ADD_FRIEND_REQUEST_LIST)
                .child(friendsSent.get(position).getUserId())
                .removeValue();

        FirebaseDatabase.getInstance()
                .getReference("users/" + friendsSent.get(position).getUserId() + "/" + REALTIME_FRIEND_REQUEST_LIST)
                .child(currentUser.getUid())
                .removeValue();
        friendsSent.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        String friendDisplayNameName = friendsSent.get(position).getUserDisplayName();
        DatabaseQueries.getFriendByDisplayName(new DatabaseQueries.GetFriendByDisplayName() {
            @Override
            public void afterGetFriendByDisplayName(UserPublicInfo friendInfo, int id) {
                popWindowCreation(friendInfo);
            }
        }, 0, friendDisplayNameName);
    }

    @Override
    public void onRefresh() {
        friendsSent.clear();
        adapter.notifyDataSetChanged();
        DatabaseQueries.getFriendSentList(this);
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

    @Override
    public void afterGetFriendSentList(ArrayList<String> friendsListId) {
        if (friendsListId != null) {
            for (String friendId : friendsListId) {
                DatabaseQueries.getFriendInfo(new DatabaseQueries.GetFriendInfo() {
                    @Override
                    public void afterGetFriendInfo(UserPublicInfo friendInfo, int id) {
                        insertFriendToAdapter(friendInfo);
                    }
                }, 0, friendId);
            }
        }
        requestFriendsRefreshSwipe.setRefreshing(false);

    }
}
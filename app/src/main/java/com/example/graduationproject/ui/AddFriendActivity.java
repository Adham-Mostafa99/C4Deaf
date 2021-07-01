package com.example.graduationproject.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.AddFriendAdapter;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendActivity extends AppCompatActivity implements AddFriendAdapter.OnItemClick,
        DatabaseQueries.GetFriendByDisplayName, AddFriendAdapter.AddFriend,
        DatabaseQueries.SendAddRequest, AddFriendAdapter.CancelFriend,
        AddFriendAdapter.FriendOrNot {

    @BindView(R.id.display_name_search)
    EditText displayNameSearch;
    @BindView(R.id.search_friends)
    Button searchFriends;
    @BindView(R.id.searched_friend_recycler)
    RecyclerView searchedFriendRecycler;

    public static final String ADDED_FRIEND_INTENT_EXTRA = "addedFriend";
    private static final String TAG = "AddFriendActivity";
    private static final int DB_GET_FRIEND_BY_DISPLAY_NAME_ID = 1;
    private static final int DB_SEND_ADD_REQUEST_ID = 2;


    private ArrayList<UserPublicInfo> friendsArrayList;
    private AddFriendAdapter adapter;
    private DatabaseQueries.GetFriendByDisplayName getFriendByDisplayName = this;
    private DatabaseQueries.SendAddRequest sendAddRequest = this;
    private PopupWindow popupWindow;
    @BindView(R.id.arrow_back)
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        init();
        initAdapter();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAdapter();
                String displayName = displayNameSearch.getText().toString().toLowerCase().trim();
                DatabaseQueries.getFriendByDisplayName(getFriendByDisplayName
                        , DB_GET_FRIEND_BY_DISPLAY_NAME_ID, displayName);

            }
        });
    }

    public void init() {
        friendsArrayList = new ArrayList<>();
    }


    public void initAdapter() {
        adapter = new AddFriendAdapter(this, friendsArrayList,
                this,
                this,
                this,
                this);
        searchedFriendRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchedFriendRecycler.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .build());
        searchedFriendRecycler.setAdapter(adapter);
    }

    public void clearAdapter() {
        friendsArrayList.clear();
        adapter.notifyDataSetChanged();
    }

    public void insertToAdapter(@NonNull UserPublicInfo searchedFriend) {
        friendsArrayList.add(searchedFriend);
        int lastItem = friendsArrayList.size() - 1;
        adapter.notifyItemChanged(lastItem);
    }

    @Override
    public void onItemClick(int position) {
        UserPublicInfo friendInfo = friendsArrayList.get(position);
        popWindowCreation(friendInfo);

    }

    @Override
    public void afterGetFriendByDisplayName(UserPublicInfo friendInfo, int id) {
        switch (id) {
            case DB_GET_FRIEND_BY_DISPLAY_NAME_ID:
                Log.v(TAG, "friend info :" + friendInfo.getUserDisplayName());
                insertToAdapter(friendInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAddFriend(int position) {
        String clickedFriendId = friendsArrayList.get(position).getUserId();

        if (clickedFriendId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            Toast.makeText(getApplicationContext(), "can't add your self", Toast.LENGTH_SHORT).show();
        } else {
            //check if he is in friends list
            DatabaseQueries.checkClickedUserInFriendList(new DatabaseQueries.IsUserInFriendList() {
                @Override
                public void isUserInFriendList(boolean isFound) {
                    if (isFound)
                        Toast.makeText(getApplication(), "already friend", Toast.LENGTH_SHORT).show();
                    else {
                        DatabaseQueries.sendAddRequest(sendAddRequest, clickedFriendId, DB_SEND_ADD_REQUEST_ID);
                    }
                }
            }, clickedFriendId);
        }
    }

    @Override
    public void afterSendAddRequest(boolean isSuccess, int id) {
        switch (id) {
            case DB_SEND_ADD_REQUEST_ID:
                if (isSuccess)
                    Toast.makeText(getApplication(), "sent Added request", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplication(), "you already send request", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
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

    @Override
    public void onCancelFriend(int position) {
        String clickedFriendId = friendsArrayList.get(position).getUserId();
        DatabaseQueries.cancelRequest(clickedFriendId);
    }

    @Override
    public void friendOrNot(int position, Button add) {
        String clickedFriendId = friendsArrayList.get(position).getUserId();

        if (!clickedFriendId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            //check if he is in friends list
            DatabaseQueries.checkClickedUserInFriendList(new DatabaseQueries.IsUserInFriendList() {
                @Override
                public void isUserInFriendList(boolean isFound) {
                    if (!isFound) {
                        add.setVisibility(View.VISIBLE);
                    }

                }
            }, clickedFriendId);
        }
    }
}
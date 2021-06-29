package com.example.graduationproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.ChatListAdapter;
import com.example.graduationproject.adapters.UserFriendsAdapter;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.UserMenuChat;
import com.example.graduationproject.models.UserPublicInfo;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.graduationproject.ui.UserFriendsActivity.FRIEND_ID_INTENT;

public class ChatMenuActivity extends AppCompatActivity implements ChatListAdapter.OnItemClickListener
        , SwipeRefreshLayout.OnRefreshListener, UserFriendsAdapter.OnItemClick, DatabaseQueries.GetUserFriends
        , DatabaseQueries.GetUserMenuChat {

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.search_button)
    Button searchButton;

    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String FRIEND_ID_INTENT_EXTRA = "friendId";
    private static final String TAG = "ChatMenuActivity";
    private static final int DB_GET_USER_FRIENDS_ID = 1;
    private static final int DB_GET_MENU_CHAT_ID = 2;


    private PopupWindow popupWindow;
    private TextView noFriendsTextMsg;
    private RecyclerView newMsgFriendList;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ChatListAdapter adapter;
    UserFriendsAdapter newChatAdapter;
    ArrayList<UserMenuChat> userFriends;
    ArrayList<UserPublicInfo> newMsgFriends;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private UserPublicInfo currentUserInfo;

    private DatabaseQueries.GetUserMenuChat getUserMenuChat = this;
    @BindView(R.id.no_chat_items)
    TextView noChatItems;
    @BindView(R.id.fab)
    FloatingActionButton newMsgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_menu);
        ButterKnife.bind(this);
        init();
        initializeAdapter();
        initializeFirebase();


        DatabaseQueries.getCurrentUserInfo(new DatabaseQueries.GetCurrentUserInfo() {
            @Override
            public void afterGetCurrentUserInfo(UserPublicInfo userInfo, int id) {
                currentUserInfo = userInfo;
            }
        }, currentUser.getUid(), 0);

        try {
            DatabaseQueries.getUserMenuChat(this, DB_GET_MENU_CHAT_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (currentUser != null) {
            //set user photo from Uri
            Glide
                    .with(this)
                    .load(currentUser.getPhotoUrl())
                    .centerCrop()
                    .placeholder(R.drawable.user_photo)
                    .into(menu);
            //set user profile
            setNavHeaderInfo(currentUser.getPhotoUrl(), currentUser.getDisplayName());
        }
        //when click in item on menu in navigation drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.user_friends:
                        Toast.makeText(getApplicationContext(), "friends", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), UserFriendsActivity.class));
                        break;
                    case R.id.user_friends_requests:
                        startActivity(new Intent(getApplicationContext(), FriendsRequestsActivity.class));
                        break;
                    case R.id.sent_requests:
                        startActivity(new Intent(getApplicationContext(), SentRequests.class));
                        break;
                    case R.id.account_setting:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                        break;
                    case R.id.sign_out:
                        signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        break;
                }
                return true;
            }
        });

        //when click on menu button
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //when click on new chat button
        newMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindowCreation();

            }
        });

        //when click on search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO will add search Friends in my friends
            }
        });

    }

    public void signOut() {
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(getApplicationContext(), gso).signOut();
    }

    //initialize resources
    public void init() {
        noChatItems.setVisibility(View.VISIBLE);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    //initialize adapters Objects
    public void initializeAdapter() {
        userFriends = new ArrayList<>();
        adapter = new ChatListAdapter(this, userFriends, this);
        //add recyclerView orientation
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        //add recyclerView divider
        recyclerViewChat.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        //set customAdapter to recyclerView
        recyclerViewChat.setAdapter(adapter);
    }

    //initialize Firebase Objects
    public void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
//        db = FirebaseFirestore.getInstance();
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("users/" + currentUser.getUid() + "/menu-chat");
    }

    /**
     * set information for header of navigation [photo-name-...]
     *
     * @param UserPhotoUrl user photo in header of navigation
     * @param userName     user name in header of navigation
     */
    public void setNavHeaderInfo(Uri UserPhotoUrl, String userName) {
        //create instance from HeaderView
        View headerView = navigationView.getHeaderView(0);

        //user name and photo
        ImageView userPhotoImage = headerView.findViewById(R.id.header_photo);
        TextView userNameText = headerView.findViewById(R.id.header_user_name);

        //set user photo from Uri
        Glide
                .with(this)
                .load(UserPhotoUrl)
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(userPhotoImage);

        //set UserName
        userNameText.setText(userName);
    }

    public void refreshAdapter(@NonNull ArrayList<UserMenuChat> data) {
        userFriends.clear();
        userFriends.addAll(data);
        adapter.notifyDataSetChanged();
    }

//    public void insertChatToAdapter(UserMenuChat userMenuChat) {
//        userFriends.add(userMenuChat);
//        int lastItem = userFriends.size() - 1;
//        adapter.notifyItemInserted(lastItem);
//    }

    public void setFriendsToAdapter(@NonNull DataSnapshot dataSnapshot) {
        ArrayList<UserMenuChat> menuChatArrayList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            menuChatArrayList.add(snapshot.getValue(UserMenuChat.class));
        }
        refreshAdapter(menuChatArrayList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // update ui
//            onRefresh();
        } else {
            finish();
            //user logout or not found
        }
    }

    /**
     * handel on click back button
     * will then hide navigation drawer
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else {
            //some thing like close app
        }
    }

    @Override
    public void onClickItem(int position) {
        String friendId = userFriends.get(position).getUserId();
        if (currentUserInfo != null) {
            if (currentUserInfo.getUserState().equals("normal"))
                startActivity(new Intent(this, ChatPageNormal.class)
                        .putExtra(FRIEND_ID_INTENT, friendId));
            else if(currentUserInfo.getUserState().equals("deaf"))
                startActivity(new Intent(this, ChatPageDeaf.class)
                        .putExtra(FRIEND_ID_INTENT, friendId));
        } else {
            DatabaseQueries.getCurrentUserInfo(new DatabaseQueries.GetCurrentUserInfo() {
                @Override
                public void afterGetCurrentUserInfo(UserPublicInfo userInfo, int id) {
                    currentUserInfo = userInfo;
                }
            }, currentUser.getUid(), 0);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null)
                    currentUser.reload();
                DatabaseQueries.getUserMenuChat(getUserMenuChat, DB_GET_MENU_CHAT_ID);
            }
        }, 1000);
    }


    public void popWindowCreation() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.new_msg_pop_window, null);

        //TODO make pop ex.. 300*200

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        noFriendsTextMsg = popupView.findViewById(R.id.no_friends_to_show_text_view);
        newMsgFriendList = popupView.findViewById(R.id.new_msg_friends_list);
        newMsgFriends = new ArrayList<>();
        newChatAdapter = new UserFriendsAdapter(this, newMsgFriends, this);

        newMsgFriendList.setAdapter(newChatAdapter);
        newMsgFriendList.setLayoutManager(new LinearLayoutManager(this));
        newMsgFriendList.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .build());

        DatabaseQueries.getUserFriends(this, DB_GET_USER_FRIENDS_ID);

    }


    public void insertFriendToAdapter(UserPublicInfo userPublicInfo) {
        int firstItemInList = 0;
        newMsgFriends.add(firstItemInList, userPublicInfo);
        newChatAdapter.notifyItemInserted(firstItemInList);
        newMsgFriendList.smoothScrollToPosition(firstItemInList);
    }


    // onclick in any friend in newMsg
    @Override
    public void onItemClick(int position) {

        //TODO make animation when click
        //open user
        String friendId = newMsgFriends.get(position).getUserId();
        if (currentUserInfo.getUserState().equals("Normal"))
            startActivity(new Intent(this, ChatPageNormal.class)
                    .putExtra(FRIEND_ID_INTENT, friendId));
        else
            startActivity(new Intent(this, ChatPageDeaf.class)
                    .putExtra(FRIEND_ID_INTENT, friendId));
        popupWindow.dismiss();

    }

    @Override
    public void afterGetUserFriends(ArrayList<String> friendsId, int id) {
        switch (id) {
            case DB_GET_USER_FRIENDS_ID:
                if (!friendsId.isEmpty()) {

                    for (String friendId : friendsId) {
                        DatabaseQueries.getFriendInfo(new DatabaseQueries.GetFriendInfo() {
                            @Override
                            public void afterGetFriendInfo(UserPublicInfo friendInfo, int id) {
                                insertFriendToAdapter(friendInfo);
                            }
                        }, 0, friendId);
                    }
                    noFriendsTextMsg.setVisibility(View.GONE);
                    newMsgFriendList.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void afterGetUserMenuChat(DataSnapshot snapshot, int id) {
        switch (id) {
            case DB_GET_MENU_CHAT_ID:
                setFriendsToAdapter(snapshot);
                noChatItems.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                break;
            default:
                break;
        }
    }

}
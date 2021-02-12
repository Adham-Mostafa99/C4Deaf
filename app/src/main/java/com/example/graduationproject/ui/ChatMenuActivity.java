package com.example.graduationproject.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.example.graduationproject.models.UserMenuChat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMenuActivity extends AppCompatActivity implements ChatListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.search_button)
    ImageView searchButton;
    @BindView(R.id.new_chat_button)
    ImageView newChatButton;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String FRIEND_ID_INTENT_EXTRA = "friendId";
    private static final String TAG = "ChatMenuActivity";
    private static final int CHOOSE_FRIEND_TO_CHAT = 300;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ChatListAdapter adapter;
    ArrayList<UserMenuChat> userFriends;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_menu);
        ButterKnife.bind(this);
        init();
        initializeAdapter();
        initializeFirebase();

        try {
            getUserMenuChat();
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
            Log.v(TAG, "path:" + currentUser.getPhotoUrl().toString());
            Log.v(TAG, "uri:" + currentUser.getPhotoUrl());
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
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
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
        newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });

        //when click on search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserFriendsActivity.class));
            }
        });

    }

    //initialize resources
    public void init() {
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
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/" + currentUser.getUid() + "/menu-chat");
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

    public void insertChatToAdapter(UserMenuChat userMenuChat) {
        userFriends.add(userMenuChat);
        int lastItem = userFriends.size() - 1;
        adapter.notifyItemInserted(lastItem);
    }

    public void setFriendsToAdapter(@NonNull DataSnapshot dataSnapshot) {
        ArrayList<UserMenuChat> menuChatArrayList = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            menuChatArrayList.add(snapshot.getValue(UserMenuChat.class));
        }
        refreshAdapter(menuChatArrayList);
    }


    public void getUserMenuChat() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setFriendsToAdapter(snapshot);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createNewChat(UserMenuChat userMenuChat) {
        Log.v(TAG, myRef + "");
        myRef
                .child("11")
                .setValue(userMenuChat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        insertChatToAdapter(userMenuChat);
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


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // update ui
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
        Toast.makeText(this, friendId, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ChatPageNormal.class)
                .putExtra(FRIEND_ID_INTENT_EXTRA, friendId));
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null)
                    currentUser.reload();
                getUserMenuChat();
            }
        }, 1000);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CHOOSE_FRIEND_TO_CHAT) {
//            if (resultCode == RESULT_OK) {
//                createNewChat(new UserMenuChat(
//                        "dodo"
//                        , "hello"
//                        , ""
//                        , "14:54 PM"));
//            }
//        }
//    }
}
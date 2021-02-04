package com.example.graduationproject.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.User;
import com.example.graduationproject.adapters.ChatListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMenuActivity extends AppCompatActivity implements ChatListAdapter.OnItemClickListener {

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.search_button)
    ImageView searchButton;
    @BindView(R.id.new_chat_button)
    ImageView newChatButton;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_menu);
        ButterKnife.bind(this);

        init();

        //change image of header in navigation
        View headerView = navigationView.getHeaderView(0);
        ImageView userPhoto = headerView.findViewById(R.id.header_photo);
        userPhoto.setImageResource(R.drawable.camera);


        //when click in item on menu in navigation drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.user_friends:
                        Toast.makeText(getApplicationContext(), "friends", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
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

            }
        });

        //when click on search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    /**
     * handel on click back button
     * will then hide navigation drawer
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);
        else
            super.onBackPressed();
    }

    //initialize resources
    public void init() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ArrayList<User> users = addData();
        adapter = new ChatListAdapter(this, users, this);
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

    @Override
    public void onClickItem(int position) {
        Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
    }


    public ArrayList<User> addData() {
        ArrayList<User> users = new ArrayList<>();

        users.add(new User("ahmed",
                "hello man!!!",
                "https://mangathrill.com/wp-content/uploads/2020/02/mikassaa.jpg",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://mangathrill.com/wp-content/uploads/2020/02/mikassaa.jpg",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://mangathrill.com/wp-content/uploads/2020/02/mikassaa.jpg",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://mangathrill.com/wp-content/uploads/2020/02/mikassaa.jpg",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://mangathrill.com/wp-content/uploads/2020/02/mikassaa.jpg",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://mangathrill.com/wp-content/uploads/2020/02/mikassaa.jpg",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://mangathrill.com/wp-content/uploads/2020/02/mikassaa.jpg",
                "05:14 Pm"));

        return users;
    }
}
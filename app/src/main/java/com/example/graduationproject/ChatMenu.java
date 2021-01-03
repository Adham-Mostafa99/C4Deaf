package com.example.graduationproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatMenu extends AppCompatActivity {

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.search_button)
    ImageView searchButton;
    @BindView(R.id.new_chat_button)
    ImageView newChatButton;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_menu);
        ButterKnife.bind(this);

        ArrayList<User> users = new ArrayList<>();

        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));
        users.add(new User("ahmed",
                "hello man!!!",
                "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                "05:14 Pm"));


        ChatListAdapter adapter = new ChatListAdapter(this, users);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.GRAY)
                        .sizeResId(R.dimen.divider)
                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());
        recyclerViewChat.setAdapter(adapter);

        //when click on menu button
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

}
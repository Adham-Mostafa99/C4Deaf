package com.example.graduationproject.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.NormalMessageAdapter;
import com.example.graduationproject.models.Chat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPageNormal extends AppCompatActivity {

    @BindView(R.id.chat_view)
    RelativeLayout chatView;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_normal);
        ButterKnife.bind(this);

        testChat();

    }

    public void testChat(){
        ArrayList<Chat> arrayList = new ArrayList<>();

        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));

        NormalMessageAdapter adapter=new NormalMessageAdapter(arrayList,this);
        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
    }


}
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
import com.example.graduationproject.adapters.MessageAdapter;
import com.example.graduationproject.models.Chat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPageNormal extends AppCompatActivity {

    @BindView(R.id.normal_record)
    ImageView normalRecord;
    @BindView(R.id.chat_view)
    RelativeLayout chatView;
    VideoView videoView;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_normal);
        ButterKnife.bind(this);

        testChat();

        //for test video pop up only
        normalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopVideoView();
                playVideo();

            }
        });

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
        arrayList.add(new Chat("per1", "per2", "hello", "10:25 PM"));

        MessageAdapter adapter=new MessageAdapter(arrayList,this);
        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
    }


    //make popup window or video
    public void openPopVideoView() {
        //make the width and height for the pop Window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // lets taps outside the popup also dismiss it
        boolean focusable = true;

        //inflate new layout with specific layout(pop_layout) for the pop window
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popLayout = layoutInflater.inflate(R.layout.vedio_message, null);

        //create instance of popupWindow by specific view, width, and height
        PopupWindow popupWindow = new PopupWindow(popLayout, width, height, focusable);

        //show the created instance in specific location
        popupWindow.showAtLocation(chatView, Gravity.CENTER, 0, 0);

        //declare videoView which will appear in pop up window
        videoView = popLayout.findViewById(R.id.video_view);
    }

    public void playVideo() {
        //create the path of video
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.test;

        //create uri with specific path
        Uri uri = Uri.parse(videoPath);

        //set path to the videoView
        videoView.setVideoURI(uri);

        //create MediaController for control the video
        //like play ,stop and etc...
        MediaController mediaController = new MediaController(this);

        //set this controller to the video view
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        //start video
        videoView.start();
    }

}
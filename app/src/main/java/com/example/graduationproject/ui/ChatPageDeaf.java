package com.example.graduationproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.graduationproject.R;
import com.example.graduationproject.VideoMsg;
import com.example.graduationproject.adapters.DeafMessageAdapter;
import com.example.graduationproject.models.DeafChat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPageDeaf extends AppCompatActivity {


    @BindView(R.id.recycler_view_chat_deaf)
    RecyclerView recyclerViewChatDeaf;
    @BindView(R.id.btn_emoji)
    ImageView btnEmoji;
    @BindView(R.id.text_send)
    EditText textSend;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    @BindView(R.id.deaf_record)
    ImageView deafRecord;

    private static final int OPEN_RECORD_VIDEO_REQUEST_CODE = 1;

    private ArrayList<DeafChat> msg;
    private DeafMessageAdapter adapter;

    private String fileName = null;

    public static int COUNTER=0;

    public void init() {
        msg = new ArrayList<>();

        adapter = new DeafMessageAdapter(msg, this);
        recyclerViewChatDeaf.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChatDeaf.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_deaf);
        ButterKnife.bind(this);

        //initialize objects
        init();

        deafRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName = getExternalCacheDir().getAbsolutePath();
                fileName += "/videorecord"+COUNTER+".mp4";

                // go to video recoding activity
                Intent intent = new Intent(getApplicationContext(), OpenRecordVideoActivity.class);
                intent.putExtra("file name", fileName);
                startActivityForResult(intent, OPEN_RECORD_VIDEO_REQUEST_CODE);

            }

        });

        btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = textSend.getText().toString().trim();
                textSend.setText("");
                if (!msgText.isEmpty())
                    insertItemToAdapter(new DeafChat("per1", "per2", msgText, getTimeNow()));
            }
        });

    }

    //handling the intent result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_RECORD_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String stringMsg = null;
                if (data != null) {
                    //get JsonString msg which contain video-message details
                    stringMsg = data.getStringExtra("msg");
                }
                //convert JsonString msg to Json object
                //and extract data from it like
                //path,duration and time
                //and then make an new object of DeafChat and by this data
                VideoMsg videoMsg = new VideoMsg(stringMsg);

                //get instance of DeafChat
                DeafChat deafChat = videoMsg.getDeafChatMsg();

                //insert new msg that contain video
                if (deafChat != null)
                    insertItemToAdapter(deafChat);
            }
        }
    }


    /*
    this part to control the adapter when [insert-delete] item
     */

    /**
     * @param newChat new msg that will be insert and showing in chat
     */
    //add new item to list and show it in recycler
    public void insertItemToAdapter(DeafChat newChat) {
        msg.add(newChat);
        adapter.notifyItemInserted(msg.size() - 1);
        adapter.notifyDataSetChanged();
        recyclerViewChatDeaf.getLayoutManager().scrollToPosition(msg.size() - 1);
    }



    /*
    handling time
     */

    /**
     * @param time time in milliSeconds
     * @return time in state(min:sec)
     */
    //formatting the time to show in the app
    public String reformatTime(int time) {
        //get duration in mile seconds
        //extract minutes and seconds
        /*
         * example:
         * we have 90 000 mili seconds
         * sec = 90 000 / 1000 = 90 sec
         * min = 90/60 = 1.5
         * while min is integer then value will (1)
         * seconds after that will be :
         * 90 - 1min (1 * 60) = 30 sec
         * then :
         * min = 1
         * sec = 30
         */
        int sec = time / 1000;
        int min = sec / 60;
        sec = sec - (min * 60);

        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

    //get current time for the message
    public String getTimeNow() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }

}


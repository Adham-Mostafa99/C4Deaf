package com.example.graduationproject.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.NormalMessageAdapter;
import com.example.graduationproject.models.NormalChat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPageNormal extends AppCompatActivity {
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200; //200 for microphone

    private NormalMessageAdapter adapter;
    private ArrayList<NormalChat> msg;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder recorder = null;
    private String fileName = null;
    private int recordCounter = 1; //for test


    @BindView(R.id.chat_view)
    RelativeLayout chatView;
    @BindView(R.id.recycler_view_chat)
    RecyclerView recyclerViewChat;
    @BindView(R.id.btn_emoji)
    ImageView btnEmoji;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    @BindView(R.id.normal_record_button)
    RecordButton normalRecordButton;
    @BindView(R.id.normal_record_view)
    RecordView normalRecordView;
    @BindView(R.id.edit_text_send)
    LinearLayout editTextSend;
    @BindView(R.id.text_send)
    EditText textSend;

    //initialize the view
    public void init() {
        msg = new ArrayList<>();
        adapter = new NormalMessageAdapter(msg, this);
        recyclerViewChat.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(linearLayoutManager);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecord" + recordCounter + ".3gp";

        //sync recordButton with recordView
        normalRecordButton.setRecordView(normalRecordView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_normal);
        ButterKnife.bind(this);

        //initialize objects
        init();

        normalRecordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..

                //get permission for record
                ActivityCompat.requestPermissions(ChatPageNormal.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

                // Record to the external cache directory for visibility
                fileName = getExternalCacheDir().getAbsolutePath();
                fileName += "/audiorecord" + recordCounter + ".3gp";
                startRecording(fileName);
                hideInputText(editTextSend);
                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                showInputText(editTextSend);
                Log.d("RecordView", "onCancel");
            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                showInputText(editTextSend);
                stopRecording();
                String time = reformatTime((int) recordTime);

                //add record to chat
                insertItemToAdapter(new NormalChat("per1", "per2", fileName, time, getTimeNow()));
                Log.d("RecordView", "onFinish");
                Log.d("RecordTime", time);
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                showInputText(editTextSend);
                Log.d("RecordView", "onLessThanSecond");
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
                    insertItemToAdapter(new NormalChat("per1", "per2", msgText, getTimeNow()));
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopAdapter();
        recorder.release();
        recorder = null;
    }


    /*
    this part for handling record [get record, store and stop it ]
    formatting time
    hide/show the editText
     */
    //handling record permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    /**
     * @param fileName the location of record file output contain its name
     */
    //start getting record and store it
    public void startRecording(String fileName) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();

        Log.v("vounter", recordCounter + "");
        recordCounter++;
    }

    //strop record and release Media record from memory
    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

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

    /**
     * @param linearLayout layout which contain the editText view that will be hidden
     */
    //hide inputText for record
    public void hideInputText(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * @param linearLayout layout which contain the editText view that will be show
     */
    //show inputText when record end
    public void showInputText(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
    }


    /*
    this part to control the adapter when [insert-delete] item
     */

    /**
     * @param newChat new msg that will be insert and showing in chat
     */
    //add new item to list and show it in recycler
    public void insertItemToAdapter(NormalChat newChat) {
        msg.add(newChat);
        adapter.notifyItemInserted(msg.size() - 1);
        adapter.notifyDataSetChanged();
        recyclerViewChat.getLayoutManager().scrollToPosition(msg.size() - 1);
    }


    //get current time for the message
    public String getTimeNow() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }

}
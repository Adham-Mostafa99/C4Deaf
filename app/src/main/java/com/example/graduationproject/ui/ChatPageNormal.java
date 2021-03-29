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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.NormalMessageAdapter;
import com.example.graduationproject.models.DatabasePaths;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.NormalChat;
import com.example.graduationproject.models.UserMenuChat;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPageNormal extends AppCompatActivity implements DatabaseQueries.CreateNewChat
        , DatabaseQueries.GetFriendInfo, DatabaseQueries.SendMsgText, DatabaseQueries.ReadMsg {
    private static final String LOG_TAG = "AudioRecordTest";
    private static final String TAG = "ChatPageNormal";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200; //200 for microphone
    private static final int DB_CREATE_NEW_CHAT_ID = 1;
    private static final int DB_SEND_TEXT_MSG_USER_ID = 2;
    private static final int DB_SEND_TEXT_MSG_FRIEND_ID = 3;
    private static final int DB_GET_FRIEND_INFO_ID = 4;
    private static final int DB_READ_MSG_ID = 5;


    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.arrow_back)
    ImageView arrowBack;

    private NormalMessageAdapter adapter;
    private ArrayList<NormalChat> msg;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private MediaRecorder recorder = null;
    private String fileName = null;
    private int recordCounter = 1; //for test

    private FirebaseUser currentUser;

    private String friendId;
    private UserPublicInfo friendInfo;

    private DatabaseQueries.CreateNewChat createNewChat = this;

    private boolean isFriendInfoUpdated = false;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_normal);
        ButterKnife.bind(this);

        //initialize objects
        initFirebase();
        init();
        DatabaseQueries.readMsg(this, DB_READ_MSG_ID, friendId);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                insertItemToAdapter(new NormalChat("per1", fileName, time, getTimeNow()));
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
                if (!msgText.isEmpty() && isFriendInfoUpdated)
                    sendTextMsg(msgText, getTimeNow());
            }
        });

    }


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

        friendId = getIntent().getStringExtra(ChatMenuActivity.FRIEND_ID_INTENT_EXTRA);
        DatabaseQueries.getFriendInfo(this, DB_GET_FRIEND_INFO_ID, friendId);
    }

    public void initFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopAdapter();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
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
    public void hideInputText(@NonNull LinearLayout linearLayout) {
        linearLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * @param linearLayout layout which contain the editText view that will be show
     */
    //show inputText when record end
    public void showInputText(@NonNull LinearLayout linearLayout) {
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

    public void sendTextMsg(String msg, String msgTime) {
        String senderId = currentUser.getUid();

        HashMap<String, Object> textMsg = new HashMap<>();
        textMsg.put("sender", senderId);
        textMsg.put("msg", msg);
        textMsg.put("msgTime", msgTime);
        textMsg.put("msgType", "text");

        //add current user msg
        DatabaseQueries.sendMsgText(this, DB_SEND_TEXT_MSG_USER_ID, textMsg, currentUser.getUid(), friendId);

        //add the msg in other user
        DatabaseQueries.sendMsgText(this, DB_SEND_TEXT_MSG_FRIEND_ID, textMsg, friendId, currentUser.getUid());

    }

    public void updateUi(@NonNull UserPublicInfo friendInfo) {
        Glide
                .with(this)
                .load(friendInfo.getUserPhotoPath())
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(userImage);
        userName.setText(friendInfo.getUserDisplayName());
    }

    //get current time for the message
    public String getTimeNow() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }

    @Override
    public void afterCreateNewChat(int id) {
        //something here
    }

    @Override
    public void afterSendMsgText(boolean isSent, int id, HashMap<String, Object> textMsg) {
        switch (id) {
            case DB_SEND_TEXT_MSG_USER_ID:
                if (isSent) {
                    DatabaseQueries.createNewChat(createNewChat, DB_CREATE_NEW_CHAT_ID, currentUser.getUid()
                            , new UserMenuChat(friendId
                                    , friendInfo.getUserDisplayName()
                                    , textMsg.get("msg").toString()
                                    , friendInfo.getUserPhotoPath()
                                    , textMsg.get("msgTime").toString()));
                }
                break;
            case DB_SEND_TEXT_MSG_FRIEND_ID:
                if (isSent) {
                    DatabaseQueries.createNewChat(createNewChat, DB_CREATE_NEW_CHAT_ID, friendId
                            , new UserMenuChat(currentUser.getUid()
                                    , currentUser.getDisplayName()
                                    , textMsg.get("msg").toString()
                                    , currentUser.getPhotoUrl().toString()
                                    , textMsg.get("msgTime").toString()));

                }
            default:
                break;
        }
    }

    @Override
    public void afterGetFriendInfo(UserPublicInfo friendInfo, int id) {
        switch (id) {
            case DB_GET_FRIEND_INFO_ID:
                this.friendInfo = friendInfo;
                updateUi(friendInfo);
                isFriendInfoUpdated = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void afterReadMsg(HashMap<String, Object> currentMsg, int id) {
        switch (id) {
            case DB_READ_MSG_ID:
                if (currentMsg != null) {
                    Log.v(TAG, currentMsg.toString());
                    String sender = (String) currentMsg.get("sender");
                    String msg = (String) currentMsg.get("msg");
                    String msgTime = (String) currentMsg.get("msgTime");
                    String msgType = (String) currentMsg.get("msgType");
                    if (msgType != null && msgType.equals("text")) {
                        insertItemToAdapter(new NormalChat(sender, msg, msgTime));
                    }
                }
                break;
            default:
                break;
        }
    }
}
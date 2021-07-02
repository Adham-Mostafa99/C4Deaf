package com.example.graduationproject.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.graduationproject.R;
import com.example.graduationproject.speech_to_text.VoiceRecognize;
import com.example.graduationproject.adapters.NormalMessageAdapter;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.NormalChat;
import com.example.graduationproject.models.UserMenuChat;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vanniktech.emoji.EmojiPopup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPageNormal extends AppCompatActivity implements DatabaseQueries.CreateNewChat
        , DatabaseQueries.GetFriendInfo, DatabaseQueries.SendMsg, DatabaseQueries.ReadMsg {
    private static final String LOG_TAG = "AudioRecordTest";
    private static final String TAG = "ChatPageNormal";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200; //200 for microphone
    private static final int DB_CREATE_NEW_CHAT_ID = 1;
    private static final int DB_SEND_TEXT_MSG_USER_ID = 2;
    private static final int DB_SEND_TEXT_MSG_FRIEND_ID = 3;
    private static final int DB_SEND_RECORD_AUDIO_MSG_USER_ID = 4;
    private static final int DB_SEND_RECORD_AUDIO_MSG_FRIEND_ID = 5;
    private static final int DB_GET_FRIEND_INFO_ID = 10;
    private static final int DB_READ_MSG_ID = 20;


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
    private DatabaseQueries.SendMsg sendMsg = this;

    private boolean isFriendInfoUpdated = false;


    @BindView(R.id.chat_view)
    RelativeLayout chatView;
    @BindView(R.id.recycler_view_chat_normal)
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

    private VoiceRecognize voiceRecognize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_normal);
        ButterKnife.bind(this);

        //get permission for record
        ActivityCompat.requestPermissions(ChatPageNormal.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        //initialize objects
        initFirebase();
        init();

        final EmojiPopup emojiPopup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.chat_view))
                .build(textSend);

        btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();
            }
        });


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

                if (friendInfo.getUserState().equals("normal")) {
                    // Record to the external cache directory for visibility
                    fileName = getExternalCacheDir().getAbsolutePath();
                    fileName += "/" + UUID.randomUUID().toString() + ".m4a";
                    startRecording(fileName);
                    hideInputText(editTextSend);
                    Log.d("RecordView", "onStart");
                } else if(friendInfo.getUserState().equals("deaf")) {
                    hideInputText(editTextSend);
                    voiceRecognize = new VoiceRecognize(getApplicationContext(), new VoiceRecognize.Result() {
                        @Override
                        public void result(String msg) {
                            sendTextMsg(msg, getTimeNow());
                        }
                    });
                    voiceRecognize.startRecognize();
                }
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
                Log.d("RecordView", "onFinish");
                Log.d("RecordTime", time);

                if (friendInfo.getUserState().equals("Normal"))
                    sendRecordAudio(fileName, time);
                else {
                    voiceRecognize.stopRecognize();
//                    ConvertSpeechToText convertSpeechToText = new ConvertSpeechToText(getApplicationContext(), fileName);
//                    try {
//                        convertSpeechToText.convert(new ConvertSpeechToText.OnConvert() {
//                            @Override
//                            public void afterConvert(String msgText) {
//                                sendTextMsg(msgText, getTimeNow());
//                            }
//                        });
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    //convert record to video
//                    Toast.makeText(getApplicationContext(), "will be converted", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                showInputText(editTextSend);
                Log.d("RecordView", "onLessThanSecond");
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


        textSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString()))
                    showBtnRecord();
                else
                    showBtnSend();


            }
        });

    }

    public void showBtnSend() {
        normalRecordButton.setVisibility(View.INVISIBLE);
        normalRecordButton.setClickable(false);
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setClickable(true);

    }

    public void showBtnRecord() {
        btnSend.setVisibility(View.INVISIBLE);
        btnSend.setClickable(false);
        normalRecordButton.setVisibility(View.VISIBLE);
        normalRecordButton.setClickable(true);
    }


    //initialize the view
    public void init() {
        msg = new ArrayList<>();
        adapter = new NormalMessageAdapter(msg, this);
        recyclerViewChat.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(linearLayoutManager);

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
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioEncodingBitRate(16 * 44100);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(fileName);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
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
        DatabaseQueries.sendMsg(this, DB_SEND_TEXT_MSG_USER_ID, textMsg, currentUser.getUid(), friendId);

        //add the msg in other user
        DatabaseQueries.sendMsg(this, DB_SEND_TEXT_MSG_FRIEND_ID, textMsg, friendId, currentUser.getUid());

    }

    public void sendRecordAudio(String filePath, String time) {
        DatabaseQueries.insertRecordAudioToStorage(new DatabaseQueries.InsertRecordAudioToStorage() {
            @Override
            public void afterInsertRecordAudioToStorage(String recordName, String downloadRecordAudioPathUrl) {

                String senderId = currentUser.getUid();

                HashMap<String, Object> textMsg = new HashMap<>();
                textMsg.put("sender", senderId);
                textMsg.put("recordName", recordName);
                textMsg.put("msg", downloadRecordAudioPathUrl);
                textMsg.put("msgDuration", time);
                textMsg.put("msgTime", getTimeNow());
                textMsg.put("msgType", "record_audio");


                //add current user msg
                DatabaseQueries.sendMsg(sendMsg, DB_SEND_RECORD_AUDIO_MSG_USER_ID, textMsg, currentUser.getUid(), friendId);

                //add the msg in other user
                DatabaseQueries.sendMsg(sendMsg, DB_SEND_RECORD_AUDIO_MSG_FRIEND_ID, textMsg, friendId, currentUser.getUid());

            }
        }, fileName);
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
    public void afterSendMsg(boolean isSent, int id, HashMap<String, Object> msg) {
        switch (id) {
            case DB_SEND_TEXT_MSG_USER_ID:
                if (isSent) {
                    DatabaseQueries.createNewChat(createNewChat, DB_CREATE_NEW_CHAT_ID, currentUser.getUid()
                            , new UserMenuChat(friendId
                                    , friendInfo.getUserDisplayName()
                                    , msg.get("msg").toString()
                                    , friendInfo.getUserPhotoPath()
                                    , msg.get("msgTime").toString()));
                }
                break;
            case DB_SEND_TEXT_MSG_FRIEND_ID:
                if (isSent) {
                    DatabaseQueries.createNewChat(createNewChat, DB_CREATE_NEW_CHAT_ID, friendId
                            , new UserMenuChat(currentUser.getUid()
                                    , currentUser.getDisplayName()
                                    , msg.get("msg").toString()
                                    , currentUser.getPhotoUrl().toString()
                                    , msg.get("msgTime").toString()));

                }
                break;
            case DB_SEND_RECORD_AUDIO_MSG_USER_ID:
                if (isSent) {
                    DatabaseQueries.createNewChat(createNewChat, DB_CREATE_NEW_CHAT_ID, currentUser.getUid()
                            , new UserMenuChat(friendId
                                    , friendInfo.getUserDisplayName()
                                    , "record...."
                                    , friendInfo.getUserPhotoPath()
                                    , msg.get("msgTime").toString()));
                }
                break;
            case DB_SEND_RECORD_AUDIO_MSG_FRIEND_ID:
                if (isSent) {
                    DatabaseQueries.createNewChat(createNewChat, DB_CREATE_NEW_CHAT_ID, friendId
                            , new UserMenuChat(currentUser.getUid()
                                    , currentUser.getDisplayName()
                                    , "record...."
                                    , currentUser.getPhotoUrl().toString()
                                    , msg.get("msgTime").toString()));

                }
                break;
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
                    } else if (msgType != null && msgType.equals("record_audio")) {
                        String msgDuration = (String) currentMsg.get("msgDuration");
                        String recordName = (String) currentMsg.get("recordName");
                        DatabaseQueries.downloadRecordFromUrl(new DatabaseQueries.DownloadRecordFromUrl() {
                            @Override
                            public void afterDownloadRecordFromUrl(String recordPath) {
                                insertItemToAdapter(new NormalChat(sender, recordPath, msgDuration, msgTime));
                            }
                        }, "record", msg, recordName);
                    }
                }
                break;
            default:
                break;
        }
    }
}
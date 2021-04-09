package com.example.graduationproject.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduationproject.sign_language.ConvertIconToText;
import com.example.graduationproject.sign_language.KeyboardManager;
import com.example.graduationproject.R;
import com.example.graduationproject.VideoMsg;
import com.example.graduationproject.adapters.DeafMessageAdapter;
import com.example.graduationproject.models.DatabaseQueries;
import com.example.graduationproject.models.DeafChat;
import com.example.graduationproject.models.UserMenuChat;
import com.example.graduationproject.models.UserPublicInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPageDeaf extends AppCompatActivity implements DatabaseQueries.SendMsg, DatabaseQueries.GetFriendInfo
        , DatabaseQueries.CreateNewChat, DatabaseQueries.ReadMsg, ConvertIconToText.OnPressKey {


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

    private static final String TAG = "DeafPageNormal";

    private static final int OPEN_RECORD_VIDEO_REQUEST_CODE = 1;
    private static final int DB_SEND_RECORD_VIDEO_MSG_USER_ID = 2;
    private static final int DB_SEND_RECORD_VIDEO_MSG_FRIEND_ID = 3;
    private static final int DB_CREATE_NEW_CHAT_ID = 6;
    private static final int DB_SEND_TEXT_MSG_USER_ID = 4;
    private static final int DB_SEND_TEXT_MSG_FRIEND_ID = 5;
    private static final int DB_GET_FRIEND_INFO_ID = 10;
    private static final int DB_READ_MSG_ID = 20;
    @BindView(R.id.btn_keyboard)
    ImageView btnKeyboard;
    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    @BindView(R.id.key_q)
    ImageView keyQ;
    @BindView(R.id.key_w)
    ImageView keyW;
    @BindView(R.id.key_e)
    ImageView keyE;
    @BindView(R.id.key_r)
    ImageView keyR;
    @BindView(R.id.key_t)
    ImageView keyT;
    @BindView(R.id.key_y)
    ImageView keyY;
    @BindView(R.id.key_u)
    ImageView keyU;
    @BindView(R.id.key_i)
    ImageView keyI;
    @BindView(R.id.key_a)
    ImageView keyA;
    @BindView(R.id.key_s)
    ImageView keyS;
    @BindView(R.id.key_d)
    ImageView keyD;
    @BindView(R.id.key_f)
    ImageView keyF;
    @BindView(R.id.key_g)
    ImageView keyG;
    @BindView(R.id.key_h)
    ImageView keyH;
    @BindView(R.id.key_j)
    ImageView keyJ;
    @BindView(R.id.key_k)
    ImageView keyK;
    @BindView(R.id.key_z)
    ImageView keyZ;
    @BindView(R.id.key_x)
    ImageView keyX;
    @BindView(R.id.key_c)
    ImageView keyC;
    @BindView(R.id.key_v)
    ImageView keyV;
    @BindView(R.id.key_b)
    ImageView keyB;
    @BindView(R.id.key_n)
    ImageView keyN;
    @BindView(R.id.key_m)
    ImageView keyM;
    @BindView(R.id.key_o)
    ImageView keyO;
    @BindView(R.id.key_p)
    ImageView keyP;
    @BindView(R.id.key_l)
    ImageView keyL;
    @BindView(R.id.key_space)
    ImageView keySpace;
    @BindView(R.id.key_backspace)
    ImageView keyBackspace;

    private boolean isFriendInfoUpdated = false;


    @BindView(R.id.arrow_back)
    ImageView arrowBack;
    @BindView(R.id.user_image)
    CircleImageView userImage;
    @BindView(R.id.user_name)
    TextView userName;

    private ArrayList<DeafChat> msg;
    private DeafMessageAdapter adapter;

    private String fileName = null;
    private FirebaseUser currentUser;

    private String friendId;
    private UserPublicInfo friendInfo;

    private DatabaseQueries.SendMsg sendMsg = this;
    private DatabaseQueries.CreateNewChat createNewChat = this;
    private ConvertIconToText.OnPressKey onPressKey = this;
    private Activity activity = this;

    private KeyboardManager keyboardManager;

    public void init() {
        msg = new ArrayList<>();

        adapter = new DeafMessageAdapter(msg, this);
        recyclerViewChatDeaf.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChatDeaf.setLayoutManager(linearLayoutManager);

        keyboardManager = new KeyboardManager(getApplicationContext(), activity, textSend, keyboard);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        friendId = getIntent().getStringExtra(ChatMenuActivity.FRIEND_ID_INTENT_EXTRA);
        DatabaseQueries.getFriendInfo(this, DB_GET_FRIEND_INFO_ID, friendId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page_deaf);
        ButterKnife.bind(this);


        //initialize objects
        init();

        DatabaseQueries.readMsg(this, DB_READ_MSG_ID, friendId);

        deafRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName = getExternalCacheDir().getAbsolutePath();
                fileName += "/" + UUID.randomUUID().toString() + ".mp4";

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

        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                keyboardManager.changeKeyboard(new KeyboardManager.OnHidePrimaryKeyboard() {
                    @Override
                    public void afterHidePrimaryKeyboard() {
                        ConvertIconToText convertIconToText = new ConvertIconToText(getApplicationContext(), onPressKey,
                                keyA, keyB, keyC, keyD, keyE, keyF, keyG, keyH, keyI, keyJ, keyK, keyL, keyM, keyN
                                , keyO, keyP, keyQ, keyR, keyS, keyT, keyU, keyV, keyW, keyX, keyY, keyZ, keySpace, keyBackspace);
                        convertIconToText.initWithClick();
                    }
                });
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

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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


    public void sendRecordVideo(DeafChat msg) {
        DatabaseQueries.insertRecordVideoToStorage(new DatabaseQueries.InsertRecordVideoToStorage() {
            @Override
            public void afterInsertRecordVideoToStorage(String recordName, String downloadRecordAudioPathUrl) {

                String senderId = currentUser.getUid();

                HashMap<String, Object> textMsg = new HashMap<>();
                textMsg.put("sender", senderId);
                textMsg.put("recordName", recordName);
                textMsg.put("msg", downloadRecordAudioPathUrl);
                textMsg.put("msgDuration", msg.getMediaMsgTime());
                textMsg.put("msgTime", getTimeNow());
                textMsg.put("msgType", "record_video");


                //add current user msg
                DatabaseQueries.sendMsg(sendMsg, DB_SEND_RECORD_VIDEO_MSG_USER_ID, textMsg, currentUser.getUid(), friendId);

                //add the msg in other user
                DatabaseQueries.sendMsg(sendMsg, DB_SEND_RECORD_VIDEO_MSG_FRIEND_ID, textMsg, friendId, currentUser.getUid());

            }
        }, fileName);
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
                if (deafChat != null) {
                    if (friendInfo.getUserState().equals("Deaf"))
                        sendRecordVideo(deafChat);
                    else {
                        //convert video to text
                        Toast.makeText(getApplicationContext(), "will be converted", Toast.LENGTH_SHORT).show();
                    }
                }


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
            case DB_SEND_RECORD_VIDEO_MSG_USER_ID:
                if (isSent) {
                    DatabaseQueries.createNewChat(createNewChat, DB_CREATE_NEW_CHAT_ID, currentUser.getUid()
                            , new UserMenuChat(friendId
                                    , friendInfo.getUserDisplayName()
                                    , "record...."
                                    , friendInfo.getUserPhotoPath()
                                    , msg.get("msgTime").toString()));
                }
                break;
            case DB_SEND_RECORD_VIDEO_MSG_FRIEND_ID:
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

    public void updateUi(@NonNull UserPublicInfo friendInfo) {
        Glide
                .with(this)
                .load(friendInfo.getUserPhotoPath())
                .centerCrop()
                .placeholder(R.drawable.user_photo)
                .into(userImage);
        userName.setText(friendInfo.getUserDisplayName());
    }

    @Override
    public void afterCreateNewChat(int id) {
        //something here
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
                        insertItemToAdapter(new DeafChat(sender, msg, msgTime));
                    } else if (msgType != null && msgType.equals("record_video")) {
                        String msgDuration = (String) currentMsg.get("msgDuration");
                        String recordName = (String) currentMsg.get("recordName");
                        DatabaseQueries.downloadRecordFromUrl(new DatabaseQueries.DownloadRecordFromUrl() {
                            @Override
                            public void afterDownloadRecordFromUrl(String recordPath) {
                                insertItemToAdapter(new DeafChat(sender, recordPath, msgDuration, msgTime));
                            }
                        }, "video", msg, recordName);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void pressKey(int id) {
        switch (id) {
            case R.id.key_a:
                textSend.append("a");
                break;
            case R.id.key_b:
                textSend.append("b");
                break;
            case R.id.key_space:
                textSend.append(" ");
                break;
            case R.id.key_backspace:
                Log.v(TAG, "back space");
                int length = textSend.getText().length();
                if (length > 0) {
                    textSend.getText().delete(length - 1, length);
                }
                break;
            default:
                break;
        }

    }
}


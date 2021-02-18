package com.example.graduationproject.models;

import com.example.graduationproject.adapters.NormalMessageAdapter;

public class NormalChat {
    public static final int MSG_TEXT_TYPE = 10;
    public static final int MSG_RECORD_TYPE = 20;

    private String sender;
    private String message;
    private String time;
    private String mediaMsgPath;
    private String mediaMsgTime;
    private int msgType;

    public NormalChat(String sender, String message, String time) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.msgType = MSG_TEXT_TYPE;
    }

    public NormalChat(String sender, String mediaMsgPath, String mediaMsgTime, String time) {
        this.sender = sender;
        this.time = time;
        this.mediaMsgPath = mediaMsgPath;
        this.mediaMsgTime = mediaMsgTime;
        this.msgType = MSG_RECORD_TYPE;
    }

    public String getMediaMsgTime() {
        return mediaMsgTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getMediaMsgPath() {
        return mediaMsgPath;
    }
}

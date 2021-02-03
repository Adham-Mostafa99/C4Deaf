package com.example.graduationproject.models;

import com.example.graduationproject.adapters.DeafMessageAdapter;
import com.example.graduationproject.adapters.NormalMessageAdapter;

public class DeafChat {
    private String sender;
    private String receiver;
    private String message;
    private String time;
    private String mediaMsgPath;
    private String mediaMsgTime;
    private int msgType;

    public DeafChat(String sender, String receiver, String message, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.msgType = DeafMessageAdapter.MSG_TYPE_SENDER_TEXT;
    }

    public DeafChat(String sender, String receiver, String mediaMsgPath, String mediaMsgTime, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.mediaMsgPath = mediaMsgPath;
        this.mediaMsgTime = mediaMsgTime;
        this.msgType = DeafMessageAdapter.MSG_TYPE_SENDER_VIDEO;
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

    public String getReceiver() {
        return receiver;
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

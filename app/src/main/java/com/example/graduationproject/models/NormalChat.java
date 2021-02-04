package com.example.graduationproject.models;

import com.example.graduationproject.adapters.NormalMessageAdapter;

public class NormalChat {
    private String sender;
    private String receiver;
    private String message;
    private String time;
    private String mediaMsgPath;
    private String mediaMsgTime;
    private int msgType;

    public NormalChat(String sender, String receiver, String message, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.msgType = NormalMessageAdapter.MSG_TYPE_SENDER_TEXT;
    }

    public NormalChat(String sender, String receiver, String mediaMsgPath, String mediaMsgTime, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.mediaMsgPath = mediaMsgPath;
        this.mediaMsgTime = mediaMsgTime;
        this.msgType = NormalMessageAdapter.MSG_TYPE_SENDER_RECORD;
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

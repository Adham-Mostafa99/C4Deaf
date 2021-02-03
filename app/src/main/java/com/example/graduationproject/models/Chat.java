package com.example.graduationproject.models;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String time;
    private int mediaMsg;

    public Chat(String sender, String receiver, String message, String time, int recordMsg) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.mediaMsg = recordMsg;
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

    public int getMediaMsg() {
        return mediaMsg;
    }
}

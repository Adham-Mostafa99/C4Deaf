package com.example.graduationproject.models;

public class User {
    private String userId;
    private String userName;
    private String userMessage;
    private String userImageUrl;
    private String messageTime;

    public User() {}

    public User(String userName, String userMessage, String userImageUrl, String messageTime) {
        this.userName = userName;
        this.userMessage = userMessage;
        this.userImageUrl = userImageUrl;
        this.messageTime=messageTime;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) {this.userId = userId;}

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getMessageTime() {
        return messageTime;
    }
}

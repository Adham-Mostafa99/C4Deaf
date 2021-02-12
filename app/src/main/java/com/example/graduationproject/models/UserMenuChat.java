package com.example.graduationproject.models;

public class UserMenuChat {
    private String userId;
    private String userName;
    private String userMessage;
    private String userPhotoUrl;
    private String messageTime;

    public UserMenuChat() {
        //for documents.toObject()
    }

    public UserMenuChat(String userId, String userName, String userMessage, String userPhotoUrl, String messageTime) {
        this.userId = userId;
        this.userName = userName;
        this.userMessage = userMessage;
        this.userPhotoUrl = userPhotoUrl;
        this.messageTime = messageTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public String getMessageTime() {
        return messageTime;
    }

    @Override
    public String toString() {
        return "UserMenuChat{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", userImageUrl='" + userPhotoUrl + '\'' +
                ", messageTime='" + messageTime + '\'' +
                '}';
    }
}

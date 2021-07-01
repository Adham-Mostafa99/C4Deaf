package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserPublicInfo implements Parcelable {
    private String userId;
    private String userFirstName;
    private String userLastName;
    private String userDisplayName;
    private String userState;
    private String userGender;
    private String userPhotoPath;

    public UserPublicInfo() {
        //documents.toObject
    }

    public UserPublicInfo(String userId, String userFirstName, String userLastName, String userDisplayName, String userState, String userGender, String userPhotoPath) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userDisplayName = userDisplayName;
        this.userState = userState;
        this.userGender = userGender;
        this.userPhotoPath = userPhotoPath;
    }

    protected UserPublicInfo(Parcel in) {
        userId = in.readString();
        userFirstName = in.readString();
        userLastName = in.readString();
        userDisplayName = in.readString();
        userState = in.readString();
        userGender = in.readString();
        userPhotoPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userFirstName);
        dest.writeString(userLastName);
        dest.writeString(userDisplayName);
        dest.writeString(userState);
        dest.writeString(userGender);
        dest.writeString(userPhotoPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserPublicInfo> CREATOR = new Creator<UserPublicInfo>() {
        @Override
        public UserPublicInfo createFromParcel(Parcel in) {
            return new UserPublicInfo(in);
        }

        @Override
        public UserPublicInfo[] newArray(int size) {
            return new UserPublicInfo[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getUserState() {
        return userState;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserPhotoPath() {
        return userPhotoPath;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public void setUserPhotoPath(String userPhotoPath) {
        this.userPhotoPath = userPhotoPath;
    }

    @Override
    public String toString() {
        return "UserPublicInfo{" +
                "userId='" + userId + '\'' +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", userState='" + userState + '\'' +
                ", userGender='" + userGender + '\'' +
                ", userPhotoUrl='" + userPhotoPath + '\'' +
                '}';
    }
}

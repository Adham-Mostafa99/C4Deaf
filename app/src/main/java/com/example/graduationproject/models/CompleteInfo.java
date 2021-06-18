package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CompleteInfo implements Parcelable {
    private String userId;
    private String userEmail;
    private String userName;
    private String userPhotoUrl;
    private String userPhone;

    public CompleteInfo() {
    }

    public CompleteInfo(String userId, String userEmail, String userName, String userPhotoUrl, String userPhone) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.userPhone = userPhone;
    }

    protected CompleteInfo(Parcel in) {
        userId = in.readString();
        userEmail = in.readString();
        userName = in.readString();
        userPhotoUrl = in.readString();
        userPhone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userEmail);
        dest.writeString(userName);
        dest.writeString(userPhotoUrl);
        dest.writeString(userPhone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CompleteInfo> CREATOR = new Creator<CompleteInfo>() {
        @Override
        public CompleteInfo createFromParcel(Parcel in) {
            return new CompleteInfo(in);
        }

        @Override
        public CompleteInfo[] newArray(int size) {
            return new CompleteInfo[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public String getUserPhone() {
        return userPhone;
    }
}

package com.example.graduationproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserPrivateInfo implements Parcelable {
    private String userEmail;
    private String userPassword;
    private String userPhone;
    private String userDate;

    public UserPrivateInfo() {
        //documents.toObject
    }

    public UserPrivateInfo(String userEmail, String userPassword, String userPhone, String userDate) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.userDate = userDate;
    }

    protected UserPrivateInfo(@NonNull Parcel in) {
        userEmail = in.readString();
        userPassword = in.readString();
        userPhone = in.readString();
        userDate = in.readString();
    }

    public static final Creator<UserPrivateInfo> CREATOR = new Creator<UserPrivateInfo>() {
        @Override
        public UserPrivateInfo createFromParcel(Parcel in) {
            return new UserPrivateInfo(in);
        }

        @Override
        public UserPrivateInfo[] newArray(int size) {
            return new UserPrivateInfo[size];
        }
    };

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserDate() {
        return userDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userEmail);
        dest.writeString(userPassword);
        dest.writeString(userPhone);
        dest.writeString(userDate);
    }

    @Override
    public String toString() {
        return "UserPrivateInfo{" +
                "userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userDate='" + userDate + '\'' +
                '}';
    }
}

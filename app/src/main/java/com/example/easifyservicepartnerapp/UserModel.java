package com.example.easifyservicepartnerapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserModel implements Parcelable {

    String fullName;
    String email;
    String phoneNo;
    String status;

    public UserModel(String fullName, String email, String phoneNo, String status) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.status = status;
    }

    public UserModel(String fullName, String email, String phoneNo) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    protected UserModel(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        phoneNo = in.readString();
        status = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {

        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeString(phoneNo);
        parcel.writeString(status);

    }
}

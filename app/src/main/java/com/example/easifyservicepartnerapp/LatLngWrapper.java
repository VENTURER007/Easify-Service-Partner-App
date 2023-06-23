package com.example.easifyservicepartnerapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LatLngWrapper implements Parcelable {
    double latitude;
    double longitude;

    public LatLngWrapper() {
        // Default constructor required for Firebase deserialization
    }

    public LatLngWrapper(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected LatLngWrapper(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<LatLngWrapper> CREATOR = new Creator<LatLngWrapper>() {
        @Override
        public LatLngWrapper createFromParcel(Parcel in) {
            return new LatLngWrapper(in);
        }

        @Override
        public LatLngWrapper[] newArray(int size) {
            return new LatLngWrapper[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}


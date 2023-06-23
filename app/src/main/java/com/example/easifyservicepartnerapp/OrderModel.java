package com.example.easifyservicepartnerapp;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class OrderModel implements Parcelable {
    String order_id;
    String user_id;
    String time_stamp;
    LatLngWrapper location;
    String landmark;
    String service_partner_id;


    String status;


    protected OrderModel(Parcel in) {
        order_id = in.readString();
        user_id = in.readString();
        time_stamp = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
        landmark = in.readString();
        service_partner_id = in.readString();
        status = in.readString();
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public OrderModel(String serviceKey, String user_id, String currentDateAndTime, LatLngWrapper location, String landmark, String service_partner_id, String status) {
        this.order_id = serviceKey;
        this.user_id = user_id;
        this.time_stamp = currentDateAndTime;
        this.location = location;
        this.landmark = landmark;
        this.service_partner_id = service_partner_id;
        this.status = status;
    }

    public OrderModel() {
        // Default constructor required for Firebase deserialization
    }





    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }



    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getService_partner_id() {
        return service_partner_id;
    }

    public void setService_partner_id(String service_partner_id) {
        this.service_partner_id = service_partner_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public LatLngWrapper getLocation() {
        return location;
    }

    public void setLocation(LatLngWrapper location) {
        this.location = location;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(order_id);
        parcel.writeString(user_id);
        parcel.writeString(time_stamp);
        parcel.writeParcelable(location,flags);
        parcel.writeString(landmark);
        parcel.writeString(status);
        parcel.writeString(service_partner_id);
    }
}

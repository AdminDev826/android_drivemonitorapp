package com.lineztech.farhan.vehicaltarckingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Farhan on 7/26/2016.
 */
public class Cars implements Parcelable {
    public Cars(Parcel source) {

        carName = source.readString();
        trackerID = source.readString();
        trackerID = source.readString();

    }

    public Cars() {

    }


    public String getCarLogo() {
        return carLogo;
    }

    public void setCarLogo(String carLogo) {
        this.carLogo = carLogo;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getTrackerID() {
        return trackerID;
    }

    public void setTrackerID(String trackerID) {
        this.trackerID = trackerID;
    }

    private  String carName;
    private  String carLogo;
    private String trackerID;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(carName);
        dest.writeString(trackerID);
        dest.writeString(trackerID);


    }
    public static final Parcelable.Creator<Cars> CREATOR
            = new Parcelable.Creator<Cars>() {
        public Cars createFromParcel(Parcel in) {
            return new Cars(in);
        }

        public Cars[] newArray(int size) {
            return new Cars[size];
        }
    };
}

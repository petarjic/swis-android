package com.swis.android.model.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Latlng implements Parcelable {
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("latitude")
    private double latitude;

    protected Latlng(Parcel in) {
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Latlng> CREATOR = new Creator<Latlng>() {
        @Override
        public Latlng createFromParcel(Parcel in) {
            return new Latlng(in);
        }

        @Override
        public Latlng[] newArray(int size) {
            return new Latlng[size];
        }
    };

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

package com.swis.android.model.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Place implements Parcelable {


    @SerializedName("detail_url")
    private String detail_url;
    @SerializedName("address")
    private Address address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("latlng")
    private Latlng latlng;
    @SerializedName("website")
    private String website;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;


    protected Place(Parcel in) {
        detail_url = in.readString();
        phone = in.readString();
        website = in.readString();
        name = in.readString();
        id = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(detail_url);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeString(name);
        dest.writeString(id);
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Latlng getLatlng() {
        return latlng;
    }

    public void setLatlng(Latlng latlng) {
        this.latlng = latlng;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

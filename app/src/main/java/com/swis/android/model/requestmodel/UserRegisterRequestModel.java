package com.swis.android.model.requestmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserRegisterRequestModel implements Parcelable {

    @SerializedName("name")
    private String name ;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("device_type")
    private String device_type;

    @SerializedName("device_token")
    private String device_token;

    @SerializedName("device_id")
    private String device_id;

    public UserRegisterRequestModel() {
    }

    protected UserRegisterRequestModel(Parcel in) {
        name = in.readString();
        username = in.readString();
        email = in.readString();
        password = in.readString();
        device_type = in.readString();
        device_token = in.readString();
        device_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(device_type);
        dest.writeString(device_token);
        dest.writeString(device_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserRegisterRequestModel> CREATOR = new Creator<UserRegisterRequestModel>() {
        @Override
        public UserRegisterRequestModel createFromParcel(Parcel in) {
            return new UserRegisterRequestModel(in);
        }

        @Override
        public UserRegisterRequestModel[] newArray(int size) {
            return new UserRegisterRequestModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}

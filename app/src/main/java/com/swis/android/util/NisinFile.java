package com.swis.android.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NisinFile implements Parcelable {
    @SerializedName("localPath") private String photoPath;
    @SerializedName("remote_path") private String remote_path;
    @SerializedName("upload_status") private int upload_status = -1;
    @SerializedName("fileIdentifier") private String photoIdentifier;
    @SerializedName("media_type") private String media_type;


    public NisinFile(String photoPath, String media_type) {
        this.photoPath = photoPath;
        this.media_type = media_type;
    }

    protected NisinFile(Parcel in) {
        photoPath = in.readString();
        remote_path = in.readString();
        upload_status = in.readInt();
        photoIdentifier = in.readString();
        media_type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoPath);
        dest.writeString(remote_path);
        dest.writeInt(upload_status);
        dest.writeString(photoIdentifier);
        dest.writeString(media_type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NisinFile> CREATOR = new Creator<NisinFile>() {
        @Override
        public NisinFile createFromParcel(Parcel in) {
            return new NisinFile(in);
        }

        @Override
        public NisinFile[] newArray(int size) {
            return new NisinFile[size];
        }
    };

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getRemote_path() {
        return remote_path;
    }

    public void setRemote_path(String remote_path) {
        this.remote_path = remote_path;
    }

    public int getUpload_status() {
        return upload_status;
    }

    public void setUpload_status(int upload_status) {
        this.upload_status = upload_status;
    }

    public String getPhotoIdentifier() {
        return photoIdentifier;
    }

    public void setPhotoIdentifier(String photoIdentifier) {
        this.photoIdentifier = photoIdentifier;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }
}
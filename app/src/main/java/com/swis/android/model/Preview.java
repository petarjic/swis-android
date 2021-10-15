package com.swis.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Preview implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("time")
    public long time;
    @SerializedName("filePath")
    public String filePath;
    @SerializedName("searchType")
    public String searchType;
    @SerializedName("isLink")
    public boolean isLink;


    public Preview() {

    }


    protected Preview(Parcel in) {
        id = in.readInt();
        title = in.readString();
        time = in.readLong();
        filePath = in.readString();
        searchType = in.readString();
        isLink = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeLong(time);
        dest.writeString(filePath);
        dest.writeString(searchType);
        dest.writeByte((byte) (isLink ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Preview> CREATOR = new Creator<Preview>() {
        @Override
        public Preview createFromParcel(Parcel in) {
            return new Preview(in);
        }

        @Override
        public Preview[] newArray(int size) {
            return new Preview[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public boolean isLink() {
        return isLink;
    }

    public void setLink(boolean link) {
        isLink = link;
    }
}

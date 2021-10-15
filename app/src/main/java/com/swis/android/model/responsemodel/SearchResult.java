package com.swis.android.model.responsemodel;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SearchResult implements Parcelable {


    @SerializedName("provider")
    private String provider;
    @SerializedName("provider_icon")
    private String provider_icon;
    @SerializedName("type")
    private String type;
    @SerializedName("image")
    private String image;
    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;
    @SerializedName("description")
    private String description;
    @SerializedName("website")
    private String website;
    @SerializedName("datetime")
    private String datetime;
    @SerializedName("video_duration")
    private String video_duration;
    @SerializedName("title")
    private String title;
    @SerializedName("id")
    private String id;

    @SerializedName("bundle")
    private Bundle bundle;


    public SearchResult(Parcel in) {
        provider = in.readString();
        provider_icon = in.readString();
        type = in.readString();
        image = in.readString();
        thumbnailUrl = in.readString();
        description = in.readString();
        website = in.readString();
        video_duration = in.readString();
        datetime = in.readString();
        title = in.readString();
        id = in.readString();
        bundle = in.readBundle();
    }

    public SearchResult() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(provider);
        dest.writeString(provider_icon);
        dest.writeString(type);
        dest.writeString(image);
        dest.writeString(thumbnailUrl);
        dest.writeString(video_duration);
        dest.writeString(description);
        dest.writeString(website);
        dest.writeString(datetime);
        dest.writeString(title);
        dest.writeString(id);
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProvider_icon() {
        return provider_icon;
    }

    public void setProvider_icon(String provider_icon) {
        this.provider_icon = provider_icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }


    public String getVideo_duration() {
        return video_duration;
    }

    public void setVideo_duration(String video_duration) {
        this.video_duration = video_duration;
    }
}

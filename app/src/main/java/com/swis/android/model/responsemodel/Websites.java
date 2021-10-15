package com.swis.android.model.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Websites implements Parcelable {

    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("type")
    private String type;
    @SerializedName("bing_id")
    private String bing_id;
    @SerializedName("description")
    private String description;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("website")
    private String website;
    @SerializedName("post_id")
    private int post_id;
    @SerializedName("id")
    private int id;
    @SerializedName("search_term")
    private String search_term;


    protected Websites(Parcel in) {
        updated_at = in.readString();
        created_at = in.readString();
        type = in.readString();
        bing_id = in.readString();
        description = in.readString();
        title = in.readString();
        image = in.readString();
        website = in.readString();
        post_id = in.readInt();
        id = in.readInt();
        search_term = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updated_at);
        dest.writeString(created_at);
        dest.writeString(type);
        dest.writeString(bing_id);
        dest.writeString(description);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(website);
        dest.writeInt(post_id);
        dest.writeInt(id);
        dest.writeString(search_term);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Websites> CREATOR = new Creator<Websites>() {
        @Override
        public Websites createFromParcel(Parcel in) {
            return new Websites(in);
        }

        @Override
        public Websites[] newArray(int size) {
            return new Websites[size];
        }
    };

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBing_id() {
        return bing_id;
    }

    public void setBing_id(String bing_id) {
        this.bing_id = bing_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearch_term() {
        return search_term;
    }

    public void setSearch_term(String search_term) {
        this.search_term = search_term;
    }
}

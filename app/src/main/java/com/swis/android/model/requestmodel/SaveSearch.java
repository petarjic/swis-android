package com.swis.android.model.requestmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SaveSearch implements Parcelable {


    @SerializedName("journey_id")
    private String journey_id;
    @SerializedName("image")
    private String image;
    @SerializedName("type")
    private String type;
    @SerializedName("bing_id")
    private String bing_id;
    @SerializedName("query")
    private String query;
    @SerializedName("description")
    private String description;
    @SerializedName("title")
    private String title;
    @SerializedName("website")
    private String website;

    public SaveSearch(Parcel in) {
        journey_id = in.readString();
        image = in.readString();
        type = in.readString();
        bing_id = in.readString();
        query = in.readString();
        description = in.readString();
        title = in.readString();
        website = in.readString();
    }

    public SaveSearch() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(journey_id);
        dest.writeString(image);
        dest.writeString(type);
        dest.writeString(bing_id);
        dest.writeString(query);
        dest.writeString(description);
        dest.writeString(title);
        dest.writeString(website);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SaveSearch> CREATOR = new Creator<SaveSearch>() {
        @Override
        public SaveSearch createFromParcel(Parcel in) {
            return new SaveSearch(in);
        }

        @Override
        public SaveSearch[] newArray(int size) {
            return new SaveSearch[size];
        }
    };

    public String getJourney_id() {
        return journey_id;
    }

    public void setJourney_id(String journey_id) {
        this.journey_id = journey_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}

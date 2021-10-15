package com.swis.android.model.responsemodel;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.google.gson.annotations.SerializedName;

public class UserInfo implements Parcelable {

    @SerializedName("followed")
    private boolean followed;
    @SerializedName("auto_accept")
    private boolean auto_accept;
    @SerializedName("follow_request_count")
    private String follow_request_count;
    @SerializedName("searches_count")
    private String searches_count;
    @SerializedName("followings_count")
    private String followings_count;
    @SerializedName("followers_count")
    private String followers_count;
    @SerializedName("api_token")
    private String api_token;
    @SerializedName("hide_searches")
    private boolean hide_searches;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("share_local_search")
    private boolean share_local_search;
    @SerializedName("text_color")
    private String text_color;
    @SerializedName("share_location")
    private boolean share_location;
    @SerializedName("notification_follow")
    private boolean notification_follow;
    @SerializedName("notification_comment")
    private boolean notification_comment;
    @SerializedName("notification_like")
    private boolean notification_like;
    @SerializedName("hide_favourite")
    private boolean hide_favourite;
    @SerializedName("hide_searched")
    private boolean hide_searched;
    @SerializedName("auto_select")
    private String auto_select;
    @SerializedName("background_url")
    private String background_url;
    @SerializedName("otp")
    private String otp;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("zip")
    private String zip;
    @SerializedName("country")
    private String country;
    @SerializedName("city")
    private String city;
    @SerializedName("address")
    private String address;
    @SerializedName("gender")
    private String gender;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("bio")
    private String bio;
    @SerializedName("dob")
    private String dob;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("isSelected")
    private boolean isSelected;

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        followed = in.readByte() != 0;
        auto_accept = in.readByte() != 0;
        follow_request_count = in.readString();
        searches_count = in.readString();
        followings_count = in.readString();
        followers_count = in.readString();
        api_token = in.readString();
        hide_searches = in.readByte() != 0;
        longitude = in.readDouble();
        latitude = in.readDouble();
        share_local_search = in.readByte() != 0;
        text_color = in.readString();
        share_location = in.readByte() != 0;
        notification_follow = in.readByte() != 0;
        notification_comment = in.readByte() != 0;
        notification_like = in.readByte() != 0;
        hide_favourite = in.readByte() != 0;
        hide_searched = in.readByte() != 0;
        auto_select = in.readString();
        background_url = in.readString();
        otp = in.readString();
        created_at = in.readString();
        zip = in.readString();
        country = in.readString();
        city = in.readString();
        address = in.readString();
        gender = in.readString();
        avatar = in.readString();
        bio = in.readString();
        dob = in.readString();
        phone = in.readString();
        email = in.readString();
        username = in.readString();
        name = in.readString();
        id = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (followed ? 1 : 0));
        dest.writeByte((byte) (auto_accept ? 1 : 0));
        dest.writeString(follow_request_count);
        dest.writeString(searches_count);
        dest.writeString(followings_count);
        dest.writeString(followers_count);
        dest.writeString(api_token);
        dest.writeByte((byte) (hide_searches ? 1 : 0));
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeByte((byte) (share_local_search ? 1 : 0));
        dest.writeString(text_color);
        dest.writeByte((byte) (share_location ? 1 : 0));
        dest.writeByte((byte) (notification_follow ? 1 : 0));
        dest.writeByte((byte) (notification_comment ? 1 : 0));
        dest.writeByte((byte) (notification_like ? 1 : 0));
        dest.writeByte((byte) (hide_favourite ? 1 : 0));
        dest.writeByte((byte) (hide_searched ? 1 : 0));
        dest.writeString(auto_select);
        dest.writeString(background_url);
        dest.writeString(otp);
        dest.writeString(created_at);
        dest.writeString(zip);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(address);
        dest.writeString(gender);
        dest.writeString(avatar);
        dest.writeString(bio);
        dest.writeString(dob);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }


    public boolean isAuto_accept() {
        return auto_accept;
    }

    public void setAuto_accept(boolean auto_accept) {
        this.auto_accept = auto_accept;
    }

    public String getFollow_request_count() {
        return follow_request_count;
    }

    public void setFollow_request_count(String follow_request_count) {
        this.follow_request_count = follow_request_count;
    }

    public String getSearches_count() {
        return searches_count;
    }

    public void setSearches_count(String searches_count) {
        this.searches_count = searches_count;
    }

    public String getFollowings_count() {
        return followings_count;
    }

    public void setFollowings_count(String followings_count) {
        this.followings_count = followings_count;
    }

    public String getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(String followers_count) {
        this.followers_count = followers_count;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }

    public boolean isHide_searches() {
        return hide_searches;
    }

    public void setHide_searches(boolean hide_searches) {
        this.hide_searches = hide_searches;
    }

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

    public boolean isShare_local_search() {
        return share_local_search;
    }

    public void setShare_local_search(boolean share_local_search) {
        this.share_local_search = share_local_search;
    }

    public String getText_color() {
        return text_color;
    }

    public void setText_color(String text_color) {
        this.text_color = text_color;
    }

    public boolean isShare_location() {
        return share_location;
    }

    public void setShare_location(boolean share_location) {
        this.share_location = share_location;
    }

    public boolean isNotification_follow() {
        return notification_follow;
    }

    public void setNotification_follow(boolean notification_follow) {
        this.notification_follow = notification_follow;
    }

    public boolean isNotification_comment() {
        return notification_comment;
    }

    public void setNotification_comment(boolean notification_comment) {
        this.notification_comment = notification_comment;
    }

    public boolean isNotification_like() {
        return notification_like;
    }

    public void setNotification_like(boolean notification_like) {
        this.notification_like = notification_like;
    }

    public boolean isHide_favourite() {
        return hide_favourite;
    }

    public void setHide_favourite(boolean hide_favourite) {
        this.hide_favourite = hide_favourite;
    }

    public boolean isHide_searched() {
        return hide_searched;
    }

    public void setHide_searched(boolean hide_searched) {
        this.hide_searched = hide_searched;
    }

    public String getAuto_select() {
        return auto_select;
    }

    public void setAuto_select(String auto_select) {
        this.auto_select = auto_select;
    }

    public String getBackground_url() {
        return background_url;
    }

    public void setBackground_url(String background_url) {
        this.background_url = background_url;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @BindingAdapter({"color"})
    public static void setFont(TextView textView, String text_color) {
        try {
            textView.setTextColor(Color.parseColor(text_color));
        } catch (Exception e) {
            textView.setTextColor(Color.parseColor("#ffffff"));

        }
    }

    @BindingAdapter({"tint_color"})
    public static void setTStringColor(ImageView imageView, String text_color) {
        try {
            imageView.setColorFilter(Color.parseColor(text_color));
        } catch (Exception e) {
            imageView.setColorFilter(Color.parseColor("#ffffff"));

        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

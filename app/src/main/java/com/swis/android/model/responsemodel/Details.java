package com.swis.android.model.responsemodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Details implements Serializable {

    @SerializedName("status")
    private String status;
    @SerializedName("following_id")
    private int following_id;
    @SerializedName("follower_id")
    private int follower_id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFollowing_id() {
        return following_id;
    }

    public void setFollowing_id(int following_id) {
        this.following_id = following_id;
    }

    public int getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(int follower_id) {
        this.follower_id = follower_id;
    }
}

package com.swis.android.util;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SwisNotification implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("type")
    private int type;
    @SerializedName("main_post_id")
    private String main_post_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMain_post_id() {
        return main_post_id;
    }

    public void setMain_post_id(String main_post_id) {
        this.main_post_id = main_post_id;
    }
}

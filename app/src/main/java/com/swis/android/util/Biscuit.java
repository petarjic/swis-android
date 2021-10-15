package com.swis.android.util;

import com.google.gson.annotations.SerializedName;

public class Biscuit {
    @SerializedName("id") String id;
    @SerializedName("name") String name;

    public Biscuit() {
    }

    public Biscuit(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

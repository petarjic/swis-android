/*
package com.nisintechnologies.swiss.model.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Comments implements Parcelable {



    protected Comments(Parcel in) {
        like = in.readByte() != 0;
        replies_count = in.readInt();
        likes_count = in.readInt();
        delete = in.readInt();
        updated_at = in.readString();
        created_at = in.readString();
        comment = in.readString();
        parent_post_id = in.readInt();
        user_id = in.readInt();
        id = in.readInt();
        user = in.readParcelable(UserInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (like ? 1 : 0));
        dest.writeInt(replies_count);
        dest.writeInt(likes_count);
        dest.writeInt(delete);
        dest.writeString(updated_at);
        dest.writeString(created_at);
        dest.writeString(comment);
        dest.writeInt(parent_post_id);
        dest.writeInt(user_id);
        dest.writeInt(id);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comments> CREATOR = new Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel in) {
            return new Comments(in);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getReplies_count() {
        return replies_count;
    }

    public void setReplies_count(int replies_count) {
        this.replies_count = replies_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getParent_post_id() {
        return parent_post_id;
    }

    public void setParent_post_id(int parent_post_id) {
        this.parent_post_id = parent_post_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
*/

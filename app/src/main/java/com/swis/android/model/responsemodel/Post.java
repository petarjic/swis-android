package com.swis.android.model.responsemodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Post implements Parcelable {

    @SerializedName("like")
    private boolean like;
    @SerializedName("replies_count")
    private int replies_count;
    @SerializedName("likes_count")
    private int likes_count;
    @SerializedName("delete")
    private int delete;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("comment")
    private String comment;
    @SerializedName("parent_post_id")
    private int parent_post_id;
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("id")
    private int id;
    @SerializedName("sender_id")
    private int sender_id;
    @SerializedName("user")
    private UserInfo user;

    @SerializedName("favourite")
    private boolean favourite;

    @SerializedName("commentsVisibility")
    private boolean commentsVisibility;


    @SerializedName("comment_count")
    private int comment_count;

    @SerializedName("visibility_count")
    private int visibility_count;


    @SerializedName("like_count")
    private int like_count;

    @SerializedName("search_term")
    private String search_term;

    @SerializedName("websites")
    private ArrayList<Websites> websites;

    @SerializedName("comments")
    private ArrayList<Post> comments;


    protected Post(Parcel in) {
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
        sender_id = in.readInt();
        user = in.readParcelable(UserInfo.class.getClassLoader());
        favourite = in.readByte() != 0;
        commentsVisibility = in.readByte() != 0;
        comment_count = in.readInt();
        visibility_count = in.readInt();
        like_count = in.readInt();
        search_term = in.readString();
        websites = in.createTypedArrayList(Websites.CREATOR);
        comments = in.createTypedArrayList(Post.CREATOR);
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
        dest.writeInt(sender_id);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (favourite ? 1 : 0));
        dest.writeByte((byte) (commentsVisibility ? 1 : 0));
        dest.writeInt(comment_count);
        dest.writeInt(visibility_count);
        dest.writeInt(like_count);
        dest.writeString(search_term);
        dest.writeTypedList(websites);
        dest.writeTypedList(comments);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
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

    public String getSearch_term() {
        return search_term;
    }

    public void setSearch_term(String search_term) {
        this.search_term = search_term;
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

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public ArrayList<Websites> getWebsites() {
        return websites;
    }

    public void setWebsites(ArrayList<Websites> websites) {
        this.websites = websites;
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

    public ArrayList<Post> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Post> comments) {
        this.comments = comments;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isCommentsVisibility() {
        return commentsVisibility;
    }

    public void setCommentsVisibility(boolean commentsVisibility) {
        this.commentsVisibility = commentsVisibility;
    }

    public int getVisibility_count() {
        return visibility_count;
    }

    public void setVisibility_count(int visibility_count) {
        this.visibility_count = visibility_count;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }
}

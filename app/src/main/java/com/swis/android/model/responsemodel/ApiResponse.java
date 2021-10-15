package com.swis.android.model.responsemodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiResponse implements Serializable {

    @SerializedName("responseCode")
    private int responseCode;


    @SerializedName("nextPage")
    private int nextPage;

    @SerializedName("responseMessage")
    private String responseMessage;

    @SerializedName("searchResults")
    private ArrayList<SearchResult> searchResults;

    @SerializedName("videos")
    private ArrayList<SearchResult> videos;

    @SerializedName("searchType")
    private String searchType;

    @SerializedName("searchQuery")
    private String searchQuery;

    @SerializedName("webSearchUrl")
    private String webSearchUrl;

    @SerializedName("nextOffset")
    private int nextOffset;

    @SerializedName("count")
    private String count;

    @SerializedName("data")
    private String data;



    @SerializedName("user")
    private UserInfo user;

    @SerializedName("followers")
    private ArrayList<UserInfo> followers;

    @SerializedName("followings")
    private ArrayList<UserInfo> followings;

    @SerializedName("users")
    private ArrayList<UserInfo> users;

    @SerializedName("details")
    private ArrayList<Details> details;

    @SerializedName("posts")
    private ArrayList<Post> posts;

    @SerializedName("post")
    private Post post;

    @SerializedName("comments")
    private ArrayList<Post> comments;

    @SerializedName("favourites")
    private ArrayList<Post> favourites;

    @SerializedName("places")
    private ArrayList<Place> places;


    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public ArrayList<UserInfo> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<UserInfo> followers) {
        this.followers = followers;
    }

    public ArrayList<UserInfo> getFollowings() {
        return followings;
    }

    public void setFollowings(ArrayList<UserInfo> followings) {
        this.followings = followings;
    }

    public ArrayList<Details> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Details> details) {
        this.details = details;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserInfo> users) {
        this.users = users;
    }

    public ArrayList<Post> getFavourites() {
        return favourites;
    }

    public void setFavourites(ArrayList<Post> favourites) {
        this.favourites = favourites;
    }

    public ArrayList<Post> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Post> comments) {
        this.comments = comments;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }


    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public int getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getWebSearchUrl() {
        return webSearchUrl;
    }

    public void setWebSearchUrl(String webSearchUrl) {
        this.webSearchUrl = webSearchUrl;
    }

    public ArrayList<SearchResult> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<SearchResult> videos) {
        this.videos = videos;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }
}

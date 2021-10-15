package com.swis.android.ApiService


import com.swis.android.model.requestmodel.*
import com.swis.android.model.responsemodel.*
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.*
import kotlin.collections.HashMap

interface ApiInterface {

    @POST("register")
    fun registerUser(@Body user: UserRegisterRequestModel): Call<UserRegisterResponseModel>

    @FormUrlEncoded
    @POST("verify/username")
    fun verifyUserName(@Field("username") username: String): Call<CommonApiResponse>

    @FormUrlEncoded
    @POST("verify/email")
    fun verifyEmail(@Field("email") email: String): Call<CommonApiResponse>


    @FormUrlEncoded
    @POST("otp/send")
    fun sendOtp(@Field("phone") phone: String, @Field("old_user") old_user: String): Call<SendOtpApiResponse>

    @FormUrlEncoded
    @POST("verify/phone")
    fun verifyPhone(@Field("phone") phone: String): Call<CommonApiResponse>

    @FormUrlEncoded
    @POST("otp/verify")
    fun otpVerify(@Field("otp") otp: String, @Field("phone") phone: String): Call<UserRegisterResponseModel>

    @POST("update/profile")
    fun updateProfile(@Body profile: ProfileUpdate): Call<UserRegisterResponseModel>

    @FormUrlEncoded
    @POST("update/profile")
    fun updateProfile(@FieldMap map: Map<String, String>): Call<ApiResponse>

    @Multipart
    @POST("update/avatar")
    fun updateAvatar(@PartMap map: HashMap<String, RequestBody>): Call<ApiResponse>

    @Multipart
    @POST("update/background")
    fun updateBackground(@PartMap map: HashMap<String, RequestBody>): Call<ApiResponse>

    @GET("recommended")
    fun getRecommendedUser(@Query("page") page: Int,@Query("query") query: String): Call<UserInfoResponseModel>

    @GET("pending-request-list")
    fun getPendingRequestUser(@Query("page") page: Int): Call<ApiResponse>

    @POST("login")
    fun loginUser(@Body userRequest: UserLoginRequestModel): Call<ApiResponse>

    @PUT("update-location")
    fun updateLocation(@Body locations: LocationUpdateRequestModel): Call<UserRegisterResponseModel>

    @GET("details")
    fun getUserDetails(@Query("user_id") user_id: String): Call<ApiResponse>

    @GET("username/{user_name}")
    fun getUserDetailsByName(@Path("user_name") user_name: String): Call<ApiResponse>

    @FormUrlEncoded
    @POST("delete-recommended")
    fun deleteRecommended(@Field("opponentId") opponentId: String): Call<ApiResponse>

    @POST("logout")
    fun logout(): Call<CommonApiResponse>


    @FormUrlEncoded
    @POST("support")
    fun reportIssue(@Field("message") message: String): Call<CommonApiResponse>

    @GET("details")
    fun getUserDetails(): Call<ApiResponse>

    @GET("followers")
    fun getFollowers(@Query("page") page: Int, @Query("user_id") user_id: String, @Query("query") query: String): Call<ApiResponse>

    @GET("followings")
    fun getFollowings(@Query("page") page: Int, @Query("user_id") user_id: String, @Query("query") query: String): Call<ApiResponse>

    @GET("settings/{str}")
    fun getPrivacyDetails(@Path("str") str: String): Call<ApiResponse>


    @FormUrlEncoded
    @PUT("approve-request")
    fun approveRequest(@Field("user_id") user_id: Int): Call<ApiResponse>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "decline-request", hasBody = true)
    fun declineRequest(@Field("user_id") user_id: Int): Call<ApiResponse>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "unfollow", hasBody = true)
    fun unfollowUser(@Field("user_id") user_id: String): Call<ApiResponse>

    @FormUrlEncoded
    @POST("password/reset")
    fun resetPassword(@Field("phone") phone: String, @Field("password") password: String): Call<ApiResponse>

    @FormUrlEncoded
    @POST("remove_follower")
    fun removeFollower(@Field("user_id") user_id: String): Call<ApiResponse>


    @FormUrlEncoded
    @POST("follow-request")
    fun sendRequest(@Field("following_id") following_id: String): Call<ApiResponse>

    @GET("followings/feed")
    fun getFollowingsFeed(@Query("page") page: Int): Call<ApiResponse>

    @GET("posts/liked-users")
    fun fetchLikedUsers(@Query("page") page: Int, @Query("post_id") post_id: Int): Call<ApiResponse>

    @GET("fetch-posts")
    fun fetchPost(@Query("page") page: Int, @Query("user_id") user_id: String, @Query("local") local: String): Call<ApiResponse>

    @GET("posts/fetch-favourites")
    fun fetchFavouritesPost(@Query("page") page: Int, @Query("user_id") user_id: String): Call<ApiResponse>

    @GET("posts/fetch-reply")
    fun fetchComments(@Query("page") page: Int, @Query("comment_id") comment_id: String, @Query("post_id") post_id: String): Call<ApiResponse>

    @GET("posts/{id}")
    fun fetchPostById(@Path("id") id: String): Call<ApiResponse>


    @FormUrlEncoded
    @POST("posts/toggle-like")
    fun likePost(@Field("post_id") post_id: String): Call<ApiResponse>

    @FormUrlEncoded
    @POST("posts/favourites")
    fun markFavourite(@Field("post_id") post_id: String): Call<ApiResponse>

    @FormUrlEncoded
    @POST("posts/reply")
    fun sendComment(@Field("main_post_id") main_post_id: String, @Field("post_id") post_id: String, @Field("comment") comment: String, @Field("user_id") userId: String, @Query("commented_post_id") commented_post_id: String): Call<ApiResponse>

    @FormUrlEncoded
    @POST("search/trending")
    fun fetchTrending(@Field("type") type: String, @Field("offset") offset: Int): Call<ApiResponse>

    @FormUrlEncoded
    @POST("search")
    fun fetchResults(@Field("query") query: String,@Field("type") type: String, @Field("offset") offset: Int): Call<ApiResponse>

    @GET("search/businesses")
    fun fetchLocalBusiness(@Query("query") query: String, @Query("page") page: Int, @Query("limit") limit: Int): Call<ApiResponse>

    @POST("save-search")
    fun saveSearch(@Body save:SaveSearch) : Call<ApiResponse>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "posts/delete_post", hasBody = true)
    fun deletePost(@Field("postId") postId:String): Call<ApiResponse>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "posts/delete_website", hasBody = true)
    fun deletePostWebsite(@Field("postId") postId:String,@Field("websiteId") websiteId:String): Call<ApiResponse>

}
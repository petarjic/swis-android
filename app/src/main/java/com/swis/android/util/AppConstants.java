package com.swis.android.util;

import org.jetbrains.annotations.Nullable;

public class AppConstants {
    public static final String EXTRA_DATA = "extra_data";
    public static final String CHANGE_PHONE_NO = "change_phone_no";
    public static final String CHANGE_PAASWORD = "change_password";
    public static final String EXTRA_OBJECT = "extra_object";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String IS_SEARCHED = "is_searched";
    public static final String IS_BOOKMARKED = "is_bookmarked";
    public static final String REFRESH_COMMENT_POST_ID = "refresh_comment_post_id";
    public static final String REFRESH_POST_AT_PARTICULAR_POSITION = "refresh_post_at_particular_position";
    public static final String NAVIGATE_TO_PROFILE = "navigate_to_profile";
    public static final String CLOSE_PREVIOUS_ACTIVITY = "close_previous_activity";
    public static final String REFRESH_POSTS ="refresh_posts" ;
    public static final String PREVIEW_COUNT ="preview_count" ;

    public static final String REFRESH_FOLLOW_REQUEST ="refresh_follow_request" ;
    public static final String IS_NEW_SEARCH = "is_new_search";
    public static final String EXTRA_ID = "extra_id";
    public static final String IS_NEW_INSERT = "is_new";
    public static final String USER_NAME = "user_name";
    public static final String NAVIGATE_TO_LIKE_PAGE = "navigate_to_likepage";
    public static final String EXTRA_COMMENT = "extra_comment";
    public static String SERVER_ERROR = "Server error";
    public static String SOMETHING_WENT_WRONG = "Something went wrong";
    public static String TOKEN = "Token";
    public static String DEVICE_TOKEN = "Device_token";
    public static int CAMERA_CAPTURE_REQUEST = 100;
    public static int PICK_IMAGE_REQUEST = 200;
    public static final String API_TOKEN="api_token";
    public static final String PHONE="phone";
    public static final String BIO="bio";
    public static final String IS_SIGN_UP="is_sign_up";
    public static final String TYPE ="type";
    public static final String ID="id";

    public class MediaType {
        public static final String IMAGE = "image";
    }

    public class FileSourceType {
        public static final int CAMERA = 0;
        public static final int GALLERY = 1;
    }

    public class DateFormat{
        public static final String DD_MM_YYYY = "MM-dd-yyyy";

    }

    public class ResultActivity{
        public static final int PREVIEW_ACTIVITY_RESULT_OK=10;
    }

    public class RequestActivity {
        public static final int CHANGE_MOBILE_REQUEST = 101;
        public static final int FINISH_ACTIVITY = 102;
        public static final int SEARCH_ACTIVITY = 103;
    }

    public class NOTIFICATIONS {
        public static final String ID = "id";
        public static final String BODY = "body";
        public static final String TITLE = "title";
        public static final String TYPE = "type";
        public static final String MAIN_POST_ID = "post_id";
    }
}

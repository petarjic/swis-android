package com.swis.android.util;

import android.app.job.JobInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Size;

import com.google.gson.Gson;
import com.swis.android.R;
import com.swis.android.model.Preview;
import com.swis.android.model.ScreenSize;
import com.swis.android.model.responsemodel.UserInfo;


public class PrefsHelper {

    public static final String BACK_IMG_UPDATED = "updated";
    public static final String USER_INFO = "user_info";
    public static final String PREVIEW_INFO = "preview_info";
    public static final String IS_LOGOUT = "is_logout";
    public static final String IS_LOGIN = "is_login";
    public static final String IS_BIO_REGISTERED = "is_bio_registered";
    public static final String IS_Mobile_REGISTERED = "is_mobile_registered";
    public static final String PRO_IMG_UPDATED = "pro";
    public static final String JOURNEY_ID="journey_id";
    public static final String SCREEN_SIZE="screen_size";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static PrefsHelper instance;

    public static PrefsHelper getPrefsHelper() {
        return instance;
    }


    public PrefsHelper(Context context) {
        instance = this;
        String prefsFile = context.getString(R.string.pref_name);
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).commit();
        }
    }

    public void deleteAllData() {

        String token=getPref(AppConstants.DEVICE_TOKEN);
        String api_key=getPref(AppConstants.API_TOKEN);
        editor.clear().commit();
        savePref(AppConstants.DEVICE_TOKEN,token);
        savePref(AppConstants.API_TOKEN,api_key);
        savePref(PrefsHelper.IS_LOGOUT,true);
    }

    public void savePref(String key, Object value) {
        delete(key);

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-primitive preference");
        }

        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean isPrefExists(String key) {
        return sharedPreferences.contains(key);
    }


    public void clearPref(String key) {
        clearPref(key);
    }
    public long getPreviousAPICallTime(int type) {
        return getPref("API" + type, 0l);
    }

    public void savePreviosAPICallTime(int type, long currentTime) {
        savePref("API" + type, currentTime);
    }


    public void saveUserInfo(UserInfo model) {

        String bookmark = new Gson().toJson(model);

        savePref(PrefsHelper.USER_INFO, bookmark);
    }

    public UserInfo getUserInfo()
    {
        String  userInfo=getPref(PrefsHelper.USER_INFO,"");
        if(userInfo.isEmpty())
            return new UserInfo();
        else
            return new Gson().fromJson(userInfo, UserInfo.class);
    }

    public void savePreviewInfo(Preview model) {

        String bookmark = new Gson().toJson(model);

        savePref(PrefsHelper.PREVIEW_INFO, bookmark);
    }

    public Preview getPreviewInfo()
    {
        String  data=getPref(PrefsHelper.PREVIEW_INFO,"");
        if(data.isEmpty())
            return null;
        else
            return new Gson().fromJson(data, Preview.class);
    }

    public void saveJourneyId(long timestamp)
    {
        savePref(JOURNEY_ID,timestamp);
    }

    public long getJourneyId()
    {
        return Long.parseLong(String.valueOf(getPref(JOURNEY_ID)));
    }

    public void saveScreenSize(ScreenSize screebSize) {
        savePref(SCREEN_SIZE, new Gson().toJson(screebSize));
    }

    public ScreenSize getScreenSize() {
        return new Gson().fromJson(getPref(SCREEN_SIZE, "{}"),ScreenSize.class);
    }

    public void savePosition(int prevPos) {
        savePref("position",prevPos);
    }

    public int getPosition()
    {
        return getPref("position",0);
    }
}

package com.swis.android.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.databinding.ViewDataBinding;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.databinding.ActivitySplashBinding;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.util.AppConstants;
import com.swis.android.util.PrefsHelper;
import com.swis.android.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends MiddleBaseActivity {

    private ActivitySplashBinding binding;
    private int type = 0;
    private String mainPostId,id;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (ActivitySplashBinding) views;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (bundle != null) {
            type = bundle.getInt(AppConstants.NOTIFICATIONS.TYPE);
            mainPostId = bundle.getString(AppConstants.NOTIFICATIONS.MAIN_POST_ID);
            id = bundle.getString(AppConstants.NOTIFICATIONS.ID);
        }


        prefHelper.saveScreenSize(Util.getScreebSize(this));
        setUp();
    }

    private void setUp() {

        if (!prefHelper.getPref(PrefsHelper.IS_LOGIN, false)) {

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    Bundle bundle = new Bundle();
                    MainActivity.Companion.start(mContext, bundle);
                    finish();
                }
                // SignUpActivity.start(mContext, bundle)


            };
            handler.postDelayed(runnable, 2000);
        } else {


                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        getUserDetails();
                        if(bundle == null){
                            bundle = new Bundle();
                        }
                        HomeActivity.start(mContext, bundle);
                        finish();
                    }
                    // SignUpActivity.start(mContext, bundle)


                };
                handler.postDelayed(runnable, 2000);
            }




    }



    private void getUserDetails() {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getUserDetails();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        prefHelper.saveUserInfo(response.body().getUser());

                    } else
                    showToast(response.body().getResponseMessage());
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast(AppConstants.SERVER_ERROR);

            }
        });



    }
}

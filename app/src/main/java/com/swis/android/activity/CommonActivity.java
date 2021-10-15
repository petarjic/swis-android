package com.swis.android.activity;

import androidx.annotation.RequiresApi;
import androidx.databinding.ViewDataBinding;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.databinding.ActivityCommonBinding;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonActivity extends MiddleBaseActivity {

    private ActivityCommonBinding binding;
    private String title,data;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (ActivityCommonBinding) views;
        if (bundle == null) {
            showToast(AppConstants.SOMETHING_WENT_WRONG);
            finish();
        }
        else {
            title = bundle.getString(AppConstants.EXTRA_TITLE);
            data = bundle.getString(AppConstants.EXTRA_DATA);

        }

        getData(data);
    }

    @Override
    protected ActionBarData getScreenActionTitle() {
        return new ActionBarData(R.drawable.back_icon,title);
    }

    @Override
    protected AppActionbarBinding getToolBar() {
        return binding.appBar;
    }

    private void getData(String str) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getPrivacyDetails(str);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        title = bundle.getString(AppConstants.EXTRA_TITLE);
                        final String mimeType = "text/html";
                        final String encoding = "UTF-8";
                        binding.webView.setWebChromeClient(new WebChromeClient());
                        binding.webView.getSettings().setJavaScriptEnabled(true);
                        binding.webView.getSettings().setAllowFileAccess(true);
                        binding.webView.setWebViewClient(new MyWebViewClient());
                        binding.webView.loadDataWithBaseURL("",response.body().getData(),mimeType,encoding,"" );

                    } else
                        showToast(response.body().getResponseMessage());
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideProgressDialog();
                showToast(AppConstants.SERVER_ERROR);

            }
        });
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgressDialog();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgressDialog();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            binding.webView.loadUrl(request);
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            binding.webView.loadUrl(Uri.parse(request.getUrl().toString()).toString());
            return true;
        }
    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, CommonActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}



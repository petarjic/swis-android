package com.swis.android.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.CommonActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.databinding.FragmentCommonBinding;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.util.AppConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonFragment extends BaseFragment {

    private FragmentCommonBinding binding;

    public CommonFragment() {
        // Required empty public constructor
    }




    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (FragmentCommonBinding) views;
        bundle=getArguments();
        if(bundle!=null)
        {
            String str=bundle.getString(AppConstants.EXTRA_DATA);
            getData(str);

        }
    }

    private void getData(String str) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getPrivacyDetails(str);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
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
                dismissProgressDialog();
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
            dismissProgressDialog();
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

package com.swis.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.database.PreviewManager;
import com.swis.android.databinding.ActivityBrowserBinding;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.listeners.PreviewCountListeners;
import com.swis.android.model.Preview;
import com.swis.android.model.requestmodel.SaveSearch;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BrowserActivity extends MiddleBaseActivity implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "tag";
    private String extraUrl, selectedTab;
    private WebView webView;
    private ActivityBrowserBinding binding;
    private Preview preview;
    private boolean isFirstTime,isYoutube;
    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;
    private View mCustomView;
    private final Handler handler = new Handler();
    private String youtubeUrl,webUrl;
    private PreviewCountListeners refreshListeners;

    @Override
    protected ActionBarData getScreenActionTitle() {
        return null;
    }

    @Override
    protected AppActionbarBinding getToolBar() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    @Override
    protected void onViewBinded(final ViewDataBinding views) {
        this.binding = (ActivityBrowserBinding) views;
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        bundle = getIntent().getExtras();
        isFirstTime=true;
        if (bundle != null && bundle.containsKey(AppConstants.EXTRA_OBJECT)) {
            extraUrl = bundle.getString(AppConstants.EXTRA_OBJECT);
            selectedTab = bundle.getString(AppConstants.EXTRA_TITLE, "");
            try {
                URL aURL = new URL(extraUrl);
                webUrl=aURL.getAuthority();
                webUrl=webUrl.replace("www.","").replace("m.","");
                String[] path=extraUrl.split(webUrl);
                for(int i=1;i<path.length;i++)
                {
                    webUrl=webUrl+path[i];
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if(selectedTab.equalsIgnoreCase("youtube"))
            {
                selectedTab="video";
                isFirstTime=false;
            }
            if(selectedTab.equalsIgnoreCase("amazon"))
            {
                selectedTab="text";
                isFirstTime=false;

            }
            if(selectedTab.equalsIgnoreCase("google"))
            {
                selectedTab="text";
                isFirstTime=false;

            }
        }

        preview=prefHelper.getPreviewInfo();

        if(preview!=null && !TextUtils.isEmpty(preview.title) && TextUtils.isEmpty(extraUrl))
        {
            extraUrl =preview.getTitle();
        }
        binding.webView.setWebChromeClient(new WebChromeClient(){
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;


            @Override
            public void onRequestFocus(WebView view) {
                super.onRequestFocus(view);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                youtubeUrl=view.getUrl();
                if(!TextUtils.isEmpty(youtubeUrl) && youtubeUrl.contains("watch?v"))
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            storeImage(Util.takeScreenshotForView(binding.activityNisinBrowser),youtubeUrl);
                        }
                    },2000);


                    if (!TextUtils.isEmpty(selectedTab) && !isFirstTime) {
                        SaveSearch search = new SaveSearch();
                        search.setBing_id("");
                        search.setDescription("");
                        search.setImage("");
                        search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                        search.setQuery("");
                        search.setTitle("");
                        search.setType("text");
                        search.setWebsite(youtubeUrl);
                        saveSearch(search);
                    }
                    else
                        isFirstTime=false;

                }
           binding.goForw.setColorFilter(view.canGoForward() ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.dark_gray));
                binding.goBack.setColorFilter(view.canGoBack() ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.dark_gray));

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            }

            public void onHideCustomView()
            {
                ((FrameLayout)BrowserActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                BrowserActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                this.mCustomViewCallback.onCustomViewHidden();

            }

            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (this.mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }


                this.mCustomView = paramView;
                this.mCustomView.setBackgroundColor(Color.BLACK);
                this.mOriginalSystemUiVisibility = BrowserActivity.this.getWindow().getDecorView().getSystemUiVisibility();
                /*if(getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }*/
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)BrowserActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                BrowserActivity.this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }

        });
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setDisplayZoomControls(false);
        binding.webView.getSettings().setBuiltInZoomControls(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.getSettings().setUseWideViewPort(true);
        binding.webView.getSettings().setSupportZoom(true);
        binding.webView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 8_3 like Mac OS X) AppleWebKit/600.14 (KHTML, like Gecko) Mobile/12F70");

        binding.webView.setWebViewClient(new MyWebViewClient());
        if(!TextUtils.isEmpty(extraUrl))
        binding.webView.loadUrl(Util.getOptimisedUrl(extraUrl));
        setListeners();

        refreshListeners = new PreviewCountListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PreviewManager.getAllData(mContext, new PreviewManager.PreviewDataLoaded() {
                    @Override
                    public void onDataLoaded(ArrayList<Preview> list) {
                        binding.tvPreviewCount.setText(list.size()==0 ? "":String.valueOf(list.size()));
                    }
                });
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(refreshListeners, new IntentFilter(AppConstants.PREVIEW_COUNT));

    }

    private void setListeners() {
        binding.goBack.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
        binding.goForw.setOnClickListener(this);
        binding.imgRecent.setOnClickListener(this);
        binding.imgSearch.setOnClickListener(this);
        binding.imgShare.setOnClickListener(this);
        binding.webView.setOnTouchListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreviewManager.getAllData(mContext, new PreviewManager.PreviewDataLoaded() {
            @Override
            public void onDataLoaded(ArrayList<Preview> list) {
                binding.tvPreviewCount.setText(list.size()==0 ? "":String.valueOf(list.size()));
            }
        });
    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            case R.id.go_forw:
                if (webView != null && webView.canGoForward()) {
                    webView.goForward();
                }
                break;
            case R.id.img_recent:
                bundle = new Bundle();
                Intent intent1 = new Intent(mContext, PreviewActivity.class);
                intent1.putExtras(bundle);
                startActivityForResult(intent1, AppConstants.RequestActivity.FINISH_ACTIVITY);
                break;
            case R.id.img_search:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean(AppConstants.IS_NEW_SEARCH, true);
                prefHelper.savePreviewInfo(null);
                SearchActivity.start(mContext, bundle2);
                break;
            case R.id.img_share:
                String url=null;
                if(!TextUtils.isEmpty(youtubeUrl))
                    url=youtubeUrl;
                else if(!TextUtils.isEmpty(webView.getUrl()))
                    url=webView.getUrl();

                if (webView != null && !TextUtils.isEmpty(url)) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Join me on SWIS to see what i searched.");
                    i.putExtra(Intent.EXTRA_TEXT, url+" Join me on SWIS to see what i searched.");
                    mContext.startActivity(Intent.createChooser(i, "Share via"));

                }
                break;
            case R.id.img_back:
                onHomeUpIconClicked();
                break;
        }
    }



    @Override
    public void onFragmentInteraction(Object data) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
        if(!TextUtils.isEmpty(hr.getExtra()))
        Log.i(TAG, "getExtra = " + hr.getExtra() + "\t\t Type=" + hr.getType());
        return false;
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, final String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            binding.goForw.setColorFilter(view.canGoForward() ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.dark_gray));
            binding.goBack.setColorFilter(view.canGoBack() ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.dark_gray));
            binding.progessBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    storeImage(Util.takeScreenshotForView(binding.activityNisinBrowser),url);
                }
            },2000);

            try {
                URL aURL = new URL(url);
                String weburl=aURL.getAuthority();
                weburl=weburl.replace("www.","").replace("m.","");
                String[] path=url.split(weburl);
                for(int i=1;i<path.length;i++)
                {
                    weburl=weburl+path[i];
                }
                if (!TextUtils.isEmpty(selectedTab) && !isFirstTime && !weburl.equalsIgnoreCase(webUrl)) {
                    SaveSearch search = new SaveSearch();
                    search.setBing_id("");
                    search.setDescription("");
                    search.setImage("");
                    search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                    search.setQuery("");
                    search.setTitle("");
                    search.setType("text");
                    search.setWebsite(url);
                    saveSearch(search);
                }
                else
                    isFirstTime=false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }



        }



        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView = view;
            binding.progessBar.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(webView.getTitle().trim())) {
                binding.tvDesc.setText(webView.getTitle());

            }
            storeImage(Util.takeScreenshotForView(binding.activityNisinBrowser), url);
    }



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            Uri uri = Uri.parse(request);
            proceedUrl(view, uri);
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            proceedUrl(view, Uri.parse(request.getUrl().toString()));

            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        private void proceedUrl(WebView view, Uri uri) {
            if (uri.toString().startsWith("mailto:")) {
                startActivity(new Intent(Intent.ACTION_SENDTO, uri));
            } else if (uri.toString().startsWith("tel:")) {
                startActivity(new Intent(Intent.ACTION_DIAL, uri));
            } else if (uri.toString().toLowerCase().contains("http")) {
                view.loadUrl(uri.toString());
            }
        }

    }

    private void storeImage(Bitmap image, final String url) {
        final File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        if (preview==null) {
            preview = new Preview();
            preview.setFilePath(pictureFile.getPath());
            preview.setTitle(url);
            preview.setTime(calendar.getTimeInMillis());
            preview.setLink(true);
            preview.setSearchType(selectedTab);
            prefHelper.savePreviewInfo(preview);
            PreviewManager.insertData(mContext, preview);
        } else {
            PreviewManager.getPreviewData(mContext, new PreviewManager.PreviewLoaded() {
                @Override
                public void onDataLoaded(Preview list) {
                    preview = list;
                    if(preview!=null) {
                        preview.setFilePath(pictureFile.getPath());
                        preview.setTitle(url);
                        preview.setTime(calendar.getTimeInMillis());
                        preview.setLink(true);
                        preview.setSearchType(selectedTab);
                        prefHelper.savePreviewInfo(preview);
                        PreviewManager.updateData(mContext, preview);
                    }
                }
            }, preview.getTime());


        }

        PreviewManager.getAllData(mContext, new PreviewManager.PreviewDataLoaded() {
            @Override
            public void onDataLoaded(ArrayList<Preview> list) {
                binding.tvPreviewCount.setText(list.size()==0 ? "":String.valueOf(list.size()));
            }
        });
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 10, fos);


            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        Calendar calendar = Calendar.getInstance();
        String timeStamp = String.valueOf(calendar.getTimeInMillis());
        File mediaFile;
        String mImageName = "swis_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void saveSearch(SaveSearch search) {

        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).saveSearch(search);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        Intent pushNotification = new Intent(AppConstants.REFRESH_POSTS);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AppConstants.RequestActivity.FINISH_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                showToast("hello");
            }
            else if(resultCode==AppConstants.ResultActivity.PREVIEW_ACTIVITY_RESULT_OK && data!=null)
            {
                if(data.getBooleanExtra(AppConstants.IS_NEW_INSERT,false))
                    preview=null;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(refreshListeners);
    }
}

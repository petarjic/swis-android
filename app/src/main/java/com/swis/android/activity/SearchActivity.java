package com.swis.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.adapter.CustomRecyclerAllAdapter;
import com.swis.android.adapter.CustomRecyclerBusinessAdapter;
import com.swis.android.adapter.CustomRecyclerImagesAdapter;
import com.swis.android.adapter.CustomRecyclerNewsAdapter;
import com.swis.android.adapter.CustomRecyclerSearchAdapter;
import com.swis.android.adapter.CustomRecyclerVideosAdapter;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.database.PreviewManager;
import com.swis.android.databinding.ActivitySearchBinding;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.databinding.ItemTextHeaderBinding;
import com.swis.android.fragment.ChangeProfilePicFragment;
import com.swis.android.fragment.SettingFragment;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.listeners.PreviewCountListeners;
import com.swis.android.model.Preview;
import com.swis.android.model.requestmodel.SaveSearch;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Place;
import com.swis.android.model.responsemodel.SearchResult;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.AppConstants;
import com.swis.android.util.CustomTextWatcher;
import com.swis.android.util.GridDividerDecoration;
import com.swis.android.util.PermissionUtil;
import com.swis.android.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends MiddleBaseActivity implements View.OnClickListener, OnItemClickListeners {

    private static final String TAG = "TAG";
    private ActivitySearchBinding binding;
    private boolean isSearch, isNewSearch;
    private String selectedTab,searchText;
    private int page;
    private ArrayList<SearchResult> searchResult;
    private CustomRecyclerAllAdapter allAdapter;
    private CustomRecyclerNewsAdapter newsAdapter;
    private CustomRecyclerImagesAdapter imageVideoAdapter;
    private CustomRecyclerSearchAdapter searchAdapter;
    private CustomRecyclerVideosAdapter videosAdapter;
    private Preview preview;
    private ArrayList<Place> placeArrayList;
    private ItemTextHeaderBinding headerBinding;
    private PreviewCountListeners refreshListeners;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected AppActionbarBinding getToolBar() {
        return binding.appBar;
    }

    @Override
    protected ActionBarData getScreenActionTitle() {
        return new ActionBarData(R.drawable.back_icon, "Search");
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (ActivitySearchBinding) views;

        if (PermissionUtil.checkSDCardPermission(mContext)) {
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtil.SDCARD_REQUEST_CODE);
        }


        if (bundle != null) {
            isNewSearch = bundle.getBoolean(AppConstants.IS_NEW_SEARCH, false);
            searchText = bundle.getString(AppConstants.EXTRA_DATA, "");
        }
        headerBinding = ItemTextHeaderBinding.inflate(LayoutInflater.from(mContext));
        headerBinding.tvLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Util.copyToClipboard(headerBinding.tvLabel, mContext);
                return false;
            }
        });
        headerBinding.tvBusLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Util.copyToClipboard(headerBinding.tvBusLabel, mContext);
                return false;
            }
        });

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


        preview=prefHelper.getPreviewInfo();
        setListeners();
        if(!TextUtils.isEmpty(searchText))
        {
            binding.searchView.setText(searchText);
            isSearch=true;
        }

        if (TextUtils.isEmpty(searchText) && preview != null && !TextUtils.isEmpty(preview.title)) {
            isNewSearch = false;
            selectedTab = preview.getSearchType();
            String text = preview.getTitle();

            if (!TextUtils.isEmpty(text)) {
                page = 0;
                binding.searchView.setText(text);
                isSearch = true;
                switch (selectedTab) {
                    case "text":
                        binding.tvAll.performClick();
                        break;
                    case "news":
                        binding.tvNews.performClick();
                        break;
                    case "video":
                        binding.tvVideos.performClick();
                        break;
                    case "image":
                        binding.tvImages.performClick();
                        break;
                    case "youtube":
                        binding.tvYoutube.performClick();
                        break;

                    case "amazon":
                        binding.tvAmazon.performClick();
                        break;
                }
            }
        } else
            binding.tvAll.performClick();

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

    private void setListeners() {
        binding.appBar.drawerPopUp.setOnClickListener(this);
        binding.imgDelete.setOnClickListener(this);
        binding.tvAll.setOnClickListener(this);
        binding.tvImages.setOnClickListener(this);
        binding.tvNews.setOnClickListener(this);
        binding.tvVideos.setOnClickListener(this);
        binding.tvMaps.setOnClickListener(this);
        binding.tvYoutube.setOnClickListener(this);
        binding.tvAmazon.setOnClickListener(this);
        binding.imgSearch.setOnClickListener(this);
        binding.imgRecent.setOnClickListener(this);
        headerBinding.tvSeeMore.setOnClickListener(this);
        headerBinding.tvBusLabel.setOnClickListener(this);


        binding.searchView.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.imgDelete.setVisibility(TextUtils.isEmpty(s.toString()) && count == 0 ? View.GONE : View.VISIBLE);
                if(TextUtils.isEmpty(s.toString()) && count == 0 && isSearch) {
                    page=0;
                    if(!TextUtils.isEmpty(selectedTab)) {
                        selectedTab = selectedTab.equalsIgnoreCase("text") ? "all" : selectedTab;

                        fetchTrending(selectedTab, page);
                    }
                }

                // isSearch=!TextUtils.isEmpty(s.toString()) && count == 0;

            }
        });

        binding.searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (getText(binding.searchView).length() > 0) {
                        page = 0;
                        if(!TextUtils.isEmpty(selectedTab)) {

                            selectedTab = selectedTab.equalsIgnoreCase("all") ? "text" : selectedTab;
                            performSearch(getText(binding.searchView), page, selectedTab);
                            if (selectedTab.equalsIgnoreCase("text")) {
                                getLocalBusiness(getText(binding.searchView), page);
                            }
                            isSearch = true;
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void getLocalBusiness(String query, int page) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchLocalBusiness(query, page, 5);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        placeArrayList = response.body().getPlaces();
                        CustomRecyclerBusinessAdapter businessAdapter = new CustomRecyclerBusinessAdapter(mContext, placeArrayList);
                        businessAdapter.setOnItemClickListeners(SearchActivity.this);
                        headerBinding.sparkBus.setLayoutManager(new LinearLayoutManager(mContext));
                        headerBinding.sparkBus.setAdapter(businessAdapter);
                        headerBinding.cvBus.setVisibility(response.body().getPlaces().size() > 0 ? View.VISIBLE : View.GONE);
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


    private void performSearch(final String query, final int offset, final String type) {

        Util.hideSoftKeyboard(SearchActivity.this);
        if(page==0)
        {
            binding.sparkView.setVisibility(View.INVISIBLE);
        }
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchResults(query, type, offset);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.sparkView.setDataLoadingFromServerCompleted();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (offset == 0) {
                            binding.sparkView.setVisibility(View.VISIBLE);

                            searchResult = response.body().getSearchResults();
                            binding.sparkView.setLayoutManager(new LinearLayoutManager(mContext));

                            switch (type) {
                                case "text":
                                    if(response.body().getSearchType().equalsIgnoreCase(type)) {
                                        binding.tvAll.setText("ALL");
                                        videosAdapter = new CustomRecyclerVideosAdapter(mContext, response.body().getVideos());
                                        videosAdapter.setOnItemClickListeners(SearchActivity.this);
                                        headerBinding.sparkVideo.setLayoutManager(new GridLayoutManager(mContext, 2));
                                        headerBinding.sparkVideo.addItemDecoration(new GridDividerDecoration(mContext));
                                        headerBinding.sparkVideo.setAdapter(videosAdapter);
                                        videosAdapter.setOnItemClickListeners(SearchActivity.this);
                                        headerBinding.cvVideo.setVisibility(response.body().getVideos().size() > 0 ? View.VISIBLE : View.GONE);
                                        searchAdapter = new CustomRecyclerSearchAdapter(mContext, searchResult);
                                        searchAdapter.setOnItemClickListeners(SearchActivity.this);
                                        searchAdapter.setHeaderView(headerBinding.getRoot());
                                        binding.sparkView.setAdapter(searchAdapter);
                                        binding.sparkView.setNestedScrollingEnabled(false);
                                        binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                            @Override
                                            public void onLoadMore() {
                                                performSearch(query, page, selectedTab);
                                            }
                                        });
                                    }
                                    break;
                                case "image":
                                case "video":
                                    if(response.body().getSearchType().equalsIgnoreCase(type)) {
                                        binding.tvAll.setText("ALL");
                                        imageVideoAdapter = new CustomRecyclerImagesAdapter(mContext, searchResult);
                                        imageVideoAdapter.setOnItemClickListeners(SearchActivity.this);
                                        binding.sparkView.setAdapter(imageVideoAdapter);
                                        binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                            @Override
                                            public void onLoadMore() {
                                                performSearch(query, page, selectedTab);
                                            }
                                        });
                                    }
                                    break;
                                case "news":
                                    if(response.body().getSearchType().equalsIgnoreCase(type)) {
                                        binding.tvAll.setText("ALL");
                                        newsAdapter = new CustomRecyclerNewsAdapter(mContext, searchResult);
                                        newsAdapter.setOnItemClickListeners(SearchActivity.this);
                                        binding.sparkView.setAdapter(newsAdapter);
                                        binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                            @Override
                                            public void onLoadMore() {
                                                performSearch(query, page, selectedTab);
                                            }
                                        });
                                    }
                                    break;

                            }


                            storeImage(Util.takeScreenshotForView(binding.llMain));


                        } else {
                            if (response.body().getSearchResults().size() == 0) {
                                switch (type) {
                                    case "text":
                                        searchAdapter.setFooterView(null);
                                        searchAdapter.notifyDataSetChanged();
                                        binding.sparkView.addLoadMoreListener(null);
                                        searchAdapter.setLoadMoreEnabled(false);
                                        break;
                                    case "image":
                                    case "video":
                                        imageVideoAdapter.setFooterView(null);
                                        imageVideoAdapter.notifyDataSetChanged();
                                        binding.sparkView.addLoadMoreListener(null);
                                        imageVideoAdapter.setLoadMoreEnabled(false);
                                        break;
                                    case "news":
                                        newsAdapter.setFooterView(null);
                                        newsAdapter.notifyDataSetChanged();
                                        binding.sparkView.addLoadMoreListener(null);
                                        newsAdapter.setLoadMoreEnabled(false);
                                        break;

                                }

                            }
                            searchResult.addAll(response.body().getSearchResults());
                            binding.sparkView.getAdapter().notifyDataSetChanged();
                        }

                        page = response.body().getNextOffset();

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
    public void onClick(View v) {
        Intent intent = null;
        if (searchResult != null && searchResult.size() > 0) {
            searchResult.clear();
        }
        switch (v.getId()) {
            case R.id.img_delete:
                binding.searchView.setText("");
                binding.imgDelete.setVisibility(View.GONE);
                break;
            case R.id.img_recent:
                bundle = new Bundle();
                Intent intent1 = new Intent(mContext, PreviewActivity.class);
                intent1.putExtras(bundle);
                startActivityForResult(intent1, AppConstants.RequestActivity.FINISH_ACTIVITY);
                break;
            case R.id.img_search:
                prefHelper.savePreviewInfo(null);
                if (!TextUtils.isEmpty(getText(binding.searchView))) {
                    binding.searchView.setText("");
                    Bundle bundle2 = new Bundle();
                    bundle2.putBoolean(AppConstants.IS_NEW_SEARCH, true);
                    SearchActivity.start(mContext, bundle2);
                }
                else
                    binding.tvAll.performClick();
                break;

            case R.id.tv_all:

                binding.tvAll.setTextColor(getResources().getColor(R.color.blue));
                binding.tvNews.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvImages.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvVideos.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvMaps.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvYoutube.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvAmazon.setTextColor(getResources().getColor(R.color.newgrey));
                page = 0;
                selectedTab = "text";
                if (isSearch && !TextUtils.isEmpty(getText(binding.searchView))) {
                    performSearch(getText(binding.searchView), page, "text");
                } else
                    fetchTrending("all", page);
                break;
            case R.id.tv_news:
                binding.tvAll.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvNews.setTextColor(getResources().getColor(R.color.blue));
                binding.tvImages.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvVideos.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvMaps.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvYoutube.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvAmazon.setTextColor(getResources().getColor(R.color.newgrey));
                page = 0;
                selectedTab = "news";
                if (isSearch && !TextUtils.isEmpty(getText(binding.searchView))) {
                    performSearch(getText(binding.searchView), page, "news");
                } else
                    fetchTrending("news", page);
                break;
            case R.id.tv_videos:
                binding.tvAll.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvNews.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvImages.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvVideos.setTextColor(getResources().getColor(R.color.blue));
                binding.tvMaps.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvYoutube.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvAmazon.setTextColor(getResources().getColor(R.color.newgrey));
                page = 0;
                selectedTab = "video";
                if (isSearch && !TextUtils.isEmpty(getText(binding.searchView))) {
                    performSearch(getText(binding.searchView), page, "video");
                } else
                    fetchTrending("video", page);
                break;
            case R.id.tv_images:
                binding.tvAll.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvNews.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvImages.setTextColor(getResources().getColor(R.color.blue));
                binding.tvVideos.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvMaps.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvYoutube.setTextColor(getResources().getColor(R.color.newgrey));
                binding.tvAmazon.setTextColor(getResources().getColor(R.color.newgrey));
                page=0;
                selectedTab = "image";
                if (isSearch && !TextUtils.isEmpty(getText(binding.searchView))) {
                    performSearch(getText(binding.searchView), page, "image");
                } else
                    fetchTrending("image", page);
                break;
            case R.id.tv_maps:
                intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + getText(binding.searchView)));
                startActivity(intent);
                break;

            case R.id.tv_youtube:
                String temp1 = selectedTab;
                selectedTab = "youtube";
                isNewSearch=false;
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, "http://www.youtube.com/results?search_query=" + getText(binding.searchView));
                bundle.putString(AppConstants.EXTRA_TITLE, selectedTab);
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                selectedTab = temp1;
                break;
            case R.id.tv_amazon:
                String temp2 = selectedTab;
                selectedTab = "amazon";
                isNewSearch=false;
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, "http://www.amazon.com/s?k=" + getText(binding.searchView));
                bundle.putString(AppConstants.EXTRA_TITLE, selectedTab);
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                selectedTab = temp2;
                break;
            case R.id.tv_see_more:
                binding.tvVideos.performClick();
                break;
            case R.id.tv_bus_label:
                intent = new Intent(mContext, BusinessListActivity.class);
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_TITLE, getText(binding.searchView));
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
    }

    private void fetchTrending(final String type, final int offset) {
        Util.hideSoftKeyboard(SearchActivity.this);

        if(page==0)
        {
            binding.sparkView.setVisibility(View.INVISIBLE);
        }

        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchTrending(type, offset);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.sparkView.setDataLoadingFromServerCompleted();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        binding.sparkView.setVisibility(View.VISIBLE);

                        binding.tvAll.setText("TRENDING");
                        if (offset == 0) {


                            searchResult = response.body().getSearchResults();
                            binding.sparkView.setLayoutManager(new LinearLayoutManager(mContext));

                            switch (type) {
                                case "all":
                                    if(type.equalsIgnoreCase(response.body().getSearchType())) {
                                        ArrayList<SearchResult> arrayList = new ArrayList<>();
                                        arrayList.add(searchResult.get(0));
                                        arrayList.add(searchResult.get(1));
                                        arrayList.add(searchResult.get(2));
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelableArrayList(AppConstants.EXTRA_OBJECT, arrayList);
                                        SearchResult search = new SearchResult();
                                        search.setBundle(bundle);
                                        searchResult.remove(0);
                                        searchResult.remove(0);
                                        searchResult.remove(0);
                                        searchResult.add(0, search);
                                        allAdapter = new CustomRecyclerAllAdapter(mContext, searchResult);
                                        binding.sparkView.setAdapter(allAdapter);
                                        allAdapter.setOnItemClickListeners(SearchActivity.this);
                                        binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                            @Override
                                            public void onLoadMore() {
                                                fetchTrending(type, page);
                                            }
                                        });
                                    }
                                    break;
                                case "image":
                                case "video":
                                    if(type.equalsIgnoreCase(response.body().getSearchType())) {
                                        imageVideoAdapter = new CustomRecyclerImagesAdapter(mContext, searchResult);
                                        imageVideoAdapter.setOnItemClickListeners(SearchActivity.this);
                                        binding.sparkView.setAdapter(imageVideoAdapter);
                                        binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                            @Override
                                            public void onLoadMore() {
                                                fetchTrending(type, page);
                                            }
                                        });
                                    }
                                    break;
                                case "news":
                                    if(type.equalsIgnoreCase(response.body().getSearchType())) {

                                        newsAdapter = new CustomRecyclerNewsAdapter(mContext, searchResult);
                                        newsAdapter.setOnItemClickListeners(SearchActivity.this);
                                        binding.sparkView.setAdapter(newsAdapter);
                                        binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                            @Override
                                            public void onLoadMore() {
                                                fetchTrending(type, page);
                                            }
                                        });
                                    }
                                    break;

                            }

                        } else {
                            if (response.body().getSearchResults().size() == 0) {

                                switch (type) {
                                    case "all":

                                        allAdapter.setFooterView(null);
                                        allAdapter.notifyDataSetChanged();
                                        binding.sparkView.addLoadMoreListener(null);
                                        allAdapter.setLoadMoreEnabled(false);
                                        break;
                                    case "image":
                                    case "video":
                                        imageVideoAdapter.setFooterView(null);
                                        imageVideoAdapter.notifyDataSetChanged();
                                        binding.sparkView.addLoadMoreListener(null);
                                        imageVideoAdapter.setLoadMoreEnabled(false);
                                        break;
                                    case "news":
                                        newsAdapter.setFooterView(null);
                                        newsAdapter.notifyDataSetChanged();
                                        binding.sparkView.addLoadMoreListener(null);
                                        newsAdapter.setLoadMoreEnabled(false);
                                        break;

                                }

                            }
                            searchResult.addAll(response.body().getSearchResults());
                            binding.sparkView.getAdapter().notifyDataSetChanged();
                        }

                        page = response.body().getNextOffset();

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


    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    private void storeImage(Bitmap image) {
        final File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        if (isNewSearch) {
            isNewSearch = false;
            preview = new Preview();
            preview.setFilePath(pictureFile.getPath());
            preview.setTitle(getText(binding.searchView));
            preview.setTime(calendar.getTimeInMillis());
            preview.setLink(false);
            preview.setSearchType(selectedTab);
            prefHelper.savePreviewInfo(preview);
            PreviewManager.insertData(mContext, preview);
        } else {
            if(preview!=null) {
                PreviewManager.getPreviewData(mContext, new PreviewManager.PreviewLoaded() {
                    @Override
                    public void onDataLoaded(Preview list) {
                        preview = list;
                        try {
                            preview.setFilePath(pictureFile.getPath());
                            preview.setTitle(getText(binding.searchView));
                            preview.setTime(calendar.getTimeInMillis());
                            preview.setLink(false);
                            preview.setSearchType(selectedTab);
                            prefHelper.savePreviewInfo(preview);
                            PreviewManager.updateData(mContext, preview);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, preview.time);
            }


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

    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = null;
        Intent intent = null;
        SaveSearch search;
        ArrayList<SearchResult> list = null;
        switch (view.getId()) {
            case R.id.ll_url_one:
                isNewSearch=false;
                bundle = ((SearchResult) allAdapter.getList().get(0)).getBundle();
                list = bundle.getParcelableArrayList(AppConstants.EXTRA_OBJECT);
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, list.get(0).getWebsite());
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                search = new SaveSearch();
                search.setBing_id(list.get(0).getId());
                search.setDescription(list.get(0).getDescription());
                search.setImage(list.get(0).getImage());
                search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                search.setQuery(list.get(0).getTitle());
                search.setTitle(list.get(0).getTitle());
                search.setType("text");
                search.setWebsite(list.get(0).getWebsite());
                saveSearch(search);
                break;
            case R.id.ll_url_two:
                isNewSearch=false;
                bundle = ((SearchResult) allAdapter.getList().get(0)).getBundle();
                list = bundle.getParcelableArrayList(AppConstants.EXTRA_OBJECT);
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, list.get(1).getWebsite());
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                search = new SaveSearch();
                search.setBing_id(list.get(1).getId());
                search.setDescription(list.get(1).getDescription());
                search.setImage(list.get(1).getImage());
                search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                search.setQuery(list.get(1).getTitle());
                search.setTitle(list.get(1).getTitle());
                search.setType("text");
                search.setWebsite(list.get(1).getWebsite());
                saveSearch(search);
                break;
            case R.id.ll_url_three:
                isNewSearch=false;
                bundle = ((SearchResult) allAdapter.getList().get(0)).getBundle();
                list = bundle.getParcelableArrayList(AppConstants.EXTRA_OBJECT);
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, list.get(2).getWebsite());
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                search = new SaveSearch();
                search.setBing_id(list.get(2).getId());
                search.setDescription(list.get(2).getDescription());
                search.setImage(list.get(2).getImage());
                search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                search.setQuery(list.get(2).getTitle());
                search.setTitle(list.get(2).getTitle());
                search.setType("text");
                search.setWebsite(list.get(2).getWebsite());
                saveSearch(search);
                break;
            case R.id.tv_name1:
                isNewSearch=false;
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, placeArrayList.get(position).getDetail_url());
                bundle.putString(AppConstants.EXTRA_TITLE, selectedTab);
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                search = new SaveSearch();
                search.setBing_id(placeArrayList.get(position).getId());
                search.setDescription("");
                search.setImage("");
                search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                search.setQuery(placeArrayList.get(position).getName());
                search.setTitle(placeArrayList.get(position).getName());
                search.setType("text");
                search.setWebsite(placeArrayList.get(position).getDetail_url());
                saveSearch(search);
                break;
            case R.id.ll_call1:
                Util.dialPhone(placeArrayList.get(position).getPhone(), mContext);
                break;
            case R.id.ll_direction1:
                String url = String.format("http://maps.google.com/maps?daddr=%f,%f", placeArrayList.get(position).getLatlng().getLatitude(), placeArrayList.get(position).getLatlng().getLongitude());
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.ll_website1:
                bundle = new Bundle();
                isNewSearch=false;
                bundle.putString(AppConstants.EXTRA_OBJECT, placeArrayList.get(position).getWebsite());
                bundle.putString(AppConstants.EXTRA_TITLE, selectedTab);
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                search = new SaveSearch();
                search.setBing_id(placeArrayList.get(position).getId());
                search.setDescription("");
                search.setImage("");
                search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                search.setQuery(placeArrayList.get(position).getName());
                search.setTitle(placeArrayList.get(position).getName());
                search.setType("text");
                search.setWebsite(placeArrayList.get(position).getWebsite());
                saveSearch(search);
                break;
            case R.id.ll_video:
                isNewSearch=false;
                SearchResult video = (SearchResult) videosAdapter.getList().get(position);
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, video.getWebsite());
                bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                intent = new Intent(mContext, BrowserActivity.class);
                intent.putExtras(bundle);
                search = new SaveSearch();
                search.setBing_id(video.getId());
                search.setDescription(video.getDescription());
                search.setImage(video.getThumbnailUrl());
                search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                search.setQuery(getText(binding.searchView));
                search.setTitle(video.getTitle());
                search.setType("video");
                search.setWebsite(video.getWebsite());
                saveSearch(search);
                startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                break;
            default:
                if(searchResult!=null && searchResult.size()>0) {
                    isNewSearch = false;
                    bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_TITLE, selectedTab);
                    bundle.putString(AppConstants.EXTRA_OBJECT, searchResult.get(position).getWebsite());
                    bundle.putParcelable(AppConstants.EXTRA_DATA, preview);
                    intent = new Intent(mContext, BrowserActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, AppConstants.RequestActivity.FINISH_ACTIVITY);
                    if (!TextUtils.isEmpty(searchResult.get(position).getWebsite()) && !searchResult.get(position).getWebsite().contains("watch?v")) {
                        search = new SaveSearch();
                        search.setBing_id(searchResult.get(position).getId());
                        search.setDescription(searchResult.get(position).getDescription());
                        search.setImage(searchResult.get(position).getImage()!=null ? searchResult.get(position).getImage():searchResult.get(position).getThumbnailUrl());
                        search.setJourney_id(String.valueOf(prefHelper.getJourneyId()));
                        search.setQuery(TextUtils.isEmpty(getText(binding.searchView)) ? searchResult.get(position).getTitle() : getText(binding.searchView));
                        search.setTitle(searchResult.get(position).getTitle());
                        search.setType(selectedTab);
                        search.setWebsite(searchResult.get(position).getWebsite());
                        saveSearch(search);
                    }
                }
                break;
        }


    }

    @Override
    public void onBackPressed() {
        Util.hideSoftKeyboard(this);
        Intent intent=new Intent();
        setResult(RESULT_OK,intent);
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.RequestActivity.FINISH_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
            else if(resultCode==RESULT_CANCELED && data!=null)
            {
                preview=prefHelper.getPreviewInfo();
            }else if(resultCode==AppConstants.ResultActivity.PREVIEW_ACTIVITY_RESULT_OK && data!=null)
            {
                if(data.getBooleanExtra(AppConstants.IS_NEW_INSERT,false))
                         preview=null;
            }

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {


            case PermissionUtil.SDCARD_REQUEST_CODE:
                if (PermissionUtil.checkSDCardPermission(mContext)) {
                }
                else
                {
                    boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (! showRationale) {

                        Util.showAlert(mContext, "To keep the history of your searches, Please allow storage permission from setting", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 200);

                            }
                        });



                    } else  {
                        Util.showAlert(mContext,"To keep the history of your searches, Please allow storage permission");

                    }

                }
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(refreshListeners);
    }
}

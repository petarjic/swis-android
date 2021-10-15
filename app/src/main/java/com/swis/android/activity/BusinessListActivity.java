package com.swis.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.adapter.CustomRecyclerBusinessAdapter;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.ActivityBusinessListBinding;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.requestmodel.SaveSearch;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Place;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessListActivity extends MiddleBaseActivity implements OnItemClickListeners {

    private ActivityBusinessListBinding binding;
    private ArrayList<Place> placeArrayList;
    private int page=0,limit=10;
    private CustomRecyclerBusinessAdapter businessAdapter;
    private String query;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_business_list;
    }

    @Override
    protected AppActionbarBinding getToolBar() {
        return binding.appBar;
    }

    @Override
    protected ActionBarData getScreenActionTitle() {
        return new ActionBarData(R.drawable.back_icon, query);
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (ActivityBusinessListBinding) views;
        if(bundle!=null)
        {
            query=bundle.getString(AppConstants.EXTRA_TITLE);
            if(!TextUtils.isEmpty(query))
                getLocalBusiness(query,page,limit);
        }
    }

    private void getLocalBusiness(final String query, int pageNo, final int limit) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchLocalBusiness(query, pageNo,limit);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.sparkBus.setDataLoadingFromServerCompleted();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if(page==0) {
                            placeArrayList = response.body().getPlaces();
                            businessAdapter = new CustomRecyclerBusinessAdapter(mContext, placeArrayList);
                            businessAdapter.setOnItemClickListeners(BusinessListActivity.this);
                            binding.sparkBus.setLayoutManager(new LinearLayoutManager(mContext));
                            binding.sparkBus.setAdapter(businessAdapter);
                            binding.sparkBus.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    getLocalBusiness(query,page,limit);
                                }
                            });
                        }
                        else {
                            if (response.body().getPlaces().size() == 0) {

                                businessAdapter.setFooterView(null);
                                businessAdapter.notifyDataSetChanged();
                                binding.sparkBus.addLoadMoreListener(null);
                                businessAdapter.setLoadMoreEnabled(false);

                            }
                            placeArrayList.addAll(response.body().getPlaces());
                            binding.sparkBus.getAdapter().notifyDataSetChanged();
                        }

                        page = response.body().getNextPage();

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

    @Override
    public void onItemClick(View view, int position) {
        Intent intent=null;
        SaveSearch search=null;
        switch (view.getId())
        {
            case R.id.tv_name1:
                bundle = new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT, placeArrayList.get(position).getDetail_url());
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
                bundle.putString(AppConstants.EXTRA_OBJECT, placeArrayList.get(position).getWebsite());
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
                saveSearch(search); break;
        }
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
}

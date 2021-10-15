package com.swis.android.fragment;


import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.adapter.SparkFollowRequestAdapter;
import com.swis.android.base.BaseFragment;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentFollowRequestBinding;
import com.swis.android.listeners.CustomTextChangeListener;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowRequestFragment extends BaseFragment implements OnItemClickListeners, SwipeRefreshLayout.OnRefreshListener {

    private FragmentFollowRequestBinding binding;
    private int page;
    private ArrayList<UserInfo> followers;
    private SparkFollowRequestAdapter adapter;
    private boolean isSearched;

    public FollowRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).showBackButton();
        ((HomeActivity) getActivity()).setTitle("Follow Request");


    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_follow_request;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentFollowRequestBinding) views;
        binding.swipeLayout.setOnRefreshListener(this);
        page=0;
        getFollowersRequest(page);
        binding.searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(binding.searchView.getText().toString().length()>0)
                    {
                        page = 0;
                        isSearched=true;
                        getFollowersRequest(page);
                        Util.hideSoftKeyboard(getActivity());

                    }

                }

                return false;
            }
        });

        binding.searchView.addTextChangedListener(new CustomTextChangeListener() {
            @Override
            public void onTextChanged(@Nullable CharSequence s, int start, int before, int count) {
                if (s.length() == 0 && start != before) {
                    if(isSearched)
                    {
                        page = 0;
                        isSearched=false;
                        getFollowersRequest(page);

                    }

                }


                binding.imgDelete.setVisibility(s.length()>0 ? View.VISIBLE:View.GONE);

            }
        });

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchView.setText("");
            }
        });
    }

    private void getFollowersRequest(int pageNo) {
        if (pageNo == 0)
            binding.swipeLayout.setRefreshing(true);
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getPendingRequestUser(pageNo);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.swipeLayout.setRefreshing(false);
                binding.sparkView.setDataLoadingFromServerCompleted();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (page == 0) {
                            if(followers!=null)
                                followers.clear();
                            followers = response.body().getFollowers();
                            adapter = new SparkFollowRequestAdapter(mContext, followers);
                            binding.sparkView.setLayoutManager(new LinearLayoutManager(mContext));
                            binding.sparkView.setAdapter(adapter);
                            adapter.setOnItemClickListeners(FollowRequestFragment.this);
                            binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    getFollowersRequest(page);
                                }
                            });

                        } else {
                            if (response.body().getFollowers().size() == 0) {
                                adapter.setFooterView(null);
                                adapter.notifyDataSetChanged();
                                binding.sparkView.addLoadMoreListener(null);
                                adapter.setLoadMoreEnabled(false);

                            }
                            followers.addAll(response.body().getFollowers());
                            adapter.notifyDataSetChanged();
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
                binding.swipeLayout.setRefreshing(false);
                binding.sparkView.setDataLoadingFromServerCompleted();
            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.img_accept:
                acceptUserReq(Integer.parseInt(followers.get(position).getId()), position);

                break;

            case R.id.img_reject:
                rejectUserReq(Integer.parseInt(followers.get(position).getId()), position);

                break;
        }
    }

    private void rejectUserReq(int id, final int position) {

        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).declineRequest(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        UserInfo userInfo = ((UserInfo) adapter.getList().remove(position));
                        adapter.notifyItemRemoved(position);
                        UserInfo userInfo1 = prefHelper.getUserInfo();
                        userInfo.setFollow_request_count(String.valueOf(Integer.parseInt(userInfo1.getFollow_request_count()) - 1));
                        prefHelper.saveUserInfo(userInfo1);
                        Intent pushNotification = new Intent(AppConstants.REFRESH_FOLLOW_REQUEST);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);

                    }
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

    private void acceptUserReq(int id, final int position) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).approveRequest(id);
        showProgressDialog();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        UserInfo userInfo = ((UserInfo) adapter.getList().remove(position));
                        adapter.notifyItemRemoved(position);
                        UserInfo userInfo1 = prefHelper.getUserInfo();
                        userInfo1.setFollow_request_count(String.valueOf(Integer.parseInt(userInfo1.getFollow_request_count()) - 1));
                        prefHelper.saveUserInfo(userInfo1);
                        Intent pushNotification = new Intent(AppConstants.REFRESH_FOLLOW_REQUEST);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);


                    }
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

    @Override
    public void onRefresh() {
        page=0;
        getFollowersRequest(page);
    }
}

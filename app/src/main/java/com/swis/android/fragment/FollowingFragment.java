package com.swis.android.fragment;


import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.adapter.SparkFollowersRequestAdapter;
import com.swis.android.base.BaseFragment;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentFollowingBinding;
import com.swis.android.listeners.CustomTextChangeListener;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Confirmation;
import com.swis.android.util.Util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFragment extends BaseFragment implements OnItemClickListeners, SwipeRefreshLayout.OnRefreshListener {

    private FragmentFollowingBinding binding;
    private int page;
    private String query = "";
    private String id;
    private ArrayList<UserInfo> followings;
    private SparkFollowersRequestAdapter adapter;
    private UserInfo userInfo;
    private boolean isSearched;

    public FollowingFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_following;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentFollowingBinding) views;
        bundle=getArguments();
        if (bundle != null) {
            userInfo = bundle.getParcelable(AppConstants.EXTRA_OBJECT);
        }
        if (userInfo != null) {
            id = userInfo.getId();
        } else
            id = prefHelper.getUserInfo().getId();
        binding.searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(binding.searchView.getText().toString().length()>0)
                    {
                        page = 0;
                        isSearched=true;
                        getFollowings(page, binding.searchView.getText().toString(), id);
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
                        getFollowings(page, binding.searchView.getText().toString(), id);

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

        binding.swipeLayout.setOnRefreshListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).showBackButton();
        ((HomeActivity) getActivity()).setTitle("Following");

        page = 0;
        getFollowings(page, query, id);

    }

    private void getFollowings(int pageNo, final String query, final String id) {
        if (pageNo == 0)
            binding.swipeLayout.setRefreshing(true);
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getFollowings(pageNo, id, query);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.swipeLayout.setRefreshing(false);
                binding.sparkView.setDataLoadingFromServerCompleted();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (page == 0) {
                            if (followings != null)
                                followings.clear();
                            followings = response.body().getFollowings();
                            adapter = new SparkFollowersRequestAdapter(mContext, followings, false);
                            binding.sparkView.setLayoutManager(new LinearLayoutManager(mContext));
                            binding.sparkView.setAdapter(adapter);
                            adapter.setOnItemClickListeners(FollowingFragment.this);
                            binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    getFollowings(page, query, id);
                                }
                            });

                        } else {
                            if (response.body().getFollowings().size() == 0) {
                                adapter.setFooterView(null);
                                adapter.notifyDataSetChanged();
                                binding.sparkView.addLoadMoreListener(null);
                                adapter.setLoadMoreEnabled(false);
                            }
                            followings.addAll(response.body().getFollowings());
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
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onItemClick(View view, final int position) {


        switch (view.getId()) {
            case R.id.btn_follow:
                if (((TextView) view).getText().toString().equalsIgnoreCase("Follow"))
                    sendFollowRequest((UserInfo) adapter.getList().get(position), position);
                else {

                    dialogConfirmation("Are you sure you want to unfollow?", new Confirmation() {
                        @Override
                        public void onYes() {
                            unfollowUser((UserInfo) adapter.getList().get(position),position);
                            super.onYes();
                        }

                        @Override
                        public void onNo() {
                            super.onNo();
                        }
                    });

                }
                break;
            default:
                Fragment fragment = new ProfileNewFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.EXTRA_OBJECT, (UserInfo) adapter.getList().get(position));
                bundle.putString(AppConstants.EXTRA_DATA, "Following");
                fragment.setArguments(bundle);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment, true);
                break;

        }


    }


    @Override
    public void onRefresh() {
        page = 0;
        getFollowings(page, query, id);
    }

    private void unfollowUser(final UserInfo userInfo, final int position) {

        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).unfollowUser(userInfo.getId());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        showToast(response.body().getResponseMessage());
                        Intent pushNotification = new Intent(AppConstants.REFRESH_POSTS);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
                        if(id.equalsIgnoreCase(prefHelper.getUserInfo().getId()))
                        {
                            UserInfo userInfo = ((UserInfo) adapter.getList().remove(position));
                            adapter.notifyItemRemoved(position);
                        }
                        else
                        {
                            ((UserInfo) adapter.getList().get(position)).setFollowed(false);
                            adapter.notifyItemChanged(position);
                        }

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

    private void sendFollowRequest(UserInfo userInfo, final int position) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).sendRequest(userInfo.getId());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        showToast(response.body().getResponseMessage());
                         if (response.body().getDetails().get(0).getStatus().equalsIgnoreCase("approved")) {
                             Intent pushNotification = new Intent(AppConstants.REFRESH_POSTS);
                             LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
                             ((UserInfo) adapter.getList().get(position)).setFollowed(true);
                            adapter.notifyItemChanged(position);
                        }
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
}

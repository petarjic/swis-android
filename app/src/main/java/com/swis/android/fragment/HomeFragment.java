package com.swis.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.CommentActivity;
import com.swis.android.activity.HomeActivity;
import com.swis.android.adapter.SparkHomeFollowingAdapter;
import com.swis.android.adapter.SparkHomePostAdapter;
import com.swis.android.base.BaseActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentHomeBinding;
import com.swis.android.databinding.HomePageHeaderViewBinding;
import com.swis.android.listeners.HomePageFollowersRefreshListeners;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.listeners.PostRefreshListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnItemClickListeners {

    private FragmentHomeBinding binding;
    private SparkHomeFollowingAdapter adapter;
    private SparkHomePostAdapter postAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<UserInfo> userInfos = new ArrayList<>();
    private ArrayList<Post> posts = new ArrayList<>();
    private int page;
    private int postPage, position = -1;
    private String userId, local, title;
    private HomePageHeaderViewBinding headerViewBinding;
    private PostRefreshListeners listeners;
    private HomePageFollowersRefreshListeners refreshListeners;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        postPage = 0;
        page = 0;
        binding = (FragmentHomeBinding) views;
        bundle = getArguments();
        if (bundle != null) {
            local = bundle.getString(AppConstants.EXTRA_DATA);
            title = bundle.getString(AppConstants.EXTRA_TITLE);
        }
        binding.swipeLayout.setOnRefreshListener(this);

        listeners = new PostRefreshListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (position >= 0) {
                    bundle = intent.getExtras();
                    Post latestPost = (Post) bundle.get(AppConstants.EXTRA_OBJECT);
                    posts.get(position).setComments(latestPost.getComments());
                    posts.get(position).setComment_count(latestPost.getComment_count());
                    postAdapter.notifyItemChanged(position+1);
                    position = -1;
                }
            }
        };

        refreshListeners = new HomePageFollowersRefreshListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setUpView(false);
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(refreshListeners, new IntentFilter(AppConstants.REFRESH_POSTS));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(listeners, new IntentFilter(AppConstants.REFRESH_POST_AT_PARTICULAR_POSITION));
        headerViewBinding = HomePageHeaderViewBinding.inflate(LayoutInflater.from(mContext));
        adapter = new SparkHomeFollowingAdapter(mContext, userInfos);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        headerViewBinding.sparkFollowings.setLayoutManager(linearLayoutManager);
        adapter.setOnClickListeners(HomeFragment.this);
        headerViewBinding.sparkFollowings.setAdapter(adapter);
        postAdapter = new SparkHomePostAdapter(mContext, posts, false);
        postAdapter.setHeaderView(headerViewBinding.getRoot());
        binding.sparkPost.setLayoutManager(new LinearLayoutManager(mContext));
        binding.sparkPost.setAdapter(postAdapter);
        setUpView(true);
        setListeners();
    }

    private void setUpView(boolean flag) {
        userId = "";
        if (TextUtils.isEmpty(local)) {
            page = 0;
            getFollowings(page);
        } else
            postAdapter.setHeaderViewVisibility(false);
        postPage = 0;

        getPost(postPage, userId, local,flag);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(refreshListeners);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(listeners);
    }

    private void setListeners() {
        headerViewBinding.imgForward.setOnClickListener(this);
        headerViewBinding.imgReverse.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setTitle(TextUtils.isEmpty(local) ? "SWIS":"Searches near me");
        ((HomeActivity) getActivity()).actionBarSetup();
    }

    private void getFollowings(int pageNo) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getFollowingsFeed(pageNo);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {

                        if(!(response.body().getNextPage() > 2 && page == 0)){
                            if (response.body().getNextPage() == 1) {
                                userInfos.clear();
                                userInfos.addAll(response.body().getFollowings());
                                headerViewBinding.imgForward.setVisibility(userInfos.size() > 0 ? View.VISIBLE : View.GONE);
                                headerViewBinding.imgReverse.setVisibility(userInfos.size() > 0 ? View.VISIBLE : View.GONE);
                                adapter.notifyDataSetChanged();
                            } else {
                                userInfos.addAll(response.body().getFollowings());
                                adapter.notifyItemRangeInserted(userInfos.size() - response.body().getFollowings().size(), response.body().getFollowings().size());

                            }

                            page = response.body().getNextPage();

                            if (response.body().getFollowings().size() > 0)
                                getFollowings(page);
                        }

                    } else
                        showToast(response.body().getResponseMessage());
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast(AppConstants.SERVER_ERROR);
                headerViewBinding.sparkFollowings.setDataLoadingFromServerCompleted();
            }
        });
    }

    private void getPost(int pageNo, final String userId, final String local, final boolean flag) {
        if (pageNo == 0)
            binding.swipeLayout.setRefreshing(flag);
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchPost(pageNo, userId, local);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.sparkPost.setDataLoadingFromServerCompleted();
                binding.swipeLayout.setRefreshing(false);
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (postPage == 0) {
                            posts.clear();
                            posts.addAll(response.body().getPosts());
                            binding.sparkPost.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    getPost(postPage, userId, local, flag);
                                }
                            });

                            postAdapter.setOnItemClickListeners(HomeFragment.this);

                            postAdapter.notifyDataSetChanged();


                        } else {
                            if (response.body().getPosts().size() == 0) {
                                postAdapter.setFooterView(null);
                                postAdapter.notifyDataSetChanged();
                                binding.sparkPost.addLoadMoreListener(null);
                                postAdapter.setLoadMoreEnabled(false);
                            }
                            posts.addAll(response.body().getPosts());
                            postAdapter.notifyDataSetChanged();

                            //postAdapter.notifyItemRangeInserted(posts.size()-response.body().getPosts().size(),response.body().getPosts().size());
                        }

                        postPage = response.body().getNextPage();

                    } else
                        showToast(response.body().getResponseMessage());
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast(AppConstants.SERVER_ERROR);
                binding.swipeLayout.setRefreshing(true);
                binding.sparkPost.setDataLoadingFromServerCompleted();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_forward:
                int pos = linearLayoutManager.findLastVisibleItemPosition();
                if (pos != adapter.getItemCount())
                    headerViewBinding.sparkFollowings.smoothScrollToPosition(pos + 4);
                break;
            case R.id.img_reverse:

                int pos2 = linearLayoutManager.findFirstVisibleItemPosition();
                if (pos2 > 3)
                    headerViewBinding.sparkFollowings.smoothScrollToPosition(pos2 - 4);
                else
                    headerViewBinding.sparkFollowings.smoothScrollToPosition(0);

                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 0;
        postPage = 0;
        userId = "";
        if (TextUtils.isEmpty(local)) {

            getFollowings(page);
        }
        getPost(postPage, userId, local, true);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.follow_profile_Pic:

                postPage = 0;
                if (userInfos.get(position).isSelected())
                    userId = userInfos.get(position).getId();
                else
                    userId = "";
                getPost(postPage, userId, local, true);
                break;
            case R.id.img_like:
                int count = posts.get(position).getLike_count();
                posts.get(position).setLike(!posts.get(position).isLike());
                posts.get(position).setLike_count(posts.get(position).isLike() ? count + 1 : count - 1);
                postAdapter.notifyItemChanged(position + 1);
                likePost(String.valueOf(posts.get(position).getId()), position);
                break;
            case R.id.profile_Pic:
                ProfileNewFragment fragment = new ProfileNewFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable(AppConstants.EXTRA_OBJECT, ((Post) postAdapter.getList().get(position)).getUser());
                bundle2.putString(AppConstants.EXTRA_DATA, (String.valueOf(((BaseActivity) getActivity()).getActionBarTitle())));
                fragment.setArguments(bundle2);
                addFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment, true);
                break;
            case R.id.ll_bookmark:
                posts.get(position).setFavourite(!posts.get(position).isFavourite());
                postAdapter.notifyItemChanged(position + 1);
                markFavourite(String.valueOf(posts.get(position).getId()), position);
                break;
            case R.id.ll_give_comment:
            case R.id.ll_view_comments:
                goToCommentsPage(position,true);
                break;
            case R.id.tv_view_all:
                goToCommentsPage(position,false);
                break;
            case R.id.tv_like_count:
                LikesFragment likesFragment = new LikesFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.EXTRA_OBJECT, posts.get(position));
                bundle.putString(AppConstants.EXTRA_DATA, (String.valueOf(((BaseActivity) getActivity()).getActionBarTitle())));
                likesFragment.setArguments(bundle);
                addFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), likesFragment, true);
                break;
        }
    }

    private void goToCommentsPage(int position, boolean flag) {
        this.position = position;
        bundle = new Bundle();
        bundle.putParcelable(AppConstants.EXTRA_OBJECT, posts.get(position));
        bundle.putBoolean(AppConstants.EXTRA_TITLE, flag);
        CommentActivity.start(mContext,bundle);
     }

    private void likePost(String id, final int position) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).likePost(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void markFavourite(String id, final int position) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).markFavourite(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }
}

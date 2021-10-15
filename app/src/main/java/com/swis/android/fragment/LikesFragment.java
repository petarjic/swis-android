package com.swis.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.adapter.LikesAdapter;
import com.swis.android.base.BaseActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentLikesBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Confirmation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class LikesFragment extends BaseFragment implements OnItemClickListeners {

    private FragmentLikesBinding binding;
    private Post post;
    private int page;
    private ArrayList<UserInfo> userInfos=new ArrayList<>();
    private LikesAdapter likesAdapter;
    private String title;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_likes;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).setTitle("Likes");
        ((HomeActivity) getActivity()).showBackButton();

    }



    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding= (FragmentLikesBinding) views;
        bundle=getArguments();

        if(bundle!=null)
        {
            title=bundle.getString(AppConstants.EXTRA_DATA);
            post=bundle.getParcelable(AppConstants.EXTRA_OBJECT);
        }
        if(post!=null)
        {
            if(post.getWebsites()!=null)
            binding.tvName.setText(TextUtils.isEmpty(post.getWebsites().get(0).getSearch_term()) ? post.getComment():post.getWebsites().get(0).getSearch_term());
            else
                binding.tvName.setText(post.getComment());
            Glide.with(mContext).load(post.getUser().getAvatar()).placeholder(R.drawable.default_user).into(binding.likesProfilePic);
        }
        page=0;
        getLikes(page,post.getId());

    }

    private void getLikes(int pageNo, int id) {

        Call<ApiResponse> call= ApiClient.getClient().create(ApiInterface.class).fetchLikedUsers(pageNo,id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.sparkLike.setDataLoadingFromServerCompleted();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (page == 0) {
                            userInfos.clear();
                            userInfos.addAll(response.body().getUsers());
                            likesAdapter = new LikesAdapter(mContext, userInfos);
                            binding.sparkLike.setLayoutManager(new LinearLayoutManager(mContext));
                            binding.sparkLike.setAdapter(likesAdapter);
                            likesAdapter.setOnItemClickListeners(LikesFragment.this);
                            binding.sparkLike.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    getLikes(page, post.getId());
                                }
                            });

                        } else {
                            if (response.body().getUsers().size() == 0) {
                                likesAdapter.setFooterView(null);
                                likesAdapter.notifyDataSetChanged();
                                binding.sparkLike.addLoadMoreListener(null);
                                likesAdapter.setLoadMoreEnabled(false);
                            }
                            userInfos.addAll(response.body().getUsers());
                            likesAdapter.notifyDataSetChanged();

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
                binding.sparkLike.setDataLoadingFromServerCompleted();

            }
        });

    }

    @Override
    public void onItemClick(View view, final int position) {
        if(view.getId()== R.id.btn_follow) {
            if (((TextView) view).getText().toString().equalsIgnoreCase("Follow"))
                sendFollowRequest((UserInfo) likesAdapter.getList().get(position), position);
            else {

                dialogConfirmation("Are you sure you want to unfollow?", new Confirmation() {
                    @Override
                    public void onYes() {
                        unfollowUser((UserInfo) likesAdapter.getList().get(position), position);
                        super.onYes();
                    }

                    @Override
                    public void onNo() {
                        super.onNo();
                    }
                });

            }
        }
        else{
            ProfileNewFragment fragment = new ProfileNewFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstants.EXTRA_OBJECT, (UserInfo) likesAdapter.getList().get(position));
            bundle.putString(AppConstants.EXTRA_DATA, (String.valueOf(((BaseActivity)getActivity()).getActionBarTitle())));
            fragment.setArguments(bundle);
            replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment, true);

        }


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

                            ((UserInfo) likesAdapter.getList().get(position)).setFollowed(false);
                            likesAdapter.notifyItemChanged(position);

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
                            ((UserInfo) likesAdapter.getList().get(position)).setFollowed(true);
                            likesAdapter.notifyItemChanged(position);
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


    @Override
    public void onStop() {
        super.onStop();
        ((HomeActivity) getActivity()).setTitle(title);
        ((HomeActivity) getActivity()).hideBackButton();


    }
}
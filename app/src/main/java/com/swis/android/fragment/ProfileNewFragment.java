package com.swis.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.CommentActivity;
import com.swis.android.activity.HomeActivity;
import com.swis.android.adapter.SparkHomePostAdapter;
import com.swis.android.base.BaseActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentNewProfileBinding;
import com.swis.android.databinding.ProfileHeaderBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.listeners.PostDeleteListeners;
import com.swis.android.listeners.PostRefreshListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.model.responsemodel.Websites;
import com.swis.android.util.AppConstants;
import com.swis.android.util.PrefsHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileNewFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, OnItemClickListeners, PostDeleteListeners {

    private FragmentNewProfileBinding binding;
    private ProfileHeaderBinding headerBinding;
    private UserInfo userInfo;
    private String title;
    private SparkHomePostAdapter postAdapter;
    private ArrayList<Post> posts = new ArrayList<>();
    private int postPage;
    private String userId, local,userName;
    private int position = -1;
    private PostRefreshListeners listeners;
    private boolean isBookmarkedTab,isOtherProfile;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_profile;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentNewProfileBinding) views;

        binding.swipeLayout.setOnRefreshListener(this);

        headerBinding = ProfileHeaderBinding.inflate(LayoutInflater.from(mContext));
        postAdapter = new SparkHomePostAdapter(mContext, posts, true);
        postAdapter.setHeaderView(headerBinding.getRoot());
        postAdapter.setOnDeletePostListeners(ProfileNewFragment.this);
        binding.sparkPost.setLayoutManager(new LinearLayoutManager(mContext));
        binding.sparkPost.setAdapter(postAdapter);

        bundle = getArguments();
        if (bundle != null) {
            userInfo = bundle.getParcelable(AppConstants.EXTRA_OBJECT);
            isOtherProfile=userInfo!=null;
            title = bundle.getString(AppConstants.EXTRA_DATA);
            userId=bundle.getString(AppConstants.EXTRA_ID);
            userName=bundle.getString(AppConstants.USER_NAME);
            if(!TextUtils.isEmpty(userId))
            {
                isOtherProfile=true;
            }
            if(!TextUtils.isEmpty(userName))
            {
                isOtherProfile=true;
            }
        }
        if (userInfo == null && TextUtils.isEmpty(userId) && TextUtils.isEmpty(userName) ) {
            userInfo = prefHelper.getUserInfo();
            setUpView(userInfo);
        }
        listeners = new PostRefreshListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (position >= 0) {
                    bundle = intent.getExtras();
                    Post latestPost = (Post) bundle.get(AppConstants.EXTRA_OBJECT);
                    posts.get(position).setComments(latestPost.getComments());
                    posts.get(position).setComment_count(latestPost.getComment_count());
                    postAdapter.notifyItemChanged(position + 1);
                    position = -1;
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(listeners, new IntentFilter(AppConstants.REFRESH_POST_AT_PARTICULAR_POSITION));
        if(TextUtils.isEmpty(userId) && userInfo!=null)
            userId = userInfo.getId();


        if(TextUtils.isEmpty(userName))
            getUserDetails(userId);
        else
            getUserId(userName);



        headerBinding.imgSetting.setOnClickListener(this);
        headerBinding.llFollowers.setOnClickListener(this);
        headerBinding.llFollowings.setOnClickListener(this);
        headerBinding.btnFollow.setOnClickListener(this);
        headerBinding.btnBookmarks.setOnClickListener(this);
        headerBinding.btnSearched.setOnClickListener(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(isOtherProfile)
            ((HomeActivity) getActivity()).hideBackButton();
    }

    private void getUserId(String name) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getUserDetailsByName(name.replace("@",""));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response != null && response.body() != null) {

                    if (response.body().getResponseCode() == 200) {
                        UserInfo userInfo=response.body().getUser();

                        if(userInfo!=null)
                            getUserDetails(userInfo.getId());
                    }
                    else
                        showToast(response.body().getResponseMessage());
                }
                else
                    showToast("something went wrong");

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("failure");
            }
        });
    }

    private void getUserDetails(final String id) {
        binding.swipeLayout.setRefreshing(true);
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getUserDetails(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.swipeLayout.setRefreshing(false);

                if (response != null && response.body() != null) {

                    if (response.body().getResponseCode() == 200) {
                        userInfo = response.body().getUser();

                        userId = userInfo.getId();
                        headerBinding.btnSearched.performClick();
                        if (!prefHelper.getUserInfo().getId().equalsIgnoreCase(userInfo.getId())) {
                            headerBinding.btnFollow.setText(userInfo.isFollowed() ? "Unfollow" : "Follow");
                            headerBinding.btnFollow.setVisibility(View.VISIBLE);
                            headerBinding.imgSetting.setVisibility(View.GONE);
                        } else {
                            headerBinding.btnFollow.setVisibility(View.GONE);
                            headerBinding.imgSetting.setVisibility(View.VISIBLE);

                        }
                        if (prefHelper.getUserInfo().getId().equalsIgnoreCase(id)) {
                            prefHelper.saveUserInfo(userInfo);
                        }
                        setUpView(userInfo);
                    } else
                        showToast(response.body().getResponseMessage());
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                binding.swipeLayout.setRefreshing(true);
                showToast(AppConstants.SERVER_ERROR);

            }
        });
    }

    private void setUpView(UserInfo userInfo) {

        if (prefHelper.isPrefExists(PrefsHelper.BACK_IMG_UPDATED)) {
            prefHelper.delete(PrefsHelper.BACK_IMG_UPDATED);
            Picasso.get().invalidate(userInfo.getBackground_url());
            Picasso.get().load(userInfo.getBackground_url()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(headerBinding.imgBack);
        } else
            Picasso.get().load(userInfo.getBackground_url()).into(headerBinding.imgBack);

        if (prefHelper.isPrefExists(PrefsHelper.PRO_IMG_UPDATED)) {

            prefHelper.delete(PrefsHelper.PRO_IMG_UPDATED);
            Glide.with(mContext).load(userInfo.getAvatar()).placeholder(R.drawable.default_user).into(headerBinding.ivProfile);    }
        else {
            Glide.with(mContext).load(userInfo.getAvatar()).placeholder(R.drawable.default_user).into(headerBinding.ivProfile);
        }
        headerBinding.tvAdd.setText("Lives in " + (TextUtils.isEmpty(userInfo.getCity()) ? "" : userInfo.getCity()) + ", " + (TextUtils.isEmpty(userInfo.getCity()) ? "" : userInfo.getCountry()));
        headerBinding.tvUsername.setText("@" + userInfo.getUsername());
        headerBinding.setUserInfo(userInfo);
        userId = userInfo.getId();

    }


    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setTitle("Profile");
        ((HomeActivity) getActivity()).actionBarSetup();
        if(isOtherProfile)
            ((HomeActivity) getActivity()).showBackButton();
    }


    private void getPost(int pageNo, final String userId, final String local) {
        if (pageNo == 0)
            binding.swipeLayout.setRefreshing(true);
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
                                    getPost(postPage, userId, local);
                                }
                            });

                            postAdapter.setOnItemClickListeners(ProfileNewFragment.this);
                            postAdapter.showDeleteOption(true);
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

    private void fetchBookmark(int pageNo, final String userId) {
        if (pageNo == 0)
            binding.swipeLayout.setRefreshing(true);
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchFavouritesPost(pageNo, userId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.sparkPost.setDataLoadingFromServerCompleted();
                binding.swipeLayout.setRefreshing(false);
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (postPage == 0) {
                            posts.clear();
                            posts.addAll(response.body().getFavourites());
                            binding.sparkPost.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    fetchBookmark(postPage, userId);
                                }
                            });

                            postAdapter.setOnItemClickListeners(ProfileNewFragment.this);
                            postAdapter.showDeleteOption(false);
                            postAdapter.notifyDataSetChanged();


                        } else {
                            if (response.body().getFavourites().size() == 0) {
                                postAdapter.setFooterView(null);
                                postAdapter.notifyDataSetChanged();
                                binding.sparkPost.addLoadMoreListener(null);
                                postAdapter.setLoadMoreEnabled(false);
                            }
                            posts.addAll(response.body().getFavourites());
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
            case R.id.img_setting:
                Fragment fragment = new ProfileSettingFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(AppConstants.EXTRA_DATA, true);
                fragment.setArguments(bundle);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment, true);
                break;
            case R.id.ll_followers:
                Fragment fragment1 = new FollowersFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable(AppConstants.EXTRA_OBJECT, userInfo);
                fragment1.setArguments(bundle1);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment1, true);
                break;
            case R.id.ll_followings:
                Fragment fragment2 = new FollowingFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable(AppConstants.EXTRA_OBJECT, userInfo);
                fragment2.setArguments(bundle2);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment2, true);
                break;
            case R.id.btn_follow:
                if (headerBinding.btnFollow.getText().toString().equalsIgnoreCase("unfollow"))
                    unfollowUser(userInfo.getId());
                else
                    sendFollowRequest(userInfo);
                break;

            case R.id.btn_bookmarks:
                isBookmarkedTab = true;
                headerBinding.btnSearched.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle));
                headerBinding.btnSearched.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                headerBinding.btnBookmarks.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_filled));
                headerBinding.btnBookmarks.setTextColor(mContext.getResources().getColor(R.color.white));
                postPage = 0;
                posts.clear();
                postAdapter.notifyDataSetChanged();
                fetchBookmark(postPage, userId);
                break;

            case R.id.btn_searched:
                isBookmarkedTab = false;
                headerBinding.btnBookmarks.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle));
                headerBinding.btnBookmarks.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                headerBinding.btnSearched.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_filled));
                headerBinding.btnSearched.setTextColor(mContext.getResources().getColor(R.color.white));
                postPage = 0;
                posts.clear();
                postAdapter.notifyDataSetChanged();
                getPost(postPage, userId, local);
                break;


        }
    }

    private void unfollowUser(String id) {

        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).unfollowUser(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        Intent pushNotification = new Intent(AppConstants.REFRESH_POSTS);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
                        showToast(response.body().getResponseMessage());
                        headerBinding.btnFollow.setText("Follow");
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

    private void sendFollowRequest(UserInfo userInfo) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).sendRequest(userInfo.getId());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        showToast(response.body().getResponseMessage());
                        headerBinding.btnFollow.setText("Unfollow");

                        if (response.body().getDetails().get(0).getStatus().equalsIgnoreCase("approved")) {
                            Intent pushNotification = new Intent(AppConstants.REFRESH_POSTS);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
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
    public void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(title)) {
            ((HomeActivity) getActivity()).setTitle(title);
        }
    }

    @Override
    public void onRefresh() {
        getUserDetails(userInfo.getId());
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.img_like:
                int count = posts.get(position).getLike_count();
                posts.get(position).setLike(!posts.get(position).isLike());
                posts.get(position).setLike_count(posts.get(position).isLike() ? count + 1 : count - 1);
                postAdapter.notifyItemChanged(position + 1);
                likePost(String.valueOf(posts.get(position).getId()), position);
                break;
            case R.id.ll_bookmark:
                posts.get(position).setFavourite(!posts.get(position).isFavourite());
                if (isBookmarkedTab) {
                    markFavourite(String.valueOf(posts.get(position).getId()), position);
                    posts.remove(position);
                    postAdapter.notifyItemRemoved(position + 1);
                } else {
                    postAdapter.notifyItemChanged(position + 1);
                    markFavourite(String.valueOf(posts.get(position).getId()), position);

                }
                break;
            case R.id.profile_Pic:
                if(!userId.equalsIgnoreCase(((Post) postAdapter.getList().get(position)).getUser().getId())){
                ProfileNewFragment fragment = new ProfileNewFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable(AppConstants.EXTRA_OBJECT, ((Post) postAdapter.getList().get(position)).getUser());
                bundle2.putString(AppConstants.EXTRA_DATA, (String.valueOf(((BaseActivity) getActivity()).getActionBarTitle())));
                fragment.setArguments(bundle2);
                addFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment, true);
            }
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

    private void deletePost(final int postPos, final int webPos) {
        showProgressDialog();
        Call<ApiResponse> call = null;
        if (webPos == -1)
            call = ApiClient.getClient().create(ApiInterface.class).deletePost(String.valueOf(((Post) postAdapter.getList().get(postPos)).getId()));
        else
            call = ApiClient.getClient().create(ApiInterface.class).deletePostWebsite(String.valueOf(((Post) postAdapter.getList().get(postPos)).getId()), String.valueOf(((Websites) ((Post) postAdapter.getList().get(postPos)).getWebsites().get(webPos)).getId()));

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (webPos == -1) {
                            postAdapter.getList().remove(postPos);
                        }
                        else {
                            ((Post) postAdapter.getList().get(postPos)).getWebsites().remove(webPos);
                        }

                        postAdapter.notifyDataSetChanged();

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


    private void goToCommentsPage(int position,boolean flag) {
  /*      this.position = position;
        CommentActivity commentActivity = new CommentActivity();
        Bundle bundle4 = new Bundle();
        bundle4.putParcelable(AppConstants.EXTRA_OBJECT, posts.get(position));
        bundle4.putString(AppConstants.EXTRA_DATA, (String.valueOf(((BaseActivity) getActivity()).getActionBarTitle())));
        bundle4.putBoolean(AppConstants.EXTRA_TITLE, flag);
        commentActivity.setArguments(bundle4);
        addFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), commentActivity, true);
 */
        this.position = position;
        bundle = new Bundle();
        bundle.putParcelable(AppConstants.EXTRA_OBJECT, posts.get(position));
        bundle.putBoolean(AppConstants.EXTRA_TITLE, flag);
        CommentActivity.start(mContext,bundle);
    }

    @Override
    public void onItemClick(int postPosition, int webSitePos) {

        deletePost(postPosition, webSitePos);
    }
}

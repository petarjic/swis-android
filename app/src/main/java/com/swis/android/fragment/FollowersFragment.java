package com.swis.android.fragment;


import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.adapter.SparkFollowersRequestAdapter;
import com.swis.android.base.BaseFragment;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentFollowersBinding;
import com.swis.android.listeners.CustomTextChangeListener;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Confirmation;
import com.swis.android.util.UiDialog;
import com.swis.android.util.Util;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends BaseFragment implements OnItemClickListeners, SwipeRefreshLayout.OnRefreshListener {

    private FragmentFollowersBinding binding;
    private int page;
    private String query = "";
    private String id;
    private ArrayList<UserInfo> followers;
    private SparkFollowersRequestAdapter adapter;
    private Dialog dialog;
    private UserInfo userInfo;
    private boolean showMorebutton;
    private boolean isSearched;

    public FollowersFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_followers;
    }

    @Override
    protected void onViewBinded(final ViewDataBinding views) {
        binding = (FragmentFollowersBinding) views;
        bundle = getArguments();
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
                    if (binding.searchView.getText().toString().length() > 0) {
                        page = 0;
                        isSearched = true;
                        getFollowers(page, binding.searchView.getText().toString(), id);
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
                    if (isSearched) {
                        page = 0;
                        isSearched = false;
                        getFollowers(page, binding.searchView.getText().toString(), id);

                    }

                }


                binding.imgDelete.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

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
        ((HomeActivity) getActivity()).setTitle("Followers");
        ((HomeActivity) getActivity()).showBackButton();
        page = 0;
        getFollowers(page, query, id);

    }

    private void getFollowers(int pageNo, final String query, final String id) {
        if (pageNo == 0)
            binding.swipeLayout.setRefreshing(true);
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).getFollowers(pageNo, id, query);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                binding.swipeLayout.setRefreshing(false);
                binding.sparkView.setDataLoadingFromServerCompleted();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (page == 0) {
                            if (followers != null)
                                followers.clear();
                            followers = response.body().getFollowers();
                            if (prefHelper.getUserInfo().getId().equalsIgnoreCase(id)) {
                                showMorebutton = true;
                            }
                            adapter = new SparkFollowersRequestAdapter(mContext, followers, showMorebutton);
                            binding.sparkView.setLayoutManager(new LinearLayoutManager(mContext));
                            binding.sparkView.setAdapter(adapter);
                            adapter.setOnItemClickListeners(FollowersFragment.this);
                            binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    getFollowers(page, query, id);
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
    public void onDestroy() {
        super.onDestroy();
        ((HomeActivity) getActivity()).hideBackButton();

    }


    @Override
    public void onItemClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.img_action:
                dialogRemove((UserInfo) adapter.getList().get(position), position);
                break;
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
                bundle.putString(AppConstants.EXTRA_DATA, "Followers");
                fragment.setArguments(bundle);
                replaceFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment, true);
                break;

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

    protected void dialogRemove(final UserInfo userInfo, final int position) {

        dialog = UiDialog.getBottomDialogFixed(mContext, R.layout.dialog_remove_followers);


        TextView tvOkay = (TextView) dialog.findViewById(R.id.btn_remove);
        TextView tvNO = (TextView) dialog.findViewById(R.id.btn_cancel);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.iv_profile);
        Glide.with(mContext).load(userInfo.getAvatar()).placeholder(R.drawable.default_user).into(imageView);
        tvOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFollower(userInfo.getId(), position);
                dialog.dismiss();
            }
        });
        tvNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void removeFollower(String id, final int position) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).removeFollower(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        Intent pushNotification = new Intent(AppConstants.REFRESH_POSTS);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
                        showToast(response.body().getResponseMessage());
                        UserInfo userInfo = ((UserInfo) adapter.getList().remove(position));
                        adapter.notifyItemRemoved(position);
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
    public void onRefresh() {
        page = 0;
        getFollowers(page, query, id);
    }
}

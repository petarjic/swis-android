package com.swis.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.activity.InviteFriendActivity;
import com.swis.android.adapter.SparkRecommendedAdapter;
import com.swis.android.base.BaseActivity;
import com.swis.android.base.BaseFragment;
import com.swis.android.listeners.CustomTextChangeListener;
import com.swis.android.listeners.HomePageFollowersRefreshListeners;
import com.swis.android.util.GridDividerDecoration;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.FragmentRecommendedBinding;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.model.responsemodel.UserInfoResponseModel;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedFragment extends BaseFragment implements SparkRecyclerViewLatest.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FragmentRecommendedBinding binding;
    private SparkRecommendedAdapter adapter;
    private ArrayList<UserInfo> usersInfos;
    private int page,position=-1;
    private boolean isSearched;
    private boolean isOpened = false;
    private String queryText;
    GridDividerDecoration itemDecorator;
    private HomePageFollowersRefreshListeners refreshListeners;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommended;
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentRecommendedBinding) views;

        binding.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteFriendActivity.Companion.start(mContext, new Bundle());
            }
        });
        binding.swipeLayout.setOnRefreshListener(this);
        getRecommendeUser(page, "");
        if(getActivity()!=null)
        setListenerToRootView(binding.getRoot());

        refreshListeners = new HomePageFollowersRefreshListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(position>-1) {
                    adapter.getList().remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(refreshListeners, new IntentFilter(AppConstants.REFRESH_POSTS));




        binding.searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (binding.searchView.getText().toString().length() > 0) {
                        page = 0;
                        isSearched = true;
                        queryText=binding.searchView.getText().toString();
                        getRecommendeUser(page,queryText);
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
                        queryText="";
                        getRecommendeUser(page, queryText);


                    }

                }


                binding.imgDelete.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

            }
        });

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchView.setText("");
            }
        });
    }


    public void setListenerToRootView(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                try {
                    if (view.getRootView().getHeight() - (r.bottom - r.top) > 500) { // if more than 100 pixels, its probably a keyboard...

                        ((HomeActivity) getActivity()).hideOrShowBottomNavigationBar(false);
                    } else {
                        ((HomeActivity) getActivity()).hideOrShowBottomNavigationBar(true);
                    }
                }
                catch (Exception e)
                {

                }
            }
        });
    }

    private void getRecommendeUser(int index, final String query) {
            binding.swipeLayout.setRefreshing(true);
        Call<UserInfoResponseModel> call = ApiClient.getClient().create(ApiInterface.class).getRecommendedUser(index, query);
        call.enqueue(new Callback<UserInfoResponseModel>() {
            @Override
            public void onResponse(Call<UserInfoResponseModel> call, Response<UserInfoResponseModel> response) {
                binding.sparkView.setDataLoadingFromServerCompleted();
                binding.swipeLayout.setRefreshing(false);
                if (response.body().getResponseCode().equalsIgnoreCase("200")) {
                    if (page == 0) {
                        if(usersInfos!=null && usersInfos.size()>0)
                            usersInfos.clear();
                        usersInfos = response.body().getRecommendedUser();
                        adapter = new SparkRecommendedAdapter(mContext, usersInfos);
                        adapter.setOnItemClickListener(RecommendedFragment.this);
                        binding.sparkView.setLayoutManager(new GridLayoutManager(mContext, 2));
                        if (usersInfos.size() > 0) {
                            itemDecorator=new GridDividerDecoration(mContext);
                            binding.sparkView.addItemDecoration(itemDecorator);
                        }
                        else {
                            removeDecorator();
                        }
                        binding.sparkView.setAdapter(adapter);
                        adapter.setOnItemClickListener(RecommendedFragment.this);
                        binding.sparkView.setFooterViewOnLoading(false);
                        if(usersInfos.size()>10)
                        binding.sparkView.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                getRecommendeUser(page, queryText);
                            }
                        });

                    } else {
                        if (response.body().getRecommendedUser().size() == 0) {
                            adapter.setFooterView(null);
                            adapter.notifyDataSetChanged();
                            binding.sparkView.addLoadMoreListener(null);
                            adapter.setLoadMoreEnabled(false);
                        }
                        usersInfos.addAll(response.body().getRecommendedUser());
                        adapter.notifyDataSetChanged();
                    }

                    page = Integer.parseInt(response.body().getNextPage());

                }
            }

            @Override
            public void onFailure(Call<UserInfoResponseModel> call, Throwable t) {
                binding.swipeLayout.setRefreshing(false);
                binding.sparkView.setDataLoadingFromServerCompleted();

            }
        });
    }

    public void removeDecorator() {
       /* if(itemDecorator!=null)
        binding.sparkView.removeItemDecoration(itemDecorator);
        binding.sparkView.invalidateItemDecorations();
        binding.sparkView.getAdapter().notifyDataSetChanged();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setTitle("Friends");
        ((HomeActivity) getActivity()).actionBarSetup();

    }


    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.img_Del:
                try {
                    deleteRecommended(((UserInfo) adapter.getList().get(position)).getId(), position);
                }
                catch (Exception e)
                {

                }
                    break;

            case R.id.btn_follow:
                try {
                    sendFollowRequest(((UserInfo) adapter.getList().get(position)).getId(), position);
                }
                catch (Exception e)
                {

                }
                break;
            case R.id.profile_Pic:
                this.position=position;
                ProfileNewFragment fragment = new ProfileNewFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable(AppConstants.EXTRA_OBJECT,usersInfos.get(position));
                bundle2.putString(AppConstants.EXTRA_DATA, (String.valueOf(((BaseActivity) getActivity()).getActionBarTitle())));
                fragment.setArguments(bundle2);
                addFragmentWithAnimation(getActivity().findViewById(R.id.frame_layout).getId(), fragment, true);
                break;
        }
    }

    private void deleteRecommended(String id, final int position) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).deleteRecommended(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        showToast(response.body().getResponseMessage());
                        removeDecorator();
                        adapter.getList().remove(position);
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

    private void sendFollowRequest(String id, final int position) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).sendRequest(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dismissProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        removeDecorator();
                        showToast(response.body().getResponseMessage());
                        adapter.getList().remove(position);
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
        getRecommendeUser(page, "");

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(refreshListeners);
        super.onDestroy();
    }
}

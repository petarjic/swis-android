package com.swis.android.activity;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.adapter.CommentsAdapter;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.databinding.FragmentCommentBinding;
import com.swis.android.databinding.ItemCommentsHeaderBinding;
import com.swis.android.listeners.CloseActivityListeners;
import com.swis.android.listeners.CommentAdapterReceiver;
import com.swis.android.listeners.NavigationListeners;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentActivity extends MiddleBaseActivity implements OnItemClickListeners {

    private FragmentCommentBinding binding;
    private Post posts;
    private ArrayList<Post> comments = new ArrayList<>();
    private int page;
    private CommentsAdapter adapter;
    private ItemCommentsHeaderBinding commentsHeaderBinding;
    private CommentAdapterReceiver receiver;
    private String parent_post_id, post_id, userName,userId;
    private String comment_id,commented_post_id;
    private NavigationListeners listeners,like_page_nav_listener;
    private CloseActivityListeners closeActivityListeners;
    private UserInfo userInfo;
    private GradientDrawable bgShape;
    private boolean showKeyBoard,isCalled;



    @Override
    protected AppActionbarBinding getToolBar() {
        return binding.appBar;
    }

    @Override
    protected ActionBarData getScreenActionTitle() {
        return new ActionBarData(R.drawable.back_icon, "Comments");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_comment;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*((HomeActivity) getActivity()).setTitle("Comments");
        ((HomeActivity) getActivity()).hideOrShowBottomNavigationBar(false);
        ((HomeActivity) getActivity()).showBackButton();*/

    }



    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (FragmentCommentBinding) views;

        if (bundle != null) {
            posts = bundle.getParcelable(AppConstants.EXTRA_OBJECT);
           // title=bundle.getString(AppConstants.EXTRA_DATA);
            showKeyBoard=bundle.getBoolean(AppConstants.EXTRA_TITLE);
            comment_id =bundle.getString(AppConstants.EXTRA_ID);
        }
        bgShape = (GradientDrawable)binding.etComment.getBackground();
        int size= (int) mContext.getResources().getDimension(R.dimen.dp_5);
        bgShape.setCornerRadius(size);
        binding.etComment.setBackground(bgShape);
        if (posts != null) {

            parent_post_id = String.valueOf(posts.getId());
            post_id = String.valueOf(posts.getId());
           // userName = "@" + posts.getUser().getUsername();
            commentsHeaderBinding = ItemCommentsHeaderBinding.inflate(LayoutInflater.from(mContext));
            Glide.with(mContext).load(posts.getUser().getAvatar()).placeholder(R.drawable.default_user).into(commentsHeaderBinding.commentItemProfilePic);
            Glide.with(mContext).load(prefHelper.getUserInfo().getAvatar()).placeholder(R.drawable.default_user).into(binding.ciProfile);
            posts.setSender_id(posts.getId());
            commentsHeaderBinding.tvName.setText(Html.fromHtml(posts.getWebsites().get(0).getSearch_term()));



            fetchComments(page, posts.getId(), comment_id,true);


        }

        listeners=new NavigationListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle=intent.getExtras();
                      if(bundle!=null)
                      {

                              userInfo = bundle.getParcelable(AppConstants.EXTRA_OBJECT);
                              String username = bundle.getString(AppConstants.USER_NAME);
                              navigateToProfile(userInfo,username);

                      }
            }
        };

        like_page_nav_listener=new NavigationListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle=intent.getExtras();
                      if(bundle!=null)
                      {

                              Post posts = bundle.getParcelable(AppConstants.EXTRA_OBJECT);
                              navigateToLikePage(posts);

                      }
            }
        };
        closeActivityListeners=new CloseActivityListeners() {
            @Override
            public void onReceive(Context context, Intent intent) {
               finishAffinity();
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(closeActivityListeners, new IntentFilter(AppConstants.CLOSE_PREVIOUS_ACTIVITY));

        LocalBroadcastManager.getInstance(mContext).registerReceiver(listeners, new IntentFilter(AppConstants.NAVIGATE_TO_PROFILE));

        LocalBroadcastManager.getInstance(mContext).registerReceiver(like_page_nav_listener, new IntentFilter(AppConstants.NAVIGATE_TO_LIKE_PAGE));


        adapter = new CommentsAdapter(mContext, comments);
        adapter.setHeaderView(commentsHeaderBinding.getRoot());
        adapter.setOnItemClickListeners(CommentActivity.this);
        binding.sparkComment.setLayoutManager(new LinearLayoutManager(mContext));
        binding.sparkComment.setAdapter(adapter);
        binding.etComment.requestFocus();
        if(showKeyBoard) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        else
        {
            Util.hideSoftKeyboard(this);
        }


        receiver = new CommentAdapterReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                Post posts = bundle.getParcelable(AppConstants.EXTRA_OBJECT);
                if(posts!=null)
                {
                    commented_post_id=String.valueOf(posts.getId());
                }
                if (!TextUtils.isEmpty(bundle.getString(AppConstants.EXTRA_DATA))) {
                    post_id = bundle.getString(AppConstants.EXTRA_DATA);
                } else
                    post_id = String.valueOf(posts.getId());

                userName = "@" + posts.getUser().getUsername()+" ";
                userId=posts.getUser().getId();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                binding.etComment.setText(userName);
                binding.etComment.setSelection(userName.length());
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter(AppConstants.REFRESH_COMMENT_POST_ID));


        binding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = binding.etComment.getText().toString();

                if (!TextUtils.isEmpty(text.trim())) {
                    sendComments(post_id, text,userId,commented_post_id);
                }
            }
        });
    }



    private void fetchComments(int pageNo, final int post_id, final String comment_id, boolean flag) {
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).fetchComments(pageNo, comment_id, String.valueOf(post_id));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        if (page == 0) {
                            comments.clear();
                            comments.addAll(response.body().getComments());
                            binding.sparkComment.addLoadMoreListener(new SparkRecyclerViewLatest.OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    fetchComments(page, post_id, comment_id,false);
                                }
                            });

                            adapter.notifyDataSetChanged();

                            adapter.setInnerCommentView(0);



                    } else {
                            if (response.body().getComments().size() == 0) {
                                adapter.setFooterView(null);
                              //  adapter.notifyDataSetChanged();
                                binding.sparkComment.addLoadMoreListener(null);
                                adapter.setLoadMoreEnabled(false);
                            }
                            comments.addAll(response.body().getComments());
                            adapter.notifyDataSetChanged();

                        }

                        page = response.body().getNextPage();

                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideProgressDialog();
            }
        });
    }


    @Override
    protected void onDestroy() {
        Util.hideSoftKeyboard(this);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(listeners);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(closeActivityListeners);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(like_page_nav_listener);
        super.onDestroy();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.tv_reply) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        } else {
            Post posts = (Post) adapter.getList().get(position);
            int count = posts.getLikes_count();
            posts.setLike(!posts.isLike());
            posts.setLikes_count(posts.isLike() ? count + 1 : count - 1);
            adapter.notifyItemChanged(position+1);
            likePost(String.valueOf(posts.getId()), position);
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

    private void sendComments(String id, String comments, final String user_id, String commented_post_id) {
        showProgressDialog();
        Call<ApiResponse> call = ApiClient.getClient().create(ApiInterface.class).sendComment(parent_post_id, id, comments,user_id,commented_post_id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                hideProgressDialog();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode() == 200) {
                        post_id = String.valueOf(posts.getId());
                        userName = "@" + posts.getUser().getUsername();
                        userId="";
                        binding.etComment.setText("");
                        Util.hideSoftKeyboard(CommentActivity.this);

                        Bundle bundle=new Bundle();
                        bundle.putParcelable(AppConstants.EXTRA_OBJECT,response.body().getPost());
                        Intent pushNotification = new Intent(AppConstants.REFRESH_POST_AT_PARTICULAR_POSITION);
                        pushNotification.putExtras(bundle);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);

                        page = 0;
                        fetchComments(page, posts.getId(), "",false);

                    } else
                        showToast(response.body().getResponseMessage());
                } else
                    showToast(AppConstants.SOMETHING_WENT_WRONG);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                hideProgressDialog();
                showToast(AppConstants.SERVER_ERROR);
            }
        });
    }

    public void navigateToProfile(UserInfo userInfo,String username)
    {
        Util.hideSoftKeyboard(this);
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.NOTIFICATIONS.TYPE,5);
        if(userInfo!=null)
        bundle.putString(AppConstants.NOTIFICATIONS.ID,userInfo.getId());
        bundle.putBoolean(AppConstants.NAVIGATE_TO_PROFILE,true);
        bundle.putString(AppConstants.USER_NAME,username);
        Intent intent=new Intent(mContext,HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void navigateToLikePage(Post posts) {

        Util.hideSoftKeyboard(this);
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.NOTIFICATIONS.TYPE,1);
        bundle.putBoolean(AppConstants.NAVIGATE_TO_PROFILE,true);
        bundle.putParcelable(AppConstants.EXTRA_COMMENT,posts);
        Intent intent=new Intent(mContext,HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Util.hideSoftKeyboard(this);
        super.onBackPressed();
    }
}

package com.swis.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swis.android.ApiService.ApiClient;
import com.swis.android.ApiService.ApiInterface;
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemSubCommentsBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.ApiResponse;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InnerCommentsAdapter extends EasyAdapter {
    private Context mContext;
    private OnItemClickListeners listeners;
    private List<Post> comments;
    private int visibility_count;
    private int parentPostId;
    String username = "";

    public InnerCommentsAdapter(Context context, List list, int visibility_count, int id) {
        super(context, list);
        mContext = context;
        this.visibility_count = visibility_count;
        parentPostId = id;
    }

    @Override
    protected void onBindItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        username = "";
        comments = (List<Post>) (getList().subList(getList().size() - visibility_count, getList().size()));
        Glide.with(mContext).load(comments.get(position).getUser().getAvatar()).placeholder(R.drawable.default_user).into(((MyViewHolder) holder).binding.profilePic);
        ((MyViewHolder) holder).binding.tvName.setText(comments.get(position).getUser().getUsername());


        String comment = comments.get(position).getComment();
        SpannableString spannableString = new SpannableString(comment);

        // String s = "This is a sample sentence.";
        String[] words = comment.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            // You may want to check for a non-word character before blindly
            // performing a replacement
            // It may also be necessary to adjust the character class
            words[i] = words[i].replaceAll(" ", "");
        }

        for (int i = 0; i < words.length; i++) {
            if (words[i].startsWith("@")) {
                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.blue)), comment.indexOf(words[i]), comment.indexOf(words[i]) + words[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }

                    @Override
                    public void onClick(View view) {
                        TextView textView = (TextView) view;
                        Spanned s = (Spanned) textView.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        String user_name = String.valueOf(s.subSequence(start, end));
                        getUserDetails(user_name.replace("@", ""));
                    }
                }, comment.indexOf(words[i]), comment.indexOf(words[i]) + words[i].length(), 0);
            }

        }


        ((MyViewHolder) holder).binding.tvMsg.setText(spannableString);
        ((MyViewHolder) holder).binding.tvMsg.setMovementMethod(LinkMovementMethod.getInstance());

        ((MyViewHolder) holder).binding.tvLikes.setText(String.valueOf(comments.get(position).getLikes_count()) + " likes");
        ((MyViewHolder) holder).binding.tvLikes.setVisibility(comments.get(position).getLikes_count() > 0 ? View.VISIBLE : View.GONE);
        ((MyViewHolder) holder).binding.imgLike.setImageDrawable(comments.get(position).isLike() ? mContext.getResources().getDrawable(R.drawable.asset_31) : mContext.getResources().getDrawable(R.drawable.asset_27));
        String time = Util.getTime(Long.parseLong(Util.getTimestampFromDateStringUtc(comments.get(position).getCreated_at(), "yyyy-MM-dd HH:mm:ss")));
        ((MyViewHolder) holder).binding.tvTime.setText(time);

    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemSubCommentsBinding likesBinding = ItemSubCommentsBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(likesBinding);
    }

    public void setOnItemClickListeners(Fragment likesFragment) {
        listeners = (OnItemClickListeners) likesFragment;
    }


    @Override
    public int getItemCount() {


        if (getList() != null) {
            return visibility_count;
        } else
            return 0;

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemSubCommentsBinding binding;

        public MyViewHolder(ItemSubCommentsBinding likesBinding) {
            super(likesBinding.getRoot());
            binding = likesBinding;
            binding.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  navigateToProfilePage(getAdapterPosition());
                    binding.profilePic.performClick();

                }
            });
            binding.imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post posts = (Post) comments.get(getAdapterPosition());
                    int count = posts.getLikes_count();
                    posts.setLike(!posts.isLike());
                    posts.setLikes_count(posts.isLike() ? count + 1 : count - 1);
                    notifyItemChanged(getAdapterPosition());
                    likePost(String.valueOf(posts.getId()));
                }
            });
            binding.tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_reply) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AppConstants.EXTRA_OBJECT, (Post) comments.get(getAdapterPosition()));
                        bundle.putString(AppConstants.EXTRA_DATA, String.valueOf(parentPostId));
                        Intent pushNotification = new Intent(AppConstants.REFRESH_COMMENT_POST_ID);
                        pushNotification.putExtras(bundle);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
                    }

                }
            });

            binding.profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToProfilePage(getAdapterPosition());

                }
            });
            binding.tvLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToLikePage(getAdapterPosition());
                }
            });
        }


        private void likePost(String id) {
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


    }

    public void navigateToLikePage(int adapterPosition) {
        Post posts=(Post)getList().get(adapterPosition);
        Bundle bundle=new Bundle();
        bundle.putParcelable(AppConstants.EXTRA_OBJECT,posts);
        Intent pushNotification = new Intent(AppConstants.NAVIGATE_TO_LIKE_PAGE);
        pushNotification.putExtras(bundle);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
    }

    public void navigateToProfilePage(int adapterPosition) {
        Post posts = (Post) getList().get(adapterPosition);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.EXTRA_OBJECT, posts.getUser());
        Intent pushNotification = new Intent(AppConstants.NAVIGATE_TO_PROFILE);
        pushNotification.putExtras(bundle);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
    }

    private void getUserDetails(String name) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.USER_NAME, name);
        Intent pushNotification = new Intent(AppConstants.NAVIGATE_TO_PROFILE);
        pushNotification.putExtras(bundle);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);

    }
}

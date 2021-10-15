package com.swis.android.adapter;

import android.app.Activity;
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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemCommentsBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends EasyAdapter {
    private Context mContext;
    private OnItemClickListeners listeners;
    private ArrayList<Post> comments;
    private InnerCommentsAdapter adapter;

    public CommentsAdapter(Context context, List list) {
        super(context, list);
        mContext = context;
    }

    @Override
    protected void onBindItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        comments = (ArrayList<Post>) getList();
        Glide.with(mContext).load(comments.get(position).getUser().getAvatar()).placeholder(R.drawable.default_user).into(((MyViewHolder) holder).binding.profilePic);
        ((MyViewHolder) holder).binding.tvName.setText(comments.get(position).getUser().getUsername());
        String comment=comments.get(position).getComment();
        SpannableString spannableString=new SpannableString(comment);

        // String s = "This is a sample sentence.";
        String[] words = comment.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            // You may want to check for a non-word character before blindly
            // performing a replacement
            // It may also be necessary to adjust the character class
            words[i] = words[i].replaceAll(" ", "");
        }

        for(int i=0;i<words.length;i++)
        {
            if(words[i].startsWith("@"))
            {
                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.blue)), comment.indexOf(words[i]), comment.indexOf(words[i])+words[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }

                    @Override
                    public void onClick(View view) {
                        TextView textView= (TextView) view;
                        Spanned s = (Spanned) textView.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        String user_name= String.valueOf(s.subSequence(start,end));
                        getUserDetails(user_name.replace("@",""));                    }
                },comment.indexOf(words[i]), comment.indexOf(words[i])+words[i].length(),0);
            }

        }
        ((MyViewHolder) holder).binding.tvMsg.setText(spannableString);
        ((MyViewHolder) holder).binding.tvMsg.setMovementMethod(LinkMovementMethod.getInstance());

        String time = Util.getTime(Long.parseLong(Util.getTimestampFromDateStringUtc(comments.get(position).getCreated_at(), "yyyy-MM-dd HH:mm:ss")));
        ((MyViewHolder) holder).binding.tvTime.setText(time);
        ((MyViewHolder) holder).binding.tvLikes.setText(String.valueOf(comments.get(position).getLikes_count())+" likes");
        ((MyViewHolder) holder).binding.tvLikes.setVisibility(comments.get(position).getLikes_count()>0 ? View.VISIBLE:View.GONE);
        ((MyViewHolder)holder).binding.imgLike.setImageDrawable(comments.get(position).isLike() ? mContext.getResources().getDrawable(R.drawable.asset_31):mContext.getResources().getDrawable(R.drawable.asset_27));


        ((MyViewHolder) holder).binding.tvShowReply.setVisibility((comments.get(position).getComments().size()==1 && comments.get(position).getComments().size() == comments.get(position).getVisibility_count())? View.GONE:View.VISIBLE);

        if (comments.get(position).getComments().size() > 0) {
            ((MyViewHolder) holder).binding.tvShowReply.setText(comments.get(position).getComments().size() == comments.get(position).getVisibility_count() ? mContext.getResources().getString(R.string.hide_replies) : String.format(mContext.getResources().getString(R.string.view_previous_replies),String.valueOf(comments.get(position).getComments().size()-comments.get(position).getVisibility_count())));
            ((MyViewHolder) holder).binding.sparkComment1.setVisibility(View.VISIBLE);
            adapter = new InnerCommentsAdapter(mContext, comments.get(position).getComments(), comments.get(position).getVisibility_count(), comments.get(position).getId());
            ((MyViewHolder) holder).binding.sparkComment1.setLayoutManager(new LinearLayoutManager(mContext));
            ((MyViewHolder) holder).binding.sparkComment1.setAdapter(adapter);

        } else {
            ((MyViewHolder) holder).binding.sparkComment1.setVisibility(View.GONE);
            ((MyViewHolder)holder).binding.tvShowReply.setVisibility(View.GONE);
        }



    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemCommentsBinding likesBinding = ItemCommentsBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(likesBinding);
    }

    public void setOnItemClickListeners(Activity likesFragment) {
        listeners = (OnItemClickListeners) likesFragment;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemCommentsBinding binding;

        public MyViewHolder(ItemCommentsBinding likesBinding) {
            super(likesBinding.getRoot());
            binding = likesBinding;

            binding.tvShowReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.sparkComment1.setVisibility(View.VISIBLE);
                    toggleInnerComments(getAdapterPosition()-1);
                }
            });

            binding.imgLike.setOnClickListener(this);
            binding.tvReply.setOnClickListener(this);
            binding.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  binding.profilePic.performClick();
                }
            });
            binding.profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToProfilePage();

                }
            });

            binding.tvLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToLikePage();
                }
            });
        }

        public void navigateToProfilePage() {

            Post posts=(Post)getList().get(getAdapterPosition()-1);
            Bundle bundle=new Bundle();
            bundle.putParcelable(AppConstants.EXTRA_OBJECT,posts.getUser());
            Intent pushNotification = new Intent(AppConstants.NAVIGATE_TO_PROFILE);
            pushNotification.putExtras(bundle);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
        }

        public void navigateToLikePage() {

            Post posts=(Post)getList().get(getAdapterPosition()-1);
            Bundle bundle=new Bundle();
            bundle.putParcelable(AppConstants.EXTRA_OBJECT,posts);
            Intent pushNotification = new Intent(AppConstants.NAVIGATE_TO_LIKE_PAGE);
            pushNotification.putExtras(bundle);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);
        }




        @Override
        public void onClick(View v)
        {
            if(v.getId()==R.id.tv_reply) {
                Bundle bundle=new Bundle();
                bundle.putParcelable(AppConstants.EXTRA_OBJECT,(Post)getList().get(getAdapterPosition()-1));
                Intent pushNotification = new Intent(AppConstants.REFRESH_COMMENT_POST_ID);
                pushNotification.putExtras(bundle);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);

            }
            if(listeners!=null)
            listeners.onItemClick(v,getAdapterPosition()-1);
        }
    }

    public void toggleInnerComments(int position) {
        if(comments==null)
         comments= (ArrayList<Post>) getList();
        if(comments.get(position).getVisibility_count() == comments.get(position).getComments().size()){
            comments.get(position).setVisibility_count(1);
            notifyItemChanged(position+1);
            return;
        }
        if (comments.get(position).getComments().size() - comments.get(position).getVisibility_count() > 3)
            comments.get(position).setVisibility_count(comments.get(position).getVisibility_count() + 3);
        else {
            comments.get(position).setVisibility_count(comments.get(position).getComments().size());
        }
        notifyItemChanged(position+1);
    }

    public void setInnerCommentView(int position)
    {
        if(comments==null)
            comments= (ArrayList<Post>) getList();
        if(comments!=null && comments.size()>0 && comments.get(position).getComments()!=null && comments.get(position).getComments().size()>0) {
            comments.get(position).setVisibility_count(1);
            notifyItemChanged(position);
        }
    }

    private void getUserDetails(String name) {
        Bundle bundle=new Bundle();
        bundle.putString(AppConstants.USER_NAME,name);
        Intent pushNotification = new Intent(AppConstants.NAVIGATE_TO_PROFILE);
        pushNotification.putExtras(bundle);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification);

    }

}

package com.swis.android.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.swis.android.R;
import com.swis.android.activity.BrowserActivity;
import com.swis.android.activity.HomeActivity;
import com.swis.android.activity.SearchActivity;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemPostBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.listeners.PostDeleteListeners;
import com.swis.android.model.responsemodel.Post;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.model.responsemodel.Websites;
import com.swis.android.util.AppConstants;
import com.swis.android.util.PrefsHelper;
import com.swis.android.util.UiDialog;
import com.swis.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SparkHomePostAdapter extends EasyAdapter {
    private Context mContext;
    private UserInfo userInfo;
    private Post posts;
    private boolean isShow;
    private OnItemClickListeners listeners;
    private PostDeleteListeners deleteListeners;
    private ArrayList<Websites> list;
    private int webPosition;
    private ImageAdapter imageAdapter;

    public SparkHomePostAdapter(Context context, List list, boolean isShow) {
        super(context, list);
        mContext = context;
        this.isShow = isShow;
    }

    @Override
    protected void onBindItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        posts = (Post) getList().get(position);
        userInfo = posts.getUser();
        String time = Util.getTime(Long.parseLong(Util.getTimestampFromDateStringUtc(posts.getCreated_at(), "yyyy-MM-dd HH:mm:ss")));

        ((MyViewHolder) holder).binding.tvCount.setText(time);

        Glide.with(mContext).load(userInfo.getAvatar()).placeholder(R.drawable.default_user).into(((MyViewHolder) holder).binding.profilePic);

        if (posts.getComments().size() > 0) {
            ((MyViewHolder) holder).binding.llComments.setVisibility(View.VISIBLE);

            Glide.with(mContext).load(posts.getComments().get(posts.getComments().size()-1).getUser().getAvatar()).placeholder(R.drawable.default_user).into(((MyViewHolder) holder).binding.profile);
        } else {
            ((MyViewHolder) holder).binding.llComments.setVisibility(View.GONE);
        }
        Glide.with(mContext).load(PrefsHelper.getPrefsHelper().getUserInfo().getAvatar()).placeholder(R.drawable.default_user).into(((MyViewHolder) holder).binding.profile1);
        ((MyViewHolder) holder).binding.imgLike.setImageDrawable(posts.isLike() ? mContext.getResources().getDrawable(R.drawable.asset_31) : mContext.getResources().getDrawable(R.drawable.asset_27));
        ((MyViewHolder) holder).binding.imgBookmark.setImageDrawable(posts.isFavourite() ? mContext.getResources().getDrawable(R.drawable.fav) : mContext.getResources().getDrawable(R.drawable.unfav));
        ((MyViewHolder) holder).binding.tvName.setText(TextUtils.isEmpty(posts.getWebsites().get(0).getSearch_term()) ? "" : Html.fromHtml(posts.getWebsites().get(0).getSearch_term()));
        ((MyViewHolder) holder).binding.tvCommentCount.setText(String.valueOf(posts.getComment_count()));
        ((MyViewHolder) holder).binding.tvLikeCount.setText(String.valueOf(posts.getLike_count()));
        ((MyViewHolder) holder).binding.tvDesc.setText(Html.fromHtml(posts.getWebsites().get(0).getTitle()));
        ((MyViewHolder) holder).binding.tvUrl.setText(Html.fromHtml(posts.getWebsites().get(0).getWebsite()));
        if (posts.getComments().size() > 0) {
            String comment = posts.getComments().get(posts.getComments().size()-1).getComment();
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
            ((MyViewHolder) holder).binding.tvComment.setText(spannableString);
            ((MyViewHolder) holder).binding.tvComment.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            ((MyViewHolder) holder).binding.tvComment.setText("comment");

        }
        imageAdapter = new ImageAdapter(mContext, posts.getWebsites());
        ((MyViewHolder) holder).binding.imageViewpager.setAdapter(imageAdapter);
        ((MyViewHolder) holder).binding.viewPagerIndicator.initWithViewPager(((MyViewHolder) holder).binding.imageViewpager);
        ((MyViewHolder) holder).binding.viewPagerIndicator.setVisibility(posts.getWebsites().size() > 1 ? View.VISIBLE : View.GONE);
        ((MyViewHolder) holder).binding.imgDelete.setVisibility(isShow ? View.VISIBLE : View.GONE);
        ((MyViewHolder) holder).binding.imageViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                webPosition = position;
                list = ((ImageAdapter) ((MyViewHolder) holder).binding.imageViewpager.getAdapter()).getList();
                ((MyViewHolder) holder).binding.tvName.setText(TextUtils.isEmpty(list.get(position).getSearch_term()) ? "" : Html.fromHtml(list.get(position).getSearch_term()));
                ((MyViewHolder) holder).binding.tvDesc.setText(Html.fromHtml(list.get(position).getTitle()));
                ((MyViewHolder) holder).binding.tvUrl.setText(Html.fromHtml(list.get(position).getWebsite()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getUserDetails(String name) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.NOTIFICATIONS.TYPE, 5);
        bundle.putString(AppConstants.NOTIFICATIONS.ID, "");
        bundle.putBoolean(AppConstants.NAVIGATE_TO_PROFILE, true);
        bundle.putString(AppConstants.USER_NAME,name);
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);


    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(mContext));
        return new MyViewHolder(binding);
    }

    public void setOnItemClickListeners(Fragment homeFragment) {
        listeners = (OnItemClickListeners) homeFragment;
    }

    public void setOnDeletePostListeners(Fragment profileNewFragment) {
        deleteListeners = (PostDeleteListeners) profileNewFragment;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemPostBinding binding;

        public MyViewHolder(ItemPostBinding view) {
            super(view.getRoot());
            binding = view;
            binding.llBookmark.setOnClickListener(this);
            binding.imgLike.setOnClickListener(this);
            binding.llShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Join SWIS app to see what " + PrefsHelper.getPrefsHelper().getUserInfo().getName() + " searched.");
                    i.putExtra(Intent.EXTRA_TEXT, "Join SWIS app to see what " + PrefsHelper.getPrefsHelper().getUserInfo().getName() + " searched. " + binding.tvUrl.getText().toString());
                    mContext.startActivity(Intent.createChooser(i, "Share via"));
                }
            });
            binding.tvLikeCount.setOnClickListener(this);
            binding.llViewComments.setOnClickListener(this);
            binding.llGiveComment.setOnClickListener(this);
            binding.tvViewAll.setOnClickListener(this);
            binding.profilePic.setOnClickListener(this);
            binding.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConstants.IS_NEW_SEARCH, true);
                    bundle.putString(AppConstants.EXTRA_DATA, binding.tvName.getText().toString());
                    SearchActivity.start(mContext, bundle);
                }
            });

            binding.llUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_OBJECT, binding.tvUrl.getText().toString());
                    BrowserActivity.start(mContext, bundle);
                }
            });

            binding.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogConfirmationSelectImage(getAdapterPosition() - 1);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (listeners != null)
                listeners.onItemClick(v, getAdapterPosition() - 1);
        }
    }

    private void dialogConfirmationSelectImage(final int adapterPosition) {
        final Dialog dialog = UiDialog.getBottomDialogFixed(mContext, R.layout.dialog_delete_post);
        dialog.show();
        TextView full_post = dialog.findViewById(R.id.btn_full_post);
        TextView single_post = dialog.findViewById(R.id.btn_single);
        LinearLayout layout = dialog.findViewById(R.id.ll_single);
        layout.setVisibility(((Post) getList().get(adapterPosition)).getWebsites().size() > 1 ? View.VISIBLE : View.GONE);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);

        full_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListeners != null)
                    deleteListeners.onItemClick(adapterPosition, -1);
                dialog.dismiss();
            }
        });

        single_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListeners != null)
                    deleteListeners.onItemClick(adapterPosition, webPosition);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    public void showDeleteOption(boolean isShow) {
        this.isShow = isShow;
    }


}

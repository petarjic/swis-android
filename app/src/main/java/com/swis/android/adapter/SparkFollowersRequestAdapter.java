package com.swis.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemFollowersBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.UserInfo;
import com.swis.android.util.PrefsHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SparkFollowersRequestAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<UserInfo> followers;
    private OnItemClickListeners onItemClickListeners;
    private boolean isShowMoreButton;
    private PrefsHelper prefsHelper;


    public SparkFollowersRequestAdapter(Context context, List list, boolean showMoreButton) {
        super(context, list);
        this.mContext=context;
        isShowMoreButton=showMoreButton;
        prefsHelper=PrefsHelper.getPrefsHelper();

    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        followers = (ArrayList<UserInfo>) getList();
        ImageView imageView=((MyViewHolder)holder).binding.ivProfile;
        Glide.with(mContext).load(followers.get(position).getAvatar()).placeholder(R.drawable.default_user).into(imageView);
        ((MyViewHolder)holder).binding.tvName.setText(followers.get(position).getName());
        ((MyViewHolder)holder).binding.tvUsername.setText("@"+followers.get(position).getUsername());
        ((MyViewHolder)holder).binding.btnFollow.setBackground(followers.get(position).isFollowed() ? mContext.getResources().getDrawable(R.drawable.cardviewbackgroundblue):mContext.getResources().getDrawable(R.drawable.cardviewbackground_blue_filled));
        ((MyViewHolder)holder).binding.btnFollow.setTextColor(followers.get(position).isFollowed() ? mContext.getResources().getColor(R.color.blue):mContext.getResources().getColor(R.color.white));
        ((MyViewHolder)holder).binding.btnFollow.setText(followers.get(position).isFollowed() ? "Following":"Follow");
        ((MyViewHolder)holder).binding.btnFollow.setVisibility(followers.get(position).getId().equalsIgnoreCase(prefsHelper.getUserInfo().getId()) ? View.GONE:View.VISIBLE);
        ((MyViewHolder)holder).binding.imgAction.setVisibility(isShowMoreButton ? View.VISIBLE:View.GONE);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemFollowersBinding binding=ItemFollowersBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }

    public void setOnItemClickListeners(Fragment fragment)
    {

        onItemClickListeners= (OnItemClickListeners) fragment;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemFollowersBinding binding;
        public MyViewHolder(@NonNull ItemFollowersBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
            binding.getRoot().setOnClickListener(this);
            binding.btnFollow.setOnClickListener(this);
            binding.imgAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListeners!=null)
                onItemClickListeners.onItemClick(v,getAdapterPosition());
        }
    }
}

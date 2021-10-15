package com.swis.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemLikesBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.UserInfo;
import com.squareup.picasso.Picasso;
import com.swis.android.util.PrefsHelper;

import java.util.ArrayList;
import java.util.List;

public class LikesAdapter extends EasyAdapter {
    private Context mContext;
    private OnItemClickListeners listeners;
    private ArrayList<UserInfo> userInfos;

    public LikesAdapter(Context context, List list) {
        super(context, list);
        mContext = context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        userInfos = (ArrayList<UserInfo>) getList();
        ((MyViewHolder) holder).binding.tvName.setText(userInfos.get(position).getName());
        ((MyViewHolder) holder).binding.tvUsername.setText("@"+userInfos.get(position).getUsername());
        ((MyViewHolder)holder).binding.btnFollow.setBackground(userInfos.get(position).isFollowed() ? mContext.getResources().getDrawable(R.drawable.cardviewbackgroundblue):mContext.getResources().getDrawable(R.drawable.cardviewbackground_blue_filled));
        ((MyViewHolder)holder).binding.btnFollow.setTextColor(userInfos.get(position).isFollowed() ? mContext.getResources().getColor(R.color.blue):mContext.getResources().getColor(R.color.white));
        ((MyViewHolder)holder).binding.btnFollow.setText(userInfos.get(position).isFollowed() ? "Following":"Follow");
        ((MyViewHolder)holder).binding.btnFollow.setVisibility(userInfos.get(position).getId().equalsIgnoreCase(PrefsHelper.getPrefsHelper().getUserInfo().getId()) ? View.GONE:View.VISIBLE);
        Glide.with(mContext).load(userInfos.get(position).getAvatar()).placeholder(R.drawable.default_user).into(((MyViewHolder) holder).binding.likeItemProfilePic);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemLikesBinding likesBinding = ItemLikesBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(likesBinding);
    }

    public void setOnItemClickListeners(Fragment likesFragment) {
        listeners= (OnItemClickListeners) likesFragment;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemLikesBinding binding;

        public MyViewHolder(ItemLikesBinding likesBinding) {
            super(likesBinding.getRoot());
            binding = likesBinding;
            binding.getRoot().setOnClickListener(this);
            binding.btnFollow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listeners!=null){
                listeners.onItemClick(v,getAdapterPosition());

            }
        }
    }
}

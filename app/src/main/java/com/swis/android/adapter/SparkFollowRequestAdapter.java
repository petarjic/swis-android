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
import com.swis.android.databinding.ItemFollowRequestBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SparkFollowRequestAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<UserInfo> followers;
    private OnItemClickListeners onItemClickListeners;


    public SparkFollowRequestAdapter(Context context, List list) {
        super(context, list);
        this.mContext=context;


    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        followers = (ArrayList<UserInfo>) getList();
        ImageView imageView=((MyViewHolder)holder).binding.ivProfile;
        Glide.with(mContext).load(followers.get(position).getAvatar()).placeholder(R.drawable.default_user).into(imageView);
        ((MyViewHolder)holder).binding.tvName.setText(followers.get(position).getName());
        ((MyViewHolder)holder).binding.tvUsername.setText("@"+followers.get(position).getUsername());
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemFollowRequestBinding binding=ItemFollowRequestBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }

    public void setOnItemClickListeners(Fragment fragment)
    {
        onItemClickListeners= (OnItemClickListeners) fragment;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemFollowRequestBinding binding;
        public MyViewHolder(@NonNull ItemFollowRequestBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
            binding.getRoot().setOnClickListener(this);
            binding.imgAccept.setOnClickListener(this);
            binding.imgReject.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListeners!=null)
                onItemClickListeners.onItemClick(v,getAdapterPosition());
        }
    }
}

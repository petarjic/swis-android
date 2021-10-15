package com.swis.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.ItemRecommendedBinding;
import com.swis.android.fragment.RecommendedFragment;
import com.swis.android.model.responsemodel.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SparkRecommendedAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<UserInfo> usersInfos;
    private SparkRecyclerViewLatest.OnItemClickListener listener;


    public SparkRecommendedAdapter(Context context, List list) {
        super(context, list);
        this.mContext=context;


    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        usersInfos = (ArrayList<UserInfo>) getList();
        ImageView imageView=((MyViewHolder)holder).binding.profilePic;
        Glide.with(mContext).load(usersInfos.get(position).getAvatar()).placeholder(R.drawable.default_user).into(imageView);
        ((MyViewHolder)holder).binding.name.setText(usersInfos.get(position).getName());
        ((MyViewHolder)holder).binding.tvBio.setText(usersInfos.get(position).getBio());
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemRecommendedBinding binding=ItemRecommendedBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }

    public void setOnItemClickListener(RecommendedFragment recommendedFragment) {
        listener= (SparkRecyclerViewLatest.OnItemClickListener) recommendedFragment;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemRecommendedBinding binding;
        public MyViewHolder(@NonNull ItemRecommendedBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
            binding.getRoot().setOnClickListener(this);
            binding.imgDel.setOnClickListener(this);
            binding.btnFollow.setOnClickListener(this);
            binding.profilePic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener!=null)
                listener.onItemClick(v,getAdapterPosition());
        }
    }
}

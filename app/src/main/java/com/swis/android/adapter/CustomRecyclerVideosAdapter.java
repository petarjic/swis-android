package com.swis.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemNewsBinding;
import com.swis.android.databinding.ItemVideosBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.SearchResult;
import com.swis.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerVideosAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<SearchResult> mList;
    private OnItemClickListeners listeners;

    public CustomRecyclerVideosAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        mList = (ArrayList<SearchResult>) getList();
        ((MyViewHolder) holder).binding.title.setText(mList.get(position).getTitle());
        ((MyViewHolder) holder).binding.tvTime.setText(mList.get(position).getDatetime());
        ((MyViewHolder) holder).binding.tvLabel.setText(mList.get(position).getProvider());
        ((MyViewHolder) holder).binding.tvDuration.setText(TextUtils.isEmpty(mList.get(position).getVideo_duration()) ? "" : mList.get(position).getVideo_duration());
        ((MyViewHolder) holder).binding.tvDuration.setVisibility(TextUtils.isEmpty(mList.get(position).getVideo_duration()) ? View.GONE : View.VISIBLE);
        Picasso.get().load(mList.get(position).getThumbnailUrl()).into(((MyViewHolder) holder).binding.profilePic);


    }

    public void setOnItemClickListeners(Activity activity) {
        listeners = (OnItemClickListeners) activity;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

        ItemVideosBinding binding = ItemVideosBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        ItemVideosBinding binding;

        public MyViewHolder(@NonNull ItemVideosBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.llVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listeners!=null)
                    listeners.onItemClick(v, getAdapterPosition());
                }
            });

        }


    }
}

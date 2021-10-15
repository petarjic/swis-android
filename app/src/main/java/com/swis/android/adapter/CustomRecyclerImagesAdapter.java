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
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemImagesVideosBinding;
import com.swis.android.databinding.ItemNewsBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.SearchResult;
import com.swis.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerImagesAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<SearchResult> mList;
    private OnItemClickListeners listeners;

    public CustomRecyclerImagesAdapter(Context context, List list) {
        super(context, list);
        this.mContext=context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        mList= (ArrayList<SearchResult>) getList();
        ((MyViewHolder)holder).binding.tvDesc.setText(Html.fromHtml(mList.get(position).getTitle()));
        ((MyViewHolder)holder).binding.tvUrl.setText(mList.get(position).getWebsite());
        if(!TextUtils.isEmpty(mList.get(position).getImage())) {
            Picasso.get().load(mList.get(position).getImage()).fit().centerCrop().into(((MyViewHolder) holder).binding.image);
            if(!TextUtils.isEmpty(mList.get(position).getType())){
                ((MyViewHolder)holder).binding.imgType.setVisibility(mList.get(position).getType().equalsIgnoreCase("video") ? View.VISIBLE:View.GONE);
            }
            else
            {
                ((MyViewHolder)holder).binding.imgType.setVisibility(View.GONE);

            }
        }
        else {
            Picasso.get().load(mList.get(position).getThumbnailUrl()).into(((MyViewHolder) holder).binding.image);
            ((MyViewHolder)holder).binding.imgType.setVisibility(View.VISIBLE);

        }


    }
    public void setOnItemClickListeners(Activity activity)
    {
        listeners= (OnItemClickListeners) activity;
    }
    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

        ItemImagesVideosBinding binding=ItemImagesVideosBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        ItemImagesVideosBinding binding;

        public MyViewHolder(@NonNull ItemImagesVideosBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listeners.onItemClick(v,getAdapterPosition());
                }
            });


        }


    }
}

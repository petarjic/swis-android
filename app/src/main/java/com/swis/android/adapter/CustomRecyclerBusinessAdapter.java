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
import com.swis.android.databinding.ItemBusinessBinding;
import com.swis.android.databinding.ItemNewsBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.Place;
import com.swis.android.model.responsemodel.SearchResult;
import com.swis.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerBusinessAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<Place> mList;
    private OnItemClickListeners listeners;

    public CustomRecyclerBusinessAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        mList = (ArrayList<Place>) getList();
        Place place = mList.get(position);
        ((MyViewHolder) holder).binding.tvCount1.setText(String.valueOf(position + 1));
        ((MyViewHolder) holder).binding.tvName1.setText(place.getName());
        ((MyViewHolder) holder).binding.tvDetails1.setText(place.getAddress().getNeighborhood() + (TextUtils.isEmpty(place.getAddress().getNeighborhood()) ? "": ", ") + place.getAddress().getLocality());
        ((MyViewHolder) holder).binding.busView.setVisibility(mList.size() - 1 == position ? View.GONE : View.VISIBLE);
    }

    public void setOnItemClickListeners(Activity activity) {
        listeners = (OnItemClickListeners) activity;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

        ItemBusinessBinding binding = ItemBusinessBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemBusinessBinding binding;

        public MyViewHolder(@NonNull ItemBusinessBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.llCall1.setOnClickListener(this);
            binding.llDirection1.setOnClickListener(this);
            binding.llWebsite1.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            if (listeners != null)
                listeners.onItemClick(v, getAdapterPosition());
        }
    }
}

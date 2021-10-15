package com.swis.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemPreviewBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.Preview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerPreviewAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<Preview> mList;
    private OnItemClickListeners listeners;

    public CustomRecyclerPreviewAdapter(Context context, List list) {
        super(context, list);
        mContext = context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        mList = (ArrayList<Preview>) getList();
        ((MyViewHolder) holder).binding.title.setText(mList.get(position).getTitle());
        Picasso.get().load(new File(mList.get(position).getFilePath())).into(((MyViewHolder) holder).binding.profilePic);

    }

    public void setOnItemClickListeners(Activity activity)
    {
      listeners= (OnItemClickListeners) activity;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

        ItemPreviewBinding binding = ItemPreviewBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemPreviewBinding binding;

        public MyViewHolder(@NonNull ItemPreviewBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(this);
            binding.imgDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listeners!=null)
            listeners.onItemClick(v,getAdapterPosition());
        }
    }
}

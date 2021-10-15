package com.swis.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemNewsBinding;
import com.swis.android.databinding.ItemSearchBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.SearchResult;
import com.swis.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerSearchAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<SearchResult> mList;
    private OnItemClickListeners listeners;

    public CustomRecyclerSearchAdapter(Context context, List list) {
        super(context, list);
        this.mContext=context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        mList= (ArrayList<SearchResult>) getList();
        ((MyViewHolder)holder).binding.tvDesc.setText(mList.get(position).getTitle());
        ((MyViewHolder)holder).binding.tvUrl.setText(mList.get(position).getWebsite());
        ((MyViewHolder)holder).binding.tvDetails.setText(mList.get(position).getDescription());

    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

        ItemSearchBinding binding=ItemSearchBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }

    public void setOnItemClickListeners(Activity activity)
    {
        listeners= (OnItemClickListeners) activity;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ItemSearchBinding binding;

        public MyViewHolder(@NonNull ItemSearchBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listeners!=null)
                    listeners.onItemClick(v,getAdapterPosition()-1);
                }
            });
            binding.tvDetails.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            Util.copyToClipboard((TextView) v,mContext);
            return false;
        }
    }
}

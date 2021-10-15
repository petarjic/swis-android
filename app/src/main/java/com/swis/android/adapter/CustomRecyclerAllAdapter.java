package com.swis.android.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.custom.customviews.textViews.GothamRoundedMed;
import com.swis.android.databinding.ItemAllBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.SearchResult;
import com.swis.android.util.AppConstants;
import com.swis.android.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerAllAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<SearchResult> mList, list;
    private OnItemClickListeners listeners;

    public CustomRecyclerAllAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        mList = (ArrayList<SearchResult>) getList();
        String text = null;
        if (position == 0) {
            ((MyViewHolder) holder).binding.llUrlNew.setVisibility(View.VISIBLE);
            ((MyViewHolder) holder).binding.cvUrl.setVisibility(View.GONE);
            Bundle bundle = mList.get(0).getBundle();
            list = bundle.getParcelableArrayList(AppConstants.EXTRA_OBJECT);


            Picasso.get().load(list.get(0).getImage()).into(((MyViewHolder) holder).binding.imageNew);
            if (list.get(0).getDescription().length() > 100) {
                text = list.get(0).getDescription().substring(0, 100).concat("...");

            } else {
                text = list.get(0).getDescription();

            }
            ((MyViewHolder) holder).binding.tvTitleNew.setText(Html.fromHtml(text));
            ((MyViewHolder) holder).binding.tvProviderNew.setText(Html.fromHtml(list.get(0).getProvider()));

            Picasso.get().load(list.get(1).getImage()).into(((MyViewHolder) holder).binding.imageTwo);
            if (list.get(1).getDescription().length() > 100) {
                text = list.get(1).getDescription().substring(0, 100).concat("...");

            } else {
                text = list.get(1).getDescription();

            }
            ((MyViewHolder) holder).binding.tvTitleTwo.setText(Html.fromHtml(text));
            ((MyViewHolder) holder).binding.tvProviderTwo.setText(Html.fromHtml(list.get(1).getProvider()));

            Picasso.get().load(list.get(2).getImage()).into(((MyViewHolder) holder).binding.imageThree);
            if (list.get(2).getDescription().length() > 100) {
                text = list.get(2).getDescription().substring(0, 100).concat("...");

            } else {
                text = list.get(2).getDescription();

            }
            ((MyViewHolder) holder).binding.tvTitleThree.setText(Html.fromHtml(text));
            ((MyViewHolder) holder).binding.tvProviderThree.setText(Html.fromHtml(list.get(2).getProvider()));
        } else {
            ((MyViewHolder) holder).binding.llUrlNew.setVisibility(View.GONE);
            ((MyViewHolder) holder).binding.cvUrl.setVisibility(View.VISIBLE);
            Picasso.get().load(mList.get(position).getImage()).into(((MyViewHolder) holder).binding.image);
            if (mList.get(position).getDescription().length() > 90) {
                text = mList.get(position).getDescription().substring(0, 90).concat("...");

            } else {
                text = mList.get(position).getDescription();

            }
            ((MyViewHolder) holder).binding.tvTitle.setText(Html.fromHtml(text));

            ((MyViewHolder) holder).binding.tvProvider.setText(TextUtils.isEmpty(mList.get(position).getProvider()) ? "":Html.fromHtml(mList.get(position).getProvider()));
        }
    }

    public void setOnItemClickListeners(Activity activity) {
        listeners = (OnItemClickListeners) activity;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

        ItemAllBinding binding = ItemAllBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemAllBinding binding;

        public MyViewHolder(@NonNull ItemAllBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            binding.getRoot().setOnClickListener(this);
            binding.llUrlOne.setOnClickListener(this);
            binding.llUrlThree.setOnClickListener(this);
            binding.llUrlTwo.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (listeners != null)
                listeners.onItemClick(v, getAdapterPosition());

        }


    }
}

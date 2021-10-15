package com.swis.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemAllBinding;
import com.swis.android.databinding.ItemNewsBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.SearchResult;
import com.swis.android.util.Util;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerNewsAdapter extends EasyAdapter {

    private Context mContext;
    private ArrayList<SearchResult> mList;
    private OnItemClickListeners listeners;

    public CustomRecyclerNewsAdapter(Context context, List list) {
        super(context, list);
        this.mContext=context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        mList= (ArrayList<SearchResult>) getList();
        String text=null;
        if(!TextUtils.isEmpty(mList.get(position).getImage())) {
            Picasso.get().load(mList.get(position).getImage()).into(((MyViewHolder) holder).binding.image);
            ((MyViewHolder)holder).binding.tvNewDesc.setVisibility(View.GONE);
            ((MyViewHolder)holder).binding.image.setVisibility(View.VISIBLE);
        }
        else
        {

                ((MyViewHolder)holder).binding.tvNewDesc.setText(HtmlCompat.fromHtml(mList.get(position).getDescription(),HtmlCompat.FROM_HTML_MODE_LEGACY));


            ((MyViewHolder)holder).binding.tvNewDesc.setVisibility(View.VISIBLE);
            ((MyViewHolder)holder).binding.image.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mList.get(position).getTitle()) && mList.get(position).getTitle().length()>90)
        {
            text=mList.get(position).getTitle().substring(0,90).concat("...");

        }
        else
        {
            text=mList.get(position).getTitle();

        }
        ((MyViewHolder)holder).binding.tvTitle.setText(Html.fromHtml(text));
        ((MyViewHolder)holder).binding.tvProvider.setText(Html.fromHtml(mList.get(position).getProvider()));
        ((MyViewHolder)holder).binding.tvTime.setText(mList.get(position).getDatetime());
        if(!TextUtils.isEmpty(mList.get(position).getProvider_icon())) {
            ((MyViewHolder)holder).binding.ivProIcon.setVisibility(View.VISIBLE);
            Picasso.get().load(mList.get(position).getProvider_icon()).into(((MyViewHolder) holder).binding.ivProIcon);
        }else
            ((MyViewHolder)holder).binding.ivProIcon.setVisibility(View.GONE);
    }
    public void setOnItemClickListeners(Activity activity)
    {
        listeners= (OnItemClickListeners) activity;
    }
    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

        ItemNewsBinding binding=ItemNewsBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        ItemNewsBinding binding;

        public MyViewHolder(@NonNull ItemNewsBinding itemView) {
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

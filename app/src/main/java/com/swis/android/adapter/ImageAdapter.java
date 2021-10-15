package com.swis.android.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.swis.android.activity.BrowserActivity;
import com.swis.android.databinding.ItemImageBinding;
import com.swis.android.model.responsemodel.Websites;
import com.swis.android.util.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<Websites> websites;

    ImageAdapter(Context context, ArrayList<Websites> websites) {
        this.mContext = context;
        this.websites=websites;
    }

    @Override
    public int getCount() {
        return websites.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        ItemImageBinding imageBinding=ItemImageBinding.inflate(LayoutInflater.from(mContext));
        if(!TextUtils.isEmpty(websites.get(position).getImage())) {
            imageBinding.cvDesc.setVisibility(View.GONE);
            imageBinding.flImage.setVisibility(View.VISIBLE);
            Picasso.get().load(websites.get(position).getImage()).into(imageBinding.image);
        } else
        {
            imageBinding.cvDesc.setVisibility(View.VISIBLE);
            imageBinding.flImage.setVisibility(View.GONE);
            imageBinding.tvNewDesc.setText(Html.fromHtml(websites.get(position).getDescription()));
        }
        container.addView(imageBinding.getRoot(),0);
        if(websites.size()>1){
            imageBinding.imageCount.setVisibility(View.VISIBLE);
            imageBinding.imageCount.setText(String.valueOf(position+1)+"/"+websites.size());
        }
        else
            imageBinding.imageCount.setVisibility(View.GONE);

        imageBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString(AppConstants.EXTRA_OBJECT,websites.get(position).getWebsite());
                BrowserActivity.start(mContext,bundle);
            }
        });


        imageBinding.imgType.setVisibility(websites.get(position).getType().equalsIgnoreCase("video") ? View.VISIBLE:View.GONE);

        return imageBinding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public ArrayList<Websites> getList()
    {
        return websites;
    }
}

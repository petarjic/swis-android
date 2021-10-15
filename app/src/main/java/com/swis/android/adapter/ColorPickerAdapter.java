package com.swis.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.swis.android.App;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.custom.customviews.SparkRecyclerViewLatest;
import com.swis.android.databinding.ItemColorBinding;
import com.swis.android.model.ColorList;
import com.swis.android.util.PrefsHelper;

import java.util.ArrayList;
import java.util.List;


public class ColorPickerAdapter extends EasyAdapter {

    private final Context mContext;
    private ArrayList<ColorList> mList;
    private PrefsHelper prefsHelper = null;
    private SparkRecyclerViewLatest.OnItemClickListener clickListener;

    public ColorPickerAdapter(Context context, List list) {
        super(context, list);
        this.mContext = context;
        prefsHelper = App.getInstance().getPrefsHelper();
    }

    @Override
    protected void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        mList = ((ArrayList<ColorList>) getList());

        System.out.println("color code = "+mList.get(position).getColorCode());

        ((MyViewHolder)holder).binding.circleView.setCircleColor(Color.parseColor(mList.get(position).getColorCode()));
        ((MyViewHolder)holder).binding.circleView.setCircleColor(Color.parseColor(mList.get(position).getColorCode()));

    }



    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {

            ItemColorBinding itemBinding = ItemColorBinding.inflate(LayoutInflater.from(mContext), parent, false);
            return new MyViewHolder(itemBinding);

    }

    public void setOnItemClickListener(Fragment activity) {
        clickListener = (SparkRecyclerViewLatest.OnItemClickListener) activity;
    }




    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ItemColorBinding binding = null;

        public MyViewHolder(ItemColorBinding view) {
            super(view.getRoot());
            binding = view;
            view.getRoot().setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onItemClick(v, position);
        }
    }


}

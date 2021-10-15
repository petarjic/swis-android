package com.swis.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.swis.android.R;
import com.swis.android.custom.customviews.EasyAdapter;
import com.swis.android.databinding.ItemHomeFollowingsBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.responsemodel.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SparkHomeFollowingAdapter extends EasyAdapter {
    private Context mContext;
    private UserInfo userInfo;
    private OnItemClickListeners listeners;
    private int position=-1;

    public SparkHomeFollowingAdapter(Context context, List list) {
        super(context, list);
        mContext=context;
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        userInfo=(UserInfo)getList().get(position);
        String name=userInfo.getName();
        String clientName = String.valueOf(name.charAt(0));
        for(int i=1;i<name.length();i++)
        {
            clientName +=name.charAt(i);
            if(name.charAt(i) == ' ')
            {
                clientName.replaceAll(" ","");
                break;
            }
        }
        ((MyViewHolder)holder).binding.tvName.setText(clientName);
        ((MyViewHolder)holder).binding.llImage.setBackground(userInfo.isSelected() ? mContext.getResources().getDrawable(R.drawable.cardviewbackgroundblueforimage):mContext.getResources().getDrawable(R.drawable.cardviewbackground));
        //Picasso.get().load(userInfo.getAvatar()).fit().rotate(0).into(((MyViewHolder)holder).binding.followProfilePic);
        Glide.with(mContext).load(userInfo.getAvatar()).placeholder(R.drawable.default_user).into(((MyViewHolder)holder).binding.followProfilePic);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int position) {
        ItemHomeFollowingsBinding binding=ItemHomeFollowingsBinding.inflate(LayoutInflater.from(mContext));

        return new MyViewHolder(binding);
    }

    public void setOnClickListeners(Fragment fragment) {
        listeners= (OnItemClickListeners) fragment;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemHomeFollowingsBinding binding;

        public MyViewHolder(ItemHomeFollowingsBinding view) {
            super(view.getRoot());
            binding=view;
            binding.followProfilePic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()== R.id.follow_profile_Pic)
            {
                UserInfo userInfo=(UserInfo)getList().get(getAdapterPosition());

                if(position!=-1 && position!=getAdapterPosition())
                {
                    UserInfo tempUser=(UserInfo)getList().get(position);
                    tempUser.setSelected(false);
                    notifyItemChanged(position);

                }

                    position=getAdapterPosition();
                    userInfo.setSelected(!(userInfo).isSelected());
                    notifyItemChanged(getAdapterPosition());



            }
            if(listeners!=null)
                listeners.onItemClick(v,getAdapterPosition());
        }
    }
}

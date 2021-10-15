package com.swis.android.activity;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.swis.android.R;
import com.swis.android.adapter.CustomRecyclerPreviewAdapter;
import com.swis.android.base.MiddleBaseActivity;
import com.swis.android.database.PreviewManager;
import com.swis.android.databinding.ActivityPreviewBinding;
import com.swis.android.databinding.AppActionbarBinding;
import com.swis.android.listeners.OnItemClickListeners;
import com.swis.android.model.Preview;
import com.swis.android.util.ActionBarData;
import com.swis.android.util.AppConstants;
import com.swis.android.util.GridSpacingItemDecoration;

import java.util.ArrayList;

public class PreviewActivity extends MiddleBaseActivity implements OnItemClickListeners, View.OnClickListener {

    private ActivityPreviewBinding binding;
    private CustomRecyclerPreviewAdapter adapter;
    private ArrayList<Preview> mlist;
    private Preview preview;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview;
    }

    @Override
    protected AppActionbarBinding getToolBar() {
        return binding.appBar;
    }

    @Override
    protected ActionBarData getScreenActionTitle() {
        return new ActionBarData(R.drawable.back_icon, "SWIS");
    }

    @Override
    protected void onViewBinded(ViewDataBinding views) {
        binding = (ActivityPreviewBinding) views;

           preview=prefHelper.getPreviewInfo();

        setListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreviewManager.getAllData(mContext, new PreviewManager.PreviewDataLoaded() {
            @Override
            public void onDataLoaded(ArrayList<Preview> list) {
                mlist = list;
                adapter = new CustomRecyclerPreviewAdapter(mContext, mlist);
                adapter.setOnItemClickListeners(PreviewActivity.this);
                binding.sparkView.setLayoutManager(new GridLayoutManager(mContext,2 ));
                int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dp_10);
                binding.sparkView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
                binding.sparkView.setAdapter(adapter);
            }
        });

    }

    private void setListeners() {
        binding.imgSearch.setOnClickListener(this);
        binding.tvClearAll.setOnClickListener(this);
        binding.tvDone.setOnClickListener(this);
    }

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.img_delete:
                PreviewManager.deleteData(mContext, mlist.get(position));
               if(preview!=null && mlist.get(position).getTime()==preview.getTime())
               {
                   preview=null;
               }
                mlist.remove(position);
                adapter.notifyItemRemoved(position);
                break;
            default:
                if (mlist.get(position).isLink) {
                    prefHelper.savePreviewInfo(mlist.get(position));
                    BrowserActivity.start(mContext, bundle);
                } else {
                    finish();
                    prefHelper.savePreviewInfo(mlist.get(position));
                    SearchActivity.start(mContext, bundle);
                }
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear_all:
                PreviewManager.deleteAllData(mContext);
                mlist.clear();
                adapter.notifyDataSetChanged();
                preview=null;
                break;
            case R.id.tv_done:
                onBackPressed();
                break;
            case R.id.img_search:
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();
                Bundle bundle2=new Bundle();
                bundle2.putBoolean(AppConstants.IS_NEW_SEARCH,true);
                prefHelper.savePreviewInfo(null);
                SearchActivity.start(mContext,bundle2);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra(AppConstants.IS_NEW_INSERT, preview == null);
        setResult(AppConstants.ResultActivity.PREVIEW_ACTIVITY_RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }
}

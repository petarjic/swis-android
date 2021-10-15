package com.swis.android.custom.customviews;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.swis.android.R;

import java.util.List;

public abstract class EasyAdapter<NITSVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<NITSVH> {
    private static final int ITEM = 0;
    private static final int HEADER = -1;
    private static final int FOOTER = 1;
    private static final int CENTER = 2;
    public final View mFooterMain;
    private List mainList;
    private View mHeader, mFooter,mCenter;
    private List list;
    private boolean isFooterManuallySet;
    private boolean isLoadMoreEnabled;
    public boolean isLoading;
    private SparkEmptyData emptyData;
    private OnCustomViewClickListener customViewClickListener;

    public interface OnCustomViewClickListener
    {
        void onViewClicked();
    }

    public EasyAdapter(Context context , List list) {
        this.list = list;
        this.mainList = list;
        mFooter = LayoutInflater.from(context).inflate(R.layout.footer_progess,null);
        mFooterMain = LayoutInflater.from(context).inflate(R.layout.footer_progess,null);
        setFooterView(null);
    }
    @Override
    public NITSVH onCreateViewHolder(ViewGroup parent, int viewType){
        NITSVH viewHolder;
        if (viewType == HEADER) {
            viewHolder = (NITSVH) onCreateHeaderViewHolder(parent,mHeader);
        } else if (viewType == FOOTER && (isFooterManuallySet || (isLoading() && isLoadMoreEnabled()))) {
            mFooter = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progess,null);
            viewHolder = (NITSVH) onCreateFooterViewHolder(parent,mFooter);
        } else if (viewType==CENTER) {
            mCenter = LayoutInflater.from(parent.getContext()).inflate(R.layout.spark_center_view,null);
            TextView textView=mCenter.findViewById(R.id.tv_info);
            if(TextUtils.isEmpty(emptyData.text)){
                textView.setVisibility(View.GONE);
            }else {
                textView.setText(emptyData.text);
                textView.setTextColor(emptyData.textColor);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,emptyData.textSize);
                ((FrameLayout)mCenter.findViewById(R.id.fl_container)).setPadding(0,emptyData.paddingTop,0,0);
                ((LinearLayout)mCenter.findViewById(R.id.ll_container)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(customViewClickListener!=null)
                            customViewClickListener.onViewClicked();
                    }
                });
            }
            if(emptyData.drawable!=null){
                ((ImageView)mCenter.findViewById(R.id.info_drawable)).setImageDrawable(emptyData.drawable);
            }else {
                ((ImageView)mCenter.findViewById(R.id.info_drawable)).setVisibility(View.GONE);
            }


            viewHolder = (NITSVH) onCreateCenterViewHolder(parent,mCenter);
        }
        else {
            viewHolder = onCreateItemViewHolder(parent, viewType);
        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(NITSVH holder, int position){
        if (isHeaderPosition(position)) {
            onBindHeaderViewHolder(holder, position);
        } else if (isFooterPosition(position)) {
            onBindFooterViewHolder(holder, position);
        }else if (isListEmpty()) {
            onBindCenterViewHolder(holder, position);
        } else {
            onBindItemViewHolder(holder, position-getOffSet());
        }
    }

    public int getOffSet() {
        return ((mHeader!=null)?1:0);
    }

    protected abstract void onBindItemViewHolder(NITSVH holder, int position);
    protected void onBindFooterViewHolder(NITSVH holder, int position){}
    protected void onBindHeaderViewHolder(NITSVH holder, int position){}
    protected void onBindCenterViewHolder(NITSVH holder, int position){}

    @Override
    public int getItemViewType(int position) {
        int viewType = ITEM;
        if (isHeaderPosition(position)) {
            viewType = HEADER;
        } else if (isFooterPosition(position)) {
            viewType = FOOTER;
        }
        else if (isListEmpty()) {
            viewType = CENTER;
        }
        return viewType;
    }

    @Override
    public int getItemCount(){
        if(list!=null)
            return (list.size())+((mHeader!=null)?1:0)+((mFooter!=null)?1:0)+(list.size()==0?1:0);
        else
            return ((mHeader!=null)?1:0);

    }

    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, View headerView){
        return new CommonViewHolder(headerView);
    }

    protected RecyclerView.ViewHolder onCreateCenterViewHolder(ViewGroup parent, View centerView){
        return new CommonViewHolder(centerView);
    }

    protected abstract NITSVH onCreateItemViewHolder(ViewGroup parent, int position);

    protected RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, View footerView){
        return new CommonViewHolder(footerView);
    }

    public boolean isHeaderPosition(int position) {
        return (mHeader!=null) && position == 0;
    }

    public boolean isFooterPosition(int position) {
        return (mFooter!=null) && position == (getItemCount() - 1);
    }

    public boolean isListEmpty() {
        return list.size()==0;
    }

    public void setHeaderView(View view){
        mHeader = view;
    }
    public View getHeaderView(){
        return mHeader;
    }

    public void setHeaderViewVisibility(boolean show){
        if(mHeader!=null)
            mHeader.setVisibility(!show ? View.GONE:View.VISIBLE);
    }





    /**
     * To Set Footer view
     * if this is called with LoadMore listener then this view will act as loading progress view
     * @param view This view will be set by you, if want Footer View
     */
    public void setFooterView(View view){
        if(view!=null){
            setFooterManuallySet(true);
        }else {
            setFooterManuallySet(false);
        }
        mFooter = view;
    }

    public View getFooterView(){
        return mFooter;
    }

    public void  addOnViewClickledListener(OnCustomViewClickListener onViewClickListener)
    {
        customViewClickListener=onViewClickListener;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public boolean isLoadMoreEnabled() {
        return isLoadMoreEnabled;
    }

    public void setLoadMoreEnabled(boolean loadMoreEnabled) {
        isLoadMoreEnabled = loadMoreEnabled;
    }

    private boolean isFooterManuallySet() {
        return isFooterManuallySet;
    }

    public void setFooterManuallySet(boolean footerManuallySet) {
        isFooterManuallySet = footerManuallySet;
    }

    public void setIsLoading(boolean b){
        isLoading = b;
        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRemoved(getItemCount()-1);
                }
            });
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }

    public boolean isLoading(){
        return  isLoading;
    }

    public void setEmptyData(SparkEmptyData emptyData) {
        this.emptyData = emptyData;
    }


    public class CommonViewHolder extends RecyclerView.ViewHolder {

        public CommonViewHolder(View headerView) {
            super(headerView);
        }

    }

    public List getMainList() {
        return mainList;
    }

    public void setMainList(List mainList) {
        this.mainList = mainList;
    }
}
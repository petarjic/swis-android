package com.swis.android.custom.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.swis.android.R;


/**
 * Created by Nitesh Singh(killer) on 8/31/2016.
 */
public class SparkRecyclerViewLatest extends RecyclerView implements RecyclerView.OnItemTouchListener {

    private final SparkEmptyData emptyData;
    public int emptyTextColor;
    public int emptyTextSize,emptyPaddingTop;
    public String emptyText;

    public Drawable emptyDrawable;
    private OnItemClickListener mListener;
    private OnLoadMoreListener mLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;
    private final Context mContext;
    private GestureDetector mGestureDetector;
    private View mProgressView;
    private boolean isShow=true;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public SparkRecyclerViewLatest(Context context) {
        this(context,null);
    }

    public SparkRecyclerViewLatest(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public SparkRecyclerViewLatest(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SparkCenterView);
        try {
            emptyText = ta.getString(R.styleable.SparkCenterView_empty_text);
            emptyTextSize = ta.getDimensionPixelSize(R.styleable.SparkCenterView_empty_textsize,0);
            emptyPaddingTop = ta.getDimensionPixelSize(R.styleable.SparkCenterView_empty_padding_top,0);
            emptyTextColor = ta.getColor(R.styleable.SparkCenterView_empty_textcolor, Color.BLACK);
            emptyDrawable= ta.getDrawable(R.styleable.SparkCenterView_empty_drawable);
            emptyData = new SparkEmptyData();
            emptyData.setText(emptyText);
            emptyData.setTextSize(emptyTextSize);
            emptyData.setTextColor(emptyTextColor);
            emptyData.setDrawable(emptyDrawable);
            emptyData.setPaddingTop(emptyPaddingTop);
        } finally {
            ta.recycle();
        }
    }

    public void addOnItemClickListener(OnItemClickListener listener){

        mListener = listener;
        mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        addOnItemTouchListener(this);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        mLinearLayoutManager = (LinearLayoutManager) layout;
    }

    /**
     * This will add loadmore listener
     * This method should be called after calling setAdapter method
     * @param listener listener reference
     */
    public void addLoadMoreListener(OnLoadMoreListener listener) {

        if(mLinearLayoutManager!=null && getAdapter()!=null){
            ((EasyAdapter)getAdapter()).setLoadMoreEnabled(true);
            mLoadMoreListener = listener;
            addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!((EasyAdapter) getAdapter()).isLoading() && isLastVisible()){// itemCount <= (lastVisibleItem + assumedNoOfVisbileItem) && previousLastVisibleItem<lastVisibleItem) {
                        try {
                            if(isShow) {
                                ((EasyAdapter) getAdapter()).setFooterView(((EasyAdapter) getAdapter()).mFooterMain);
                                getAdapter().notifyDataSetChanged();
                            }
                        }catch (Exception e){

                        }
                        if (mLoadMoreListener != null) {
                            mLoadMoreListener.onLoadMore();
                        }
                        else
                            ((EasyAdapter) getAdapter()).setFooterView(null);
                        ((EasyAdapter) getAdapter()).setIsLoading(true);
                    }

                }
            });
        }else{
            throw new EasyRuntimeException("addLoadMoreListener should be called after setAdapter method");
        }
    }


    /**
     * You have to call this method when your API execution will be complted
     * For Example : for Async task- in PostExecite
     * For Example : for HTTP library in onResponse and similar method
     *
     * This will stop/remove the Loading progress icon when data fetching complted and list notified, if list scrolled again then on last of the list it will be c
     * called again.
     */
    public void setDataLoadingFromServerCompleted(){
        if(getAdapter()!=null){
            ((EasyAdapter) getAdapter()).setIsLoading(false);
        }
    }

    public void setFooterViewOnLoading(boolean flag)
    {
        isShow=flag;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            if((view.getChildAdapterPosition(childView)-((EasyAdapter)getAdapter()).getOffSet())>=0){
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView)-((EasyAdapter)getAdapter()).getOffSet());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }


    public boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager)getLayoutManager());
        int pos = layoutManager.findLastVisibleItemPosition();
        int numItems = getAdapter().getItemCount();
        System.out.println("no of item = "+numItems +"  Visible Position = "+pos);
        return (pos >= (numItems-1));
    }

    public void setAdapter(@Nullable EasyAdapter adapter) {
        adapter.setEmptyData(emptyData);
        super.setAdapter(adapter);
    }


}



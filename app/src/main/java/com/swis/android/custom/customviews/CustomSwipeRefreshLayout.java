package com.swis.android.custom.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.swis.android.R;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


/**
 * Created by admin on 7/9/2016.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {


    public CustomSwipeRefreshLayout(Context context) {
        super(context);
        doCommonSetting(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        doCommonSetting(context);
    }

    private void doCommonSetting(Context context) {
        setId(R.id.swipe_layout);
        setColorSchemeResources(R.color.colorPrimary,R.color.red);
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        this.post(new Runnable() {
            @Override
            public void run() {
                CustomSwipeRefreshLayout.super.setRefreshing(refreshing);
            }
        });

    }
}

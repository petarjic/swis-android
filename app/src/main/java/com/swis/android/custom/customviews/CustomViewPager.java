package com.swis.android.custom.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by nitesh on 2/2/17.
 */

public class
CustomViewPager extends ViewPager {

    private boolean isPagerEnabled = true;
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isPagerEnabled)
        return super.onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isPagerEnabled)
        return super.onInterceptTouchEvent(ev);
        return false;
    }

    public boolean isPagerEnabled() {
        return isPagerEnabled;
    }

    public void setPagerEnabled(boolean pagerEnabled) {
        isPagerEnabled = pagerEnabled;
    }
}

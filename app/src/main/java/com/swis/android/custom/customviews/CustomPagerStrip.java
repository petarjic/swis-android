package com.nitesh.nisininvoice.custom.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerTitleStrip;

public class CustomPagerStrip extends PagerTitleStrip {
    public CustomPagerStrip(@NonNull Context context) {
        super(context);
    }

    public CustomPagerStrip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void updateViewLayout(View view, LayoutParams params) {
        super.updateViewLayout(view, params);
    }
}

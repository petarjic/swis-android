package com.swis.android.custom.customviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.swis.android.R;
import com.swis.android.custom.customviews.textViews.GothamRoundedBook;
import com.swis.android.custom.customviews.textViews.GothamRoundedMed;


public class CustomTabLayout extends TabLayout {
    private int tabSelectedTextColor;
    private Context mContext;
    private ViewPager mViewPager;
    private int textColor;

    public CustomTabLayout(Context context) {
        super(context);
        mContext = context;
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        textColor = mContext.getResources().getColor(R.color.white);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom_tab_layout);
        int color = a.getColor(R.styleable.custom_tab_layout_tabTextColor, 0);
        tabSelectedTextColor = a.getColor(R.styleable.custom_tab_layout_tabSelectedTextColor, 0);
        if(color!=0){
            textColor = color;
        }
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        this.mViewPager = viewPager;
        super.setupWithViewPager(viewPager);
        populateTabText();

    }

    @NonNull
    @Override
    public Tab newTab() {
        Tab tab = super.newTab();
        ColorStateList colors = createColorStateList(textColor,tabSelectedTextColor);
        GothamRoundedMed tv = new GothamRoundedMed(mContext);
        tv.setTextColor(colors);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        tv.setSingleLine(true);
        tv.setGravity(GRAVITY_CENTER);
        tab.setCustomView(tv);
        return tab;
    }

    private void populateTabText() {
        for (int i =0;i<mViewPager.getAdapter().getCount();i++){

            ((TextView) getTabAt(i).getCustomView()).setText("");
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ((TextView) getTabAt(i).getCustomView()).getLayoutParams();
            int valueInPixels30 = (int) getResources().getDimension(R.dimen.dp_50);

            lp.width = valueInPixels30;
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            lp.gravity = Gravity.CENTER;
            ((TextView) getTabAt(i).getCustomView()).setLayoutParams(lp);
            ((TextView) getTabAt(i).getCustomView()).setPadding(25,0,20,0);
            ((TextView) getTabAt(i).getCustomView()).setCompoundDrawablesWithIntrinsicBounds(R.drawable.home, 0, 0, 0);
            ((TextView) getTabAt(i).getCustomView()).setCompoundDrawablePadding(0);

            LinearLayout tabParent = (LinearLayout) getTabAt(i).getCustomView().getParent();
            tabParent.setLayoutParams(lp);
            tabParent.setPadding(20,0,20,0);

        }
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;

        return new ColorStateList(states, colors);
    }



}

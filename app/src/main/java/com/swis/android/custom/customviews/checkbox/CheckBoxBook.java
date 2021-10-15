package com.swis.android.custom.customviews.checkbox;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.swis.android.custom.customviews.FontsType;


public class CheckBoxBook extends androidx.appcompat.widget.AppCompatCheckBox {

    private static Typeface typeface;

    public CheckBoxBook(Context context) {
        super(context);
        applyCustomFont(context);

    }

    public CheckBoxBook(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);

    }

    public CheckBoxBook(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);

    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_BOOK.getPath());
        }
        super.setTypeface(typeface);
    }




    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_BOOK.getPath());
        setTypeface(customFont);
    }

}
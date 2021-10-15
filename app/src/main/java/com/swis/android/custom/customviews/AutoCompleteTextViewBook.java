package com.swis.android.custom.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class AutoCompleteTextViewBook extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private static Typeface typeface;

    public AutoCompleteTextViewBook(Context context) {
        super(context);
        applyCustomFont(context);

    }

    public AutoCompleteTextViewBook(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);

    }

    public AutoCompleteTextViewBook(Context context, AttributeSet attrs, int defStyle) {
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
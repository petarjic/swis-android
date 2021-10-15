package com.swis.android.custom.customviews.buttons;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.swis.android.custom.customviews.FontsType;


public class GothamRoundedBook extends androidx.appcompat.widget.AppCompatButton {

    private static Typeface typeface;

    public GothamRoundedBook(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public GothamRoundedBook(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public GothamRoundedBook(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);

    }


    @Override
    public void setTypeface(Typeface tf, int style) {
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_BOOK.getPath());
            } catch (Exception e) {
            }
        }
        super.setTypeface(typeface);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_BOOK.getPath());
        setTypeface(customFont);
    }

}

package com.swis.android.custom.customviews.buttons;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.swis.android.custom.customviews.FontsType;


public class GothamRoundedBookBold extends androidx.appcompat.widget.AppCompatButton {

    private static Typeface typeface;

    public GothamRoundedBookBold(Context context) {
        super(context);
        applyCustomFont(context);

    }

    public GothamRoundedBookBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public GothamRoundedBookBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);

    }


    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_BOOK_BOLD.getPath());
        setTypeface(customFont);
    }


}

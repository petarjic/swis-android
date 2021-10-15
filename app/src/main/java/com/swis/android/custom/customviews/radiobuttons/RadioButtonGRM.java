package com.swis.android.custom.customviews.radiobuttons;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.swis.android.custom.customviews.FontsType;


public class RadioButtonGRM extends androidx.appcompat.widget.AppCompatRadioButton {

    private static Typeface typeface;

    public RadioButtonGRM(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public RadioButtonGRM(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);

    }

    public RadioButtonGRM(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);

    }


    @Override
    public void setTypeface(Typeface tf, int style) {
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_MED.getPath());
            } catch (Exception e) {

            }
        }
        super.setTypeface(typeface);
    }




    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_MED.getPath());
        setTypeface(customFont);
    }

}

package com.swis.android.custom.customviews.edittext;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;
import com.swis.android.custom.customviews.FontsType;


public class EditTextGothamRoundedMed extends TextInputEditText {

    private static Typeface typeface;

    public EditTextGothamRoundedMed(Context context) {
        super(context);
        applyCustomFont(context);

    }

    public EditTextGothamRoundedMed(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);

    }

    public EditTextGothamRoundedMed(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);

    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_MED.getPath());
        }
        super.setTypeface(typeface);
    }




    private void applyCustomFont(Context context) {
        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), FontsType.GOTHAM_ROUNDED_MED.getPath());
        setTypeface(customFont);
    }

}
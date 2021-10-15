package com.swis.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;


import com.swis.android.R;
import com.swis.android.custom.customviews.textViews.GothamRoundedMed;

import java.util.ArrayList;

public class BiscuitBox {

    private Bitmap viewBmp;
    private Context mContext;
    SpannableStringBuilder ss = new SpannableStringBuilder();
    ArrayList<Biscuit> arrSelected = new ArrayList<>();

    public BiscuitBox() {
    }
    public BiscuitBox(Context context) {
        this.mContext =context;
    }

    public BiscuitBox getInstance(Context context) {
        return new BiscuitBox(context);
    }

    public void createBiscuit(final String text, final String id,final OnBubbleClickListener listener){
        createBiscuit(text,id,null,listener);
    }

    public void createBiscuit(final String text, final String id, Object object, final OnBubbleClickListener listener){
        GothamRoundedMed tv = new GothamRoundedMed(mContext);
        tv.setText(text);
        int valueInPixels = (int) mContext.getResources().getDimension(R.dimen.ts_16);

        tv.setTextSize(valueInPixels);
        tv.setPadding(20,20,20,20);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setBackgroundResource(R.drawable.bg_bubble_green);
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross, 0);
        tv.setCompoundDrawablePadding(20);
        tv.setClickable(true);
        tv.setTag(id);

        BitmapDrawable bd = convertViewToDrawable(tv);
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(bd, ImageSpan.ALIGN_BASELINE);
        Biscuit biscuit = new Biscuit(id,text);
        if(!arrSelected.contains(biscuit)){
            arrSelected.add(biscuit);
        }
        ss.append(biscuit.getName()+" ");
        final int start = ss.length()-(biscuit.getName().length()+1);
        final int end = ss.length()-1;
        ss.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // start(0) and end (2) will create an image span over abc text

        ss.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    int position = getPositionById(arrSelected,id);
                    int start = ss.toString().indexOf(arrSelected.get(position).getName());
                    int end = start+arrSelected.get(position).getName().length();
                    ss.delete(start, end+1);
                    arrSelected.remove(position);
                    listener.onBubbleClicked(id,ss);
                }
            }
        },start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // this will add a clickable span and on click will delete the span and text
        listener.onBubbleCreated(id,ss,object);
    }

    private int getPositionById(ArrayList<Biscuit> arrSelected, String id) {
        for (int i=0;i<arrSelected.size();i++){
            if(arrSelected.get(i).getId().equalsIgnoreCase(id))
                return i;
        }
        return 0;
    }

    private BitmapDrawable convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        BitmapDrawable drawable = new BitmapDrawable(viewBmp);
        return drawable;

    }
}

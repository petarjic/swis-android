package com.swis.android.custom.customviews.textViews;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

public class ExpandableTextView extends GothamRoundedBook
{
    private static final int MAX_LINES = 2;
    private int currentMaxLines = Integer.MAX_VALUE;

    public ExpandableTextView(Context context)
    {
        super(context);
        setEllipsize(TextUtils.TruncateAt.END);
    }
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setEllipsize(TextUtils.TruncateAt.END);
    }

    public ExpandableTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setEllipsize(TextUtils.TruncateAt.END);
    }


    @Override
    public void setMaxLines(int maxLines)
    {
        currentMaxLines = maxLines;
        super.setMaxLines(maxLines);
    }

    /* Custom method because standard getMaxLines() requires API > 16 */
    public int getMyMaxLines()
    {
        return currentMaxLines;
    }


    private void setLabelAfterEllipsis(ExpandableTextView textView, String suffix, int maxLines){

       /* if(textView.getLayout().getEllipsisCount(maxLines-1)==0) {
            return; // Nothing to do
        }*/
        if(textView.getLayout()!=null){
            int start = textView.getLayout().getLineStart(0);
            int end = textView.getLayout().getLineEnd(textView.getLineCount() - 1);
            String displayed = textView.getText().toString().substring(start, end);
            int displayedWidth = getTextWidth(displayed, textView.getTextSize());

            int textWidth;
            String newText = displayed;
            textWidth = getTextWidth(newText + suffix, textView.getTextSize());

            while(textWidth>displayedWidth){
                newText = newText.substring(0, newText.length()-1).trim();
                textWidth = getTextWidth(newText + suffix, textView.getTextSize());
            }

            textView.setText(newText + suffix);
        }

    }

    private int getTextWidth(String text, float textSize){
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        int width = (int) Math.ceil( bounds.width());
        return width;
    }

}
package com.swis.android.custom;/*
package com.nisin.energiaguanaca.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.style.ClickableSpan;
import android.view.View;

import com.nitesh.nisineduluv.job.BuzzPojo;
import com.nitesh.nisineduluv.job.BrowserActivity;
import com.nitesh.nisineduluv.util.AppConstant;

@SuppressLint("ParcelCreator")
public class CustomUrlSpan extends ClickableSpan implements ParcelableSpan {

    private final String mURL;

    public CustomUrlSpan(String url) {
        mURL = url;
    }

    public CustomUrlSpan(Parcel src) {
        mURL = src.readString();
    }
    
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    */
/** @hide *//*

    public int getSpanTypeIdInternal() {
        return 11;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    */
/** @hide *//*

    public void writeToParcelInternal(Parcel dest, int flags) {
        dest.writeString(mURL);
    }

    public String getURL() {
        return mURL;
    }

    @Override
    public void onClick(View widget) {
        Context context = widget.getContext();
        BuzzPojo buzzObject = new BuzzPojo();
        buzzObject.setTitle("");
        buzzObject.setLink(getURL());
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstant.EXTRA_OBJECT,buzzObject);
        PDFViewActivity.start(context,bundle);
    }
}
*/

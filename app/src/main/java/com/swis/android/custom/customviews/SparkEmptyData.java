package com.swis.android.custom.customviews;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nitesh on 13/2/17.
 */

public class SparkEmptyData implements Parcelable {
    @SerializedName("text") String text;
    @SerializedName("textSize") int textSize;
    @SerializedName("textColor") int textColor;
    @SerializedName("drawable") Drawable drawable;
    @SerializedName("paddingTop") int paddingTop;

    protected SparkEmptyData(Parcel in) {
        text = in.readString();
        textSize = in.readInt();
        textColor = in.readInt();
        paddingTop = in.readInt();
    }

    public static final Creator<SparkEmptyData> CREATOR = new Creator<SparkEmptyData>() {
        @Override
        public SparkEmptyData createFromParcel(Parcel in) {
            return new SparkEmptyData(in);
        }

        @Override
        public SparkEmptyData[] newArray(int size) {
            return new SparkEmptyData[size];
        }
    };

    public SparkEmptyData() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(textSize);
        dest.writeInt(textColor);
        dest.writeInt(paddingTop);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }
}

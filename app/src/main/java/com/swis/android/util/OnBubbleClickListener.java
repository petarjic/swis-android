package com.swis.android.util;

import android.text.SpannableStringBuilder;

public interface OnBubbleClickListener {
    void onBubbleClicked(String id, SpannableStringBuilder sb);
    void onBubbleCreated(String id, SpannableStringBuilder sb);
    void onBubbleCreated(String id, SpannableStringBuilder sb, Object data);
}

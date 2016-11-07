package com.lecloud.skin.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context context) {
        this(context, null, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setSingleLine();
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setMarqueeRepeatLimit(-1);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}

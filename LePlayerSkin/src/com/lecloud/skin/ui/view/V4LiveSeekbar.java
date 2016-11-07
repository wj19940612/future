package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lecloud.skin.ui.base.BaseLiveSeekBar;

public class V4LiveSeekbar extends BaseLiveSeekBar {

    public V4LiveSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4LiveSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4LiveSeekbar(Context context) {
        super(context);
    }

    @Override
    public String getLayout() {
        return "letv_skin_v4_small_seekbar_layout";
    }
}

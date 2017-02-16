package com.jnhyxx.html5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by ${wangJie} on 2017/2/16.
 * 财经日历的顶部的显示一周星期的view
 */

public class WeekCalendarLayout extends LinearLayout {
    public WeekCalendarLayout(Context context) {
        super(context);
        initView();
    }

    public WeekCalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
    }

    private void processAttrs(AttributeSet attrs) {

    }

    private void initView() {

    }
}

package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.StrUtil;


/**
 * Created by ${wangJie} on 2017/2/16.
 * 财经日历的顶部的显示一周星期的view
 */

public class WeekCalendarLayout extends LinearLayout {
    private static final String TAG = "WeekCalendarLayout";

    private static String[] weekData = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final String TODAY = "\n今天";

    private static final int DEFAULT_ITEM_HEIGHT = 24;


    public WeekCalendarLayout(Context context) {
        super(context);
        initView();
    }

    public WeekCalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        processAttrs(attrs);

    }

    private void processAttrs(AttributeSet attrs) {

    }

    private void initView() {
        setBackgroundResource(android.R.color.white);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        for (int i = 0; i < weekData.length; i++) {
            addView(createLine());
            addView(createWeekView(weekData[i]));
        }
        addView(createLine());
    }

    private View createWeekView(String week) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ITEM_HEIGHT,
                getResources().getDisplayMetrics());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        TextView textView = new TextView(getContext());
        textView.setPadding(padding, padding, padding, padding);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        textView.setBackgroundResource(R.drawable.bg_week_calendar);
        if (isToadyWeek(week)) {
            textView.setSelected(true);
            textView.setTextColor(Color.WHITE);
            textView.setText(StrUtil.mergeTextWithRatio(week, TODAY, 0.7f));
        } else {
            textView.setSelected(false);
            textView.setText(week);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.blueAssist));
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public boolean isToadyWeek(String week) {
        String dayOfWeek = DateUtil.getDayOfWeek(System.currentTimeMillis());
        String weekData = week.substring(week.length() - 1);
        Log.d(TAG, "今天的星期" + dayOfWeek + "截取的日期" + weekData);
        return dayOfWeek.equalsIgnoreCase(weekData);
    }


    private View createLine() {
        View view = new View(getContext());
        view.setBackgroundResource(R.color.blueAssist);
        int mLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.7f, getResources().getDisplayMetrics());
        LayoutParams layoutParams = new LayoutParams(0, mLineHeight);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        return view;
    }
}

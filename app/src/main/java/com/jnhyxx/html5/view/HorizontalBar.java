package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.jnhyxx.html5.R;

public class HorizontalBar extends RelativeLayout {

    private ViewPager mViewPager;
    private PageIndicator mPageIndicator;

    public HorizontalBar(Context context) {
        super(context);

        init();
    }

    public HorizontalBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        RelativeLayout.LayoutParams params =
                new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mViewPager = new ViewPager(getContext());
        mViewPager.setId(R.id.viewPager);
        addView(mViewPager, params);

        mPageIndicator = new PageIndicator(getContext());
        mPageIndicator.setCount(6);
        mPageIndicator.setSelectedPoint(Color.WHITE);
        mPageIndicator.setPoint(Color.GRAY);

        int defaultMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());
        params = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(0, 0, 0, defaultMargin);
        addView(mPageIndicator, params);
    }
}

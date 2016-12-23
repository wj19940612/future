package com.jnhyxx.html5.utils;

import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;

public abstract class OnItemOneClickListener implements AdapterView.OnItemClickListener {

    private static final int MIN_CLICK_DELAY_CLICK = 1000;

    private long mLastClickTime;

    public abstract void onItemOneClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_CLICK) {
            mLastClickTime = currentTime;
            onItemOneClick(parent, view, position, id);
        }
    }
}

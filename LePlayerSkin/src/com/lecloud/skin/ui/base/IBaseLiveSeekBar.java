package com.lecloud.skin.ui.base;

import com.lecloud.skin.ui.LetvUIListener;

public interface IBaseLiveSeekBar {
    void setLetvUIListener(LetvUIListener letvUIListener);

    void startTrackingTouch();

    void stopTrackingTouch();

    void setProgress(int progress);

    void setProgressGap(int gap);

    void setSeekToTime(int progress, boolean fromUser);

    void setTimeShiftChange(long serverTime, long currentTime, long begin);

    void setTimeText(CharSequence text);

    void reset();

    String getCurrentTime();

    String getSeekToTime();

    void setVisibility(int visibility);

    int getVisibility();
}

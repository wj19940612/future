package com.lecloud.skin.ui.utils.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by heyuekuai on 16/5/13.
 */
public class LeTimer extends Timer {
    private IChange listener;
    private long delaymillts;

    public LeTimer(IChange listener, long delaymillts) {
        this.listener = listener;
        this.delaymillts = delaymillts;
    }

    public void start() {
        schedule(new TimerTask() {
            @Override
            public void run() {
                listener.onChange();
            }
        }, delaymillts, delaymillts);
    }
}

package com.lecloud.skin.ui.utils.timer;

public class LeTimerManager implements IChange {
    private long delaymillts = 1000;
    private LeTimer timer;

    private IChange listener;

    public LeTimerManager(IChange listener, long delaymillts) {
        this.listener = listener;
        this.delaymillts = delaymillts;
    }

    private LeTimer getTimer() {
        if (timer == null) {
            timer = new LeTimer(this, delaymillts);
        }
        return timer;
    }

    @Override
    public void onChange() {
        listener.onChange();
    }

    public void start() {
        getTimer().start();
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}

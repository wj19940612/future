package com.jnhnxx.livevideo;

public interface ILivePlayer {

    void start();

    void stop();

    boolean isStopped();

    boolean isStarted();

    long getDuration();

    long getCurrentPosition();

    int getBufferPercentage();

    void setMute(boolean mute);

    boolean isMute();

    void setFullScreen(boolean fullScreen);

    boolean isFullScreen();
}


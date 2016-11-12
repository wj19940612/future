package com.jnhnxx.livevideo;

public interface ILivePlayer {

    void start();

    void pause();

    boolean isPaused();

    boolean isPlaying();

    int getBufferPercentage();

    void setMute(boolean mute);

    boolean isMute();

    void setFullScreen(boolean fullScreen);

    boolean isFullScreen();
}


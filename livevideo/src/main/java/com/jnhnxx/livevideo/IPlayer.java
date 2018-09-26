package com.jnhnxx.livevideo;

public interface IPlayer {

    int IDLE = 0;
    int INITIALIZED = 1;
    int PREPARING = 2;
    int PREPARED = 3;
    int STARTED = 4;
    int ERROR = -1;

    void start();

    void stop();

    boolean isStarted();

    long getDuration();

    long getCurrentPosition();

    int getBufferPercentage();

    void setMute(boolean mute);

    boolean isMute();

    void setFullScreen(boolean fullScreen);

    boolean isFullScreen();
}


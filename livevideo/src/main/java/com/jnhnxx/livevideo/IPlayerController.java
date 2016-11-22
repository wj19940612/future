package com.jnhnxx.livevideo;

public interface IPlayerController {

    void show();

    void hide();

    boolean isShowing();

    void enable(boolean enable);

    void mute(boolean mute);

    void start(boolean start);

    void fullScreen(boolean full);
}

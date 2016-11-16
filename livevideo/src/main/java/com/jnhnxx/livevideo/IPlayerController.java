package com.jnhnxx.livevideo;

public interface IPlayerController {

    void show();

    void hide();

    boolean isShowing();

    void onScaleButtonClick();

    void onStartButtonClick();

    void onMuteButtonClick();

    void enable(boolean enable);
}

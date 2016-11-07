package com.lecloud.skin.ui;

import android.os.Bundle;
import android.view.View;

import com.lecloud.sdk.api.md.entity.action.CoverConfig;
import com.lecloud.skin.ui.view.VideoNoticeView;

import java.util.List;

public interface ILetvUICon {
    public static final int SCREEN_LANDSCAPE = 0;
    public static final int SCREEN_PORTRAIT = 1;

    void setLetvUIListener(LetvUIListener mLetvUIListener);

    void setPlayState(boolean isPlayState);

    void setRequestedOrientation(int requestedOrientation,View view);

    void setRateTypeItems(List<String> ratetypes, String definition);
    
    void setTitle(String title);

    View getView();

    void hide();

    void show();

    boolean performClick();

    void canGesture(boolean flag);

    void isPano(boolean pano);

    void showWaterMark(CoverConfig coverConfig);

    void showWaterMark();

    void hideLoading();

    void hideWaterMark();

    void processMediaState(int event, Bundle bundle);

    void processPlayerState(int event, Bundle bundle);

    boolean isShowLoading();

    void showLoadingProgress();

    void setRePlayListener(VideoNoticeView.IReplayListener l);
    void processActionStatus(int state);
    void processLiveStatus(int state);
    
    boolean isFullScreen();
    void setGravitySensor(boolean useGSensor);

    //错误提示是否显示,如果显示那么播放按钮状态需要改变
    boolean showErrorTip();
    //是否允许快进,快退
    void  setSeekable(boolean seekable);
}

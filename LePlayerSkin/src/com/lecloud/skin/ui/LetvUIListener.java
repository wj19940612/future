package com.lecloud.skin.ui;

import android.os.Bundle;


public interface LetvUIListener {
    
    void onUIEvent(int event, Bundle bundle);
    
	void onClickPlay();
	
//	void onClickDownload();
	
	void onStartSeek();

    void onEndSeek();
	
	void onSeekTo(float per);

    int onSwitchPanoControllMode(int controllMode);

    int onSwitchPanoDisplayMode(int displayMode);

    void onSetDefination(int type);

    void resetPlay();
    
    void setRequestedOrientation(int requestedOrientation);
    
    void onProgressChanged(int progress);
    
}

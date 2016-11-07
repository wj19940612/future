package com.lecloud.skin.ui;


public interface ILetvVodUICon extends ILetvUICon{
	
	void setLetvVodUIListener(LetvVodUIListener mLetvVodUIListener);
	
	void setCurrentPosition(long position);
	
	void setDuration(long duration);
	
	void setBufferPercentage(long bufferPercentage);
	
}


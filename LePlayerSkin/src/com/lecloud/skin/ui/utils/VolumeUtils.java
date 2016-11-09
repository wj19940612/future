package com.lecloud.skin.ui.utils;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;


public class VolumeUtils {

    private static final String TAG = "VolumeUtils";
    private static VolumeUtils mInstance = null;
    private AudioManager mAudioManager;
    private VolumeUtils(Context mContext) {
    	this.mAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
    }
    
    public static VolumeUtils getInstance(Context mContext) {
        if (mInstance == null) {
            synchronized (VolumeUtils.class) {
                if (mInstance == null) {
                    mInstance = new VolumeUtils(mContext);
                }
            }
        }

        return mInstance;
    }

    public void setVolume(int percentage) {
		if (null == this.mAudioManager) {
			return;
		}

		if (percentage < 0 || percentage > 100) {
			return;
		}

		int maxValue = this.mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, percentage * maxValue / 100, 0);
	}

	/**
	 * 获取当前音量百分比(0-100)
	 */
	public int getVolume() {
		if (null == this.mAudioManager) {
			return 0;
		}
		int volume = this.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxValue = this.mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		return volume * 100 / maxValue;
	}
}

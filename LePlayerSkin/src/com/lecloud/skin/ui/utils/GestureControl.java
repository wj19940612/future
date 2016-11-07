package com.lecloud.skin.ui.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.lecloud.skin.popupwindow.GestureBrightnessPopWindow;
import com.lecloud.skin.popupwindow.GestureSeekToPopWindow;
import com.lecloud.skin.popupwindow.GestureVolumePopWindow;


/**
 * 播放控手势控制
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public  class GestureControl implements  OnTouchListener {
	public static final int GESTURE_CONTROL_DOWN = 5010;
	public static final int GESTURE_CONTROL_SEEK = 5011;
	public static final int GESTURE_CONTROL_UP = 5012;
	public static final String GESTURE_CONTROL_SEEK_GAP = "gesture_control_seek_gap";
	private static final String TAG = "GestureControl";
	public Context mContext;
	/**滑动的偏移加权*/
	private int SLIDE_DISTANCE = 300;
	
	/**
	 * 播放控制层
	 * **/
	public View mPlayControllerView;
	/**
	 * 滑动
	 */
	public GestureSeekToPopWindow mSeekToPopWindow;
	/**
	 * 是否允许触摸
	 */
	public boolean touchable = true;
	/**
	 * 声音
	 */
	private GestureVolumePopWindow mVolumePopWindow;
	/**
	 * 亮度
	 */
	private GestureBrightnessPopWindow mBrightnessPopWindow;
	private GestureDetector mGestureDetector;
	private AudioManager mAudioManager;
	/**
	 * 是否允许滑动快进
	 */
	private boolean seekable = true;
	/**
	 * 是否允许滑动控制
	 */
	private boolean scrollable = true;
//		private ISplayer mISplayer;
	
	private long seekGap = -1;
	
	
	private boolean isSeeking = false;
	private boolean isChangeVolume = false;
	private boolean isChangeBrightness = false;
	private int mCurrentBrightness = -1;
	private GestureControlListener mGestureListener;
	
	private final OnGestureListener mOnGestureListener = new OnGestureListener() {
		private int level = 0;// 记录popupwindow每次显示时候的初始值
		private float mYDown;
		private float mXDown;
		private float mYMove;

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
//			处理点击事件
			mPlayControllerView.performClick();
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			/**
			 * 关闭手势
			 * */
			if (!scrollable || e1 == null || e2 == null) {
				return false;
			}

			if (Math.abs(distanceX) > Math.abs(distanceY)) {
//				if (mVolumePopWindow.isShowing() || mBrightnessPopWindow.isShowing() || mPlayer == null ||!seekable ) {
//					return true;
//				}
				if (isChangeVolume||isChangeBrightness||!seekable) {
					return true;
				}
				isSeeking = true;
//				if (Math.abs(e2.getX() - e1.getX()) < 5) {
//					return false;
//				}
				if (!mSeekToPopWindow.isShowing()) {
					mSeekToPopWindow.showPopWindow(mPlayControllerView);
				}

//				seekGap = (long) ((e2.getX() - e1.getX()) * 1000 / mPlayControllerView.getWidth());
				seekGap = (long) ((e2.getX() - e1.getX()) /  mPlayControllerView.getWidth() * SLIDE_DISTANCE);
				Log.d("GestureControl", "seekGap="+seekGap);

				if (seekGap >= 0) {
					mSeekToPopWindow.setImageForward();
				}else {
					mSeekToPopWindow.setImageRewind();
				}
				
				if (mGestureListener != null) {
				    mGestureListener.onSeekTo((int)seekGap);
                }
//				TODO
//				Bundle data = new Bundle();
//				data.putInt("state", GESTURE_CONTROL_SEEK);
//				data.putInt(GESTURE_CONTROL_SEEK_GAP, (int)seekGap);
//				obs.notifyObserverPlus(data);

//				msec = mPosition+seekGap;
//				if (msec < 0) {
//					msec = 0;
//				} else if (msec > duration) {
//					msec = duration;
//				}
//				mSeekToPopWindow.setProgress(TimerUtils.stringForTime((int) (msec/1000))+"/"+TimerUtils.stringForTime((int) (duration/1000)));
				return false;
			}

			if (isSeeking) {
				return true;
			}

			this.mYMove = e2.getY();
			int addtion = (int) (this.mYDown - this.mYMove) * 100 / ScreenUtils.getHeight(mContext);
			if (ScreenUtils.isInRight(mContext, (int) e1.getX())) {
				isChangeVolume = true;
				if (!mVolumePopWindow.isShowing()) {
					mVolumePopWindow.showPopWindow(mPlayControllerView);
					this.level = getVolume();
				}
				int vloume = this.level + addtion;
				vloume = (vloume > 100 ? 100 : (vloume < 0 ? 0 : vloume));
				setVolume(vloume);
				mVolumePopWindow.setProgress(vloume);
//				this.mVolumePopWindow.setProgress(vloume);
				return true;
			} else if (ScreenUtils.isInLeft(mContext, (int) e1.getX())) {
				isChangeBrightness = true;
				if (!mBrightnessPopWindow.isShowing()) {
					mBrightnessPopWindow.showPopWindow(mPlayControllerView);
					this.level = (getScreenBrightness((Activity)mContext) * 100) / 255;
					mBrightnessPopWindow.setProgress(this.level);
					setScreenBrightness((Activity)mContext, this.level);
				}
				int brightness = this.level + addtion;
				brightness = (brightness > 100 ? 100 : (brightness < 0 ? 0 : brightness));
				setScreenBrightness((Activity)mContext, brightness * 255 / 100);
				mBrightnessPopWindow.setProgress(brightness);
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
//			TODO
//			Bundle data = new Bundle();
//			data.putInt("state", GESTURE_CONTROL_DOWN);
//			obs.notifyObserverPlus(data);

			this.mXDown = e.getX();
			this.mYDown = e.getY();
			return true;
		}

	};
	
	/**
	 * @param mContext
	 * @param mPlayControllerView 播放控制层
	 */
	public GestureControl(Context mContext,View mPlayControllerView) {
		this.mContext = mContext;
		this.mPlayControllerView = mPlayControllerView;
		init();
	}

	private void init() {
		this.mAudioManager = (AudioManager) this.mContext.getSystemService(Service.AUDIO_SERVICE);
		this.mGestureDetector = new GestureDetector(mContext, mOnGestureListener);
		if (mPlayControllerView != null) {
			mPlayControllerView.setOnTouchListener(this);
		}

		initPopWindow();
	}

	private void initPopWindow() {
		this.mVolumePopWindow = new GestureVolumePopWindow(this.mContext);
		mBrightnessPopWindow = new GestureBrightnessPopWindow(this.mContext);
		mSeekToPopWindow = new GestureSeekToPopWindow(this.mContext);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (isSeeking) {
			    if (mGestureListener != null) {
			        mGestureListener.onTouchUp();
                }
//				Bundle data = new Bundle();
//				data.putInt("state", GESTURE_CONTROL_UP);
//				obs.notifyObserverPlus(data);
			}
		case MotionEvent.ACTION_CANCEL:
			isChangeBrightness = false;
			isChangeVolume = false;
			isSeeking = false;

			mVolumePopWindow.dismiss();
			mBrightnessPopWindow.dismiss();
			mSeekToPopWindow.dismiss();
			seekGap = -1;
			break;

		default:
			break;
		}

		if (!touchable) {
			return false;
		}
		return mGestureDetector.onTouchEvent(event);
	}
	
	/**
	 * 设置播放控制层
	 * @param touchView
	 */
	public void setTouchView(View touchView) {
		touchView.setOnTouchListener(this);
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
	
//	------------------------音量控制--end--------------------
	
//	------------------------亮度控制----------------------

//	------------------------音量控制----------------------
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
	 * 设置亮度
	 * 
	 * @param paramInt
	 *            取值0-255
	 */
	public void setScreenBrightness(Activity activity, int paramInt) {
		this.mCurrentBrightness = paramInt;
		ScreenBrightnessManager.setScreenBrightness(activity, paramInt);

	}

	/**
	 * 获取当前亮度(取值0-255)
	 */
	public int getScreenBrightness(Activity activity) {
		if (this.mCurrentBrightness != -1) {
			return this.mCurrentBrightness;
		}
		return ScreenBrightnessManager.getScreenBrightness(activity);
	}
	
	

	public void cancelTouchable(boolean cancel) {
		this.touchable = !cancel;
	}
	
	public boolean isSeekable() {
		return seekable;
	}

	public void setSeekable(boolean seekable) {
		this.seekable = seekable;
	}
	
//	public boolean isScrollable() {
//		return scrollable;
//	}
//
//	public void setScrollable(boolean scrollable) {
//		this.scrollable = scrollable;
//	}
	
//	public ObservablePlus getObserver() {
//        Log.d(TAG, "[obs] [playerTimer] obs count:"+obs.countObservers());
//        return obs;
//    }
	
	public void setOnGestureControlListener(GestureControlListener mGestureListener){
	    this.mGestureListener = mGestureListener;
	}
	public interface GestureControlListener {
        public void onSeekTo(int seekGap);
        
        public void onTouchUp();
    }


}

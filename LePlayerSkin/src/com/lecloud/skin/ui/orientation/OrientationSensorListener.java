package com.lecloud.skin.ui.orientation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Handler;
import android.view.Surface;

/**
 * 陀螺仪监听，根据重量旋转方向
 * */
@TargetApi(Build.VERSION_CODES.FROYO)
public class OrientationSensorListener implements SensorEventListener {
	private static final int _DATA_X = 0;
	private static final int _DATA_Y = 1;
	private static final int _DATA_Z = 2;

	public static final int ORIENTATION_UNKNOWN = -1;

	private Handler rotateHandler;

	private Activity activity;

	private boolean isLock;

	private int lockOnce = -1;

	private boolean justLandscape;

	private OnDirectionChangeListener onDirectionChangeListener;
	
//	private UIPlayContext uiPlayContext;
	
	// 上次检测时间
	private long lastUpdateTime;
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 100;
			

	public OrientationSensorListener(Handler handler, Activity activity) {
		rotateHandler = handler;
		this.activity = activity;
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	public void onSensorChanged(SensorEvent event) {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否达到了检测时间间隔
		if (timeInterval < UPTATE_INTERVAL_TIME || activity == null){
			return;
		}
		lastUpdateTime = System.currentTimeMillis();
		
		int requestedOrientation = activity.getRequestedOrientation();
		if (requestedOrientation == 4) {//初始化为根据重力，所以在这里纠正方向
			int rt = activity.getWindowManager().getDefaultDisplay().getRotation();
			if (rt == Surface.ROTATION_0) {
				if (rotateHandler != null) {
					rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_1).sendToTarget();
				}
			} else if (rt == Surface.ROTATION_90) {
				if (rotateHandler != null) {
					rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_0).sendToTarget();
				}
			} else if (rt == Surface.ROTATION_180) {
				if (rotateHandler != null) {
					rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_9).sendToTarget();
				}
			} else if (rt == Surface.ROTATION_270) {
				if (rotateHandler != null) {
					rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_8).sendToTarget();
				}
			}
			return;
		}

		float[] values = event.values;
		int orientation = ORIENTATION_UNKNOWN;
		float X = -values[_DATA_X];
		float Y = -values[_DATA_Y];
		float Z = -values[_DATA_Z];
		float magnitude = X * X + Y * Y;
		// Don't trust the angle if the magnitude is small compared to the y
		// value
		if (magnitude * 4 >= Z * Z) {
			float OneEightyOverPi = 57.29577957855f;
			float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
			orientation = 90 - (int) Math.round(angle);
			// normalize to 0 - 359 range
			while (orientation >= 360) {
				orientation -= 360;
			}
			while (orientation < 0) {
				orientation += 360;
			}
		}
		if (orientation >= 60 && orientation <= 120) {// 反横屏
			if (lockOnce == -1) {
				if (requestedOrientation != 8) {
					if (isLock() && requestedOrientation != 4) {
						if (onDirectionChangeListener != null) {
							onDirectionChangeListener.onChange(orientation, 8);
						}
						return;
					}
					if (rotateHandler != null) {
						rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_8).sendToTarget();
					}
				}
			} else {
				if (lockOnce != 8) {
					lockOnce = -1;
					if (requestedOrientation != 8) {
						if (isLock() && requestedOrientation != 4) {
							if (onDirectionChangeListener != null) {
								onDirectionChangeListener.onChange(orientation, 8);
							}
							return;
						}
						if (rotateHandler != null) {
							rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_8).sendToTarget();
						}
					}
				}
			}
		} else if (orientation > 150 && orientation < 210 && !justLandscape) {// 反竖屏
			if (lockOnce == -1) {
				if (requestedOrientation != 9) {
					if (isLock() && requestedOrientation != 4) {
						if (onDirectionChangeListener != null) {
							onDirectionChangeListener.onChange(orientation, 9);
						}
						return;
					}
					if (rotateHandler != null) {
						rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_9).sendToTarget();
					}
				}
			} else {
				if (lockOnce != 9) {
					lockOnce = -1;
					if (requestedOrientation != 9) {
						if (isLock() && requestedOrientation != 4) {
							if (onDirectionChangeListener != null) {
								onDirectionChangeListener.onChange(orientation, 9);
							}
							return;
						}
						if (rotateHandler != null) {
							rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_9).sendToTarget();
						}
					}
				}
			}
		} else if (orientation > 240 && orientation < 300) {// 正横屏
			if (lockOnce == -1) {
				if (requestedOrientation != 0) {
					if (isLock() && requestedOrientation != 4) {
						if (onDirectionChangeListener != null) {
							onDirectionChangeListener.onChange(orientation, 0);
						}
						return;
					}
					if (rotateHandler != null) {
						rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_0).sendToTarget();
					}
				}
			} else {
				if (lockOnce != 0) {
					lockOnce = -1;
					if (requestedOrientation != 0) {
						if (isLock() && requestedOrientation != 4) {
							if (onDirectionChangeListener != null) {
								onDirectionChangeListener.onChange(orientation, 0);
							}
							return;
						}
						if (rotateHandler != null) {
							rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_0).sendToTarget();
						}
					}
				}
			}
		} else if (((orientation > 330 && orientation < 360) || (orientation > 0 && orientation < 30)) && !justLandscape) {// 正竖屏
			if (lockOnce == -1) {
				if (requestedOrientation != 1) {
					if (isLock() && requestedOrientation != 4) {
						if (onDirectionChangeListener != null) {
							onDirectionChangeListener.onChange(orientation, 1);
						}
						return;
					}
					if (rotateHandler != null) {
						rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_1).sendToTarget();
					}
				}
			} else {
				if (lockOnce != 1) {
					lockOnce = -1;
					if (requestedOrientation != 1) {
						if (isLock() && requestedOrientation != 4) {
							if (onDirectionChangeListener != null) {
								onDirectionChangeListener.onChange(orientation, 1);
							}
							return;
						}
						if (rotateHandler != null) {
							rotateHandler.obtainMessage(OrientationSensorUtils.ORIENTATION_1).sendToTarget();
						}
					}
				}
			}
		}
	}

	public boolean isLock() {
//  TODO
//		if (uiPlayContext != null) {
//			return isLock||uiPlayContext.isLockFlag();
//		}
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

	public void lockOnce(int orientation) {
		this.lockOnce = orientation;
	}

	public boolean isJustLandscape() {
		return justLandscape;
	}

	public void setJustLandscape(boolean justLandscape) {
		this.justLandscape = justLandscape;
	}

	public OnDirectionChangeListener getOnDirectionChangeListener() {
		return onDirectionChangeListener;
	}

	public void setOnDirectionChangeListener(OnDirectionChangeListener onDirectionChangeListener) {
		this.onDirectionChangeListener = onDirectionChangeListener;
	}

	public interface OnDirectionChangeListener {
		public void onChange(int orientation, int orientationProperty);
	}

//	public void attachUIContext(UIPlayContext uiPlayContext) {
//		this.uiPlayContext = uiPlayContext;
//	}


	public void destory() {
		activity = null;
	}
}

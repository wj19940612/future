package com.lecloud.skin.ui.orientation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;



/**
 * 播放控手势控制
 * @author dengjiaping
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public  class OrientationSensorUtils {
	public static final int ORIENTATION_8 = 1;
	public static final int ORIENTATION_9 = 2;
	public static final int ORIENTATION_0 = 3;
	public static final int ORIENTATION_1 = 4;
	
	private OrientationSensorListener mOrientationSensorListener;
	private SensorManager sm;
	private Sensor sensor;
	private Activity activity;
	private Handler handler;
	
	public OrientationSensorUtils(Activity activity,Handler handler) {
		this.activity = activity;
		this.handler = handler;
		init();
	}
	
	public void init() {
		sm = (SensorManager) activity.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		mOrientationSensorListener = new OrientationSensorListener(handler,activity);
//		mOrientationSensorListener.setJustLandscape(false);
		// 自定义旋转
//		handler = new ChangeOrientationHandler(activity);
		mOrientationSensorListener = new OrientationSensorListener(handler,activity);
//		mOrientationSensorListener.setLock(true);
	}
	
	public void onResume() {
		sm.registerListener(mOrientationSensorListener, sensor,SensorManager.SENSOR_DELAY_UI);
	}
	
	public void onPause() {
		sm.unregisterListener(mOrientationSensorListener);
	}
	
	public OrientationSensorListener getmOrientationSensorListener() {
		return mOrientationSensorListener;
	}

	public void setmOrientationSensorListener(OrientationSensorListener mOrientationSensorListener) {
		this.mOrientationSensorListener = mOrientationSensorListener;
	}

	public void setOrientation(int orientation) {
		handler.obtainMessage(orientation).sendToTarget();
	}
//  TODO
//	public void attachUIContext(UIPlayContext uiPlayContext) {
//		if (mOrientationSensorListener != null) {
//			mOrientationSensorListener.attachUIContext(uiPlayContext);
//		}
//	}

	public void destory() {
		if (mOrientationSensorListener != null) {
			mOrientationSensorListener.destory();
		}
	}

}

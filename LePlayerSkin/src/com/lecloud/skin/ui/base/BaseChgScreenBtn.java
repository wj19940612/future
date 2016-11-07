package com.lecloud.skin.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;

import com.lecloud.skin.ui.orientation.OrientationSensorUtils;
import com.lecloud.skin.ui.utils.ScreenUtils;

/**
 * 全、半屏切换
 */
public abstract class BaseChgScreenBtn extends BaseBtn {
    private OrientationSensorUtils mOrientationSensorUtils;

    public BaseChgScreenBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BaseChgScreenBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

	public BaseChgScreenBtn(Context context) {
        super(context);
        init();
    }

    private void init() {
    	setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLetvUIListener != null) {
                    if (ScreenUtils.getOrientation(getContext()) == Configuration.ORIENTATION_LANDSCAPE) {
                        // mLetvUIListener.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mOrientationSensorUtils.setOrientation(OrientationSensorUtils.ORIENTATION_1);
                    }else {
                        mOrientationSensorUtils.setOrientation(OrientationSensorUtils.ORIENTATION_0);
                    }
                    if (mOrientationSensorUtils != null) {
                        mOrientationSensorUtils.getmOrientationSensorListener().lockOnce(((Activity) getContext()).getRequestedOrientation());
                    }
                }
            }
		});
    	reset();
	}

    protected abstract int getZoomInResId();
    
    protected abstract int getZoomOutResId();

    /**
     * 展示放大状态
     */
    public void showZoomInState() {
        setImageResource(getZoomInResId());
    }

    /**
     * 展示缩小状态
     */
    public void showZoomOutState() {
        setImageResource(getZoomOutResId());
    }

    /**
     * 恢复初始状态
     */
    public void reset() {
        showZoomInState();
    }
    
    public void setOrientationSensorUtils(OrientationSensorUtils mOrientationSensorUtils) {
        this.mOrientationSensorUtils = mOrientationSensorUtils;
    }


}

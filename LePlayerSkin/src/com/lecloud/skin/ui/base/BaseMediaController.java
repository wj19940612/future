package com.lecloud.skin.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lecloud.skin.ui.LetvUIListener;
import com.lecloud.skin.ui.orientation.OrientationSensorUtils;

import java.util.List;

public abstract class BaseMediaController extends RelativeLayout{

    protected BasePlayBtn mBasePlayBtn;

    protected BaseChgScreenBtn mBaseChgScreenBtn;
    /**切换码率按钮*/
    protected BaseRateTypeBtn mBaseRateTypeBtn;

    protected LetvUIListener mLetvUIListener;
    /**切换全景陀螺仪检测方式按钮*/
    protected BaseChangeModeBtn mBaseChangeModeBtn;
    /**
     * 全景VR切换按钮
     */
    protected BaseChangeModeBtn mBaseChangeVRModeBtn;

    public BaseMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMediaController(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onInitView();
    }


    protected abstract void onInitView();

    public abstract void setLetvUIListener(LetvUIListener mLetvUIListener);

    public abstract void setPlayState(boolean isPlayState);

    public abstract void setRateTypeItems(List<String> ratetypes,String definition);

    public abstract void setCurrentPosition(long position);

    public abstract void setDuration(long duration);

    public abstract void setBufferPercentage(long bufferPercentage);

    public void isPano(boolean pano){
        if (pano) {
            mBaseChangeModeBtn.setVisibility(VISIBLE);
            mBaseChangeVRModeBtn.setVisibility(VISIBLE);
        }else {
            mBaseChangeModeBtn.setVisibility(GONE);
            mBaseChangeVRModeBtn.setVisibility(GONE);
        }
    }

    public void setOrientationSensorUtils(OrientationSensorUtils mOrientationSensorUtils) {
        mBaseChgScreenBtn.setOrientationSensorUtils(mOrientationSensorUtils);
    }

}

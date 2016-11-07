package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.lecloud.skin.ui.utils.ReUtils;
import com.lecloud.skin.videoview.pano.base.BasePanoSurfaceView;

public class V4ChangeDisplayModeBtn extends V4ChangeModeBtn {
    public static final int DISPLAY_MODE_NORMAL = BasePanoSurfaceView.DISPLAY_MODE_NORMAL;
    public static final int DISPLAY_MODE_GLASS = BasePanoSurfaceView.DISPLAY_MODE_GLASS;

    //    public static final int
    public int displayMode = DISPLAY_MODE_NORMAL;
    public V4ChangeDisplayModeBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4ChangeDisplayModeBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4ChangeDisplayModeBtn(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (displayMode == DISPLAY_MODE_NORMAL) {
                    displayMode = DISPLAY_MODE_GLASS;
                } else {
                    displayMode = DISPLAY_MODE_NORMAL;
                }
                if (mLetvUIListener != null) {
                    mLetvUIListener.onSwitchPanoDisplayMode(displayMode);
                }
                reset();
            }
        });
        reset();
    }

    @Override
    protected void reset() {
        String btnResId = displayMode == DISPLAY_MODE_GLASS ? getTouchStyle() : getMoveStyle();
        setImageResource(ReUtils.getDrawableId(getContext(), btnResId));
    }

    @Override
    protected String getMoveStyle() {
        return "letv_skin_v4_btn_vr_normal";
    }

    @Override
    protected String getTouchStyle() {
        return "letv_skin_v4_btn_vr_pressed";
    }


}

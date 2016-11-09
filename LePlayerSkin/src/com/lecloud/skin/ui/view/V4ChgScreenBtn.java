package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.base.BaseChgScreenBtn;

/**
 * 全、半屏切换
 */
public class V4ChgScreenBtn extends BaseChgScreenBtn {

    public V4ChgScreenBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4ChgScreenBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4ChgScreenBtn(Context context) {
        super(context);
    }


	@Override
	protected int getZoomInResId() {
		return R.drawable.letv_skin_v4_btn_chgscreen_small;
	}

	@Override
	protected int getZoomOutResId() {
		return R.drawable.letv_skin_v4_btn_chgscreen_large;
	}

}

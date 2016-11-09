package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lecloud.skin.ui.base.BaseChangeModeBtn;

public class V4ChangeModeBtn extends BaseChangeModeBtn {

    public V4ChangeModeBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4ChangeModeBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4ChangeModeBtn(Context context) {
        super(context);
    }

    @Override
    protected String getMoveStyle() {
        return "letv_skin_v4_btn_move";
    }

    @Override
    protected String getTouchStyle() {
        return "letv_skin_v4_btn_touch";
    }


}

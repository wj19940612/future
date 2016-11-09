package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.lecloud.sdk.api.md.entity.action.WaterConfig;
import com.lecloud.skin.ui.utils.PxUtils;

import java.util.List;

public class WaterMarkView extends RelativeLayout {
    private List<WaterConfig> mWaterMarks;
    private Context mContext;

    public WaterMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaterMarkView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public void setWaterMarks(List<WaterConfig> marks) {
        if (mWaterMarks != null) {
            removeAllViews();
            mWaterMarks = null;
        }

        mWaterMarks = marks;
        for (WaterConfig waterConfig : marks) {
            WaterMarkImageView waterMark = new WaterMarkImageView(mContext, waterConfig.getPicUrl());
            int pos = 1;
            try {
                pos = Integer.parseInt(waterConfig.getPos());
            } catch (NumberFormatException e) {
            }
            LayoutParams params = getWatermarkLocation(pos);
            addView(waterMark, params);
        }
    }

    private RelativeLayout.LayoutParams getWatermarkLocation(int location) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(PxUtils.dip2px(mContext, 40), PxUtils.dip2px(mContext, 26));
        switch (location) {
            case 1: // 左上角
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case 2: // 右上角
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                break;
            case 3: // 左下角
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case 4: // 右下角
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
        }
        int margin = PxUtils.dip2px(mContext, 12);
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        return params;

    }

    public void show() {
//        int count = getChildCount();
//        for (int i = 0; i < count; i++) {
//            View child = getChildAt(i);
//            child.setVisibility(VISIBLE);
//        }
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.INVISIBLE);
//        int count = getChildCount();
//        for (int i = 0; i < count; i++) {
//            View child = getChildAt(i);
//            child.setVisibility(INVISIBLE);
//        }
    }
}

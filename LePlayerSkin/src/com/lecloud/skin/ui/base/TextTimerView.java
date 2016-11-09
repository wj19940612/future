package com.lecloud.skin.ui.base;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecloud.skin.ui.utils.ReUtils;
import com.lecloud.skin.ui.utils.TimerUtils;

public class TextTimerView extends RelativeLayout {

    private TextView positionView;
    private TextView durationView;

    public TextTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public TextTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextTimerView(Context context) {
        super(context);
        initView(context);
    }

//    @Override
    protected void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(ReUtils.getLayoutId(context, "letv_skin_controller_text_timer"), null);
        positionView = (TextView) view.findViewById(ReUtils.getId(context, "skin_txt_position"));
        durationView = (TextView) view.findViewById(ReUtils.getId(context, "skin_txt_duration"));
        addView(view);
    }

    public void reset() {
        positionView.setText("00:00");
        //durationView.setText("00:00");
    }

    public void setTextTimer(long position, long duration) {
        positionView.setText(TimerUtils.stringForTime((int) (position / 1000)));
        durationView.setText(TimerUtils.stringForTime((int) (duration / 1000)));
    }

}

package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.base.BaseChgScreenBtn;
import com.lecloud.skin.ui.base.BasePlayBtn;
import com.lecloud.skin.ui.utils.PxUtils;

import java.util.List;

/**
 * Created by gaolinhua on 16/10/19.
 */
public class V4SmallLiveMediaControllerNew extends V4LargeLiveMediaControllerNew {
    public V4SmallLiveMediaControllerNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4SmallLiveMediaControllerNew(Context context) {
        super(context);
    }

    public V4SmallLiveMediaControllerNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitView() {
        mBasePlayBtn = (BasePlayBtn) findViewById(R.id.vnew_play_btn);
        mBaseChgScreenBtn = (BaseChgScreenBtn) findViewById(R.id.vnew_chg_btn);
        mBaseLiveSeekBar = (V4LivePageSeekBar) findViewById(R.id.vnew_seekbar);
        mBaseBackToLive = (TextView) findViewById(R.id.vnew_back_to_live);

        mBaseBackToLive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLetvUIListener.resetPlay();
                mBaseBackToLive.setVisibility(GONE);
            }
        });

        mBaseLiveSeekBar.setOnBackToLiveListener(new V4LivePageSeekBar.OnBackToLiveListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                mBaseBackToLive.setVisibility(visibility);
            }

            @Override
            public void onTextChanged(String text) {
                mBaseBackToLive.setText(text);
            }

            @Override
            public void onPositionChanged(int leftMargin) {
                RelativeLayout.LayoutParams params = (LayoutParams) mBaseBackToLive.getLayoutParams();
                params.width = PxUtils.dip2px(getContext(), 90);
                params.height = PxUtils.dip2px(getContext(), 76);
                params.leftMargin = leftMargin - params.width / 2;
                mBaseBackToLive.setLayoutParams(params);
            }
        });
    }

    @Override
    public void setRateTypeItems(List<String> ratetypes, String definition) {

    }

}

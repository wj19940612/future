package com.lecloud.skin.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.base.BaseRateTypeBtn;
import com.lecloud.skin.ui.base.BaseVodMediaController;
import com.lecloud.skin.ui.base.BaseChangeModeBtn;
import com.lecloud.skin.ui.base.BaseChgScreenBtn;
import com.lecloud.skin.ui.base.BasePlayBtn;
import com.lecloud.skin.ui.base.BasePlayerSeekBar;
import com.lecloud.skin.ui.base.TextTimerView;

public class V4LargeMediaController extends BaseVodMediaController {
	
	
    public V4LargeMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4LargeMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4LargeMediaController(Context context) {
        super(context);
    }
    
	
	@Override
	protected void onInitView() {
		mBasePlayBtn = (BasePlayBtn) findViewById(R.id.vnew_play_btn);
		mBaseChgScreenBtn = (BaseChgScreenBtn) findViewById(R.id.vnew_chg_btn);
		mBaseRateTypeBtn = (BaseRateTypeBtn) findViewById(R.id.vnew_rate_btn);
		mBaseRateTypeBtn.setVisibility(GONE);
		mBasePlayerSeekBar = (BasePlayerSeekBar) findViewById(R.id.vnew_seekbar);
		mBaseChangeModeBtn = (BaseChangeModeBtn) findViewById(R.id.vnew_change_mode);
		mBaseChangeVRModeBtn = (BaseChangeModeBtn) findViewById(R.id.vnew_change_vr_mode);
		mTextTimerView = (TextTimerView) findViewById(R.id.vnew_text_duration_ref);
		
		mBaseChgScreenBtn.showZoomOutState();
	}

}

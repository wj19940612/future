package com.lecloud.skin.ui.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.lecloud.skin.R;
import com.lecloud.skin.ui.base.BaseChgScreenBtn;
import com.lecloud.skin.ui.base.BasePlayBtn;
import com.lecloud.skin.ui.base.BasePlayerSeekBar;
import com.lecloud.skin.ui.base.BaseVodMediaController;
import com.lecloud.skin.ui.base.TextTimerView;

public class V4SmallMediaController extends BaseVodMediaController {
	
	
    public V4SmallMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public V4SmallMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public V4SmallMediaController(Context context) {
        super(context);
    }
	
	@Override
	protected void onInitView() {
		mBasePlayBtn = (BasePlayBtn) findViewById(R.id.vnew_play_btn);
		mBaseChgScreenBtn = (BaseChgScreenBtn) findViewById(R.id.vnew_chg_btn);
		mBasePlayerSeekBar = (BasePlayerSeekBar) findViewById(R.id.vnew_seekbar);
		mTextTimerView = (TextTimerView) findViewById(R.id.vnew_text_duration_ref);
	}
	
	@Override
	public void setRateTypeItems(List<String> ratetypes,String definition) {
		
	}
}

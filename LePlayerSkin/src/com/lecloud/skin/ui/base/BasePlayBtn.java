package com.lecloud.skin.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 播放按钮
 */
public abstract class BasePlayBtn extends BaseBtn {

	public BasePlayBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

	public BasePlayBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasePlayBtn(Context context) {
        super(context);
        init();
        setBackgroundDrawable(null);
    }
    
    private void init() {
    	setPlayState(true);
    	
    	setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mLetvUIListener != null) {
					mLetvUIListener.onClickPlay();
				}
			}
		});
  	}
    
    abstract protected int getPauseResId();

    abstract protected int getPlayResId();
    
    /**
     * @param isPlayState 播放状态
     */
    public void setPlayState(boolean isPlayState) {
        if (isPlayState) {
            super.setImageResource(getPlayResId());
        } else {
            super.setImageResource(getPauseResId());
        }
    }
   
}

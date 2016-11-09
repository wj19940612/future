package com.lecloud.skin.popupwindow;

import com.lecloud.skin.ui.utils.PxUtils;
import com.lecloud.skin.ui.utils.ReUtils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


/**
 * 手势seek
 */
public class GestureSeekToPopWindow extends BasePopupWindow {

    private View mPopView;
    private PopupWindow mPopupWindow;
    private ImageView imageView;
    private TextView progressTime;
    private TextView progressTimeDuration;
    private TextView progressTimeSplit;

    protected LayoutInflater mLayoutInflater;

    public GestureSeekToPopWindow(Context context) {
        super(context);
    }

    @Override
    protected View getPopContentView() {

        return null;
    }

    protected void initView(Context context) {
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mPopView = mLayoutInflater.inflate(ReUtils.getLayoutId(context, "letv_skin_v4_large_gesture_seek_layout"), null);
        imageView = (ImageView) mPopView.findViewById(ReUtils.getId(context, "seek_imageView"));
        progressTime = (TextView) mPopView.findViewById(ReUtils.getId(context, "progress_time"));
        progressTimeDuration = (TextView) mPopView.findViewById(ReUtils.getId(context, "progress_time_duration"));
        progressTimeSplit = (TextView) mPopView.findViewById(ReUtils.getId(context, "progress_time_split"));
    }

	public void setProgress(CharSequence text) {
		
		if (progressTimeSplit != null) {
			progressTimeSplit.setText(text);
			progressTimeSplit.setTextColor(0xff1073c2);
		}
		
//		if (progressTime != null) {
//			progressTime.setVisibility(View.GONE);
//		}
//		
//		if (progressTimeDuration != null) {
//			progressTimeDuration.setVisibility(View.GONE);
//		}

	}
    
	public void setProgress(CharSequence progress, CharSequence duration) {
		if (progressTime != null) {
			progressTime.setText(progress);
		}
		if (progressTimeDuration != null) {
			progressTimeDuration.setText(duration);
		}
	}
    
    public void setImageForward() {
    	setImageResource("letv_skin_v4_forward");
    }
    
    public void setImageRewind() {
    	setImageResource("letv_skin_v4_rewind");
    }
    
    public void setImageResource(String name) {
    	if (imageView != null) {
    		imageView.setImageResource(ReUtils.getDrawableId(context, name));
    	}
    }

    public boolean isShowing() {
        if (this.mPopupWindow == null) {
            return false;
        }
        return this.mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (this.mPopupWindow == null) {
            return;
        }
        this.mPopupWindow.dismiss();
    }

    public void showPopWindow(View parent) {
        this.mPopupWindow = new PopupWindow(mPopView, PxUtils.dip2px(context, 200), PxUtils.dip2px(context, 100), false);
        this.mPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    @Override
    protected int getPopHeight(View anchor) {
        return 0;
    }

    @Override
    protected int getPopWidth(View anchor) {
        return 0;
    }

	@Override
	protected void init() {
		initView(context);
	}

}

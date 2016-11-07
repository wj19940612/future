package com.lecloud.skin.popupwindow;


import com.lecloud.skin.ui.utils.ReUtils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

/**
 * 音量
 */
public class GestureVolumePopWindow extends BasePopupWindow {
   
	private View mPopView;
    private PopupWindow mPopupWindow;
    private ProgressBar mProgressbar;
    protected LayoutInflater mLayoutInflater;
    public GestureVolumePopWindow(Context context) {
		super(context);
	}
    
	@Override
	protected View getPopContentView() {
		
		return null;
	}
	
	protected void initView(Context context) {
		this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mPopView = mLayoutInflater.inflate(ReUtils.getLayoutId(context, "letv_skin_v4_large_gesture_volume_layout"), null);
        this.mProgressbar = (ProgressBar) mPopView.findViewById(ReUtils.getId(context,"progressBar"));
	}
	
	public void setProgress(int progress) {
        this.mProgressbar.setProgress(progress);
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
        this.mPopupWindow = new PopupWindow(mPopView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        this.mPopupWindow.showAtLocation(parent,Gravity.LEFT, 0, 0);
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

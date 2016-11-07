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
 * 屏幕亮度
 * 
 */
public class GestureBrightnessPopWindow extends BasePopupWindow {
    private View mPopView;
    private PopupWindow mPopupWindow;
    private ProgressBar mProgressbar;
    protected LayoutInflater mLayoutInflater;

    public GestureBrightnessPopWindow(Context context) {
        super(context);
    }

    public void showPopWindow(View parent) {
        this.mPopupWindow = new PopupWindow(this.mPopView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        this.mPopupWindow.showAtLocation(parent, Gravity.RIGHT, 0, 0);
    }

    public void dismiss() {
        if (this.mPopupWindow == null) {
            return;
        }
        this.mPopupWindow.dismiss();
    }

    public boolean isShowing() {
        if (this.mPopupWindow == null) {
            return false;
        }
        return this.mPopupWindow.isShowing();
    }

    public void setProgress(int progress) {
        this.mProgressbar.setProgress(progress);
    }

    @Override
    protected View getPopContentView() {
        return null;
    }

    protected void initView(Context context) {
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mPopView = mLayoutInflater.inflate(ReUtils.getLayoutId(context, "letv_skin_v4_large_gesture_brightness_layout"), null);
        this.mProgressbar = (ProgressBar) mPopView.findViewById(ReUtils.getId(context, "progressBar"));
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

package com.jnhyxx.html5.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;

public class HomePopup {

    TextView mTitle;
    TextView mMessage;
    TextView mClose;
    TextView mCheckDetailButton;

    private String mMessageText;
    private String mTitleText;

    private Dialog mDialog;
    private Activity mActivity;
    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void onClick(Dialog dialog);
    }

    public static HomePopup with(Activity activity, String msg, String titleTxt) {
        HomePopup dialog = new HomePopup(activity);
        dialog.setMessage(msg);
        dialog.setTitle(titleTxt);
        return dialog;
    }

    public HomePopup(Activity activity) {
        mActivity = activity;
    }

    private HomePopup setMessage(String message) {
        mMessageText = message;
        return this;
    }

    private HomePopup setTitle(String title) {
        mTitleText = title;
        return this;
    }

    public HomePopup setOnCheckDetailListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        return this;
    }

    private void scaleDialogWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDialog.getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void show() {
        create();

        if (!mActivity.isFinishing()) {
            mDialog.show();
            //scaleDialogWidth(0.9);
        }
    }

    private void create() {
        mDialog = new Dialog(mActivity, R.style.DialogTheme_NoTitle);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_home_popup, null);
        mDialog.setContentView(view);
        mTitle = (TextView) view.findViewById(R.id.title);
        mMessage = (TextView) view.findViewById(R.id.message);
        mClose = (TextView) view.findViewById(R.id.close);
        mCheckDetailButton = (TextView) view.findViewById(R.id.checkDetail);

        mTitle.setText(mTitleText);
        mMessage.setText(mMessageText);
        mMessage.setMovementMethod(new ScrollingMovementMethod());
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mCheckDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(mDialog);
                }
            }
        });
    }
}

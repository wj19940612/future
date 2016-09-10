package com.jnhyxx.html5.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SmartDialog {

    TextView mTitle;
    TextView mMessage;
    TextView mNegative;
    TextView mPosition;
    LinearLayout mDoubleButtons;
    TextView mSingleButton;

    private int mPositiveId;
    private int mNegativeId;
    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;
    private OnCancelListener mOnCancelListener;

    private boolean mIsDoubleButtons;

    private String mMessageText;

    private String mTitleText = "";

    private boolean mCancelableOnTouchOutside;

    private Dialog mDialog;

    private Activity mActivity;

    public interface OnClickListener {
        void onClick(Dialog dialog);
    }

    public interface OnCancelListener {
        void onCancel(Dialog dialog);
    }

    private static Map<String, List<SmartDialog>> mListMap = new HashMap<>();

    public static SmartDialog with(Activity activity, int resid) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(resid);
        return dialog;
    }

    public static SmartDialog with(Activity activity, String msg) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(msg);
        return dialog;
    }

    public static SmartDialog with(Activity activity, int resid, int titleId) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(resid);
        dialog.setTitle(titleId);
        return dialog;
    }

    public static SmartDialog with(Activity activity, String msg, String titleTxt) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        dialog.setMessage(msg);
        dialog.setTitle(titleTxt);
        return dialog;
    }

    /**
     * @param activity
     * @return
     * @deprecated use {@link #with(Activity activity, String msg)} instead
     */
    public static SmartDialog with(Activity activity) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        return dialog;
    }

    private static void addMap(Activity activity, SmartDialog dialog) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        if (dialogList == null) {
            dialogList = new LinkedList<>();
        }
        dialogList.add(dialog);
        mListMap.put(key, dialogList);
    }

    public static void dismiss(Activity activity) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        if (dialogList != null) {
            for (SmartDialog dialog : dialogList) {
                dialog.dismiss();
            }
            mListMap.remove(key);
        }
    }

    private SmartDialog(Activity activity) {
        mActivity = activity;
        mPositiveId = R.string.ok;
        mCancelableOnTouchOutside = true;
    }

    private void scaleDialogWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDialog.getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public SmartDialog setPositive(int textId, OnClickListener listener) {
        mPositiveId = textId;
        mPositiveListener = listener;
        return this;
    }

    public SmartDialog setPositive(int textId) {
        mPositiveId = textId;
        return this;
    }

    public SmartDialog setNegative(int textId, OnClickListener listener) {
        mNegativeId = textId;
        mNegativeListener = listener;

        mIsDoubleButtons = true;
        return this;
    }

    public SmartDialog setNegative(int textId) {
        mNegativeId = textId;

        mIsDoubleButtons = true;
        return this;
    }

    public SmartDialog setCancelableOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        return this;
    }

    public SmartDialog setCancelListener(OnCancelListener cancelListener) {
        mOnCancelListener = cancelListener;
        return this;
    }

    private SmartDialog setMessage(int messageId) {
        mMessageText = mActivity.getText(messageId).toString();
        return this;
    }

    private SmartDialog setMessage(String message) {
        mMessageText = message;
        return this;
    }

    public SmartDialog setTitle(int titleId) {
        mTitleText = mActivity.getText(titleId).toString();
        return this;
    }

    private SmartDialog setTitle(String title) {
        mTitleText = title;
        return this;
    }

    public void show() {
        create();

        if (!mActivity.isFinishing()) {
            mDialog.show();
            scaleDialogWidth(0.9);
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void create() {
        mDialog = new Dialog(mActivity, R.style.DialogTheme_NoTitle);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.activity_popup_dialog, null);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(mCancelableOnTouchOutside);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel(mDialog);

                } else if (!mCancelableOnTouchOutside) {
                    // finish current page when not allow user to cancel on touch outside
                    if (mActivity != null) {
                        mActivity.finish();
                    }
                }
            }
        });

        mTitle = (TextView) view.findViewById(R.id.title);
        mMessage = (TextView) view.findViewById(R.id.message);
        mDoubleButtons = (LinearLayout) view.findViewById(R.id.doubleButtons);
        mNegative = (TextView) view.findViewById(R.id.negative);
        mPosition = (TextView) view.findViewById(R.id.position);
        mSingleButton = (TextView) view.findViewById(R.id.singleButton);

//        mTitle.setVisibility(View.INVISIBLE);
        mMessage.setText(mMessageText);
        if (TextUtils.isEmpty(mTitleText)) {
            mTitle.setVisibility(View.GONE);
        } else {
            mTitle.setText(mTitleText);
        }
        if (mIsDoubleButtons) {
            mSingleButton.setVisibility(View.GONE);
            mDoubleButtons.setVisibility(View.VISIBLE);

            mPosition.setText(mPositiveId);
            mPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPositiveListener != null) {
                        mPositiveListener.onClick(mDialog);
                    }
                }
            });
            mNegative.setText(mNegativeId);
            mNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mNegativeListener != null) {
                        mNegativeListener.onClick(mDialog);
                    } else {
                        mDialog.dismiss();
                    }
                }
            });
        } else {
            mSingleButton.setVisibility(View.VISIBLE);
            mDoubleButtons.setVisibility(View.GONE);

            mSingleButton.setText(mPositiveId);
            mSingleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPositiveListener != null) {
                        mPositiveListener.onClick(mDialog);
                    } else {
                        mDialog.dismiss();
                    }
                }
            });
        }
    }
}

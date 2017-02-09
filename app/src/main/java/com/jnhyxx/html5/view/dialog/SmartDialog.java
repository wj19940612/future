package com.jnhyxx.html5.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
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
    private OnDismissListener mDismissListener;

    private boolean mIsDoubleButtons;

    private String mMessageText;
    private String mTitleText;

    private int mMessageTextMaxLines = 3;

    private boolean mCancelableOnTouchOutside;

    private Dialog mDialog;
    private Activity mActivity;

    private int mPositiveTextColor = Color.WHITE;
    private int mSingleButtonBg = R.drawable.btn_dialog_single;
    private int mMessageGravity = Gravity.CENTER;
    private int mTitleMargin = 15;
    private int mTitltTextColor = Color.BLACK;


    public interface OnClickListener {
        void onClick(Dialog dialog);
    }

    public interface OnCancelListener {
        void onCancel(Dialog dialog);
    }

    public interface OnDismissListener {
        void onDismiss(Dialog dialog);
    }

    private static Map<String, List<SmartDialog>> mListMap = new HashMap<>();

    public static SmartDialog single(Activity activity, String msg) {
        String key = activity.getClass().getSimpleName();
        List<SmartDialog> dialogList = mListMap.get(key);
        SmartDialog dialog;
        if (dialogList != null && dialogList.size() > 0) {
            dialog = dialogList.get(0);
        } else {
            dialog = with(activity, msg);
        }
        dialog.setMessage(msg);
        return dialog;
    }

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

    public SmartDialog setOnDismissListener(OnDismissListener onDismissListener) {
        mDismissListener = onDismissListener;
        return this;
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

    public SmartDialog setPositiveTextColor(int resColorId) {
        mPositiveTextColor = resColorId;
        return this;
    }

    public SmartDialog setSingleButtonBg(int resBgId) {
        mSingleButtonBg = resBgId;
        return this;
    }

    public SmartDialog setMessageGravity(int gravity) {
        mMessageGravity = gravity;
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

    public SmartDialog setMessage(String message) {
        mMessageText = message;
        return this;
    }

    public SmartDialog setTitle(int titleId) {
        mTitleText = mActivity.getText(titleId).toString();
        return this;
    }

    public SmartDialog setTitle(String title) {
        mTitleText = title;
        return this;
    }

    public SmartDialog setTitleTextColor(int titleTextColor) {
        mTitltTextColor = titleTextColor;
        return this;
    }

    public SmartDialog setMessageMaxLines(int maxLines) {
        mMessageTextMaxLines = maxLines;
        return this;
    }

    public SmartDialog setTitleMargin(int margin) {
        mTitleMargin = margin;
        return this;
    }

    public void show() {
        if (mDialog != null) { // single dialog
            mMessage.setText(mMessageText);
        } else {
            create();
        }

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
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mDismissListener != null) {
                    mDismissListener.onDismiss(mDialog);
                }
            }
        });

        mTitle = (TextView) view.findViewById(R.id.title);
        mMessage = (TextView) view.findViewById(R.id.message);
        mDoubleButtons = (LinearLayout) view.findViewById(R.id.doubleButtons);
        mNegative = (TextView) view.findViewById(R.id.negative);
        mPosition = (TextView) view.findViewById(R.id.position);
        mSingleButton = (TextView) view.findViewById(R.id.singleButton);

        mMessage.setText(mMessageText);
        mMessage.setGravity(mMessageGravity);
        mMessage.setMaxLines(mMessageTextMaxLines);
        if (TextUtils.isEmpty(mTitleText)) {
            mTitle.setVisibility(View.GONE);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTitleMargin, mActivity.getResources().getDisplayMetrics()), 0, 0);
            mTitle.setLayoutParams(layoutParams);
            mTitle.setText(mTitleText);
            mTitle.setTextColor(mTitltTextColor);
        }
        if (mIsDoubleButtons) {
            mSingleButton.setVisibility(View.GONE);
            mDoubleButtons.setVisibility(View.VISIBLE);

            mPosition.setText(mPositiveId);
            mPosition.setTextColor(mPositiveTextColor);
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
            mSingleButton.setBackgroundResource(mSingleButtonBg);
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

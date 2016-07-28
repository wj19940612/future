package com.jnhyxx.html5.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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

    private boolean mCancelable;

    private Dialog mDialog;

    private Activity mActivity;

    public interface OnClickListener {
        void onClick(Dialog dialog);
    }

    public interface OnCancelListener {
        void onCancel(Dialog dialog);
    }

    private static Map<String, List<SmartDialog>> mListMap = new HashMap<>();

    public static SmartDialog with(String tag, Activity activity) {
        SmartDialog dialog = new SmartDialog(activity);
        addMap(activity, dialog);
        return dialog;
    }

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

    public static void dismiss(String tag, Activity activity) {
        List<SmartDialog> dialogList = mListMap.get(tag);
        if (dialogList != null) {
            for (SmartDialog dialog : dialogList) {
                dialog.dismiss();
            }
            mListMap.remove(tag);
        }

        String key = activity.getClass().getSimpleName();
        dialogList = mListMap.get(key);
        if (dialogList != null) {
            for (SmartDialog dialog : dialogList) {
                dialog.dismiss();
            }
            mListMap.remove(tag);
        }
    }

    private SmartDialog(Activity activity) {
        mActivity = activity;
        mPositiveId = R.string.ok;
        mCancelable = true;
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

    public SmartDialog setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    public SmartDialog setCancelListener(OnCancelListener cancelListener) {
        mOnCancelListener = cancelListener;
        return this;
    }

    public SmartDialog setMessage(int messageId) {
        mMessageText = mActivity.getText(messageId).toString();
        return this;
    }

    public SmartDialog setMessage(String message) {
        mMessageText = message;
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
        mDialog.setCancelable(mCancelable);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel(mDialog);
                }
            }
        });

        mTitle = (TextView) view.findViewById(R.id.title);
        mMessage = (TextView) view.findViewById(R.id.message);
        mDoubleButtons = (LinearLayout) view.findViewById(R.id.doubleButtons);
        mNegative = (TextView) view.findViewById(R.id.negative);
        mPosition = (TextView) view.findViewById(R.id.position);
        mSingleButton = (TextView) view.findViewById(R.id.singleButton);

        mTitle.setVisibility(View.INVISIBLE);
        mMessage.setText(mMessageText);

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

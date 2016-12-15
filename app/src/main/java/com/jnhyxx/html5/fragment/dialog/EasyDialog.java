package com.jnhyxx.html5.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @deprecated
 */
public class EasyDialog extends AppCompatDialogFragment {

    private static final String MESSAGE = "message";

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.message)
    TextView mMessage;
    @BindView(R.id.negative)
    TextView mNegative;
    @BindView(R.id.position)
    TextView mPosition;
    @BindView(R.id.doubleButtons)
    LinearLayout mDoubleButtons;
    @BindView(R.id.singleButton)
    TextView mSingleButton;

    private Unbinder mBinder;

    private int mPositiveId;
    private int mNegativeId;
    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;
    private boolean mIsDoubleButtons;
    private String mMessageText;
    private boolean mCloseable;

    public interface OnClickListener {
        void onClick(Dialog dialog);
    }

    public static EasyDialog newInstance(String message) {
        EasyDialog dialog = new EasyDialog();
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, message);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessageText = getArguments().getString(MESSAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_popup_dialog, container, false);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scaleDialogWindowWidth(0.9);
        mTitle.setVisibility(View.INVISIBLE);
        mMessage.setText(mMessageText);
        getDialog().setCancelable(mCloseable);
        getDialog().setCanceledOnTouchOutside(mCloseable);

        if (mIsDoubleButtons) {
            mSingleButton.setVisibility(View.GONE);
            mDoubleButtons.setVisibility(View.VISIBLE);

            mPosition.setText(mPositiveId);
            mPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPositiveListener != null) {
                        mPositiveListener.onClick(getDialog());
                    }
                }
            });
            mNegative.setText(mNegativeId);
            mNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mNegativeListener != null) {
                        mNegativeListener.onClick(getDialog());
                    } else {
                        getDialog().dismiss();
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
                        mPositiveListener.onClick(getDialog());
                    } else {
                        getDialog().dismiss();
                    }
                }
            });
        }
    }

    protected void scaleDialogWindowWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDialog().getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public EasyDialog setPositive(int textId, OnClickListener listener) {
        mPositiveId = textId;
        mPositiveListener = listener;
        return this;
    }

    public EasyDialog setPositive(int textId) {
        mPositiveId = textId;
        return this;
    }

    public EasyDialog setNegative(int textId, OnClickListener listener) {
        mNegativeId = textId;
        mNegativeListener = listener;
        mIsDoubleButtons = true;
        return this;
    }

    public EasyDialog setNegative(int textId) {
        mNegativeId = textId;
        mIsDoubleButtons = true;
        return this;
    }

    public EasyDialog setCloseable(boolean closeable) {
        mCloseable = closeable;
        return this;
    }

    public void show(FragmentActivity activity) {
        if (activity != null && !activity.isFinishing()) {
            this.show(activity.getSupportFragmentManager(), "dialog");
        }
    }
}

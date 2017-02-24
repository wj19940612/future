package com.jnhyxx.html5.view;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveHomeChatInfo;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.squareup.picasso.Picasso;

public class TeacherCommand extends LinearLayout {

    private static final int DISMISS_DELAY = 60;

    public interface OnClickListener {
        void onTeacherHeadClick();

        void onCloseButtonClick(LiveHomeChatInfo teacherCommand);
    }

    private ImageView mTeacherHead;
    private TextView mTeacherCommand;
    private LinearLayout mTeacherCommandArea;

    private View mTeacherCommandView;
    private ImageView mCloseButton;

    private Handler mHandler;
    private OnClickListener mListener;

    private LiveHomeChatInfo mLiveHomeChatInfo;

    public TeacherCommand(Context context) {
        super(context);
        init();
    }

    public TeacherCommand(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mTeacherCommandView = LayoutInflater.from(getContext()).inflate(R.layout.view_teacher_command, null);
        mTeacherHead = (ImageView) mTeacherCommandView.findViewById(R.id.teacherHead);
        mTeacherCommand = (TextView) mTeacherCommandView.findViewById(R.id.teacherCommand);
        mTeacherCommandArea = (LinearLayout) mTeacherCommandView.findViewById(R.id.teacherCommandArea);
        mTeacherCommandArea.setVisibility(INVISIBLE);
        mTeacherHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTeacherHeadClick();
                }
            }
        });

        mCloseButton = new ImageView(getContext());
        mCloseButton.setImageResource(R.drawable.ic_teacher_command_close);
        mCloseButton.setVisibility(GONE);
        int padding = dp2px(10);
        mCloseButton.setPadding(padding, 0, 0, padding);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.setMargins(0, 0, dp2px(15), 0);
        addView(mTeacherCommandView);
        addView(mCloseButton, params);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeTeacherCommand();
                if (mListener != null) {
                    mListener.onCloseButtonClick(mLiveHomeChatInfo);
                }
            }
        });

        mHandler = new Handler();
    }

    private void closeTeacherCommand() {
        mTeacherCommandArea.setVisibility(INVISIBLE);
        mCloseButton.setVisibility(GONE);
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setTeacherHeader(String headPictureUrl) {
        if (TextUtils.isEmpty(headPictureUrl)) {
            Picasso.with(getContext()).load(R.drawable.ic_live_pic_head)
                    .resizeDimen(R.dimen.teacher_command_head_size, R.dimen.teacher_command_head_size)
                    .transform(new CircleTransform()).into(mTeacherHead);
        } else {
            Picasso.with(getContext()).load(headPictureUrl)
                    .transform(new CircleTransform())
                    .resizeDimen(R.dimen.teacher_command_head_size, R.dimen.teacher_command_head_size)
                    .into(mTeacherHead);
        }
    }

    public void setTeacherCommand(LiveHomeChatInfo teacherCommand) {
        if (teacherCommand != null && !TextUtils.isEmpty(teacherCommand.getMsg())) {
            mLiveHomeChatInfo = teacherCommand;
            mTeacherCommandArea.setVisibility(VISIBLE);
            mCloseButton.setVisibility(VISIBLE);
            mTeacherCommand.setText(getContext().getString(R.string.live_teacher_order, teacherCommand.getMsg()));
            mTeacherCommand.setMovementMethod(new ScrollingMovementMethod());

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeTeacherCommand();
                }
            }, DISMISS_DELAY * 1000);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mListener = onClickListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}

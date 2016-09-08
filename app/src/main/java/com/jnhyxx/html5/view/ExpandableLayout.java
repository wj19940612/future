package com.jnhyxx.html5.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;


/**
 * Created by Administrator on 2016/8/25.
 */

public class ExpandableLayout extends RelativeLayout {
    private static final String TAG = "ExpandableLayout";
    private Integer duration;
    private Animation animation;
    private Boolean isAnimationRunning = false;
    private Boolean isOpened = false;

    public ExpandableLayout(Context context) {
        super(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        CharSequence bottomTxt = typedArray.getText(R.styleable.ExpandableLayout_elBottomTxt);
        CharSequence leftTxt = typedArray.getText(R.styleable.ExpandableLayout_elLeftTxt);
        duration = typedArray.getInt(R.styleable.ExpandableLayout_elDuration, getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        View view = View.inflate(context, R.layout.account_view_expandable, this);
        TextView tv_leftTxt = (TextView) view.findViewById(R.id.lefe_txt_view);
        final ImageView ivAboutUsRight = (ImageView) view.findViewById(R.id.ivAboutUsRight);
        final TextView tv_bottom = (TextView) view.findViewById(R.id.bottom_txt);
        RelativeLayout rlHeadlayout = (RelativeLayout) view.findViewById(R.id.view_expandable_headerlayout);
//        if (!TextUtils.isEmpty(leftTxt)) {
//            tv_leftTxt.setText(leftTxt);
//        } else {
//            throw new NullPointerException(" left Txt is Empty");
//        }
        tv_leftTxt.setText(leftTxt);
        tv_bottom.setText(bottomTxt);
        Log.d(TAG, "左边文字 " + leftTxt + "\n右边文字 " + bottomTxt);
        tv_bottom.setVisibility(GONE);

        final RotateAnimation rotateAnimationUp = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimationUp.setDuration(200);
        rotateAnimationUp.setFillAfter(true);
        final RotateAnimation rotateAnimationDown = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimationDown.setDuration(200);
        rotateAnimationDown.setFillAfter(true);


        rlHeadlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isAnimationRunning) {
                    if (tv_bottom.getVisibility() == VISIBLE) {
                        collapse(tv_bottom);
                        ivAboutUsRight.startAnimation(rotateAnimationDown);
                    } else {
                        ivAboutUsRight.startAnimation(rotateAnimationUp);
                        expand(tv_bottom);
                    }

                    isAnimationRunning = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isAnimationRunning = false;
                        }
                    }, duration);
                }
            }
        });
        typedArray.recycle();
    }

    public void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(VISIBLE);

        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1)
                    isOpened = true;
                v.getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                    isOpened = false;
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    @Override
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        animation.setAnimationListener(animationListener);
    }
}

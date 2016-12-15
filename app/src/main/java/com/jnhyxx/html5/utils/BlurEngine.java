package com.jnhyxx.html5.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BlurEngine {

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 8f;
    private static final int ANIM_DURATION = 500;

    private BlurTask mBlurTask;
    private ImageView mBlurBackgroundView;
    private ViewGroup mViewGroup;
    private int mBackgroundRes;

    public BlurEngine(ViewGroup viewGroup) {
        mBlurTask = new BlurTask(viewGroup);
        mViewGroup = viewGroup;
        mBackgroundRes = -1;
    }

    public BlurEngine(ViewGroup viewGroup, int backgroundRes) {
        mBlurTask = new BlurTask(viewGroup);
        mViewGroup = viewGroup;
        mBackgroundRes = backgroundRes;
    }

    private Bitmap getScreenshot(View v) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private Bitmap getPureBitmap(View v, int backgroundRes) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(ContextCompat.getColor(v.getContext(), backgroundRes));
        return bitmap;
    }

    public void onResume() {
        if (mBlurBackgroundView == null) {
            if (mBackgroundRes == -1) {
                mBlurTask.execute(getScreenshot(mViewGroup));
            } else {
                mBlurTask.execute(getPureBitmap(mViewGroup, mBackgroundRes));
            }
        }
    }

    public void onDestroyView() {
        if (mBlurTask != null) {
            mBlurTask.cancel(true);
        }
        if (mBlurBackgroundView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mBlurBackgroundView
                        .animate()
                        .alpha(0f)
                        .setDuration(ANIM_DURATION)
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                removeBlurBackgroundView();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                super.onAnimationCancel(animation);
                                removeBlurBackgroundView();
                            }
                        }).start();
            } else {
                removeBlurBackgroundView();
            }
        }
    }

    private void removeBlurBackgroundView() {
        ViewGroup parent = (ViewGroup) mBlurBackgroundView.getParent();
        if (parent != null) {
            parent.removeView(mBlurBackgroundView);
        }
        mBlurBackgroundView = null;
    }

    private class BlurTask extends AsyncTask<Bitmap, Void, ImageView> {

        private WeakReference<ViewGroup> mReference;

        public BlurTask(ViewGroup viewGroup) {
            mReference = new WeakReference<>(viewGroup);
        }

        @Override
        protected ImageView doInBackground(Bitmap... bitmaps) {
            if (!isCancelled() && mReference.get() != null) {
                return createBlurView(mReference.get(), bitmaps[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ImageView blurBackgroundView) {
            super.onPostExecute(blurBackgroundView);
            if (mReference.get() != null) {
                ViewGroup viewGroup = mReference.get();

                if (viewGroup instanceof FrameLayout) {
                    mBlurBackgroundView = blurBackgroundView;

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);

                    viewGroup.addView(mBlurBackgroundView, 0, params);
                } else {
                    throw new RuntimeException("the ViewGroup of blurEngine must be a FrameLayout");
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mBlurBackgroundView.setAlpha(0f);
                    mBlurBackgroundView
                            .animate()
                            .alpha(1f)
                            .setDuration(ANIM_DURATION)
                            .setInterpolator(new LinearInterpolator())
                            .start();
                }
            }
        }

        private ImageView createBlurView(ViewGroup viewGroup, Bitmap background) {
            background = doBlur(viewGroup.getContext(), background);

            ImageView blurBackgroundView = new ImageView(viewGroup.getContext());
            blurBackgroundView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            blurBackgroundView.setImageDrawable(new BitmapDrawable(viewGroup.getResources(), background));

            return blurBackgroundView;
        }

        private Bitmap doBlur(Context context, Bitmap bm) {
            try {
                final RenderScript rs = RenderScript.create(context);
                final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

                final Allocation input = Allocation.createFromBitmap(rs, bm);
                final Allocation output = Allocation.createTyped(rs, input.getType());

                script.setRadius(BLUR_RADIUS);
                script.setInput(input);
                script.forEach(output);

                output.copyTo(bm);
                return bm;
            } catch (RSRuntimeException e) {
                // RenderScript known error : https://code.google.com/p/android/issues/detail?id=71347
                e.printStackTrace();
            }

            return null;
        }
    }
}

package com.johnz.kutils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;

public class StrUtil {

    /**
     * 按比例缩放 s2 的大小，同时拼接 s1 和 s2
     *
     * @param s1
     * @param ratio 比例值，单位字体大小 = 原字体大小 * ratio
     * @param s2
     * @return 处理后的字符串
     */
    public static SpannableString mergeTextWithRatio(String s1, String s2, float ratio) {
        SpannableString res = new SpannableString("");
        if (!TextUtils.isEmpty(s1)) {
            int start = s1.length();
            s1 = s1 + s2;
            int end = s1.length();
            res = new SpannableString(s1);
            res.setSpan(new RelativeSizeSpan(ratio), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return res;
    }

    /**
     * 按比例缩放 s2 的大小并设置 s2 颜色，同时拼接 s1 和 s2
     *
     * @param s1
     * @param ratio
     * @param s2Color
     * @param s2
     * @return 处理后的字符串
     */
    public static SpannableString mergeTextWithRatioColor(String s1, String s2, float ratio, int s2Color) {
        SpannableString res = new SpannableString("");
        if (!TextUtils.isEmpty(s1)) {
            int start = s1.length();
            s1 = s1 + s2;
            int end = s1.length();
            res = new SpannableString(s1);
            res.setSpan(new RelativeSizeSpan(ratio), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (s2Color != Color.TRANSPARENT) {
                res.setSpan(new ForegroundColorSpan(s2Color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return res;
    }

    /**
     * 设置 s2 颜色，同时拼接 s1 和 s2
     *
     * @param s1
     * @param s2
     * @param s2Color
     * @return 处理后的字符串
     */
    public static SpannableString mergeTextWithColor(String s1, String s2, int s2Color) {
        SpannableString res = new SpannableString("");
        if (!TextUtils.isEmpty(s1)) {
            int start = s1.length();
            s1 = s1 + s2;
            int end = s1.length();
            res = new SpannableString(s1);
            if (s2Color != Color.TRANSPARENT) {
                res.setSpan(new ForegroundColorSpan(s2Color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return res;
    }

    /**
     * 设置 s1 颜色，同时拼接 s1 和 s2
     *
     * @param s1
     * @param s1Color
     * @param s2
     * @return 处理后的字符串
     */
    public static SpannableString mergeTextWithColor(String s1, int s1Color, String s2) {
        SpannableString res = new SpannableString("");
        if (!TextUtils.isEmpty(s1)) {
            int start = 0;
            int end = s1.length();
            res = new SpannableString(s1 + s2);
            if (s1Color != Color.TRANSPARENT) {
                res.setSpan(new ForegroundColorSpan(s1Color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return res;
    }

    /**
     * 设置 s2 颜色，同时按顺拼接 s1, s2 和 s3
     *
     * @param s1
     * @param s2
     * @param s2Color
     * @param s3
     * @return
     */
    public static SpannableString mergeTextWithColor(String s1, String s2, int s2Color, String s3) {
        SpannableString res = new SpannableString("");
        if (!TextUtils.isEmpty(s1)) {
            int start = s1.length();
            s1 = s1 + s2;
            int end = s1.length();
            res = new SpannableString(s1 + s3);
            if (s2Color != Color.TRANSPARENT) {
                res.setSpan(new ForegroundColorSpan(s2Color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return res;
    }

    /**
     * 按比例缩放 s2 的大小并设置 s2 颜色, 再设置 s2 的(圆角)背景，同时拼接 s1 和 s2
     *
     * @param s1
     * @param s2
     * @param ratio
     * @param s2Color
     * @param bgColor
     * @param radius
     * @return
     */
    public static SpannableString mergeTextWithRatioColorAndBg(String s1, String s2, float ratio, int s2Color,
                                                               int bgColor, int radius) {
        SpannableString res = new SpannableString("");
        if (!TextUtils.isEmpty(s1)) {
            int start = s1.length();
            s1 = s1 + s2;
            int end = s1.length();
            res = new SpannableString(s1);
            if (s2Color != Color.TRANSPARENT) {
                res.setSpan(new RoundedBgSpan(ratio, s2Color, bgColor, radius), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return res;
    }

    private static class RoundedBgSpan extends ReplacementSpan {

        // this offset of y is only for make rectangle bg nice, narrow height
        private final static float OFFSET = 3;

        private float mRatio;
        private int mTextColor;
        private int mBgColor;
        private int mRadius;

        public RoundedBgSpan(float ratio, int textColor, int bgColor, int radius) {
            mRatio = ratio;
            mTextColor = textColor;
            mBgColor = bgColor;
            mRadius = radius;
        }

        @Override
        public int getSize(Paint paint, CharSequence charSequence,
                           int start, int end, Paint.FontMetricsInt fontMetricsInt) {
            return Math.round(paint.measureText(charSequence, start, end) * mRatio);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text,
                         int start, int end, float x, int top, int y, int bottom, Paint paint) {
            float yDiff = (bottom - top) * (1 - mRatio) / 2;
            float width = measureText(paint, text, start, end);
            float xDiff = width * (1 - mRatio) / 2;
            RectF rectF = new RectF(x + xDiff, top + yDiff + OFFSET, x + width - xDiff, bottom - yDiff - OFFSET);
            paint.setColor(mBgColor);
            canvas.drawRoundRect(rectF, mRadius, mRadius, paint);
            paint.setColor(mTextColor);
            paint.setTextSize(paint.getTextSize() * mRatio);
            canvas.drawText(text, start, end, x + xDiff, y - yDiff, paint);
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }

}

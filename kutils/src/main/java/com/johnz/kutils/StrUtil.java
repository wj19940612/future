package com.johnz.kutils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.TypefaceSpan;

public class StrUtil {

    /**
     * 设置 s1 的字体，按比例缩放 s2 的大小，同时拼接 s1 和 s2
     * @param s1
     * @param typeface
     * @param s2
     * @param ratio
     * @return
     */
    public static SpannableString mergeTextWithTypefaceRatio(String s1, Typeface typeface, String s2, float ratio) {
        SpannableString res = new SpannableString("");
        if (!TextUtils.isEmpty(s1)) {
            int typefaceEnd = s1.length();
            int start = s1.length();
            s1 = s1 + s2;
            int end = s1.length();
            res = new SpannableString(s1);
            res.setSpan(new CustomTypefaceSpan("", typeface), 0, typefaceEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            res.setSpan(new RelativeSizeSpan(ratio), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return res;
    }

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

    public static class CustomTypefaceSpan extends TypefaceSpan {

        private Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        public CustomTypefaceSpan(Parcel src) {
            super(src);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private static void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }

        public static final Creator<CustomTypefaceSpan> CREATOR = new Creator<CustomTypefaceSpan>() {
            @Override
            public CustomTypefaceSpan createFromParcel(Parcel source) {
                return new CustomTypefaceSpan(source);
            }

            @Override
            public CustomTypefaceSpan[] newArray(int size) {
                return new CustomTypefaceSpan[size];
            }
        };
    }


}

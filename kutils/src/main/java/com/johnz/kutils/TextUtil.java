package com.johnz.kutils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

public class TextUtil {

    /**
     * 按比例缩放 s2 的大小，同时拼接 s1 和 s2
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

}

package com.johnz.kutils;

import android.widget.TextView;

public class ViewUtil {

    public static String getTextTrim(TextView textView) {
        if (textView != null) {
            return textView.getText().toString().trim();
        }
        return "";
    }
}

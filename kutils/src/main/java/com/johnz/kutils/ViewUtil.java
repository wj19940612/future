package com.johnz.kutils;

import android.widget.EditText;

public class ViewUtil {

    public static String getEditTextTrim(EditText editText) {
        if (editText != null) {
            return editText.getText().toString().trim();
        }
        return "";
    }
}

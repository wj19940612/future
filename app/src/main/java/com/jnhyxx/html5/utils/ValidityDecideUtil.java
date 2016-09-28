package com.jnhyxx.html5.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ${wangJie} on 2016/9/28.
 * 字符串合法性的判断
 */

public class ValidityDecideUtil {

    /**
     * 判断输入数字是否为手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isMobileNum(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //正则表达式

        String phone = "^1[34578]\\d{9}$";


        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);


        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}

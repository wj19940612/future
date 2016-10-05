package com.jnhyxx.html5.utils;

/**
 * 工具类: 格式化字符串
 */
public class StrFormatter {

    /**
     * 格式化手机号 为 *** **** ****
     *
     * @param phoneNoSpace
     * @return
     */
    public static String getFormatPhoneNumber(String phoneNoSpace) {
        if (phoneNoSpace.length() <= 3) {
            return phoneNoSpace;
        } else if (phoneNoSpace.length() <= 7) {
            return phoneNoSpace.substring(0, 3)
                    + " " + phoneNoSpace.substring(3, phoneNoSpace.length());
        } else if (phoneNoSpace.length() <= 11) {
            return phoneNoSpace.substring(0, 3)
                    + " " + phoneNoSpace.substring(3, 7)
                    + " " + phoneNoSpace.substring(7, phoneNoSpace.length());
        }
        return phoneNoSpace;
    }
}

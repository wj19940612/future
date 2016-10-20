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

    /**
     * 格式化银行卡 4444 4444 4444 4444 444
     *
     * @param bankCardNoSpace
     * @return
     */
    public static String getFormatBankCardNumber(String bankCardNoSpace) {
        if (bankCardNoSpace.length() <= 4) {
            return bankCardNoSpace;
        } else if (bankCardNoSpace.length() <= 8) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() <= 12) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() <= 16) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, 12)
                    + " " + bankCardNoSpace.substring(12, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() < 20) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, 12)
                    + " " + bankCardNoSpace.substring(12, 16)
                    + " " + bankCardNoSpace.substring(16, bankCardNoSpace.length());
        }
        return bankCardNoSpace;
    }

    /**
     * 隐藏的银行卡格式   *** **** **** **** ****
     *
     * @param bankCardNoSpace
     * @return
     */
    public static String getHintFormatBankCardNumber(String bankCardNoSpace) {
        if (bankCardNoSpace.length() == 16) {
            return "**** **** **** " + bankCardNoSpace.substring(bankCardNoSpace.length() - 4, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() == 19) {
            return "*** **** **** **** " + bankCardNoSpace.substring(bankCardNoSpace.length() - 4, bankCardNoSpace.length());
        }
        return bankCardNoSpace;
    }
}

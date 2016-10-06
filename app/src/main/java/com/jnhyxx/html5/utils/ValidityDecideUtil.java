package com.jnhyxx.html5.utils;

import java.text.ParseException;
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
        CharSequence inputStr = phoneNumber;
        String phone = "(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7}";
        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param identityCard 身份证号
     * @return true 有效：false 无效
     * @throws ParseException
     */
    public static boolean IDCardValidate(String identityCard) {
        String regex = "\\d{15}|\\d{17}[0-9Xx]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(identityCard);
        return matcher.matches();
    }

    /**
     * 限制昵称只能输入中文、字母和数字
     *
     * @param nickName
     * @return
     */
    public static boolean getNicknameStatus(String nickName) {
        nickName = nickName.trim();
        Pattern numberLetter = Pattern.compile("^[A-Za-z0-9]+$");
        Pattern chinese = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher numberMatcher = numberLetter.matcher(nickName);
        Matcher matcher = chinese.matcher(nickName);
        return numberMatcher.matches() || matcher.matches();
    }

    /**
     * 校验银行卡卡号
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeCardId
     * @return
     */
    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            throw new IllegalArgumentException("Bank card code must be number!");
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }
}

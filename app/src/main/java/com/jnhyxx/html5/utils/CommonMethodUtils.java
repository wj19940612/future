package com.jnhyxx.html5.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.domain.account.UserInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 王杰 on 2016/8/29.
 * 公共方法的utils
 */

public class CommonMethodUtils {
    private static final String TAG = "CommonMethodUtils";
    private static WeakReference<Context> sContext;

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        sContext = new WeakReference<Context>(context);
        Context mContext = sContext.get();
        if (mContext instanceof Activity) {
            WindowManager windowManager = ((Activity) mContext).getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            int width = outMetrics.widthPixels;
            return width;
        }
        return -1;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (context instanceof Activity) {
            WindowManager windowManager = ((Activity) context).getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            int heightPixels = outMetrics.heightPixels;
            return heightPixels;
        }
        return -1;
    }

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

    /**
     * 隐藏手机号码中间四位
     *
     * @param phoneNumber
     * @return
     */
    public static String hidePhoneNumberMiddle(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            String newPhoneNumber = phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, phoneNumber.length());
            return newPhoneNumber;
        }
        return " ";
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param identityCard 身份证号
     * @return true 有效：false 无效
     * @throws ParseException
     */
    public static boolean IDCardValidate(String identityCard )  {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return identityCard.matches(regx) || identityCard.matches(reg1) || identityCard.matches(regex);
    }


    /**
     * 根据传入的银行卡号码隐藏前面部分,只显示最后四位
     *
     * @param bankNumber
     * @return
     */
    public static String bankNumber(String bankNumber) {
        bankNumber = bankNumber.trim();
        String safeBankNumber = "";
        //不管多少长度，只显示16位 ，最后四位显示。
        safeBankNumber = "****  ****  ****  " + bankNumber.substring(bankNumber.length() - 4, bankNumber.length());
        //根据长度生成*
        /*StringBuilder mStringBuilder = new StringBuilder();
        for (int i = 1; i < bankNumber.length() - 3; i++) {
            if ( i % 4 == 0) {
                mStringBuilder.append("*  ");
            } else {
                mStringBuilder.append("*");
            }
        }
        mStringBuilder.append("  ");
        String starString = mStringBuilder.toString();
        safeBankNumber = starString + bankNumber.substring(bankNumber.length() - 4, bankNumber.length());*/
        return safeBankNumber;
    }

    /**
     * 限制昵称只能输入中文、字母和数字
     *
     * @param nickName
     * @return
     */
    public static boolean getNicknameStatus(String nickName) {
//        nickName = nickName.trim();
//        Pattern number = Pattern.compile("[0-9]*");
//        Matcher numberMatcher = number.matcher(nickName);
//        Pattern letter = Pattern.compile("[a-zA-Z]");
//        Matcher letterMatcher = letter.matcher(nickName);
//        Pattern chinese = Pattern.compile("[\u4e00-\u9fa5]");
//        Matcher chineseMatcher = chinese.matcher(nickName);
//        if (numberMatcher.matches() && letterMatcher.matches() && chineseMatcher.matches()) {
//            return true;
//        }
//

        String all = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,16}";//{2,10}表示字符的长度是2-10
        Pattern pattern = Pattern.compile(all);
        boolean result = Pattern.matches(all, nickName);
        return result;
    }

    /**
     * 拼接的获取图片验证码的地址
     *
     * @param userPhone
     * @return
     */
    public static String imageCodeUri(String userPhone, String imageUrl) {
        String url = "";
        if (!TextUtils.isEmpty(userPhone)) {
            String mHost = BuildConfig.API_HOST;
//            String mUri = "/user/user/getRegImage.do";
            String user = "?userPhone=";
            url = new StringBuilder(mHost).append(imageUrl).append(user).append(userPhone).toString();
        }
        return url;
    }

    /**
     * 判断用户是否实名认证
     *
     * @param userInfo
     * @return
     */
    public static boolean isNameAuth(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        } else if (userInfo.getIdStatus() == UserInfo.REAL_NAME_AUTH_STATUS_WRITE || userInfo.getIdStatus() == UserInfo.REAL_NAME_AUTH_STATUS_ATTESTATION) {
            return true;
        }
        return false;
    }

    /**
     * 判断银行卡是否绑定
     *
     * @param userInfo
     * @return
     */
    public static boolean isBankAuth(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        } else if (userInfo.getCardState() == UserInfo.BANK_CARD_AUTH_STATUS_WRITE || userInfo.getCardState() == UserInfo.BANK_CARD_AUTH_STATUS_ATTESTATION) {
            return true;
        }
        return false;
    }
}


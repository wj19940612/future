package com.jnhyxx.html5.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.domain.account.UserInfo;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;

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
        } else if (userInfo.getIdStatus() == UserInfo.REAL_NAME_STATUS_FILLED || userInfo.getIdStatus() == UserInfo.REAL_NAME_STATUS_VERIFIED) {
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
        } else if (userInfo.getCardState() == UserInfo.BANKCARD_STATUS_FILLED || userInfo.getCardState() == UserInfo.BANKCARD_STATUS_BOUND) {
            return true;
        }
        return false;
    }

    /**
     * 获取年、月、日
     *
     * @param dateTime
     * @return
     */
    public static String getYear(String dateTime) {
        String monthTime = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy--MM--DD");
        monthTime = simpleDateFormat.format(dateTime);
        return monthTime;
    }

    public static String getHour(String hourTime) {
        hourTime = hourTime.trim();
        String hourDate = "";
        String[] time = hourTime.split(" ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        if (time.length == 2) {
            hourTime = time[1];
        }
        hourDate = simpleDateFormat.format(hourTime);
        return hourDate;
    }

    /**
     * 用来处理交易名义界面的remark的具体描述
     *
     * @param typeDetail
     * @param remark
     * @return
     */
    public static String getRemarkInfo(String typeDetail, String remark) {
        if (TextUtils.isEmpty(remark)) return "";
        if (remark.contains(typeDetail)) {
            remark = remark.replace(typeDetail, "");
        }
        return remark;
    }

}


package com.jnhyxx.html5.utils;

import android.text.TextUtils;
import android.util.Log;

import com.johnz.kutils.DateUtil;

/**
 * 工具类: 格式化字符串
 */
public class StrFormatter {
    private static final String TAG = "StrFormatter";

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
        } else if (bankCardNoSpace.length() <= 20) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, 12)
                    + " " + bankCardNoSpace.substring(12, 16)
                    + " " + bankCardNoSpace.substring(16, bankCardNoSpace.length());
        } else if (bankCardNoSpace.length() <= 25) {
            return bankCardNoSpace.substring(0, 4)
                    + " " + bankCardNoSpace.substring(4, 8)
                    + " " + bankCardNoSpace.substring(8, 12)
                    + " " + bankCardNoSpace.substring(12, 16)
                    + " " + bankCardNoSpace.substring(16, 20)
                    + " " + bankCardNoSpace.substring(20, bankCardNoSpace.length());
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
        if (bankCardNoSpace.length() >= 16) {
            return "**** **** **** " + bankCardNoSpace.substring(bankCardNoSpace.length() - 4);
        }
        return bankCardNoSpace;
    }

    public static String getChinesePrefixTime(long inventoryNextTime) {
        String HHmm = DateUtil.format(inventoryNextTime, "HH:mm");
        if (HHmm.compareTo("00:00") >= 0 && HHmm.compareTo("06:00") <= 0) {
            return "凌晨 " + HHmm;
        } else if (HHmm.compareTo("06:00") > 0 && HHmm.compareTo("12:00") <= 0) {
            return "上午 " + HHmm;
        } else if (HHmm.compareTo("12:00") > 0 && HHmm.compareTo("18:00") <= 0) {
            return "下午 " + HHmm;
        } else if (HHmm.compareTo("18:00") > 0 && HHmm.compareTo("24:00") < 0) {
            return "晚上 " + HHmm;
        }
        return "";
    }

    /**
     * 资讯直播的时间调整
     *
     * @param createTime
     * @return
     */
    public static String getTimeHint(String createTime) {
        if (!TextUtils.isEmpty(createTime)) {
            String serverTime = DateUtil.format(createTime, DateUtil.DEFAULT_FORMAT, "HH:mm").replace(":", "");
            String localTime = DateUtil.format(System.currentTimeMillis(), "HH:mm").replace(":", "");

            if (!TextUtils.isEmpty(serverTime)) {
                if (serverTime.equalsIgnoreCase(localTime)) {
                    return "刚刚";
                } else if (localTime.compareTo(serverTime) >= 0) {
                    Long serverTimeNum = Long.valueOf(serverTime);
                    Long localTimeNum = Long.valueOf(localTime);
                    Long timeDifference = localTimeNum - serverTimeNum;
                    Log.d(TAG, "系统时间  " + serverTime + "本地时间  " + localTime + "时间差  " + timeDifference);
                    if (0 <= timeDifference && timeDifference < 60) {
                        return timeDifference % 60 < 1 ? "刚刚" : timeDifference % 60 + "分钟前";
                    } else if (timeDifference >= 60) {
                        return timeDifference / 60 + "小时前";
                    } else {
                        return DateUtil.format(createTime, DateUtil.DEFAULT_FORMAT, "HH:mm");
                    }
                }
            }
        }
        return createTime;
    }

    public static String getFormatServicePhone(String servicePhone) {
        if (servicePhone.length() < 7) {
            return servicePhone;
        }
        return servicePhone.substring(0, 3) +
                "-" + servicePhone.substring(3, 7) +
                "-" + servicePhone.substring(7, servicePhone.length());
    }

}

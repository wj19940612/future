package com.jnhyxx.html5.domain.local;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.account.UserInfo;

public class LocalUser {

    private UserInfo mUserInfo;
    private int bankId = -1;
    private String mPhone;

    private static LocalUser sLocalUser;
    private static boolean sReload;

    public static LocalUser getUser() {
        if (sLocalUser == null || sReload) {
            sLocalUser = loadFromPreference();
        }
        return sLocalUser;
    }

    private static LocalUser loadFromPreference() {
        sReload = false;
        String userJson = Preference.get().getUserJson();
        if (!TextUtils.isEmpty(userJson)) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, LocalUser.class);
        }

        return new LocalUser();
    }

    private void saveToPreference() {
        String userJson = new Gson().toJson(this);
        Preference.get().setUserJson(userJson);
        sReload = true;
    }

    public void setUserInfo(UserInfo userInfo, String phone) {
        mUserInfo = userInfo;
        mPhone = phone;
        saveToPreference();
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
        saveToPreference();
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public int getBankId() {
        return bankId;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public boolean isLogin() {
        return mUserInfo != null;
    }

    public boolean isRealNameFilled() {
        if (mUserInfo != null) {
            return mUserInfo.getIdStatus() > UserInfo.REAL_NAME_STATUS_UNFILLED;
        }
        return false;
    }

    public boolean isBankcardFilled() {
        if (mUserInfo != null) {
            return mUserInfo.getCardState() > UserInfo.BANKCARD_STATUS_UNFILLED;
        }
        return false;
    }
    //银行卡是否认证过,也就是是否交易成功过
    public boolean isBankcardApproved() {
        if (mUserInfo != null) {
            return mUserInfo.getCardState() < UserInfo.REAL_NAME_STATUS_VERIFIED;
        }
        return false;
    }

    public void logout() {
        mUserInfo = null;
        saveToPreference();
    }

    /**
     * @return
     * @deprecated
     */
    public String getToken() {
        return "";
    }

    public double getAvailableBalance() {
        if (mUserInfo != null) {
            return mUserInfo.getMoneyUsable();
        }
        return 0;
    }

    public double getAvailableScore() {
        if (mUserInfo != null) {
            return mUserInfo.getScoreUsable();
        }
        return 0;
    }

    /**
     * @return
     * @deprecated
     */
    public String getUserPhoneNum() {
        if (mUserInfo != null) {
            return mUserInfo.getUserPhone();
        }
        return "";
    }

    public String getPhone() {
        return mPhone;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserInfo=" + mUserInfo +
                '}';
    }

}

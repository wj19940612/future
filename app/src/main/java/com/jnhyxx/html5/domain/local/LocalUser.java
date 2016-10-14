package com.jnhyxx.html5.domain.local;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.account.UserInfo;

public class LocalUser {

    private UserInfo mUserInfo;
    private String mPhone;
    private String mCardHolderName;
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

    public String getCardHolderName() {
        return mCardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.mCardHolderName = cardHolderName;
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

    public boolean isBankcardBound() {
        if (mUserInfo != null) {
            return mUserInfo.getCardState() == UserInfo.BANKCARD_STATUS_BOUND;
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

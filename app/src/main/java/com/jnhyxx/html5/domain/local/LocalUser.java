package com.jnhyxx.html5.domain.local;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.account.UserInfo;

public class LocalUser {

    private UserInfo mUserInfo;

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

    public void logout() {
        mUserInfo = null;
        saveToPreference();
    }

    /**
     * @deprecated
     * @return
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

    public String getUserPhoneNum() {
        if (mUserInfo != null) {
            return mUserInfo.getUserPhone();
        }
        return "";
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserInfo=" + mUserInfo +
                '}';
    }

}

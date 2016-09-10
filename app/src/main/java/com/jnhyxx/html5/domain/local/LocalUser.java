package com.jnhyxx.html5.domain.local;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.account.UserInfo;

public class LocalUser {

    public interface AsyncCallback<T> {
        void get(T t);
    }

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

    @Override
    public String toString() {
        return "User{" +
                "mUserInfo=" + mUserInfo +
                '}';
    }

    public double getAvailableBalance() {
        return 0; // TODO: 8/29/16 可用资金
    }

    public String getUserPhone() {
        return "13567124531"; // TODO: 9/8/16 获取用户手机号,作为唯一用户标示
    }

}

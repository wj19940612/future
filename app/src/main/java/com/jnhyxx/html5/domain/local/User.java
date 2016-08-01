package com.jnhyxx.html5.domain.local;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.domain.LoginInfo;

public class User {

    public interface AsyncCallback<T> {
        void get(T t);
    }

    private LoginInfo mLoginInfo;

    private static User sUser;
    private static boolean sReload;

    public static User getUser() {
        if (sUser == null || sReload) {
            sUser = loadFromPreference();
        }
        return sUser;
    }

    private static User loadFromPreference() {
        sReload = false;
        String userJson = Preference.get().getUserJson();
        if (!TextUtils.isEmpty(userJson)) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }

        return new User();
    }

    private void saveToPreference() {
        String userJson = new Gson().toJson(this);
        Preference.get().setUserJson(userJson);
        sReload = true;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        mLoginInfo = loginInfo;
        saveToPreference();
    }

    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    public boolean isLogin() {
        return mLoginInfo != null;
    }

    public void logout() {
        mLoginInfo = null;
        saveToPreference();
    }

    public String getToken() {
        if (getLoginInfo() != null) {
            return getLoginInfo().getTokenInfo().getToken();
        }
        return "";
    }


}

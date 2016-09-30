package com.jnhyxx.html5;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

    private static final String SHARED_PREFERENCES_NAME = BuildConfig.FLAVOR + "_prefs";

    public interface Key {
        String IS_FOREGROUND = "isForeground";
        String USER_JSON = "userJson";
        String SERVER_TIME = "serverTime";
        String HAD_SHOW_TRADE_AGREEMENT = "hadShowTradeAgreement";
        String IS_TRADE_RULE_CLICKED = "isTradeRuleClicked";
        String PUSH_CLIENT_ID = "pushClientId";
    }

    private static Preference sInstance;

    private SharedPreferences mPrefs;

    public static Preference get() {
        if (sInstance == null) {
            sInstance = new Preference();
        }
        return sInstance;
    }

    private Preference() {
        mPrefs = App.getAppContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return mPrefs.edit();
    }

    public void setForeground(boolean foreground) {
        getEditor().putBoolean(Key.IS_FOREGROUND, foreground).commit();
    }

    public boolean isForeground() {
        return mPrefs.getBoolean(Key.IS_FOREGROUND, false);
    }

    public void setUserJson(String userJson) {
        getEditor().putString(Key.USER_JSON, userJson).commit();
    }

    public String getUserJson() {
        return mPrefs.getString(Key.USER_JSON, null);
    }

    public void setTimestamp(String key, long timestamp) {
        getEditor().putLong(key, timestamp).commit();
    }

    public long getTimestamp(String key) {
        return mPrefs.getLong(key, 0);
    }

    public void setServerTime(long serverTime) {
        getEditor().putLong(Key.SERVER_TIME, serverTime).commit();
    }

    public long getServerTime() {
        return mPrefs.getLong(Key.SERVER_TIME, 0);
    }

    public boolean hadShowTradeAgreement(String userPhone, String varietyType) {
        return mPrefs.getBoolean(userPhone + Key.HAD_SHOW_TRADE_AGREEMENT + varietyType, false);
    }

    public void setTradeAgreementShowed(String userPhone, String varietyType) {
        getEditor().putBoolean(userPhone + Key.HAD_SHOW_TRADE_AGREEMENT + varietyType, true).commit();
    }

    public boolean isTradeRuleClicked(String userPhone, String varietyType) {
        return mPrefs.getBoolean(userPhone + Key.IS_TRADE_RULE_CLICKED + varietyType, false);
    }

    public void setTradeRuleClicked(String userPhone, String varietyType) {
        getEditor().putBoolean(userPhone + Key.IS_TRADE_RULE_CLICKED + varietyType, true).commit();
    }

    public void setPushClientId(String clientId) {
        getEditor().putString(Key.PUSH_CLIENT_ID, clientId).commit();
    }

    public String getPushClientId() {
        return mPrefs.getString(Key.PUSH_CLIENT_ID, "");
    }
}

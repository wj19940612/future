package com.jnhyxx.html5;

import android.content.Context;
import android.content.SharedPreferences;

import com.jnhyxx.html5.domain.live.ChatData;
import com.jnhyxx.html5.domain.msg.SysMessage;

public class Preference {

    private static final String SHARED_PREFERENCES_NAME = BuildConfig.FLAVOR + "_prefs";

    public interface Key {
        String IS_FOREGROUND = "isForeground";
        String USER_JSON = "userJson";
        String SERVER_TIME = "serverTime";
        String HAD_SHOW_TRADE_AGREEMENT = "hadShowTradeAgreement";
        String IS_TRADE_RULE_CLICKED = "isTradeRuleClicked";
        String PUSH_CLIENT_ID = "pushClientId";
        String PHONE_NUMBER = "phone";
        String SERVICE_PHONE = "servicePhone";
        String SERVICE_QQ = "serviceQQ";
        String SYS_MESSAGE_ID = "sys_message_id";
        String LAST_TEACHER_COMMAND = "last_teacher_command";
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
        getEditor().putBoolean(Key.IS_FOREGROUND, foreground).apply();
    }

    public boolean isForeground() {
        return mPrefs.getBoolean(Key.IS_FOREGROUND, false);
    }

    public void setPhone(String phone) {
        getEditor().putString(Key.PHONE_NUMBER, phone).commit();
    }

    public String getPhone() {
        return mPrefs.getString(Key.PHONE_NUMBER, null);
    }

    public void setUserJson(String userJson) {
        getEditor().putString(Key.USER_JSON, userJson).commit();
    }

    public String getUserJson() {
        return mPrefs.getString(Key.USER_JSON, null);
    }

    public void setTimestamp(String key, long timestamp) {
        getEditor().putLong(key, timestamp).apply();
    }

    public long getTimestamp(String key) {
        return mPrefs.getLong(key, 0);
    }

    public void setServerTime(long serverTime) {
        getEditor().putLong(Key.SERVER_TIME, serverTime).apply();
    }

    public long getServerTime() {
        return mPrefs.getLong(Key.SERVER_TIME, 0);
    }

    public boolean hadShowTradeAgreement(String userPhone, String varietyType) {
        return mPrefs.getBoolean(userPhone + Key.HAD_SHOW_TRADE_AGREEMENT + varietyType, false);
    }

    public void setTradeAgreementShowed(String userPhone, String varietyType) {
        getEditor().putBoolean(userPhone + Key.HAD_SHOW_TRADE_AGREEMENT + varietyType, true).apply();
    }

    public boolean isTradeRuleClicked(String userPhone, String varietyType) {
        return mPrefs.getBoolean(userPhone + Key.IS_TRADE_RULE_CLICKED + varietyType, false);
    }

    public void setTradeRuleClicked(String userPhone, String varietyType) {
        getEditor().putBoolean(userPhone + Key.IS_TRADE_RULE_CLICKED + varietyType, true).apply();
    }

    public void setPushClientId(String clientId) {
        getEditor().putString(Key.PUSH_CLIENT_ID, clientId).commit();
    }

    public String getPushClientId() {
        return mPrefs.getString(Key.PUSH_CLIENT_ID, "");
    }

    public void setServicePhone(String servicePhone) {
        getEditor().putString(Key.SERVICE_PHONE, servicePhone).apply();
    }

    public String getServicePhone() {
        return mPrefs.getString(Key.SERVICE_PHONE, null);
    }

    public void setServiceQQ(String serviceQQ) {
        getEditor().putString(Key.SERVICE_QQ, serviceQQ).apply();
    }

    public String getServiceQQ() {
        return mPrefs.getString(Key.SERVICE_QQ, null);
    }

    public boolean hasShowedThisSysMessage(SysMessage sysMessage) {
        String sysMessageId = mPrefs.getString(Key.SYS_MESSAGE_ID, "");
        if (sysMessageId.equals(sysMessage.getId())) {
            return true;
        }
        return false;
    }

    public void setThisSysMessageShowed(SysMessage sysMessage) {
        getEditor().putString(Key.SYS_MESSAGE_ID, sysMessage.getId()).apply();
    }

    public boolean hasShowedThisLastTeacherCommand(ChatData teacherCommand) {
        if (teacherCommand != null) {
            long timeStamp = mPrefs.getLong(Key.LAST_TEACHER_COMMAND, -1);
            if (teacherCommand.getCreateTime() == timeStamp) {
                return true;
            }
        }
        return false;
    }

    public void setThisLastTeacherCommandShowed(ChatData teacherCommand) {
        if (teacherCommand.getMsg() != null) {
            getEditor().putLong(Key.LAST_TEACHER_COMMAND, teacherCommand.getCreateTime()).apply();
        }
    }
}

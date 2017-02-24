package com.jnhyxx.html5;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.jnhyxx.html5.domain.live.LiveHomeChatInfo;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.jnhyxx.html5.domain.order.LightningOrderAsset;

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
        String SERVER_IP_PORT = "server_ip_port";
        String TAG_SHOWED = "tag_showed";
        String IS_FIRST_WITHDRAW = "isFirstWithdraw";
<<<<<<< HEAD
        String PAY_WAY = "payWay";
=======
        String PRODUCT_OPTIONAL_FOREIGN = "productOptionalForeign";
        String PRODUCT_OPTIONAL_DOMESTIC = "productOptionalDomestic";
        String PAY_WAY = "pay_way";
>>>>>>> f/newVersion
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
        String sysMessageCreateTime = mPrefs.getString(Key.SYS_MESSAGE_ID, "");
        if (sysMessageCreateTime.equals(sysMessage.getCreateTime())) {
            return true;
        }
        return false;
    }

    public void setThisSysMessageShowed(SysMessage sysMessage) {
        getEditor().putString(Key.SYS_MESSAGE_ID, sysMessage.getCreateTime()).apply();
    }

    public boolean hasShowedThisLastTeacherCommand(LiveHomeChatInfo teacherCommand) {
        if (teacherCommand != null) {
            long timeStamp = mPrefs.getLong(Key.LAST_TEACHER_COMMAND, -1);
            if (teacherCommand.getCreateTime() == timeStamp) {
                return true;
            }
        }
        return false;
    }

    public void setThisLastTeacherCommandShowed(LiveHomeChatInfo teacherCommand) {
        if (teacherCommand.getMsg() != null) {
            getEditor().putLong(Key.LAST_TEACHER_COMMAND, teacherCommand.getCreateTime()).apply();
        }
    }

    /**
     * 存储闪现下单数据
     *
     * @param lightningOrderKey   由产品varietyId+用户手机号码+支付方式组成
     * @param lightningOrderAsset
     */
    public void setLightningOrderAsset(String lightningOrderKey, LightningOrderAsset lightningOrderAsset) {
        getEditor().putString(lightningOrderKey, new Gson().toJson(lightningOrderAsset)).commit();
    }

    public LightningOrderAsset getLightningOrderAsset(String lightningOrderKey) {
        String lightningOrderJson = mPrefs.getString(lightningOrderKey, null);
        if (lightningOrderJson != null) {
            return new Gson().fromJson(lightningOrderJson, LightningOrderAsset.class);
        }
        return null;
    }

    public void setMarketServerIpPort(String serverIpPort) {
        getEditor().putString(Key.SERVER_IP_PORT, serverIpPort).apply();
    }

    public String getMarketServerIpPort() {
        return mPrefs.getString(Key.SERVER_IP_PORT, null);
    }

    public boolean isTagShowed(String varietyType, int fundType) {
        return mPrefs.getBoolean(varietyType + Key.TAG_SHOWED + fundType, true);
    }

    public void disableTagShowed(String varietyType, int fundType) {
        getEditor().putBoolean(varietyType + Key.TAG_SHOWED + fundType, false).apply();
    }

    public boolean isFirstWithdraw(String key) {
        return mPrefs.getBoolean(key, true);
    }

    public void setIsFirstWithdraw(String key, boolean isFirstWithdraw) {
        getEditor().putBoolean(key, isFirstWithdraw).apply();
    }

    public void setProductOptionalForeign(String productOptional) {
        getEditor().putString(Key.PRODUCT_OPTIONAL_FOREIGN, productOptional).apply();
    }

    public String getProductOptionalForeign() {
        return mPrefs.getString(Key.PRODUCT_OPTIONAL_FOREIGN, "");
    }

    public void setProductOptionalDomestic(String productOptional) {
        getEditor().putString(Key.PRODUCT_OPTIONAL_DOMESTIC, productOptional).apply();
    }

    public String getProductOptionalDomestic() {
        return mPrefs.getString(Key.PRODUCT_OPTIONAL_DOMESTIC, "");
    }

    public int getRechargePayWay() {
        return mPrefs.getInt(Key.PAY_WAY, 0);
    }

    public void setRechargePayWay(int payWay) {
        getEditor().putInt(Key.PAY_WAY, payWay).apply();
    }
}

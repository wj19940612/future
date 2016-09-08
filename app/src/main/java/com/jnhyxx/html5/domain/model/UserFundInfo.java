package com.jnhyxx.html5.domain.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */

public class UserFundInfo {

    /**
     moneyUsable  可用资金余额,
     redbagUsable 可用红包余额,
     scoreUsable  可用积分余额,
     moneyFrozen  冻结资金,
     moneyDrawUsable 可提现资金余额,
     margin       当前用户保证金余额,
     marginScore  积分保证金,
     createTime   创建时间
     updateTime   更新时间
     */

    private String createTime;
    private int margin;
    private int marginScore;
    private int moneyDrawUsable;
    private int moneyFrozen;
    private int moneyUsable;
    private int redbagUsable;
    private int scoreUsable;
    private String updateTime;
    private int userId;

    public static UserFundInfo objectFromData(String str) {

        return new Gson().fromJson(str, UserFundInfo.class);
    }

    public static UserFundInfo objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), UserFundInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<UserFundInfo> arrayUserFundInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<UserFundInfo>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<UserFundInfo> arrayUserFundInfoFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<UserFundInfo>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getMarginScore() {
        return marginScore;
    }

    public void setMarginScore(int marginScore) {
        this.marginScore = marginScore;
    }

    public int getMoneyDrawUsable() {
        return moneyDrawUsable;
    }

    public void setMoneyDrawUsable(int moneyDrawUsable) {
        this.moneyDrawUsable = moneyDrawUsable;
    }

    public int getMoneyFrozen() {
        return moneyFrozen;
    }

    public void setMoneyFrozen(int moneyFrozen) {
        this.moneyFrozen = moneyFrozen;
    }

    public int getMoneyUsable() {
        return moneyUsable;
    }

    public void setMoneyUsable(int moneyUsable) {
        this.moneyUsable = moneyUsable;
    }

    public int getRedbagUsable() {
        return redbagUsable;
    }

    public void setRedbagUsable(int redbagUsable) {
        this.redbagUsable = redbagUsable;
    }

    public int getScoreUsable() {
        return scoreUsable;
    }

    public void setScoreUsable(int scoreUsable) {
        this.scoreUsable = scoreUsable;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

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
 * 登陆成功后返回的数据
 */

public class UserInfo {

    /**
     * moneyUsable : 94724.14  可用资金余额
     * scoreUsable : 85282      可用积分余额
     * userName : 二级渠道newtest116 用户昵称
     * userPhone : 13777804454     用户手机号
     */

    private double moneyUsable;
    private int scoreUsable;
    private String userName;
    private String userPhone;

    public static UserInfo objectFromData(String str) {

        return new Gson().fromJson(str, UserInfo.class);
    }

    public static UserInfo objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), UserInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<UserInfo> arrayUserInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<UserInfo>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<UserInfo> arrayUserInfoFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<UserInfo>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public double getMoneyUsable() {
        return moneyUsable;
    }

    public void setMoneyUsable(double moneyUsable) {
        this.moneyUsable = moneyUsable;
    }

    public int getScoreUsable() {
        return scoreUsable;
    }

    public void setScoreUsable(int scoreUsable) {
        this.scoreUsable = scoreUsable;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "moneyUsable=" + moneyUsable +
                ", scoreUsable=" + scoreUsable +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                '}';
    }
}

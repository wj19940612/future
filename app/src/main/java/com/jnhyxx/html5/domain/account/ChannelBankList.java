package com.jnhyxx.html5.domain.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 * 渠道银行列表
 */

public class ChannelBankList implements Serializable {
    private static final long serialVersionUID = 703015678840401941L;
    /**
     * icon :
     * id : 1
     * limitDay : 20000
     * limitSingle : 10000
     * name : 中国工商银行
     */
    //银行图标
    private String icon;
    private int id;
    /**
     * 单日最高提现额度
     */
    private int limitDay;
    /**
     * 单笔最高提现额度
     */
    private int limitSingle;
    /**
     * 银行名称
     */
    private String name;

    public static ChannelBankList objectFromData(String str) {

        return new Gson().fromJson(str, ChannelBankList.class);
    }

    public static ChannelBankList objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), ChannelBankList.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ChannelBankList> arrayChannelBankListFromData(String str) {

        Type listType = new TypeToken<ArrayList<ChannelBankList>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<ChannelBankList> arrayChannelBankListFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<ChannelBankList>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(int limitDay) {
        this.limitDay = limitDay;
    }

    public int getLimitSingle() {
        return limitSingle;
    }

    public void setLimitSingle(int limitSingle) {
        this.limitSingle = limitSingle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChannelBankList{" +
                "icon='" + icon + '\'' +
                ", id=" + id +
                ", limitDay=" + limitDay +
                ", limitSingle=" + limitSingle +
                ", name='" + name + '\'' +
                '}';
    }
}

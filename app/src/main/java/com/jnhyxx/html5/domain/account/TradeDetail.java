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
 * Created by Administrator on 2016/8/30.
 */

public class TradeDetail implements Serializable {
    /**
     * 资金明细表
     */
    public static final int FUND_DETAIL_TABLE = 837;
    /**
     * 代表将要查 积分明细表
     */
    public static final int INTEGRAL_DETAIL_TABLE = 791;
    private static final long serialVersionUID = -7454080372304618453L;
    /**
     * createTime : 2016-09-18 15:28:03
     * id : 74
     * remark : 赠送积分
     * score : 100000
     * scoreLeft : -1603300
     * type : 3
     * typeDetail : 3002
     * userId : 37
     * money : 2.2
     */

    private String createTime;
    private int id;
    private String remark;
    private int score;
    private int scoreLeft;
    private int type;
    private int typeDetail;
    private int userId;
    private double money;

    public static TradeDetail objectFromData(String str) {

        return new Gson().fromJson(str, TradeDetail.class);
    }

    public static TradeDetail objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), TradeDetail.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<TradeDetail> arrayTradeDetailFromData(String str) {

        Type listType = new TypeToken<ArrayList<TradeDetail>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<TradeDetail> arrayTradeDetailFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<TradeDetail>>() {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScoreLeft() {
        return scoreLeft;
    }

    public void setScoreLeft(int scoreLeft) {
        this.scoreLeft = scoreLeft;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(int typeDetail) {
        this.typeDetail = typeDetail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "TradeDetail{" +
                "createTime='" + createTime + '\'' +
                ", id=" + id +
                ", remark='" + remark + '\'' +
                ", score=" + score +
                ", scoreLeft=" + scoreLeft +
                ", type=" + type +
                ", typeDetail=" + typeDetail +
                ", userId=" + userId +
                ", money=" + money +
                '}';
    }
}

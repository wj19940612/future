package com.jnhyxx.html5.domain.account;

import java.io.Serializable;

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
    /**
     * 流水描述
     */
    private String remark;
    /**
     * '本次流水积分发生金额',
     */
    private double score;
    /**
     * '本次流水后积分剩余',
     */
    private double scoreLeft;
    /**
     * 流水类型
     */
    private int type;
    /**
     * 流水具体类型
     */
    private int typeDetail;
    private int userId;
    /**
     * 本次流水现金发生金额'
     */
    private double money;
    /**
     * '本次流水后现金剩余',
     */
    private double moneyLeft;

    public int getIoOrderId() {
        return ioOrderId;
    }

    public void setIoOrderId(int ioOrderId) {
        this.ioOrderId = ioOrderId;
    }

    /**
     *
     */
    private int ioOrderId;

    public double getMoneyLeft() {
        return moneyLeft;
    }

    public void setMoneyLeft(double moneyLeft) {
        this.moneyLeft = moneyLeft;
    }

    public void setScoreLeft(double scoreLeft) {
        this.scoreLeft = scoreLeft;
    }

    public void setScore(double score) {
        this.score = score;
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

    public double getScore() {
        return score;
    }


    public double getScoreLeft() {
        return scoreLeft;
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
                ", moneyLeft=" + moneyLeft +
                '}';
    }
}

package com.jnhyxx.html5.domain.account;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/31.
 * 用户资金信息
 */

public class UserFundInfo implements Serializable {

    /**
     * createTime : 2016-08-16 15:33:14
     * margin : 4972.0
     * marginScore : 25003.0
     * moneyDrawUsable : 105726.14
     * moneyFrozen : -0.02
     * moneyUsable : 105726.14
     * redbagUsable : 0.0
     * scoreUsable : -1603300.0
     * updateTime : 2016-08-19 14:14:05
     * userId : 37
     */


    /**
     *    createTime 创建时间
     */
    private String createTime;
    /**
     *   '当前用户保证金余额',
     */
    private double margin;
    /**
     * '积分保证金',
     */
    private double marginScore;
    /**
     * '可提现资金余额',
     */
    private double moneyDrawUsable;
    /**
     * '冻结资金',
     */
    private double moneyFrozen;
    /**
     * '可用资金余额',
     */
    private double moneyUsable;
    /**
     *  '可用红包余额',
     */
    private double redbagUsable;
    /**
     * '可用积分余额',
     */
    private double scoreUsable;
    private String updateTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public double getMarginScore() {
        return marginScore;
    }

    public void setMarginScore(double marginScore) {
        this.marginScore = marginScore;
    }

    public double getMoneyDrawUsable() {
        return moneyDrawUsable;
    }

    public void setMoneyDrawUsable(double moneyDrawUsable) {
        this.moneyDrawUsable = moneyDrawUsable;
    }

    public double getMoneyFrozen() {
        return moneyFrozen;
    }

    public void setMoneyFrozen(double moneyFrozen) {
        this.moneyFrozen = moneyFrozen;
    }

    public double getMoneyUsable() {
        return moneyUsable;
    }

    public void setMoneyUsable(double moneyUsable) {
        this.moneyUsable = moneyUsable;
    }

    public double getRedbagUsable() {
        return redbagUsable;
    }

    public void setRedbagUsable(double redbagUsable) {
        this.redbagUsable = redbagUsable;
    }

    public double getScoreUsable() {
        return scoreUsable;
    }

    public void setScoreUsable(double scoreUsable) {
        this.scoreUsable = scoreUsable;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserFundInfo{" +
                "createTime='" + createTime + '\'' +
                ", margin=" + margin +
                ", marginScore=" + marginScore +
                ", moneyDrawUsable=" + moneyDrawUsable +
                ", moneyFrozen=" + moneyFrozen +
                ", moneyUsable=" + moneyUsable +
                ", redbagUsable=" + redbagUsable +
                ", scoreUsable=" + scoreUsable +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}

package com.jnhyxx.html5.domain.account;

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

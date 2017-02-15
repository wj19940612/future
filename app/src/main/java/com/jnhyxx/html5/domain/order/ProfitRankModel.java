package com.jnhyxx.html5.domain.order;

/**
 * Created by ${wangJie} on 2017/2/15.
 * 昨日盈利榜的model
 */

public class ProfitRankModel {

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 盈利
     */
    private double profit;

    public ProfitRankModel() {

    }

    public ProfitRankModel(double profit, String phone) {
        this.profit = profit;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    @Override
    public String toString() {
        return "ProfitRankModel{" +
                "phone='" + phone + '\'' +
                ", profit=" + profit +
                '}';
    }
}

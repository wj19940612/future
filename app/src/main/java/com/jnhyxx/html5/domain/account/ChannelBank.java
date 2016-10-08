package com.jnhyxx.html5.domain.account;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/9.
 * 渠道银行列表
 */

public class ChannelBank implements Serializable {

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

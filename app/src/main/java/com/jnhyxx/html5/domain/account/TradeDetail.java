package com.jnhyxx.html5.domain.account;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/30.
 */

public class TradeDetail implements Serializable {
    private static final long serialVersionUID = -7454080372304618453L;
    /**
     * 资金明细表
     */
    public static final int FUND_DETAIL_TABLE = 837;
    /**
     * 代表将要查 积分明细表
     */
    public static final int INTEGRAL_DETAIL_TABLE = 791;

    //收益增加的tradeDetail标示
    public static final int LOGO_INCOME_ADD = 2202;
    //收益减少的tradeDetail标示
    public static final int LOGO_INCOME_CUT = -2202;

    //保证金冻结
    public static final int LOGO_MARGIN_FREEZE = -2102;
    //保证金返回  2201, "返还保证金
    public static final int LOGO_MARGIN_BACK = 2201;
    //返还保证金  2302
    public static final int DEPOSIT_BACK = 2302;

    /**
     * -2101, "支付手续费
     * 2301, "返还手续费"
     */
    public static final int LOGO_FEE_APPLY = -2101;
    public static final int LOGO_FEE_BACK = 2301;


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
                ", money=" + money +
                ", moneyLeft=" + moneyLeft +
                ", ioOrderId=" + ioOrderId +
                '}';
    }
}

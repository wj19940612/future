package com.jnhyxx.html5.domain.local;


public class SubmittedOrder {

    //普通下单
    public static final int SUBMIT_TYPE_NORMAL = 1;
    public static final int SUBMIT_TYPE_LIGHTNING_ORDER = 2;

    private int varietyId;
    private int payType;
    private int assetsId;
    private int handsNum;
    private int stopWinBeat; // should be stopWinPoint
    private double orderPrice; // should be lastPrice
    private int direction; // long - 1, short - 0

    /**
     * 1  普通下单
     * 2  闪电下单
     */
    private int submitType;

    public SubmittedOrder(int varietyId, int direction) {
        this.varietyId = varietyId;
        this.direction = direction;
        this.submitType = SUBMIT_TYPE_NORMAL;
    }

    public SubmittedOrder(int varietyId, int direction, int submitType) {
        this.varietyId = varietyId;
        this.direction = direction;
        this.submitType = submitType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public void setAssetsId(int assetsId) {
        this.assetsId = assetsId;
    }

    public void setHandsNum(int handsNum) {
        this.handsNum = handsNum;
    }

    public void setStopProfitPoint(int stopWinBeat) {
        this.stopWinBeat = stopWinBeat;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getDirection() {
        return direction;
    }

    public int getSubmitType() {
        return submitType;
    }

    public void setSubmitType(int submitType) {
        this.submitType = submitType;
    }

    @Override
    public String toString() {
        return "SubmittedOrder{" +
                "varietyId=" + varietyId +
                ", payType=" + payType +
                ", assetsId=" + assetsId +
                ", handsNum=" + handsNum +
                ", stopWinBeat=" + stopWinBeat +
                ", orderPrice=" + orderPrice +
                ", direction=" + direction +
                ", submitType=" + submitType +
                '}';
    }
}

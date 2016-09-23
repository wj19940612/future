package com.jnhyxx.html5.domain.local;

public class SubmittedOrder {

    private int varietyId;
    private int payType;
    private int assetsId;
    private int handsNum;
    private int stopWinBeat; // should be stopWinPoint
    private double orderPrice; // should be lastPrice
    private int direction; // long - 1, short - 0

    public SubmittedOrder(int varietyId, int direction) {
        this.varietyId = varietyId;
        this.direction = direction;
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
                '}';
    }
}

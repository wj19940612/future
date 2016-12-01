package com.jnhyxx.html5.domain.order;

public class StopProfitLossConfig {


    /**
     * beatFewPoints : 0.1
     * direction : 0
     * downStopLoseMoney : 1220
     * downStopWinMoney : 74053
     * eachPointMoney : 100
     * leaveBeatBanBuy : 1
     * payType : 1
     * stopLoseOffsetPoint : 0
     * stopLossMoney : 1225.8
     * stopWinMoney : 1209.1
     * stopWinOffsetPoint : 0
     * upStopWinMoney : 1179
     */

    private double beatFewPoints;
    private double stopLoseOffsetPoint;
    private double stopWinOffsetPoint;
    private double downStopWinMoney;
    private double upStopWinMoney;

    public double getBeatFewPoints() {
        return beatFewPoints;
    }

    /**
     * 止损偏移量: 跳动次数 * 一次跳动的点数 beatFewPoints
     *
     * @return
     */
    public double getStopLoseOffsetPoint() {
        return stopLoseOffsetPoint;
    }

    /**
     * 止盈偏移量: 跳动次数 * 一次跳动的点数 beatFewPoints
     *
     * @return
     */
    public double getStopWinOffsetPoint() {
        return stopWinOffsetPoint;
    }

    /**
     * 买涨 最高止盈价
     *
     * @return
     */
    public double getUpStopWinMoney() {
        return upStopWinMoney;
    }

    /**
     * 买跌 最高止盈价
     *
     * @return
     */
    public double getDownStopWinMoney() {
        return downStopWinMoney;
    }

    public double getHighestStopProfitPrice(int buyOrSell) {
        if (buyOrSell == AbsOrder.DIRECTION_LONG) {
            return getUpStopWinMoney();
        } else {
            return getDownStopWinMoney();
        }
    }

    @Override
    public String toString() {
        return "StopProfitLossConfig{" +
                "beatFewPoints=" + beatFewPoints +
                ", stopLoseOffsetPoint=" + stopLoseOffsetPoint +
                ", stopWinOffsetPoint=" + stopWinOffsetPoint +
                ", downStopWinMoney=" + downStopWinMoney +
                ", upStopWinMoney=" + upStopWinMoney +
                '}';
    }
}

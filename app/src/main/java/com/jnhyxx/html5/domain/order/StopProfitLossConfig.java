package com.jnhyxx.html5.domain.order;

public class StopProfitLossConfig {



    /**
     limitStopLossMoney: 1170.5,
     stopLoseOffsetPoint: 0.3,
     upStopWinMoney: 1180.7,
     stopLossMoney: 1170.5,
     downStopLoseMoney: 1170.8,
     payType: 1,
     beatFewPoints: 0.1,
     stopWinOffsetPoint: 0.2,
     stopWinMoney: 1176.9,
     limitStopWinMoney: 1180.9,
     eachPointMoney: 100,
     direction: 1
     */

    private double beatFewPoints;
    private double stopLoseOffsetPoint;
    private double stopWinOffsetPoint;
    private double limitStopLossMoney; // 最初订单的止损
    private double limitStopWinMoney; // 止盈的上限, 最高止盈价

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
     * 最初订单的止损价
     *
     * @return
     */
    public double getFirstStopLossPrice() {
        return limitStopLossMoney;
    }

    /**
     * 止盈的上限, 最高止盈价
     *
     * @return
     */
    public double getHighestStopProfitPrice() {
        return limitStopWinMoney;
    }

    @Override
    public String toString() {
        return "StopProfitLossConfig{" +
                "beatFewPoints=" + beatFewPoints +
                ", stopLoseOffsetPoint=" + stopLoseOffsetPoint +
                ", stopWinOffsetPoint=" + stopWinOffsetPoint +
                ", limitStopLossMoney=" + limitStopLossMoney +
                ", limitStopWinMoney=" + limitStopWinMoney +
                '}';
    }
}

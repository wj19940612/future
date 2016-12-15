package com.jnhyxx.html5.domain.market;

public class FullMarketData {



    /**
     * askPrice : 45.14
     * askVolume : 24
     * bidPrice : 45.13
     * bidVolume : 14
     * highestPrice : 45.66
     * instrumentId : CL1610
     * lastPrice : 45.13
     * lowestPrice : 44.85
     * openPrice : 45.57
     * preClsPrice : 45.88
     * preSetPrice : 45.88
     * settlePrice : 45.88
     * tradeDay : 2016-09-12
     * upDropPrice : -0.75
     * upDropSpeed : -0.02
     * upTime : 1473669891736
     * positionVolume 今日持仓量
     * prePositionVolume 昨日持仓量
     *
     * 内盘新增:
     * downLimitPrice: 2621,
     * upLimitPrice: 2956,
     * exchangeId:
     * openInterest: 1399424,
     * preOpenInterest: 1399424,
     * turnover: 2.468655404E+10,
     * upTimeFormat: "2016-11-16 22:13:15.000",
     * volume : 296902
     *
     */

    private double askPrice; // 卖一价
    private int askVolume; // 卖一量
    private double bidPrice; // 买一量
    private int bidVolume; // 买一量
    private double highestPrice; //当日最高价
    private String instrumentId; // 合约代码
    private double lastPrice;
    private double lowestPrice; //当日最低价
    private double openPrice; //开盘价
    private double preClsPrice; //昨日收盘价
    private double preSetPrice; //昨日结算价
    private double settlePrice; //结算价
    private String tradeDay; // 交易日
    private double upDropPrice; //涨跌值
    private double upDropSpeed; //涨跌幅
    private long upTime; // 行情时间戳
    private long positionVolume;//今日持仓量
    private long prePositionVolume;//昨日持仓量

    private double downLimitPrice; //跌停板
    private double upLimitPrice; //涨停板
    private String exchangeId;
    private long openInterest; //今日持仓量
    private long preOpenInterest; //昨日持仓量
    private double turnover; //成交金额
    private String upTimeFormat;
    private long volume; //成交量

    public double getAskPrice() {
        return askPrice;
    }

    public int getAskVolume() {
        return askVolume;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public int getBidVolume() {
        return bidVolume;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getPreClsPrice() {
        return preClsPrice;
    }

    public double getPreSetPrice() {
        return preSetPrice;
    }

    public double getSettlePrice() {
        return settlePrice;
    }

    public long getPositionVolume() {
        return positionVolume;
    }

    public long getPrePositionVolume() {
        return prePositionVolume;
    }

    public long getOpenInterest() {
        return openInterest;
    }

    public long getPreOpenInterest() {
        return preOpenInterest;
    }

    public long getVolume() {
        return volume;
    }

    public double getTurnover() {
        return turnover;
    }

    public double getDownLimitPrice() {
        return downLimitPrice;
    }

    public double getUpLimitPrice() {
        return upLimitPrice;
    }

    @Override
    public String toString() {
        return "FullMarketData{" +
                "askPrice=" + askPrice +
                ", askVolume=" + askVolume +
                ", bidPrice=" + bidPrice +
                ", bidVolume=" + bidVolume +
                ", highestPrice=" + highestPrice +
                ", instrumentId='" + instrumentId + '\'' +
                ", lastPrice=" + lastPrice +
                ", lowestPrice=" + lowestPrice +
                ", openPrice=" + openPrice +
                ", preClsPrice=" + preClsPrice +
                ", preSetPrice=" + preSetPrice +
                ", settlePrice=" + settlePrice +
                ", tradeDay='" + tradeDay + '\'' +
                ", upDropPrice=" + upDropPrice +
                ", upDropSpeed=" + upDropSpeed +
                ", upTime=" + upTime +
                ", positionVolume=" + positionVolume +
                ", prePositionVolume=" + prePositionVolume +
                '}';
    }
}

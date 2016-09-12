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
     * volume : 296902
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
    private int volume; //交易总数量

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public int getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(int askVolume) {
        this.askVolume = askVolume;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(int bidVolume) {
        this.bidVolume = bidVolume;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(double highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
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

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getPreClsPrice() {
        return preClsPrice;
    }

    public void setPreClsPrice(double preClsPrice) {
        this.preClsPrice = preClsPrice;
    }

    public double getPreSetPrice() {
        return preSetPrice;
    }

    public void setPreSetPrice(double preSetPrice) {
        this.preSetPrice = preSetPrice;
    }

    public double getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(double settlePrice) {
        this.settlePrice = settlePrice;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }

    public double getUpDropPrice() {
        return upDropPrice;
    }

    public void setUpDropPrice(double upDropPrice) {
        this.upDropPrice = upDropPrice;
    }

    public double getUpDropSpeed() {
        return upDropSpeed;
    }

    public void setUpDropSpeed(double upDropSpeed) {
        this.upDropSpeed = upDropSpeed;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}

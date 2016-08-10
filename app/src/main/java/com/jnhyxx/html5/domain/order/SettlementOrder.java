package com.jnhyxx.html5.domain.order;

import com.jnhyxx.html5.domain.market.Product;

public class SettlementOrder {

    public static final int TRADE_TYPE_LONG = 0;
    public static final int TRADE_TYPE_SHORT = 1;

    public interface SaleOperation {
        int BACKGROUND_SALE = 1;
        int USER_SALE = 2;
        int SYSTEM_SALE = 3;
        int RISK_CONTROL_SALE = 4;
        int UNKNOWN = 100;
    }

    /**
     * id : 98077
     * fundType : 0
     * tradeType : 0
     * displayId : 1ufg29h0b0bxql1i8
     * nickName : null
     * futuresType : 1002
     * futuresId : 130
     * futuresCode : CL1608
     * accountUserId : null
     * couponId : null
     * realDiscount : 0.0
     * financyAllocation : null
     * cashFund : 180.0
     * theoryCounterFee : 39.0
     * shouldCounterFee : 25.0
     * counterFee : 25.0
     * lossProfit : 800.0
     * stopLoss : 150.0
     * stopProfit : 800.0
     * stopLossPrice : 45.11
     * stopProfitPrice : 46.06
     * buyPrice : 45.26
     * count : 1
     * buyDate : 2016-07-15 17:01:26
     * createDate : 2016-07-15 17:01:27
     * salePrice : 46.06
     * saleDate : 2016-07-15 20:38:29
     * saleOpSource : 4
     * sysSetSaleDate : 2016-07-16 04:58:00
     * status : 6
     * currency : USD
     * rate : 6.6
     * buyPid : 10515
     */

    private int id;
    private int fundType;
    private int tradeType;
    private String displayId;
    private Object nickName;
    private int futuresType;
    private int futuresId;
    private String futuresCode;
    private Object accountUserId;
    private Object couponId;
    private double realDiscount;
    private Object financyAllocation;
    private double cashFund;
    private double theoryCounterFee;
    private double shouldCounterFee;
    private double counterFee;
    private double lossProfit;
    private double stopLoss;
    private double stopProfit;
    private double stopLossPrice;
    private double stopProfitPrice;
    private double buyPrice;
    private int count;
    private String buyDate;
    private String createDate;
    private double salePrice;
    private String saleDate;
    private int saleOpSource; //卖出类型 1后台人工卖出 2用户自己主动卖出 3系统清仓卖出 4风控卖出 100未知的卖出方式
    private String sysSetSaleDate;
    private int status;
    private String currency;
    private double rate;
    private int buyPid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFundType() {
        return fundType;
    }

    public void setFundType(int fundType) {
        this.fundType = fundType;
    }

    public int getTradeType() {
        return tradeType;
    }

    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public Object getNickName() {
        return nickName;
    }

    public void setNickName(Object nickName) {
        this.nickName = nickName;
    }

    public int getFuturesType() {
        return futuresType;
    }

    public void setFuturesType(int futuresType) {
        this.futuresType = futuresType;
    }

    public int getFuturesId() {
        return futuresId;
    }

    public void setFuturesId(int futuresId) {
        this.futuresId = futuresId;
    }

    public String getFuturesCode() {
        return futuresCode;
    }

    public void setFuturesCode(String futuresCode) {
        this.futuresCode = futuresCode;
    }

    public Object getAccountUserId() {
        return accountUserId;
    }

    public void setAccountUserId(Object accountUserId) {
        this.accountUserId = accountUserId;
    }

    public Object getCouponId() {
        return couponId;
    }

    public void setCouponId(Object couponId) {
        this.couponId = couponId;
    }

    public double getRealDiscount() {
        return realDiscount;
    }

    public void setRealDiscount(double realDiscount) {
        this.realDiscount = realDiscount;
    }

    public Object getFinancyAllocation() {
        return financyAllocation;
    }

    public void setFinancyAllocation(Object financyAllocation) {
        this.financyAllocation = financyAllocation;
    }

    public double getCashFund() {
        return cashFund;
    }

    public void setCashFund(double cashFund) {
        this.cashFund = cashFund;
    }

    public double getTheoryCounterFee() {
        return theoryCounterFee;
    }

    public void setTheoryCounterFee(double theoryCounterFee) {
        this.theoryCounterFee = theoryCounterFee;
    }

    public double getShouldCounterFee() {
        return shouldCounterFee;
    }

    public void setShouldCounterFee(double shouldCounterFee) {
        this.shouldCounterFee = shouldCounterFee;
    }

    public double getCounterFee() {
        return counterFee;
    }

    public void setCounterFee(double counterFee) {
        this.counterFee = counterFee;
    }

    public double getLossProfit() {
        return lossProfit;
    }

    public void setLossProfit(double lossProfit) {
        this.lossProfit = lossProfit;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public double getStopProfit() {
        return stopProfit;
    }

    public void setStopProfit(double stopProfit) {
        this.stopProfit = stopProfit;
    }

    public double getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public double getStopProfitPrice() {
        return stopProfitPrice;
    }

    public void setStopProfitPrice(double stopProfitPrice) {
        this.stopProfitPrice = stopProfitPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public int getSaleOpSource() {
        return saleOpSource;
    }

    public void setSaleOpSource(int saleOpSource) {
        this.saleOpSource = saleOpSource;
    }

    public String getSysSetSaleDate() {
        return sysSetSaleDate;
    }

    public void setSysSetSaleDate(String sysSetSaleDate) {
        this.sysSetSaleDate = sysSetSaleDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getBuyPid() {
        return buyPid;
    }

    public void setBuyPid(int buyPid) {
        this.buyPid = buyPid;
    }

    public boolean isForeign() {
        return !currency.equalsIgnoreCase(Product.CURRENCY_RMB);
    }
}

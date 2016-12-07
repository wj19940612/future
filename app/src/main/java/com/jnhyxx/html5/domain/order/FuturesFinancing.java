package com.jnhyxx.html5.domain.order;

import com.jnhyxx.html5.domain.market.Product;
import com.jnhyxx.html5.view.OrderConfigurationSelector;
import com.johnz.kutils.FinanceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FuturesFinancing {

    /**
     * assets : [{"cvId":170,"marginBeat":332.5,"stopWinBeats":{"1":66.5,"5":332.5,"10":665},"fees":10,"marginBeatHands":{"1":50,"5":250,"10":500,"20":1000},"isDefault":0,"handsMultiple":["1","5","10","20"],"feesHands":{"1":10,"5":50,"10":100,"20":200},"stopLossBeat":199.5,"assetsId":10}]
     * contractsCode : GC1612
     * marginPoint : 2
     * sign : $
     * marketPoint : 2
     * feesPoint : 2
     * varietyName : 美黄金
     * eachPointMoney : 100
     * currencyUnit : 美元
     * ratio : 6.65
     */

    private String contractsCode;
    private int marginPoint;
    private String sign;
    private int marketPoint;
    private int feesPoint;
    private String varietyName;
    private double eachPointMoney;
    private String currencyUnit;
    private double ratio;
    /**
     * cvId : 170
     * marginBeat : 332.5
     * stopWinBeats : {"1":66.5,"5":332.5,"10":665}
     * fees : 10
     * marginBeatHands : {"1":50,"5":250,"10":500,"20":1000}
     * isDefault : 0
     * handsMultiple : ["1","5","10","20"]
     * feesHands : {"1":10,"5":50,"10":100,"20":200}
     * stopLossBeat : 199.5
     * assetsId : 10
     */

    private List<AssetsBean> assets;

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public int getMarginPoint() {
        return marginPoint;
    }

    public void setMarginPoint(int marginPoint) {
        this.marginPoint = marginPoint;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getMarketPoint() {
        return marketPoint;
    }

    public void setMarketPoint(int marketPoint) {
        this.marketPoint = marketPoint;
    }

    public int getFeesPoint() {
        return feesPoint;
    }

    public void setFeesPoint(int feesPoint) {
        this.feesPoint = feesPoint;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public void setVarietyName(String varietyName) {
        this.varietyName = varietyName;
    }

    public double getEachPointMoney() {
        return eachPointMoney;
    }

    public void setEachPointMoney(double eachPointMoney) {
        this.eachPointMoney = eachPointMoney;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public List<AssetsBean> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetsBean> assets) {
        this.assets = assets;
    }

    public void sort() {
        Collections.sort(getAssets(), new Comparator<AssetsBean>() {
            @Override
            public int compare(AssetsBean o1, AssetsBean o2) {
                return (int) (o1.getStopLossBeat() - o2.getStopLossBeat());
            }
        });
    }

    public static class AssetsBean {

        private double marginBeat;
        private Map<String, Double> stopWinBeats;
        private int fees;
        private Map<String, Double> marginBeatHands;
        private int isDefault;
        private Map<String, Double> feesHands;
        private double stopLossBeat;
        private int assetsId;
        private List<String> handsMultiple;

        public Map<String, Double> getStopWinBeats() {
            return stopWinBeats;
        }

        public Map<String, Double> getMarginBeatHands() {
            return marginBeatHands;
        }

        public Map<String, Double> getFeesHands() {
            return feesHands;
        }

        public double getMarginBeat() {
            return marginBeat;
        }

        public void setMarginBeat(double marginBeat) {
            this.marginBeat = marginBeat;
        }

        public int getFees() {
            return fees;
        }

        public void setFees(int fees) {
            this.fees = fees;
        }

        public int getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(int isDefault) {
            this.isDefault = isDefault;
        }

        public double getStopLossBeat() {
            return stopLossBeat;
        }

        public void setStopLossBeat(double stopLossBeat) {
            this.stopLossBeat = stopLossBeat;
        }

        public int getAssetsId() {
            return assetsId;
        }

        public void setAssetsId(int assetsId) {
            this.assetsId = assetsId;
        }

        public List<String> getHandsMultiple() {
            return handsMultiple;
        }

//        @Override
//        public String toString() {
//            StringBuilder builder = new StringBuilder();
//            for (Map.Entry<String, Double> entry : stopWinBeats.entrySet()) {
//                builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
//            }
//            return builder.toString();
//        }


        @Override
        public String toString() {
            return "AssetsBean{" +
                    "marginBeat=" + marginBeat +
                    ", stopWinBeats=" + stopWinBeats +
                    ", fees=" + fees +
                    ", marginBeatHands=" + marginBeatHands +
                    ", isDefault=" + isDefault +
                    ", feesHands=" + feesHands +
                    ", stopLossBeat=" + stopLossBeat +
                    ", assetsId=" + assetsId +
                    ", handsMultiple=" + handsMultiple +
                    '}';
        }
    }

    public static class TradeQuantity implements OrderConfigurationSelector.OrderConfiguration {
        private int quantity;
        private double fee; // rmb
        private double margin; // internal is rmb, foreign is not m

        public int getQuantity() {
            return quantity;
        }

        public TradeQuantity(int quantity, double fee, double margin) {
            this.quantity = quantity;
            this.fee = fee;
            this.margin = margin;
        }

        public double getFee() {
            return fee;
        }

        public double getMargin() {
            return margin;
        }

        @Override
        public String getValue() {
            return quantity + "手";
        }

        @Override
        public boolean isDefault() {
            return false;
        }
    }

    public static class StopProfit implements OrderConfigurationSelector.OrderConfiguration {

        private int stopProfitPoint;
        private double stopProfit;
        private int profitLossScale;
        private String sign;

        public StopProfit(int stopProfitPoint, double stopProfit, int profitLossScale, String sign) {
            this.stopProfitPoint = stopProfitPoint;
            this.stopProfit = stopProfit;
            this.profitLossScale = profitLossScale;
            this.sign = sign;
        }

        public int getStopProfitPoint() {
            return stopProfitPoint;
        }

        @Override
        public String getValue() {
            return sign + FinanceUtil.formatWithScale(stopProfit, profitLossScale);
        }

        @Override
        public boolean isDefault() {
            return false;
        }

        public double getStopProfit() {
            return stopProfit;
        }
    }

    public static class StopLoss implements OrderConfigurationSelector.OrderConfiguration {

        private int profitLossScale;
        private String sign;
        private AssetsBean assetsBean;

        public StopLoss(int profitLossScale, String sign, AssetsBean assetsBean) {
            this.profitLossScale = profitLossScale;
            this.sign = sign;
            this.assetsBean = assetsBean;
        }

        @Override
        public String getValue() {
            return sign + FinanceUtil.formatWithScale(assetsBean.getStopLossBeat(), profitLossScale);
        }

        @Override
        public boolean isDefault() {
            return assetsBean.getIsDefault() == 1;
        }

        public AssetsBean getAssetsBean() {
            return assetsBean;
        }

        public List<StopProfit> getStopProfitList() {
            List<StopProfit> result = new ArrayList<>();
            Map<String, Double> stopWinBeats = assetsBean.getStopWinBeats();
            for (Map.Entry<String, Double> entry : stopWinBeats.entrySet()) {
                int stopProfitPoint = Integer.valueOf(entry.getKey()).intValue();
                double stopProfit = entry.getValue().doubleValue();
                result.add(new StopProfit(stopProfitPoint, stopProfit, profitLossScale, sign));
            }
            Collections.sort(result, new Comparator<StopProfit>() {
                @Override
                public int compare(StopProfit o1, StopProfit o2) {
                    return o1.getStopProfitPoint() - o2.getStopProfitPoint();
                }
            });
            return result;
        }

        public List<TradeQuantity> getTradeQuantityList() {
            List<TradeQuantity> result = new ArrayList<>();
            for (String hand : assetsBean.getHandsMultiple()) {
                Double fee = assetsBean.getFeesHands().get(hand);
                double feePrimary = 0;
                if (fee != null) {
                    feePrimary = fee.doubleValue();
                }
                Double margin = assetsBean.getMarginBeatHands().get(hand);
                double marginPrimary = 0;
                if (margin != null) {
                    marginPrimary = margin.doubleValue();
                }
                result.add(new TradeQuantity(Integer.valueOf(hand).intValue(), feePrimary, marginPrimary));
            }
            Collections.sort(result, new Comparator<TradeQuantity>() {
                @Override
                public int compare(TradeQuantity o1, TradeQuantity o2) {
                    return o1.getQuantity() - o2.getQuantity();
                }
            });
            return result;
        }
    }

    public List<StopLoss> getStopLossList(Product product) {
        List<StopLoss> result = new ArrayList<>();
        for (AssetsBean assetsBean : assets) {
            result.add(new StopLoss(product.getLossProfitScale(), product.getSign(), assetsBean));
        }
        return result;
    }

//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        for (AssetsBean assetsBean : assets) {
//            builder.append(assetsBean.getStopLossBeat()).append("\n")
//                    .append(assetsBean.toString()).append("\n");
//        }
//        return builder.toString();
//    }


    @Override
    public String toString() {
        return "FuturesFinancing{" +
                "contractsCode='" + contractsCode + '\'' +
                ", marginPoint=" + marginPoint +
                ", sign='" + sign + '\'' +
                ", marketPoint=" + marketPoint +
                ", feesPoint=" + feesPoint +
                ", varietyName='" + varietyName + '\'' +
                ", eachPointMoney=" + eachPointMoney +
                ", currencyUnit='" + currencyUnit + '\'' +
                ", ratio=" + ratio +
                ", assets=" + assets +
                '}';
    }
}

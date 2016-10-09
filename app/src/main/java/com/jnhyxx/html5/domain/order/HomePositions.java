package com.jnhyxx.html5.domain.order;

import java.util.List;

/**
 * 首页持仓数据
 */
public class HomePositions {

    /**
     * varietyType : au
     * handsNum : 5
     */

    private List<CashOpSBean> cashOpS;
    /**
     * varietyType : GC
     * handsNum : 6
     */

    private List<IntegralOpSBean> integralOpS;

    public List<CashOpSBean> getCashOpS() {
        return cashOpS;
    }

    public void setCashOpS(List<CashOpSBean> cashOpS) {
        this.cashOpS = cashOpS;
    }

    public List<IntegralOpSBean> getIntegralOpS() {
        return integralOpS;
    }

    public void setIntegralOpS(List<IntegralOpSBean> integralOpS) {
        this.integralOpS = integralOpS;
    }

    public static class CashOpSBean implements Position {
        private String varietyType;
        private int handsNum;


        public void setVarietyType(String varietyType) {
            this.varietyType = varietyType;
        }


        public void setHandsNum(int handsNum) {
            this.handsNum = handsNum;
        }

        @Override
        public String getVarietyType() {
            return varietyType;
        }

        @Override
        public int getHandsNum() {
            return handsNum;
        }

    }

    public static class IntegralOpSBean implements Position {
        private String varietyType;
        private int handsNum;


        public void setVarietyType(String varietyType) {
            this.varietyType = varietyType;
        }

        public void setHandsNum(int handsNum) {
            this.handsNum = handsNum;
        }

        @Override
        public String getVarietyType() {
            return varietyType;
        }

        @Override
        public int getHandsNum() {
            return handsNum;
        }
    }

    public interface Position {
        String getVarietyType();
        int getHandsNum();
    }
}

package com.jnhyxx.html5.domain.market;

/**
 * 持仓简要数据内容
 */
public class PositionBrief {

    /**
     * score : 0
     * cash : 0
     * instrumentCode : pp
     */

    private int score;
    private int cash;
    private String instrumentCode;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }
}

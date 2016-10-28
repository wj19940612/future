package com.jnhyxx.html5.domain.order;

import java.util.List;

public class SettledOrderSet {

    /**
     * data : [{"batchNoTime":0,"currencyUnit":"美元","direction":0,"handsNum":1,"orderStatus":4,"orderTime":1474342989000,"ratio":6.65,"realAvgPrice":43.64,"realMarketVal":43640,"sellTime":1474343063000,"showId":"11mj","stopLossPrice":10,"stopWinPrice":10,"unwindAvgPrice":43.65,"unwindType":2,"winOrLoss":-10},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300766000,"ratio":6.65,"realAvgPrice":44.47,"realMarketVal":44470,"sellTime":1474300782000,"showId":"11m2","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.5,"unwindType":0,"winOrLoss":30},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300767000,"ratio":6.65,"realAvgPrice":44.47,"realMarketVal":44470,"sellTime":1474300771000,"showId":"11m4","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.47,"unwindType":0,"winOrLoss":0},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300767000,"ratio":6.65,"realAvgPrice":44.47,"realMarketVal":44470,"sellTime":1474300768000,"showId":"11m3","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.47,"unwindType":0,"winOrLoss":0},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300753000,"ratio":6.65,"realAvgPrice":44.46,"realMarketVal":44460,"sellTime":1474300761000,"showId":"11m1","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.47,"unwindType":0,"winOrLoss":10},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300753000,"ratio":6.65,"realAvgPrice":44.46,"realMarketVal":44460,"sellTime":1474300758000,"showId":"11m0","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.46,"unwindType":0,"winOrLoss":0},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300753000,"ratio":6.65,"realAvgPrice":44.46,"realMarketVal":44460,"sellTime":1474300755000,"showId":"11lz","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.45,"unwindType":0,"winOrLoss":-10},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300669000,"ratio":6.65,"realAvgPrice":44.5,"realMarketVal":44500,"sellTime":1474300745000,"showId":"11lv","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.44,"unwindType":0,"winOrLoss":-60},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300669000,"ratio":6.65,"realAvgPrice":44.5,"realMarketVal":44500,"sellTime":1474300742000,"showId":"11lt","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.44,"unwindType":0,"winOrLoss":-60},{"batchNoTime":0,"currencyUnit":"美元","direction":1,"handsNum":1,"orderStatus":4,"orderTime":1474300669000,"ratio":6.65,"realAvgPrice":44.5,"realMarketVal":44500,"sellTime":1474300740000,"showId":"11ls","stopLossPrice":200,"stopWinPrice":180,"unwindAvgPrice":44.44,"unwindType":0,"winOrLoss":-60}]
     * pageSize : 10
     * resultCount : 122
     * start : 1
     * total : 1
     */

    private int pageSize;
    private int resultCount;
    private int start;
    private int total;

    private List<SettledOrder> data;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SettledOrder> getData() {
        return data;
    }

    public void setData(List<SettledOrder> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SettledOrderSet{" +
                "pageSize=" + pageSize +
                ", resultCount=" + resultCount +
                ", start=" + start +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}

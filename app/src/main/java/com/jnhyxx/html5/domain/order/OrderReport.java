package com.jnhyxx.html5.domain.order;

import java.util.List;

public class OrderReport {

    /**
     * count : 693
     * resultList : [{"nick":"***73","futuresType":"美原油","time":"11:39","tradeType":"做空"},{"nick":"***95","futuresType":"美原油","time":"10:46","tradeType":"做空"},{"nick":"***19","futuresType":"美黄金","time":"10:36","tradeType":"做空"},{"nick":"***19","futuresType":"美黄金","time":"10:31","tradeType":"做空"},{"nick":"***19","futuresType":"美黄金","time":"10:30","tradeType":"做空"}]
     */

    private int count;
    /**
     * nick : ***73
     * futuresType : 美原油
     * time : 11:39
     * tradeType : 做空
     */

    private List<ResultListBean> resultList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ResultListBean> getResultList() {
        return resultList;
    }

    public void setResultList(List<ResultListBean> resultList) {
        this.resultList = resultList;
    }

    public static class ResultListBean {
        private String nick;
        private String futuresType;
        private String time;
        private String tradeType;

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getFuturesType() {
            return futuresType;
        }

        public void setFuturesType(String futuresType) {
            this.futuresType = futuresType;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }
    }
}

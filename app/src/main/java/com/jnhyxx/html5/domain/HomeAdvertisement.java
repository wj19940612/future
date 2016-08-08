package com.jnhyxx.html5.domain;

import java.util.List;

public class HomeAdvertisement {

    /**
     * "id": 62,
     * "middleBanner": "/stock/image/20160721/1469104972479019269.jpg",
     * "url": "/newsnotice/stock/newsnotice_00062122.html"
     */

    private List<NewsNoticeImgListBean> news_notice_img_list;

    public List<NewsNoticeImgListBean> getNews_notice_img_list() {
        return news_notice_img_list;
    }

    public void setNews_notice_img_list(List<NewsNoticeImgListBean> news_notice_img_list) {
        this.news_notice_img_list = news_notice_img_list;
    }

    public static class NewsNoticeImgListBean {
        private int id;
        private String middleBanner;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMiddleBanner() {
            return middleBanner;
        }

        public void setMiddleBanner(String middleBanner) {
            this.middleBanner = middleBanner;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

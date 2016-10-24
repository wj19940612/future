package com.jnhyxx.html5.domain;

/**
 * Created by ${wangJie} on 2016/10/20.
 * 存储资讯直播数据
 */

public class InfoLiveMessage {
    private String content;
    private String time;
    private String imageIUrl;
    private boolean isHtml;

    public InfoLiveMessage() {
    }

    public InfoLiveMessage(String time, String content, String imageIUrl, boolean isHtml) {
        this.time = time;
        this.content = content;
        this.imageIUrl = imageIUrl;
        this.isHtml = isHtml;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageIUrl() {
        return imageIUrl;
    }

    public void setImageIUrl(String imageIUrl) {
        this.imageIUrl = imageIUrl;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }
}

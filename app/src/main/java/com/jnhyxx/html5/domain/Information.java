package com.jnhyxx.html5.domain;

import java.io.Serializable;

public class Information implements Serializable{

    private static final long serialVersionUID = -7266947195942724446L;


    public static final int TYPE_BANNER = 0;
    public static final int TYPE_MARKET_ANALYSIS = 2;
    public static final int TYPE_INDUSTRY_ANALYSIS = 3;

    /**
     * channelId : 12
     * content : <p>333</p>
     * cover :
     * createTime : 2016-09-29 10:27:45
     * id : 57ec7c210cf2ea574a418053
     * operator : admin
     * status : 0
     * style : html
     * summary : 222
     * title : 111
     * type : 0
     * updateTime : 2016-09-29 10:27:45
     */

    private int channelId;
    private String content;
    private String cover;
    private String createTime;
    private String id;
    private String operator;
    private int status;
    private String style; // h5 or html
    private String summary;
    private String title;
    private int type;
    private String updateTime;

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isH5Style() {
        return this.style.equalsIgnoreCase("h5");
    }

    @Override
    public String toString() {
        return "Information{" +
                "channelId=" + channelId +
                ", content='" + content + '\'' +
                ", cover='" + cover + '\'' +
                ", createTime='" + createTime + '\'' +
                ", id='" + id + '\'' +
                ", operator='" + operator + '\'' +
                ", status=" + status +
                ", style='" + style + '\'' +
                ", summary='" + summary + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}

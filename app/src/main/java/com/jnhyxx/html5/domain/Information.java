package com.jnhyxx.html5.domain;

import com.jnhyxx.html5.net.API;

import java.io.Serializable;

public class Information implements Serializable {

    private static final long serialVersionUID = -7266947195942724446L;

    /**
     * 首页banner
     */
    public static final int TYPE_BANNER = 0;
    /**
     * 行情分析
     */
    public static final int TYPE_MARKET_ANALYSIS = 2;
    /**
     * 行业资讯
     */
    public static final int TYPE_INDUSTRY_ANALYSIS = 3;
    /**
     * 交易攻略
     */
    public static final int TYPE_TRADING_STRATEGY = 4;

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
    /**
     * 资讯内容
     */
    private String content;
    /**
     * 资讯封面
     */
    private String cover;
    private String createTime;
    private String id;
    /**
     * 经营者
     */
    private String operator;
    private int status;
    /**
     * html为文本资讯,h5为连接资讯
     */
    private String style; // h5 or html
    /**
     * 资讯摘要
     */

    private String summary;
    private String title;
    private int type;
    private String updateTime;
    /**
     * 咨询来源
     */
    private String source;

    public Information() {
    }

    public Information(String cover) {
        this.cover = cover;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getContent() {
        if (isH5Style() && content.startsWith("/")) {
            return API.getHost() + content;
        }
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
                ", source='" + source + '\'' +
                '}';
    }
}

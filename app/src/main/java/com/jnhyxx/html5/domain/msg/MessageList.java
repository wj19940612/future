package com.jnhyxx.html5.domain.msg;

import java.io.Serializable;

/**
 * Created by ${wangJie} on 2016/9/20.
 * 主界面咨询fragment中的model
 */

public class MessageList implements Serializable {

    private static final long serialVersionUID = 7585809056510209026L;

    public static final String STYLE_TXT = "";
    public static final String STYLE_H5 = "h5";


    /**
     * channelId : 25
     * content : 123
     * cover : http://www.abc.com
     * createTime : 2016-09-08 10:08:19
     * id : 1
     * operator :  admin
     * status : 0
     * style : h5
     * summary : 123
     * title : 123
     * type : 0
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
    private int id;
    private String operator;

    private int status;
    /**
     * /**
     * html为文本资讯,h5为连接资讯
     */
    private String style;
    /**
     * 资讯摘要
     */
    private String summary;
    /**
     * 资讯标题
     */
    private String title;
    private int type;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "MessageList{" +
                "channelId=" + channelId +
                ", content='" + content + '\'' +
                ", cover='" + cover + '\'' +
                ", createTime='" + createTime + '\'' +
                ", id=" + id +
                ", operator='" + operator + '\'' +
                ", status=" + status +
                ", style='" + style + '\'' +
                ", summary='" + summary + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                '}';
    }
}

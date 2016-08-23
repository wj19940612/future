package com.jnhyxx.html5.domain.msg;

import java.io.Serializable;

public class SysTradeMessage implements Serializable{

    private static final long serialVersionUID = 6318735334000377658L;
    /**
     * id : 96
     * title :  关于香港交易所延迟开市的通知
     * content : 尊敬的用户：
     由于香港受台风影响，香港交易所规定：8月2日9:15-12:00期间交易所休市。由于台风影响具有不确定性，开市时间可能会再次延后，届时将再另行通知，敬请知悉。
     * createDate : 2016-08-02 10:08:32
     * updateDate : 2016-08-02 10:11:49
     */
    private int id;
    private String title;
    private String content;
    private String createDate;
    private String updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}

package com.jnhyxx.html5.domain.message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${wangJie} on 2016/9/20.
 * 资讯详情界面的model
 */

public class MessageInfo implements Serializable {
    private static final long serialVersionUID = -5774162956082162437L;

    public static final String TYPE_HTML = "html";
    public static final String TYPE_H5="h5";

    /**
     * channelId : 12
     * content : <p>1234djvbdbkvfdlbk nfkbkfdkbkdfbdfkdvvvvvvvvvvvvvvvvvvvvvvvvvvvkbkslkfllsnxvndx,vzmvnldzcxnk,kxnvldxfbfnnnfsnfsnsfnafd</p>
     * cover :
     * createTime : 2016-09-12 16:10:57
     * id : 57d663110cf2f0ccd123d47c
     * operator : admin
     * status : 0
     * style : html
     * summary : 1234
     * title : 1234
     * type : 2
     * updateTime : 2016-09-12 16:10:57
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
    private String operator;
    private int status;
    /**
     * html为文本资讯,h5为连接资讯
     */
    private String style;
    /**
     * 资讯摘要
     */
    private String summary;
    /**
     *资讯标题
     */
    private String title;
    private int type;
    private String updateTime;


    public static MessageInfo objectFromData(String str) {

        return new Gson().fromJson(str, MessageInfo.class);
    }

    public static MessageInfo objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), MessageInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<MessageInfo> arrayMessageInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<MessageInfo>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<MessageInfo> arrayMessageInfoFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<MessageInfo>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

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
}

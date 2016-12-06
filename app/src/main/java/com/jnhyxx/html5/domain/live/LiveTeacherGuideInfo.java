package com.jnhyxx.html5.domain.live;

import java.util.List;

/**
 * Created by ${wangJie} on 2016/11/11.
 */

public class LiveTeacherGuideInfo {

    /**
     * data : [{"chatType":1,"createTime":1478743189762,"deleted":false,"msg":"凯哥考到了没啊","name":"叶老师","normalSpeak":true,"once":true,"order":true,"text":true,"timeStamp":1478743189762,"topChannelId":12,"userId":139},{"chatType":1,"createTime":1478743194364,"deleted":false,"msg":"凯哥","name":"叶老师","normalSpeak":true,"once":true,"order":false,"text":true,"timeStamp":1478743194364,"topChannelId":12,"userId":139},{"chatType":1,"createTime":1478836139118,"deleted":false,"msg":"指令测试1111","name":"叶老师","normalSpeak":true,"once":true,"order":true,"text":true,"timeStamp":1478836139118,"topChannelId":12,"userId":139}]
     * pageSize : 40
     * resultCount : 3
     * start : 0
     * total : 1
     */

    private int pageSize;
    private int resultCount;
    private int start;
    private int total;
    /**
     * chatType : 1
     * createTime : 1478743189762
     * deleted : false
     * msg : 凯哥考到了没啊
     * name : 叶老师
     * normalSpeak : true
     * once : true
     * order : true
     * text : true
     * timeStamp : 1478743189762
     * topChannelId : 12
     * userId : 139
     */

    private List<ChatData> data;

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

    public List<ChatData> getData() {
        return data;
    }

    public void setData(List<ChatData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LiveTeacherGuideInfo{" +
                "pageSize=" + pageSize +
                ", resultCount=" + resultCount +
                ", start=" + start +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}

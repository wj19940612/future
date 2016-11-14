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

    private List<DataInfo> data;

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

    public List<DataInfo> getData() {
        return data;
    }

    public void setData(List<DataInfo> data) {
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

    public static class DataInfo {

        private int chatType;
        private long createTime;
        private boolean deleted;
        private String msg;
        private String name;
        private boolean normalSpeak;
        private boolean once;
        private boolean order;
        private boolean text;
        private long timeStamp;
        private int topChannelId;
        private int userId;

        public int getChatType() {
            return chatType;
        }

        public void setChatType(int chatType) {
            this.chatType = chatType;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isNormalSpeak() {
            return normalSpeak;
        }

        public void setNormalSpeak(boolean normalSpeak) {
            this.normalSpeak = normalSpeak;
        }

        public boolean isOnce() {
            return once;
        }

        public void setOnce(boolean once) {
            this.once = once;
        }

        public boolean isOrder() {
            return order;
        }

        public void setOrder(boolean order) {
            this.order = order;
        }

        public boolean isText() {
            return text;
        }

        public void setText(boolean text) {
            this.text = text;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public int getTopChannelId() {
            return topChannelId;
        }

        public void setTopChannelId(int topChannelId) {
            this.topChannelId = topChannelId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public boolean isTeacherGuide() {
            if (getChatType() == 1) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "DataInfo{" +
                    "chatType=" + chatType +
                    ", createTime=" + createTime +
                    ", deleted=" + deleted +
                    ", msg='" + msg + '\'' +
                    ", name='" + name + '\'' +
                    ", normalSpeak=" + normalSpeak +
                    ", once=" + once +
                    ", order=" + order +
                    ", text=" + text +
                    ", timeStamp=" + timeStamp +
                    ", topChannelId=" + topChannelId +
                    ", userId=" + userId +
                    '}';
        }
    }
}

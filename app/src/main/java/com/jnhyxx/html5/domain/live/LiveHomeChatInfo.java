package com.jnhyxx.html5.domain.live;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ${wangJie} on 2016/11/10.
 * 直播滑动界面的聊天信息
 */

public class LiveHomeChatInfo {

    /**
     * data : [{"chatType":1,"createTime":1478743189762,"deleted":false,"msg":"凯哥考到了没啊","name":"叶老师","normalSpeak":true,"once":true,"order":true,"text":true,"timeStamp":1478743189762,"topChannelId":12,"userId":139},{"chatType":1,"createTime":1478743194364,"deleted":false,"msg":"凯哥","name":"叶老师","normalSpeak":true,"once":true,"order":false,"text":true,"timeStamp":1478743194364,"topChannelId":12,"userId":139},{"address":"/125.120.84.157:62713","chatType":2,"createTime":1478743259824,"deleted":false,"msg":"1","name":"毛泽东","normalSpeak":true,"once":true,"text":true,"timeStamp":1478743259824,"topChannelId":12,"userId":181},{"address":"/125.120.84.157:62379","chatType":2,"createTime":1478743330039,"deleted":false,"msg":"888","name":"毛泽东","normalSpeak":true,"once":true,"text":true,"timeStamp":1478743330039,"topChannelId":12,"userId":181}]
     * pageSize : 15
     * resultCount : 4
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

    public void sort() {
        Collections.sort(getData(), new Comparator<ChatData>() {
            @Override
            public int compare(ChatData o1, ChatData o2) {
                return (int) (o1.getCreateTime() - o2.getCreateTime());
            }
        });
    }

    /**
     * 聊天人的类型,0代表管理员 1 代表老师 2 代表普通用户
     */

    public static class ChatData {


        /**
         * 聊天人的类型,0代表管理员 1 代表老师 2 代表普通用户
         */
        public static final int CHAT_TYPE_MANAGER = 0;
        public static final int CHAT_TYPE_TEACHER = 1;
        public static final int CHAT_TYPE_COMMON_USER = 2;

        private int chatType;

        private long createTime;
        /**
         * false表示正常，true表示已屏蔽
         */
        private boolean deleted;
        private String msg;
        private String name;
        /**
         * 是否正常说话，false表示已禁言，true表示正常
         */
        private boolean normalSpeak;
        /**
         * 是否是本次聊天
         */
        private boolean once;
        /**
         * 是否是指令 true为指令
         */
        private boolean order;
        /**
         * 是否文本
         */
        private boolean text;
        /**
         * 最上面一条数据
         */
        private long timeStamp;
        private int topChannelId;
        private int userId;
        /**
         * 是否用户自己，true表示是他自己
         */
        private boolean owner;

        public boolean isOwner() {
            return owner;
        }

        public void setOwner(boolean owner) {
            this.owner = owner;
        }

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

        public boolean isCommonUser() {
            if (getChatType() == CHAT_TYPE_MANAGER || getChatType() == CHAT_TYPE_TEACHER) {
                return false;
            }
            return true;
        }

        public void setLiveSpeakInfo(LiveSpeakInfo liveSpeakInfo) {
            setMsg(liveSpeakInfo.getMsg());
            setOwner(liveSpeakInfo.isOwner());
            setName(liveSpeakInfo.getName());
            setCreateTime(liveSpeakInfo.getTime());
            setText(liveSpeakInfo.isIsText());
            setOrder(liveSpeakInfo.isIsOrder());
            setChatType(liveSpeakInfo.getAccountType());
        }

        @Override
        public String toString() {
            return "ChatData{" +
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
                    ", owner=" + owner +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "LiveHomeChatInfo{" +
                "pageSize=" + pageSize +
                ", resultCount=" + resultCount +
                ", start=" + start +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}

package com.jnhyxx.html5.domain.live;

public class ChatData {

    /**
     * 聊天人的类型, 0代表管理员 1 代表老师 2 代表普通用户
     */
    public static final int CHAT_TYPE_MANAGER = 0;
    public static final int CHAT_TYPE_TEACHER = 1;
    public static final int CHAT_TYPE_COMMON_USER = 2;

    /**
     * chatType : 1
     * createTime : 1478743189762
     * deleted : false false表示正常，true表示已屏蔽
     * msg : 凯哥考到了没啊
     * name : 叶老师
     * normalSpeak : true false表示已禁言，true表示正常
     * once : true 是否是本次聊天
     * order : true 是否是指令 true为指令
     * text : true 是否文本
     * timeStamp : 1478743189762
     * topChannelId : 12
     * userId : 139
     * ower: true 是否用户自己，true表示是他自己
     */

    private int chatType;
    private long createTime;
    private boolean deleted;
    private String msg;
    private String name;
    private boolean normalSpeak;
    private boolean once;
    private boolean order;
    private boolean text;
    private int topChannelId;
    private int userId;
    private boolean owner;
    private boolean isMoreThanFiveMin;

    public ChatData(LiveSpeakInfo speakInfo) {
        msg = speakInfo.getMsg();
        owner = speakInfo.isOwner();
        name = speakInfo.getName();
        createTime = speakInfo.getTime();
        text = speakInfo.isIsText();
        order =speakInfo.isIsOrder();
        chatType = speakInfo.getAccountType();
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

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isMoreThanFiveMin() {
        return isMoreThanFiveMin;
    }

    public void setMoreThanFiveMin(boolean moreThanFiveMin) {
        isMoreThanFiveMin = moreThanFiveMin;
    }
    public boolean isTeacherGuide() {
        if (chatType == CHAT_TYPE_TEACHER) {
            return true;
        }
        return false;
    }

    public boolean isNormalUser() {
        if (getChatType() == CHAT_TYPE_MANAGER || getChatType() == CHAT_TYPE_TEACHER) {
            return false;
        }
        return true;
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
                ", topChannelId=" + topChannelId +
                ", userId=" + userId +
                ", owner=" + owner +
                ", isMoreThanFiveMin=" + isMoreThanFiveMin +
                '}';
    }
}

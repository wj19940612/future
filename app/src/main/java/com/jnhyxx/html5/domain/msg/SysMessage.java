package com.jnhyxx.html5.domain.msg;

import java.io.Serializable;

/**
 * 系统消息和交易提醒的model
 */
public class SysMessage implements Serializable {
    private static final long serialVersionUID = 6769828919623446685L;

    /**
     * 0,发送状态 0 待审和 1 已审核  2 待发送
     */
    public static final int PUSH_STATUS_CHECK_PENDING = 0;
    public static final int PUSH_STATUS_CHECK_PENDED = 1;
    public static final int PUSH_STATUS_WAIT_SEND = 2;

    /**
     * 0代表 提现信息提示 1 代表订单 平仓 和 止赢止损
     */
    public static final int WITHDRAW_INFO_HINT = 0;
    public static final int TRADE_STATUS = 1;
    /**
     * 2 代表系统消息 3 代表 交易提醒
     */
    public static final int PUSH_TYPE_SYSTEM = 2;
    public static final int PUSH_TYPE_TRADE_HINT = 3;


    /**
     * channelId : 12
     * createTime : 2016-09-27 18:03:07
     * htmlLink : eee
     * id : 4
     * operatorId : 12
     * operatorName : 一级渠道管理员
     * pass : true
     * pushContent : 4444
     * pushMsg : eee
     * pushStatus : 1
     * pushTopic : eee
     * pushType : 1
     * text : false
     */


    /**
     * 一级渠道Id
     */
    private int channelId;
    private String createTime;


    /**
     * 超链接 (test为false时候 这个链接有值)
     */
    private String htmlLink;
    /**
     * 资讯ID
     */
    private String id;
    /**
     * 操作人员Id
     */
    private int operatorId;
    /**
     * ,操作人员名称
     */
    private String operatorName;
    /**
     * 是否通过审核
     */
    private boolean pass;
    /**
     * 咨询简要
     */
    private String pushContent;

    /**
     * ,咨询详情(test为true时候 这个文本有值)
     */
    private String pushMsg;
    /**
     * 0,发送状态 0 待审和 1 已审核  2 待发送
     */
    private int pushStatus;
    /**
     * 主题
     */
    private String pushTopic;
    /**
     * 1, 发送类型  0代表是 行情资讯 1 代表行业资讯  2 代表系统消息 3 代表 交易提醒
     */
    private int pushType;
    /**
     * 是链接 还是纯文本   false 是html  true是h5连接  后台错误
     */
    private boolean text;


    private boolean isText;
    /**
     * 0代表 提现信息提示 1 代表订单 平仓 和 止赢止损
     */
    private int taskType;
    /**
     * true 表示 成功 消息 false表示 是失败消息 显示在交易提醒中的图标
     */
    private boolean success;

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

    public String getPushContent() {
        return pushContent;
    }

    public String getPushTopic() {
        return pushTopic;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public int getTaskType() {
        return taskType;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getPushMsg() {
        return pushMsg;
    }

    public void setPushMsg(String pushMsg) {
        this.pushMsg = pushMsg;
    }

    public String getHtmlLink() {
        return htmlLink;
    }

    public void setHtmlLink(String htmlLink) {
        this.htmlLink = htmlLink;
    }

    public boolean getIsText() {
        return isText;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public int getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(int pushStatus) {
        this.pushStatus = pushStatus;
    }

    public void setPushTopic(String pushTopic) {
        this.pushTopic = pushTopic;
    }

    public int getPushType() {
        return pushType;
    }

    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    @Override
    public String toString() {
        return "SysTradeMessage{" +
                "channelId=" + channelId +
                ", createTime='" + createTime + '\'' +
                ", htmlLink='" + htmlLink + '\'' +
                ", id=" + id +
                ", operatorId=" + operatorId +
                ", operatorName='" + operatorName + '\'' +
                ", pass=" + pass +
                ", pushContent='" + pushContent + '\'' +
                ", pushMsg='" + pushMsg + '\'' +
                ", pushStatus=" + pushStatus +
                ", pushTopic='" + pushTopic + '\'' +
                ", pushType=" + pushType +
                ", text=" + text +
                ", taskType=" + taskType +
                ", success=" + success +
                '}';
    }

    /**
     * 是否是提现信息提示
     *
     * @return
     */
    public boolean isTradeStatus() {
        if (getTaskType() == WITHDRAW_INFO_HINT) {
            return true;
        }
        return false;
    }
}

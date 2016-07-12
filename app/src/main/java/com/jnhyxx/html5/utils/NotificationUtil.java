package com.jnhyxx.html5.utils;

import java.util.Map;

public class NotificationUtil {

    /**
     * message_type   1 系统消息通知，跳转到系统消息页面  2 交易提醒通知，跳转到交易提醒页面
     * level:         0 常规消息  1 重要消息
     * messageId:       系统消息id
     */

    public static final String MESSAGE_TYPE = "message_type";
    public static final String LEVEL = "level";
    public static final String MESSAGE_ID = "messageId";

    private static final String LEVEL_IMPORTANT = "1";

    private static final String MESSAGE_TYPE_SYSTEM = "1";
    private static final String MESSAGE_TYPE_TRADE = "2";

    public static boolean isImportant(Map<String, String> map) {
        String isImportant = map.get(LEVEL);
        return isImportant.equals(LEVEL_IMPORTANT);
    }

    public static String getMessageId(Map<String, String> map) {
        String messageId = map.get(MESSAGE_ID);
        return messageId;
    }

    public static String getMessageType(Map<String, String> map) {
        String messageType = map.get(MESSAGE_TYPE);
        return messageType;
    }

    public static boolean isSystemMessage(Map<String, String> map) {
        String messageType = getMessageType(map);
        return messageType.equals(MESSAGE_TYPE_SYSTEM);
    }

    public static boolean isTradeMessage(Map<String, String> map) {
        String messageType = getMessageType(map);
        return messageType.equals(MESSAGE_TYPE_TRADE);
    }

}

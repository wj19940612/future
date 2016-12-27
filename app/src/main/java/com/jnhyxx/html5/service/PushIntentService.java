package com.jnhyxx.html5.service;

/**
 * Created by ${wangJie} on 2016/12/23.
 */

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.MainActivity;
import com.jnhyxx.html5.activity.account.MessageCenterListItemInfoActivity;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class PushIntentService extends GTIntentService {

    private static final String TAG = "PushIntentService";

    public static final String PUSH_ACTION = "com.jnhyxx.html5.service.PushIntentService";

    public static final String KEY_PUSH_DATA = "PUSH_DATA";

    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;

    public PushIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);
            handleMessage(context, data);
        }

        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        // 获取ClientID(CID)
        // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
        Preference.get().setPushClientId(clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void handleMessage(Context context, String data) {
        try {
            Log.d(TAG, "===data  " + data);
            SysMessage sysMessage = new Gson().fromJson(data, SysMessage.class);
            if (mainIsTopActivity(context) && sysMessage.getPushSendType() != SysMessage.PUSH_SYS_TYPE_PUSH) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(PUSH_ACTION).putExtra(KEY_PUSH_DATA, sysMessage));
            } else {
                Intent messageIntent = setPendingIntent(context, data);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, messageIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                createNotification(context, pendingIntent, data);
            }
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "  " + e.getCause());
        }
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

    private void createNotification(Context context, PendingIntent pendingIntent, String data) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        SysMessage sysMessage = new Gson().fromJson(data, SysMessage.class);
        builder.setContentTitle(sysMessage.getPushTopic());
        builder.setContentText(sysMessage.getPushContent());
        builder.setContentIntent(pendingIntent);
        if (!TextUtils.isEmpty(sysMessage.getCreateTime())) {
            builder.setWhen(DateUtil.getStringToDate(sysMessage.getCreateTime()));
        }
        builder.setAutoCancel(true);

//        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
//        Bitmap bitmap = drawable.getBitmap();
//        builder.setLargeIcon(bitmap);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, builder.build());
    }

    @NonNull
    private Intent setPendingIntent(Context context, String data) {
        Intent messageIntent = new Intent(context, MessageCenterListItemInfoActivity.class);
        SysMessage sysMessage = new Gson().fromJson(data, SysMessage.class);
        messageIntent.putExtra(Launcher.EX_PAYLOAD, sysMessage);
        return messageIntent;
    }

    private boolean mainIsTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName topActivity = activityManager.getRunningTasks(1).get(0).topActivity;
        Log.d(TAG, "上层的Activity " + topActivity.getClassName());
        MainActivity mainActivity = new MainActivity();
        Log.d(TAG, "MainActivity  " + mainActivity.getClass().getName());
        return topActivity.getClassName().contains(mainActivity.getClass().getName());
    }

}

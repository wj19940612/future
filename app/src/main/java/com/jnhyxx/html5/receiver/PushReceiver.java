package com.jnhyxx.html5.receiver;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.MainActivity;
import com.jnhyxx.html5.activity.account.MessageCenterListItemInfoActivity;
import com.jnhyxx.html5.domain.msg.SysMessage;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

public class PushReceiver extends BroadcastReceiver {

    private static final String TAG = "PushReceiver";

    public static final String PUSH_ACTION = "com.jnhyxx.html5.receiver.PushReceiver";

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive  接到新的push 消息" + intent.getAction() + " 数据" + intent.getExtras().toString());
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        ToastUtil.curt("onReceive  接到新的push 消息");
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                Log.d("GetuiSdkDemo", "第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);
                    Log.d(TAG, "===data  " + data);

                    if (mainIsTopActivity(context)) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(PUSH_ACTION));
                        ToastUtil.curt("最上层是MainActivity");
                    } else {
                        Intent messageIntent = setPendingIntent(context, data);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, messageIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        creatNotification(context, pendingIntent);

                    }


                    Log.d("GetuiSdkDemo", "receiver payload : " + data);

                    payloadData.append(data);
                    payloadData.append("\n");

                    Log.d("GetuiSdkDemo", "onReceive: " + data);
                    Log.d("GetuiSdkDemo", "onReceive: " + payloadData.toString());
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");

                Log.d("GetuiSdkDemo", "clientId: " + cid);

                Preference.get().setPushClientId(cid);

                break;
            case PushConsts.GET_SDKONLINESTATE:
                boolean online = bundle.getBoolean("onlineState");
                Log.d("GetuiSdkDemo", "online = " + online);
                break;

            case PushConsts.SET_TAG_RESULT:
                String sn = bundle.getString("sn");
                String code = bundle.getString("code");

                String text = "设置标签失败, 未知异常";
                switch (Integer.valueOf(code)) {
                    case PushConsts.SETTAG_SUCCESS:
                        text = "设置标签成功";
                        break;

                    case PushConsts.SETTAG_ERROR_COUNT:
                        text = "设置标签失败, tag数量过大, 最大不能超过200个";
                        break;

                    case PushConsts.SETTAG_ERROR_FREQUENCY:
                        text = "设置标签失败, 频率过快, 两次间隔应大于1s";
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

                Log.d("GetuiSdkDemo", "settag result sn = " + sn + ", code = " + code);
                Log.d("GetuiSdkDemo", "settag result sn = " + text);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }

    private void creatNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("通知的头部");
        builder.setContentText("通知的内容");
        builder.setContentIntent(pendingIntent);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.string.app_name, builder.build());
    }

    @NonNull
    private Intent setPendingIntent(Context context, String data) {
        Intent messageIntent = new Intent(context, MessageCenterListItemInfoActivity.class);
        SysMessage sysMessage = new SysMessage();
        sysMessage.setPushTopic("本地push测试数据");
        sysMessage.setPushMsg(data);
        sysMessage.setCreateTime(DateUtil.format(System.currentTimeMillis()));
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

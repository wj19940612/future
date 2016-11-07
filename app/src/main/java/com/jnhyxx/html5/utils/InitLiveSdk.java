package com.jnhyxx.html5.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jnhyxx.html5.App;
import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.lecloud.sdk.listener.OnInitCmfListener;

import java.util.List;


/**
 * Created by ${wangJie} on 2016/11/7.
 * 直播所用的SDK初始化
 */

public class InitLiveSdk {
    private static final String TAG = "InitLiveSdk";

    public static boolean cdeInitSuccess;
    private static InitLiveSdk sInitLiveSdk;


    private InitLiveSdk() {
        initSdk();
    }

    public static InitLiveSdk getInstance() {
        if (sInitLiveSdk == null) {
            return new InitLiveSdk();
        }
        return sInitLiveSdk;
    }

    private static void initSdk() {
        String processName = getProcessName(App.getAppContext(), android.os.Process.myPid());
        if (App.getAppContext().getApplicationInfo().packageName.equals(processName)) {
//            LeCloudPlayerConfig.getInstance().setPrintSdcardLog(true).setIsApp().setUseLiveToVod(true);//setUseLiveToVod 使用直播转点播功能 (直播结束后按照点播方式播放)
            SharedPreferences preferences = App.getAppContext().getSharedPreferences("host", Context.MODE_PRIVATE);
            int host = preferences.getInt("host", LeCloudPlayerConfig.HOST_DEFAULT);
            try {
                LeCloudPlayerConfig.setHostType(host);
//                LeCloudPlayerConfig.USE_CDE_PORT = true;
                LeCloudPlayerConfig.setmInitCmfListener(new OnInitCmfListener() {

                    @Override
                    public void onCdeStartSuccess() {
                        //cde启动成功,可以开始播放
                        cdeInitSuccess = true;
                        Log.d(TAG, "onCdeStartSuccess: ");
                    }

                    @Override
                    public void onCdeStartFail() {
                        //cde启动失败,不能正常播放;如果使用remote版本则可能是remote下载失败;
                        //如果使用普通版本,则可能是so文件加载失败导致
                        cdeInitSuccess = false;
                        Log.d(TAG, "onCdeStartFail: ");
                    }

                    @Override
                    public void onCmfCoreInitSuccess() {
                        //不包含cde的播放框架需要处理
                    }

                    @Override
                    public void onCmfCoreInitFail() {
                        //不包含cde的播放框架需要处理
                    }

                    @Override
                    public void onCmfDisconnected() {
                        //cde服务断开,会导致播放失败,重启一次服务
                        try {
                            cdeInitSuccess = false;
                            LeCloudPlayerConfig.init(App.getAppContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                LeCloudPlayerConfig.init(App.getAppContext());

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG,"异常"+e.getCause());
                e.printStackTrace();
            }
        }
    }


    private static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return "";
    }
}


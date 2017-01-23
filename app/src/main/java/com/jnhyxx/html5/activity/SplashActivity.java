package com.jnhyxx.html5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.service.PushIntentService;
import com.jnhyxx.html5.service.PushService;
import com.johnz.kutils.AppInfo;
import com.umeng.onlineconfig.OnlineConfigAgent;

public class SplashActivity extends BaseActivity {

    private TextView mVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // init getui push
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);

        mVersionName = (TextView) findViewById(R.id.versionName);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("yybd")) {
            mVersionName.setText("V" + AppInfo.getVersionName(this));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    gotoMain();
                }
            }
        }).start();

        OnlineConfigAgent.getInstance().setDebugMode(BuildConfig.DEBUG);
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
    }

    private void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {

    }
}

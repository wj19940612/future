package com.jnhyxx.html5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.jnhyxx.html5.BuildConfig;
import com.jnhyxx.html5.R;
import com.johnz.kutils.AppInfo;
import com.umeng.onlineconfig.OnlineConfigAgent;

public class SplashActivity extends BaseActivity {

    private TextView mVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // init getui push
        PushManager.getInstance().initialize(this.getApplicationContext());

        mVersionName = (TextView) findViewById(R.id.versionName);
        mVersionName.setText(AppInfo.getVersionName(this));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mVersionName.setVisibility(View.VISIBLE);
            }
        }, 1000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally{
                    gotoMain();
                }
            }
        }).start();

        OnlineConfigAgent.getInstance().setDebugMode(BuildConfig.DEBUG);
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
    }

    private void gotoMain(){
        startActivity(new Intent(this, MainActivity.class));
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {

    }
}

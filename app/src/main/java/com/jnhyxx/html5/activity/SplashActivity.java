package com.jnhyxx.html5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.AppInfo;

public class SplashActivity extends BaseActivity {

    private TextView mVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mVersionName = (TextView) findViewById(R.id.versionName);
        mVersionName.setText(AppInfo.getVersionName());

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
    }

    private void gotoMain(){
        startActivity(new Intent(this, MainActivity.class));
        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {

    }
}

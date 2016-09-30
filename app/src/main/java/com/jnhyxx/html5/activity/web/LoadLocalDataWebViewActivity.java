package com.jnhyxx.html5.activity.web;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.jnhyxx.html5.activity.WebViewActivity;

/**
 * Created by ${wangJie} on 2016/9/30.
 */

public class LoadLocalDataWebViewActivity extends WebViewActivity{
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

      initData(getIntent());
    }
    protected void initData(Intent intent) {
    }
}

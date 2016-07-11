package com.jnhyxx.html5.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.umeng.message.PushAgent;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
    }

    protected String getTAG() {
        return this.getClass().getSimpleName();
    }

    protected Activity getActivity() {
        return this;
    }
}

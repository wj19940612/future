package com.jnhyxx.html5.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jnhyxx.html5.Preference;
import com.umeng.message.PushAgent;

public class BaseActivity extends AppCompatActivity {

    protected static String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();

        TAG = this.getClass().getSimpleName();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Preference.get().setForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preference.get().setForeground(false);
    }

    protected Activity getActivity() {
        return this;
    }
}

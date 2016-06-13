package com.jnhyxx.html5.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected String getTAG() {
        return this.getClass().getSimpleName();
    }

    protected Activity getActivity() {
        return this;
    }
}

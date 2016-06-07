package com.jnhyxx.html5;

import com.wo.main.WP_App;

public class App1 extends App {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            WP_App.on_AppInit(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

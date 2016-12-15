package com.jnhyxx.html5.activity.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.WebViewActivity;
import com.jnhyxx.html5.net.API;
import com.johnz.kutils.Launcher;

public class PaidToPromoteActivity extends WebViewActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBar().setRightText(R.string.my_users);
        getTitleBar().setRightVisible(true);
        getTitleBar().setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyUsersPage();
            }
        });
    }

    private void openMyUsersPage() {
        Launcher.with(this, WebViewActivity.class)
                .putExtra(WebViewActivity.EX_TITLE, getString(R.string.my_users))
                .putExtra(WebViewActivity.EX_URL, API.getPromoteMyUsers())
                .putExtra(WebViewActivity.EX_RAW_COOKIE, getRawCookie())
                .execute();
    }
}

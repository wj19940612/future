package com.jnhyxx.html5.activity.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.MainActivity;
import com.jnhyxx.html5.utils.NotificationUtil;
import com.johnz.kutils.Launcher;

public class MessageDialogActivity extends Activity  {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    private TextView mTitle;
    private TextView mMessage;
    private TextView mNegative;
    private TextView mPosition;

    private String mMessageType;
    private String mMessageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        scaleDialogWindowWidth(0.8);

        initView();
        processIntent(getIntent());
    }

    private void initView() {
        findViewById(R.id.doubleButtons).setVisibility(View.VISIBLE);
        findViewById(R.id.singleButton).setVisibility(View.GONE);

        mTitle = (TextView) findViewById(R.id.title);
        mMessage = (TextView) findViewById(R.id.message);
        mNegative = (TextView) findViewById(R.id.negative);
        mPosition = (TextView) findViewById(R.id.position);
        mNegative.setText(R.string.i_get_it);
        mPosition.setText(R.string.check_detail);

        mNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(MessageDialogActivity.this, MainActivity.class)
                        .putExtra(NotificationUtil.KEY_MESSAGE_ID, mMessageId)
                        .putExtra(NotificationUtil.KEY_MESSAGE_TYPE, mMessageType)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .execute();

                finish();
            }
        });
    }

    private void processIntent(Intent intent) {
        String title = intent.getStringExtra(TITLE);
        String message = intent.getStringExtra(MESSAGE);
        mTitle.setText(title);
        mMessage.setText(message);

        mMessageId = intent.getStringExtra(NotificationUtil.KEY_MESSAGE_ID);
        mMessageType = intent.getStringExtra(NotificationUtil.KEY_MESSAGE_TYPE);
    }

    private void scaleDialogWindowWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

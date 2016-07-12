package com.jnhyxx.html5.activity;

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

public class PopupDialogActivity extends Activity  {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    private TextView mTitle;
    private TextView mMessage;
    private TextView mNegative;
    private TextView mPosition;

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
        mTitle = (TextView) findViewById(R.id.title);
        mMessage = (TextView) findViewById(R.id.message);
        mNegative = (TextView) findViewById(R.id.negative);
        mPosition = (TextView) findViewById(R.id.position);

        mNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void processIntent(Intent intent) {
        String title = intent.getStringExtra(TITLE);
        String message = intent.getStringExtra(MESSAGE);
        mTitle.setText(title);
        mMessage.setText(message);

        // TODO: 7/11/16 system message and trade message
    }

    private void scaleDialogWindowWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

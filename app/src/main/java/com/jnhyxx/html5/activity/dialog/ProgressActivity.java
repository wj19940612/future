package com.jnhyxx.html5.activity.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.net.APIBase;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProgressActivity extends AppCompatActivity {

    private static final String EXTRA_ADD_TAG = "add_tag";
    private static final String EXTRA_REMOVE_TAG = "remove_tag";

    private Map<String, Integer> mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        mMap = new ConcurrentHashMap<>();

        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        String addTag = intent.getStringExtra(EXTRA_ADD_TAG);
        String removeTag = intent.getStringExtra(EXTRA_REMOVE_TAG);

        if (!TextUtils.isEmpty(addTag)) {
            Integer count = mMap.get(addTag);
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            mMap.put(addTag, count);
            Log.d("test", "processIntent: count+ = " + count);
        }

        if (!TextUtils.isEmpty(removeTag)) {
            Integer count = mMap.get(removeTag);
            if (count != null) {
                count--;
                mMap.put(removeTag, count);
                if (count == 0) {
                    mMap.remove(removeTag);
                }
            }
            Log.d("test", "processIntent: count- = " + count);

            if (mMap.isEmpty()) {
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("test", "onNewIntent: " + intent);
        processIntent(intent);
    }


    @Override
    protected void onDestroy() {
        Set<String> keySet = mMap.keySet();
        for (String tag : keySet) {
            APIBase.cancel(tag);
        }
        super.onDestroy();
    }

    public static void show(Activity activity, String tag) {
        Intent intent = new Intent(activity, ProgressActivity.class);
        intent.putExtra(EXTRA_ADD_TAG, tag);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    public static void dismiss(Activity activity, String tag) {
        Intent intent = new Intent(activity, ProgressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_REMOVE_TAG, tag);
        activity.startActivity(intent);
    }
}

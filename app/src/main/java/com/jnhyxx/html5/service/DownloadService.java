package com.jnhyxx.html5.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UpgradeUtil;
import com.johnz.kutils.FileSystem;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DownloadService extends Service {

    private static final String TAG = "HYDownload";
    private DownloadManager mDownloadManager;
    private long mDownloadId;
    private String mDownloadUri;
    private String mSavedFilePath;
    private Set<String> mUrlSet;

    private BroadcastReceiver mDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId != -1 && downloadId == mDownloadId) {
                sendDownloadCompleteBroadcast();
                Log.d(TAG, "onReceive: download complete");
                processDownloadFile(mDownloadId);
                mUrlSet.remove(mDownloadUri);
                stopSelf();
            }
        }
    };

    private void sendDownloadCompleteBroadcast() {
        Intent intent = new Intent(UpgradeUtil.ACTION_UPGRADE_COMPLETE);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUrlSet = new HashSet<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + startId);

        startDownload(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload(Intent intent) {
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        String downloadUri = intent.getStringExtra(UpgradeUtil.DOWNLOAD_URI);
        String appName = createAppName(downloadUri);
        mSavedFilePath = null;

        if (checkApkExist(appName)) {
            Log.d(TAG, "startDownload: apk exist");
            sendDownloadCompleteBroadcast();
            installApk();
            return;
        }

        if (mUrlSet.add(downloadUri)) {
            Uri uri = Uri.parse(downloadUri);
            mDownloadUri = downloadUri;

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(getString(R.string.downloading, appName));
            if (FileSystem.isExternalStorageWriteable()) {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName);
            } else {
                request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, appName);
            }
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();
            mDownloadId = mDownloadManager.enqueue(request);

            Log.d(TAG, "startDownload: " + mDownloadId);

            registerReceiver(mDownloadCompleteReceiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            ToastUtil.show(R.string.download_start);
        } else {
            ToastUtil.show(R.string.please_not_download_again);
        }
    }

    private boolean checkApkExist(String appName) {
        File file;
        if (FileSystem.isExternalStorageWriteable()) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), appName);
        } else {
            file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), appName);
        }

        if (file.exists()) {
            mSavedFilePath = file.getAbsolutePath();
            return true;
        } else {
            return false;
        }
    }

    private String createAppName(String downloadUri) {
        int lastLeftSprint = downloadUri.lastIndexOf("/");
        return downloadUri.substring(lastLeftSprint + 1, downloadUri.length());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        try {
            unregisterReceiver(mDownloadCompleteReceiver);
            //unregisterReceiver(mNotificationClickedReceiver);
        } catch (IllegalArgumentException e) {
            // throw when receiver not register
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void processDownloadFile(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = mDownloadManager.query(query);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        mSavedFilePath = cursor.getString(columnIndex);
        processDownloadStatus(status);
    }

    private void processDownloadStatus(int status) {
        switch (status) {
            case DownloadManager.STATUS_SUCCESSFUL:
                Log.i(TAG, "processDownloadStatus: successful");
                installApk();
                break;
            case DownloadManager.STATUS_FAILED:
                Log.i(TAG, "processDownloadStatus: failed");
                ToastUtil.show(R.string.download_failure);
                stopSelf();
                break;
            case DownloadManager.STATUS_PENDING:
                Log.i(TAG, "processDownloadStatus: pending");
                break;
            case DownloadManager.STATUS_PAUSED:
                Log.i(TAG, "processDownloadStatus: pause");
                break;
            case DownloadManager.STATUS_RUNNING:
                Log.i(TAG, "processDownloadStatus: running");
                break;
        }
    }

    private void installApk() {
        if (!TextUtils.isEmpty(mSavedFilePath)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, "installApk: " + mSavedFilePath);
            intent.setDataAndType(Uri.fromFile(new File(mSavedFilePath)),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }
}

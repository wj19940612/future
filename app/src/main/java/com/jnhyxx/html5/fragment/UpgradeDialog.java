package com.jnhyxx.html5.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.service.DownloadService;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.utils.UpgradeUtil;
import com.johnz.kutils.FileSystem;

public class UpgradeDialog extends AppCompatDialogFragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private TextView mUpgradeLog;
    private TextView mDownloadInstall;
    private TextView mUpgradeLater;

    private boolean mForceUpgrade;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isResumed()) {
                mDownloadInstall.setText(R.string.download_complete);
            }
        }
    };

    public static UpgradeDialog newInstance(boolean forceUpgrade) {
        UpgradeDialog frag = new UpgradeDialog();
        Bundle args = new Bundle();
        args.putBoolean(UpgradeUtil.FORCE_UPDATE, forceUpgrade);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mForceUpgrade = getArguments().getBoolean(UpgradeUtil.FORCE_UPDATE);
        }
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mReceiver, new IntentFilter(UpgradeUtil.ACTION_UPGRADE_COMPLETE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(getActivity())
                    .unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upgrade_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUpgradeLog = (TextView) view.findViewById(R.id.upgradeLog);
        mDownloadInstall = (TextView) view.findViewById(R.id.downloadInstall);
        mUpgradeLater = (TextView) view.findViewById(R.id.upgradeLater);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scaleDialogWindowWidth(0.90);
        setDialogCancelable(mForceUpgrade);
        mUpgradeLater.setVisibility(mForceUpgrade? View.INVISIBLE: View.VISIBLE);

        String upgradeLog = new StringBuilder(getString(R.string.app_name))
                .append(" ").append(UpgradeUtil.getVersionCode(getActivity())).append(":\n\n")
                .append(UpgradeUtil.getUpdateLog(getActivity())).toString();
        mUpgradeLog.setText(upgradeLog);
        mUpgradeLog.setMovementMethod(new ScrollingMovementMethod());

        mDownloadInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStoragePermissionGranted()) return;
                downloadInstall();
            }
        });
        mUpgradeLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private boolean isStoragePermissionGranted() {
        return FileSystem.isStoragePermissionGranted(getActivity(), FileSystem.REQ_CODE_ASK_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FileSystem.REQ_CODE_ASK_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mDownloadInstall.performLongClick();
                } else {
                    ToastUtil.show(R.string.download_stop);
                }
            }
        }
    }

    private void downloadInstall() {
        String downloadUri = UpgradeUtil.getDownloadURI(getActivity());
        if (TextUtils.isEmpty(downloadUri)) {
            ToastUtil.show(R.string.download_failure);
            return;
        }

        Intent intent = new Intent(getActivity(), DownloadService.class);
        intent.putExtra(UpgradeUtil.DOWNLOAD_URI, downloadUri);
        getActivity().startService(intent);

        mDownloadInstall.setText(R.string.button_downloading);
    }

    private void setDialogCancelable(boolean forceUpgrade) {
        boolean cancelable = !forceUpgrade;
        getDialog().setCancelable(cancelable);
        getDialog().setCanceledOnTouchOutside(cancelable);
    }

    private void scaleDialogWindowWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDialog().getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}

package com.jnhyxx.html5.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.UpgradeUtil;

public class UpgradeDialog extends AppCompatDialogFragment {

    private TextView mUpgradeLog;
    private TextView mDownloadInstall;
    private TextView mUpgradeLater;

    private boolean mForceUpgrade;

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

        String upgradeLog = new StringBuilder(getString(R.string.app_name))
                .append(" ").append(UpgradeUtil.getVersionCode(getActivity())).append(":\n\n")
                .append(UpgradeUtil.getUpdateLog(getActivity())).toString();
        mUpgradeLog.setText(upgradeLog);
        mDownloadInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void downloadInstall() {

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

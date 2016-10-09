package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.johnz.kutils.AppInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {
    private static final String TAG = "AboutUsActivity";
    private static final String SERVICE_QQ = "3088152027";
    @BindView(R.id.versionName)
    TextView mTVVersionName;
    //公司热线
    @BindView(R.id.companyTelephone)
    RelativeLayout mRlCompanyTelephone;
    //记录被点击的item;
    private int selectPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        String versionName = AppInfo.getVersionName(getApplicationContext());
        mTVVersionName.setText(getString(R.string.account_about_us_app_version, getString(R.string.app_name), versionName));
    }

    @OnClick({R.id.companyInfo, R.id.managerTeam, R.id.companyCulture, R.id.collaborateCase, R.id.companyTelephone, R.id.serviceQq})
    public void onClick(View view) {
        switch (view.getId()) {
            //公司信息
            case R.id.companyInfo:
                changeViewStatus(R.id.companyInfo);
                break;
            //管理团队
            case R.id.managerTeam:
                changeViewStatus(R.id.managerTeam);
                break;
            //企业文化
            case R.id.companyCulture:
                changeViewStatus(R.id.companyCulture);
                break;
            //合作案例
            case R.id.collaborateCase:
                changeViewStatus(R.id.collaborateCase);
                break;
            //公司热线
            case R.id.companyTelephone:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.account_about_us_company_telephone_number)));
                startActivity(intent);
                break;
            case R.id.serviceQq:
                String serviceQQUrl = "mqqwpa://im/chat?chat_type=wpa&uin=" + SERVICE_QQ + "&version=1";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(serviceQQUrl)));
                break;

        }
    }

    /**
     * 传入的view的id
     *
     * @param viewId
     */
    private void changeViewStatus(int viewId) {
        boolean tv_companyInfoStatus;
        boolean tvManagerTeamStatus;
        switch (viewId) {
            case R.id.companyTelephone:
                break;
        }
    }
}


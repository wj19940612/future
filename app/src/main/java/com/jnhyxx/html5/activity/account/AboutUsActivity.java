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
    private static final String SERVIVE_QQ = "3088152027";
    @BindView(R.id.activityAboutUsTvVersionName)
    TextView mTVVersionName;
    //    //公司简介
//    @BindView(R.id.activity_about_us_rl_company_info)
//    RelativeLayout rl_companyInfo;
//    @BindView(R.id.activity_about_us_tv_company_info)
//    TextView tv_companyInfo;
//    //管理团队
//    @BindView(R.id.activity_about_us_rl_manager_team)
//    RelativeLayout rl_manager_team;
//    @BindView(R.id.activity_about_us_tv_manager_team)
//    TextView tv_manager_team;
//    //企业文化
//    @BindView(R.id.activity_about_us_rl_company_culture)
//    RelativeLayout rl_company_cultrue;
//    @BindView(R.id.activity_about_us_tv_company_culture)
//    TextView tv_company_cultrue;
//    //合作案例
//    @BindView(R.id.activity_about_us_rl_collaborate_case)
//    RelativeLayout rl_collaborate_case;
//    @BindView(R.id.activity_about_us_tv_collaborate_case)
//    TextView tv_collaborate_case;
    //公司热线
    @BindView(R.id.activityAboutUsRCompanyTelephone)
    RelativeLayout mRlCompanyTelphone;
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

    @OnClick({R.id.activityAboutUsRlCompanyInfo, R.id.activityAboutUsRlManagerTeam, R.id.activityAboutUsRlCompanyCulture, R.id.activityAboutUsRlCollaborateCase, R.id.activityAboutUsRCompanyTelephone, R.id.activityAboutUsRlServiceQq})
    public void onClick(View view) {
        switch (view.getId()) {
            //公司信息
            case R.id.activityAboutUsRlCompanyInfo:
                changeViewStatus(R.id.activityAboutUsRlCompanyInfo);
                break;
            //管理团队
            case R.id.activityAboutUsRlManagerTeam:
                changeViewStatus(R.id.activityAboutUsRlManagerTeam);
                break;
            //企业文化
            case R.id.activityAboutUsRlCompanyCulture:
                changeViewStatus(R.id.activityAboutUsRlCompanyCulture);
                break;
            //合作案例
            case R.id.activityAboutUsRlCollaborateCase:
                changeViewStatus(R.id.activityAboutUsRlCollaborateCase);
                break;
            //公司热线
            case R.id.activityAboutUsRCompanyTelephone:
                // TODO: 2016/8/24  
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.account_about_us_company_telephone_number)));
                startActivity(intent);
                break;
            case R.id.activityAboutUsRlServiceQq:
                String serviceQQUrl = "mqqwpa://im/chat?chat_type=wpa&uin=" + SERVIVE_QQ + "&version=1";
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
            case R.id.activityAboutUsRCompanyTelephone:
                break;
        }
    }
}


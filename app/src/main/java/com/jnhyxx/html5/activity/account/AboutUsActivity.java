package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.ExpandableLayout;
import com.johnz.kutils.AppInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.versionName)
    TextView mVersionName;
    //公司热线
    @BindView(R.id.companyTelephone)
    RelativeLayout mRlCompanyTelephone;
    @BindView(R.id.serViceQQ)
    TextView mSerViceQQ;
    @BindView(R.id.servicePhone)
    TextView mServicePhone;
    @BindView(R.id.companyInfo)
    ExpandableLayout mCompanyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initData();

        String servicePhone = Preference.get().getServicePhone();
        servicePhone = servicePhone.substring(0, 3) + "-" + servicePhone.substring(3, 7) + "-" + servicePhone.substring(7, servicePhone.length());
        mServicePhone.setText(servicePhone);
        mSerViceQQ.setText(Preference.get().getServiceQQ());
    }

    private void initData() {
        String versionName = AppInfo.getVersionName(getApplicationContext());
        mVersionName.setText(getString(R.string.account_about_us_app_version, getString(R.string.app_name), versionName));
        String appName = getString(R.string.app_name);
        mCompanyInfo.setBottomTxt(getString(R.string.account_about_us_company_info_child, appName));
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
                String servicePhone = Preference.get().getServicePhone().replaceAll("-", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + servicePhone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.serviceQq:
                String serviceQQUrl = API.getServiceQQ(Preference.get().getServiceQQ());
                Intent intentQQ = new Intent(Intent.ACTION_VIEW, Uri.parse(serviceQQUrl));
                if (intentQQ.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentQQ);
                } else {
                    ToastUtil.show(R.string.install_qq_first);
                }
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


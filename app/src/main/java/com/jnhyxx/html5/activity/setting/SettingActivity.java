package com.jnhyxx.html5.activity.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.account.model.LocalCacheUserInfoManager;
import com.jnhyxx.html5.activity.account.model.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.rlUserNameSetting)
    RelativeLayout rlUserNameSetting;
    @BindView(R.id.rlRealNameSetting)
    RelativeLayout rlRealNameSetting;
    @BindView(R.id.rlBindBankCardSetting)
    RelativeLayout rlBindBankCardSetting;

    @BindView(R.id.tvUserNameSetting)
    TextView tvUserNameSetting;
    @BindView(R.id.tvRealNameSetting)
    TextView tvRealNameSetting;
    @BindView(R.id.tvBindBankCardSetting)
    TextView tvBindBankCardSetting;
    @BindView(R.id.tvUserphoneSetting)
    TextView tvUserphoneSetting;
    @BindView(R.id.tvLoginOutSetting)
    TextView loginOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        UserInfo userInfo = LocalCacheUserInfoManager.getInstance().getUser();
        if (!TextUtils.isEmpty(userInfo.getUserName())) {
            tvUserNameSetting.setText(userInfo.getUserName());
        }
        if (!TextUtils.isEmpty(userInfo.getUserPhone())) {
            String userPhone = userInfo.getUserPhone();
            String phone = "12345678900";

        }
    }


    @OnClick({R.id.rlUserNameSetting, R.id.rlRealNameSetting, R.id.rlBindBankCardSetting, R.id.tvLoginOutSetting})
    void onClick(View view) {
        switch (view.getId()) {
            //用户名
            case R.id.rlUserNameSetting:
                break;
            case R.id.rlRealNameSetting:
                break;
            case R.id.rlBindBankCardSetting:
                break;
            case R.id.tvLoginOutSetting:
                break;
        }
    }
}

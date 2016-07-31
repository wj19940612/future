package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.BankcardAuth;
import com.jnhyxx.html5.domain.NameAuth;
import com.jnhyxx.html5.domain.ProfileSummary;
import com.jnhyxx.html5.view.IconTextRow;
import com.johnz.kutils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {

    public static final String NAME_AUTH_RESULT = "nameAuthResult";
    public static final String BANKCARD_AUTH_RESULT = "bankcardAuthResult";

    @BindView(R.id.nameAuth)
    IconTextRow mNameAuth;
    @BindView(R.id.bankcard)
    IconTextRow mBankcard;
    @BindView(R.id.phoneAuth)
    IconTextRow mPhoneAuth;

    private ProfileSummary mProfileSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        updateProfileView(getIntent());
    }

    private void updateProfileView(Intent intent) {
        mProfileSummary =
                (ProfileSummary) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
        updateProfileView(mProfileSummary);
    }

    private void updateProfileView(ProfileSummary profileSummary) {
        if (profileSummary.getUserStatus() == NameAuth.STATUS_NOT_FILLED) {
            mNameAuth.setSubText(R.string.unauthorized);
        } else if (profileSummary.getUserStatus() == NameAuth.STATUS_FILLED) {
            mNameAuth.setSubText(R.string.filled);
        } else if (profileSummary.getUserStatus() == NameAuth.STATUS_BE_BOUND) {
            mNameAuth.setSubText(R.string.authorized);
        }

        if (profileSummary.getBankStatus() == BankcardAuth.STATUS_NOT_FILLED) {
            mBankcard.setSubText(R.string.unauthorized);
        } else if (profileSummary.getBankStatus() == BankcardAuth.STATUS_FILLED) {
            mBankcard.setSubText(R.string.filled);
        } else if (profileSummary.getBankStatus() == BankcardAuth.STATUS_BE_BOUND) {
            mBankcard.setSubText(R.string.authorized);
        }

        mPhoneAuth.setSubText(profileSummary.getTele());
    }

    @OnClick({R.id.nameAuth, R.id.bankcard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nameAuth:
                Launcher.with(getActivity(), NameAuthActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mProfileSummary.createNameAuth())
                        .executeForResult(REQUEST_CODE);
                break;
            case R.id.bankcard:
                Launcher.with(getActivity(), BankcardAuthActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mProfileSummary.createBankcardAuth())
                        .executeForResult(REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            NameAuth.Result result = (NameAuth.Result) data.getSerializableExtra(NAME_AUTH_RESULT);
            if (result != null) {
                mNameAuth.setSubText(R.string.filled);
            }

            BankcardAuth bankcardAuth = (BankcardAuth) data.getSerializableExtra(BANKCARD_AUTH_RESULT);
            if (bankcardAuth != null) {
                mBankcard.setSubText(R.string.filled);
            }
        }
    }
}

package com.jnhyxx.html5.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jnhyxx.html5.Preference;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.account.RechargeActivity;
import com.jnhyxx.html5.domain.finance.SupportApplyWay;
import com.jnhyxx.html5.utils.UmengCountEventIdUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/2/7.
 */

public class SelectRechargeWayDialogFragment extends DialogFragment {

    @BindView(R.id.close)
    ImageView mClose;
    @BindView(R.id.bankCardPay)
    TextView mBankCardPay;
    @BindView(R.id.aliPayPay)
    TextView mAliPayPay;
    @BindView(R.id.weChartPay)
    TextView mWeChartPay;
    @BindView(R.id.payWayLayout)
    LinearLayout mPayWayLayout;

    private Unbinder mBind;
    private SupportApplyWay mSupportApplyWay;

    public static final int PAY_WAY_BANK = 0;
    public static final int PAY_WAY_ALIPAY = 1;
    public static final int PAY_WAY_WECHAT = 2;

    private onPayWayListener mOnPayWayListener;

    public interface onPayWayListener {
        void selectPayWay(int payWay);
    }


    public static SelectRechargeWayDialogFragment newInstance(SupportApplyWay supportApplyWay) {
        Bundle args = new Bundle();
        SelectRechargeWayDialogFragment fragment = new SelectRechargeWayDialogFragment();
        args.putSerializable("supportApplyWay", supportApplyWay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        if (getArguments() != null) {
            mSupportApplyWay = (SupportApplyWay) getArguments().getSerializable("supportApplyWay");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RechargeActivity) {
            mOnPayWayListener = (onPayWayListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement PayWayListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        updateView(mSupportApplyWay);
        choosePayWay(Preference.get().getRechargePayWay());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_select_recharge_way, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    public void show(FragmentManager manager) {
        show(manager, "SelectRechargeWayDialogFragment");
    }

    @OnClick({R.id.close, R.id.bankCardPay, R.id.aliPayPay, R.id.weChartPay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                closeDialog();
                break;
            case R.id.bankCardPay:
                choosePayWay(PAY_WAY_BANK);
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.PAY_BANK_CARD);
                mOnPayWayListener.selectPayWay(PAY_WAY_BANK);
                closeDialog();
                break;
            case R.id.aliPayPay:
                MobclickAgent.onEvent(getActivity(), UmengCountEventIdUtils.PAY_ALIPAY);
                choosePayWay(PAY_WAY_ALIPAY);
                mOnPayWayListener.selectPayWay(PAY_WAY_ALIPAY);
                closeDialog();
                break;
            case R.id.weChartPay:
                choosePayWay(PAY_WAY_WECHAT);
                mOnPayWayListener.selectPayWay(PAY_WAY_WECHAT);
                closeDialog();
                break;
        }
    }

    private void closeDialog() {
        dismissAllowingStateLoss();
    }

    public void choosePayWay(int payWay) {
        Preference.get().setRechargePayWay(payWay);
        unSelectAll();
        mPayWayLayout.getChildAt(payWay).setSelected(true);
    }

    public void unSelectAll() {
        for (int i = 0; i < mPayWayLayout.getChildCount(); i++) {
            mPayWayLayout.getChildAt(i).setSelected(false);
        }
    }

    private void updateView(SupportApplyWay supportApplyWay) {
        if (supportApplyWay.isBank()) {
            mBankCardPay.setVisibility(View.VISIBLE);
        } else {
            mBankCardPay.setVisibility(View.GONE);
        }
        if (supportApplyWay.isAlipay()) {
            mAliPayPay.setVisibility(View.VISIBLE);
        } else {
            mAliPayPay.setVisibility(View.GONE);
        }
        // TODO: 2017/2/9 暂无微信的sdk 
//        if (supportApplyWay.isWechat()) {
//            mWeChartPay.setVisibility(View.VISIBLE);
//        } else {
//            mWeChartPay.setVisibility(View.GONE);
//        }
    }
}

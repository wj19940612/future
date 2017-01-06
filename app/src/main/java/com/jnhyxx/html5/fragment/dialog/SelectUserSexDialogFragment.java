package com.jnhyxx.html5.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.view.WheelView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.wheelView;

/**
 * Created by ${wangJie} on 2017/1/6.
 * 选择性别的dialog
 */

public class SelectUserSexDialogFragment extends BaseGravityBottomDialogFragment {

    @BindView(wheelView)
    WheelView mWheelView;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.ok)
    TextView mOk;

    private Unbinder mBind;

    OnUserSexListener mOnUserSexListener;

    String sex;

    public interface OnUserSexListener {
        void onSelected(String userSex);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserSexListener) {
            mOnUserSexListener = (OnUserSexListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SelectUserSexDialogFragment.OnUserSexListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialof_fragment_selsect_sex, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWheelView.setOffset(1);
        mWheelView.setOffset(1);
        mWheelView.setItems(new String[]{"男", "女"});
        mWheelView.setSelectedItem("男");
        mWheelView.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, String item) {
                sex = item;
            }
        });
    }

    @OnClick({R.id.cancel, R.id.ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                dismiss();
                if (!TextUtils.isEmpty(sex)) {
                    LocalUser.getUser().getUserInfo().setChinaSex(sex);
                    mOnUserSexListener.onSelected(sex);
                }
                break;
        }
    }
}

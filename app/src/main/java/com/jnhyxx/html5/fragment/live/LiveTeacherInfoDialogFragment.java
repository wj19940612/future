package com.jnhyxx.html5.fragment.live;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.utils.CommonMethodUtils;
import com.jnhyxx.html5.view.CircularAnnulusImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/11/10.
 */

public class LiveTeacherInfoDialogFragment extends DialogFragment {

    @BindView(R.id.teacherHeadImage)
    CircularAnnulusImageView mTeacherHeadImage;
    @BindView(R.id.teacherName)
    TextView mTeacherName;
    @BindView(R.id.hideDialog)
    ImageView mHideDialog;
    @BindView(R.id.teacherGoodInfo)
    TextView mTeacherGoodInfo;
    @BindView(R.id.teacherResumeInfo)
    TextView mTeacherResumeInfo;


    private static final String KEY_TEACHER_INFO = "TEACHER_INFO";

    private LiveMessage.TeacherInfo mTeacherInfo;
    private Unbinder mBind;
    private View mDialogView;

    public static LiveTeacherInfoDialogFragment newInstance(LiveMessage.TeacherInfo teacherInfo) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_TEACHER_INFO, teacherInfo);
        LiveTeacherInfoDialogFragment fragment = new LiveTeacherInfoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTeacherInfo = (LiveMessage.TeacherInfo) getArguments().getSerializable(KEY_TEACHER_INFO);
        }
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDialogView = inflater.inflate(R.layout.fragment_live_teacher_info, container, false);
        mBind = ButterKnife.bind(this, mDialogView);
        return mDialogView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        int screenHeight = CommonMethodUtils.getScreenHeight(getActivity());
        int screenWidth = CommonMethodUtils.getScreenWidth(getActivity());

        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout((int) (screenWidth * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initData() {
        if (!TextUtils.isEmpty(mTeacherInfo.getPictureUrl())) {
            Picasso.with(getActivity()).load(mTeacherInfo.getPictureUrl()).into(mTeacherHeadImage);
        }
        mTeacherName.setText(mTeacherInfo.getName());
        mTeacherGoodInfo.setText(mTeacherInfo.getGoodAt());
        mTeacherResumeInfo.setText(mTeacherInfo.getIntroduction());
        mHideDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog().isShowing()){
                    dismiss();
                }
            }
        });
    }


    public void show(FragmentManager manager) {
        show(manager, LiveTeacherInfoDialogFragment.class.getSimpleName());
    }
}

package com.jnhyxx.html5.fragment.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LiveTeacherInfoFragment extends DialogFragment {

    private static final String KEY_TEACHER_INFO = "teacher_info";

    @BindView(R.id.teacherHead)
    ImageView mTeacherHead;
    @BindView(R.id.teacherName)
    TextView mTeacherName;
    @BindView(R.id.hideDialog)
    ImageView mHideDialog;
    @BindView(R.id.teacherGoodInfo)
    TextView mTeacherGoodInfo;
    @BindView(R.id.teacherResumeInfo)
    TextView mTeacherResumeInfo;

    private LiveMessage.TeacherInfo mTeacherInfo;
    private Unbinder mBind;

    public static LiveTeacherInfoFragment newInstance(LiveMessage.TeacherInfo teacherInfo) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_TEACHER_INFO, teacherInfo);
        LiveTeacherInfoFragment fragment = new LiveTeacherInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_live_teacher_info, container, false);
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
        scaleDialogWindowWidth(0.85);

        initData();
    }

    private void initData() {
        if (TextUtils.isEmpty(mTeacherInfo.getPictureUrl())) {
            Picasso.with(getActivity()).load(R.drawable.ic_live_pic_head)
                    .transform(new CircleTransform()).into(mTeacherHead);
        } else {
            Picasso.with(getActivity()).load(mTeacherInfo.getPictureUrl())
                    .transform(new CircleTransform()).into(mTeacherHead);
        }

        mTeacherName.setText(mTeacherInfo.getName());
        mTeacherGoodInfo.setText(mTeacherInfo.getGoodAt());
        mTeacherResumeInfo.setText(mTeacherInfo.getIntroduction());
        mHideDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog().isShowing()) {
                    dismiss();
                }
            }
        });
    }


    public void show(FragmentManager manager) {
        show(manager, LiveTeacherInfoFragment.class.getSimpleName());
    }

    private void scaleDialogWindowWidth(double scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDialog().getWindow().setLayout((int) (displayMetrics.widthPixels * scale),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

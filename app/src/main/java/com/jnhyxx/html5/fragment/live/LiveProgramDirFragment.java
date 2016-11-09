package com.jnhyxx.html5.fragment.live;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.view.CircularAnnulusImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/11/9.
 */

public class LiveProgramDirFragment extends BaseFragment {


    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.emptyLayout)
    TextView mEmptyLayout;

    private Unbinder mBind;
    private static final String KEY_LIVE_MESSAGE = "LIVE_MESSAGE";

    private LiveMessage mLiveMessage;
    private boolean mIsOpen;


    private FragmentStatusListener mFragmentStatusListener;

    public interface FragmentStatusListener {
        void hideFragment();
    }

    public void setFragmentStatusListener(FragmentStatusListener fragmentStatusListener) {
        mFragmentStatusListener = fragmentStatusListener;
    }


    public static LiveProgramDirFragment newInstance(LiveMessage liveMessage) {

        LiveProgramDirFragment fragment = new LiveProgramDirFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_LIVE_MESSAGE, liveMessage);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLiveMessage = (LiveMessage) getArguments().getSerializable(KEY_LIVE_MESSAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_window_live_program, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LiveProgramDirAdapter liveProgramDirAdapter = new LiveProgramDirAdapter(getActivity());
        liveProgramDirAdapter.addAll(mLiveMessage.getProgram());
        mListView.setAdapter(liveProgramDirAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mFragmentStatusListener != null) {
            mFragmentStatusListener.hideFragment();
        }
        mFragmentStatusListener = null;
    }

    @OnClick(R.id.emptyLayout)
    public void onClick() {
        mFragmentStatusListener.hideFragment();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (mIsOpen) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_top);
        } else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top);
        }
    }

    public void setFragmentStatus(boolean isOpen) {
        this.mIsOpen = isOpen;
    }

    static class LiveProgramDirAdapter extends ArrayAdapter<LiveMessage.ProgramInfo> {

        Context mContext;

        public LiveProgramDirAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LiveProgramDirAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_popup_window_live_program, null);
                viewHolder = new LiveProgramDirAdapter.ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (LiveProgramDirAdapter.ViewHolder) convertView.getTag();
            }
            viewHolder.bindData(getItem(position), mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.managerHeadImage)
            CircularAnnulusImageView mManagerHeadImage;
            @BindView(R.id.teacherName)
            TextView mTeacherName;
            @BindView(R.id.timeHint)
            TextView mTimeHint;
            @BindView(R.id.liveTime)
            TextView mLiveTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindData(LiveMessage.ProgramInfo programInfo, Context context) {
                if (programInfo != null) {
                    String pictureUrl = programInfo.getPictureUrl();
                    if (!TextUtils.isEmpty(pictureUrl)) {
                        Picasso.with(context).load(pictureUrl).error(R.drawable.ic_live_pic_head).into(mManagerHeadImage);
                    }
                    mLiveTime.setText(programInfo.getLiveTime());
                    mTimeHint.setText(programInfo.getCycleStr());
                    mTeacherName.setText(programInfo.getTeacherName());
                }
            }
        }
    }
}

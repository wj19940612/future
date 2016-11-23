package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.jnhyxx.html5.utils.BlurEngine;
import com.jnhyxx.html5.utils.transform.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveProgrammeList {

    private PopupWindow mPopupWindow;
    private LiveProgramAdapter mProgramAdapter;
    private BlurEngine mBlurEngine;

    public LiveProgrammeList(Context context, ViewGroup backgroundContainer) {
        initPopupWindow(context);
        if (backgroundContainer != null) {
            mBlurEngine = new BlurEngine(backgroundContainer, R.color.blackSeventyPercent, true);
        }
    }

    public void show(View anchor) {
        if (mPopupWindow != null) {
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAsDropDown(anchor);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mBlurEngine.onDestroyView();
                    }
                });
                mBlurEngine.onResume();
            } else {
                mPopupWindow.dismiss();
            }
        }
    }

    private void initPopupWindow(Context context) {
        ListView popupView = (ListView) LayoutInflater.from(context).inflate(R.layout.popup_window_live_programme, null);
        mProgramAdapter = new LiveProgramAdapter(context);
        popupView.setAdapter(mProgramAdapter);
        mPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.blackHalfTransparent)));
        mPopupWindow.setClippingEnabled(true);
    }

    public void setProgramme(List<LiveMessage.ProgramInfo> program) {
        if (mProgramAdapter != null && program != null) {
            mProgramAdapter.clear();
            mProgramAdapter.addAll(program);
        }
    }

    static class LiveProgramAdapter extends ArrayAdapter<LiveMessage.ProgramInfo> {

        public LiveProgramAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_popup_window_live_program, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindData(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.teacherHead)
            ImageView mTeacherHead;
            @BindView(R.id.teacherName)
            TextView mTeacherName;
            @BindView(R.id.workday)
            TextView mWorkday;
            @BindView(R.id.liveTime)
            TextView mLiveTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindData(LiveMessage.ProgramInfo programInfo, Context context) {
                    String pictureUrl = programInfo.getPictureUrl();
                    if (TextUtils.isEmpty(pictureUrl)) {
                        Picasso.with(context).load(R.drawable.ic_live_pic_head)
                                .transform(new CircleTransform())
                                .into(mTeacherHead);
                    } else {
                        Picasso.with(context).load(pictureUrl)
                                .transform(new CircleTransform())
                                .error(R.drawable.ic_live_pic_head)
                                .into(mTeacherHead);
                    }
                    String liveTime;
                    if (programInfo.getLiveTime().contains(",")) {
                        liveTime = programInfo.getLiveTime().replaceAll(",", "\n");
                    } else {
                        liveTime = programInfo.getLiveTime();
                    }
                    mLiveTime.setText(liveTime);
                    // TODO: 2016/11/11 目前先写死为'工作日'，由于原始数据过长，后期优化
                    mWorkday.setText("工作日");
                    mTeacherName.setText(programInfo.getTeacherName());
            }
        }

    }
}

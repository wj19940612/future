package com.jnhyxx.html5.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.live.LiveMessage;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ${wangJie} on 2016/11/9.
 * 节目单的popupWindow
 */

public class LiveProgramDir {

    private static Context sContext;
    private static WeakReference<Context> sWeakReference;

    private static PopupWindow sPopupWindow;

    public static void showLiveProgramDirPopupWindow(Context context, List<LiveMessage.ProgramInfo> programInfoArrayList, View dropDownView) {
        sWeakReference = new WeakReference<Context>(context);
        View popupView = LayoutInflater.from(sWeakReference.get()).inflate(R.layout.popup_window_live_program, null);
        initPopupWindow(popupView, dropDownView);
        TextView mEmptyLayout = (TextView) popupView.findViewById(R.id.emptyLayout);
        mEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sPopupWindow != null) {
                    sPopupWindow.dismiss();
                }
            }
        });
        ListView listView = (ListView) popupView.findViewById(R.id.listView);
        listView.setDivider(null);
        LiveProgramDirAdapter liveProgramDirAdapter = new LiveProgramDirAdapter(sWeakReference.get());
        liveProgramDirAdapter.addAll(programInfoArrayList);
        listView.setAdapter(liveProgramDirAdapter);
    }

    private static void initPopupWindow(View popupView, View dropDownView) {
        sPopupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        sPopupWindow.setOutsideTouchable(false);
        sPopupWindow.setFocusable(true);
        sPopupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(sWeakReference.get(), R.color.bg_live_program_popupwindow)));
        sPopupWindow.setClippingEnabled(true);
        sPopupWindow.showAsDropDown(dropDownView);
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_popup_window_live_program, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
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

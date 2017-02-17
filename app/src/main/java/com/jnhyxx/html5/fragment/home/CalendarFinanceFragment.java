package com.jnhyxx.html5.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.msg.CalendarFinanceModel;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.WeekCalendarLayout;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.StrUtil;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.status;

/**
 * Created by ${wangJie} on 2017/2/16.
 */

public class CalendarFinanceFragment extends BaseFragment implements WeekCalendarLayout.OnWeekSelectListener {

    @BindView(R.id.calendarWeek)
    WeekCalendarLayout mCalendarWeek;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Unbinder mBind;
    private CalendarFinanceAdapter mCalendarFinanceAdapter;

    public static CalendarFinanceFragment newInstance() {
        CalendarFinanceFragment fragment = new CalendarFinanceFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_finance, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCalendarWeek.setOnWeekSelectListener(this);
        mListView.setEmptyView(mEmpty);

        Calendar instance = Calendar.getInstance();
        int dayOfYear = Calendar.DAY_OF_YEAR;
        Log.d(TAG, "日期 " + DateUtil.format(dayOfYear, "yyyy-MM-dd"));

        getCalendarFinanceData();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCalendarFinanceAdapter.clear();
                getCalendarFinanceData();
            }
        });
    }

    private void getCalendarFinanceData() {
        API.Message.findNewsByUrl(API.getCalendarFinanceUrl("2017-02-17"))
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2<Resp<Object>, Object>() {

                    @Override
                    public void onRespSuccess(Object object) {
                        Log.d(TAG, "财经日历 " + object.toString());
                        stopRefreshAnimation();
                        CalendarFinanceModel calendarFinanceModel = new Gson().fromJson(object.toString().replaceAll("\\\"", "\""), CalendarFinanceModel.class);
                        Log.d(TAG, "转换后的参数" + calendarFinanceModel.toString());
                        updateCalendarFinanceData(calendarFinanceModel);
                        for (CalendarFinanceModel.EconomicCalendarsBean data : calendarFinanceModel.getEconomicCalendars()) {
                            Log.d(TAG, "EconomicCalendarsBean  " + data.toString());
                        }
                        for (CalendarFinanceModel.ImportThingsBean data : calendarFinanceModel.getImportThings()) {
                            Log.d(TAG, "ImportThingsBean  " + data.toString());
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                })
                .fire();
    }

    private void updateCalendarFinanceData(CalendarFinanceModel calendarFinanceModel) {
        if (calendarFinanceModel == null) return;
        if (mCalendarFinanceAdapter == null) {
            mCalendarFinanceAdapter = new CalendarFinanceAdapter(getActivity());
            mListView.setAdapter(mCalendarFinanceAdapter);
        }
        mCalendarFinanceAdapter.addAll(calendarFinanceModel.getEconomicCalendars());
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onWeekSelected(int index, String week) {
        ToastUtil.curt("所选择的日期" + index + " 星期 " + week);
    }


    static class CalendarFinanceAdapter extends ArrayAdapter<CalendarFinanceModel.EconomicCalendarsBean> {

        Context mContext;

        public CalendarFinanceAdapter(Context context) {
            super(context, 0);
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_calendar_finance, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.countryBanner)
            ImageView mCountryBanner;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.star)
            ImageView mStar;
            @BindView(status)
            TextView mStatus;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.beforeData)
            TextView mBeforeData;
            @BindView(R.id.expectData)
            TextView mExpectData;
            @BindView(R.id.publishData)
            TextView mPublishData;
            @BindView(R.id.reviseBefore)
            TextView mReviseBefore;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(CalendarFinanceModel.EconomicCalendarsBean item, Context context) {
                Log.d("wangjieTest", "财经日历 " + item.toString());
                String organizeMarkUrl = API.Message.getOrganizeMarkUrl(item.getState());
                if (!TextUtils.isEmpty(organizeMarkUrl)) {
                    Picasso.with(context).load(organizeMarkUrl).into(mCountryBanner);
                }
                mTime.setText(DateUtil.format(item.getTime(), "yyyy-MM-dd HH:mm", "HH-mm"));

                handleStatus(item, context);

                String title = item.getState() + item.getTitle();
                mTitle.setText(title);

                mBeforeData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.before_data_), item.getBefore(), R.color.blackPrimary));
                mExpectData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.expect_data_), item.getForecast(), R.color.blackPrimary));
                mPublishData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.real_data_), item.getReality(), R.color.blackPrimary));
            }

            /**
             * 处理利空和利多消息
             *
             * @param item
             * @param context
             */
            private void handleStatus(CalendarFinanceModel.EconomicCalendarsBean item, Context context) {
                if (TextUtils.isEmpty(item.getEffect())) {
                    mStatus.setText(R.string.not_publish);
                    mStatus.setTextColor(ContextCompat.getColor(context, R.color.colorDisable));
                } else {
                    if (item.isLido()) {
                        mStatus.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mStatus.setText(context.getString(R.string.lido, item.getEffect().replaceAll("|", "")));
                    } else {
                        String[] split = item.getEffect().split("|");
                        if (split.length > 1) {
                            String liDuo = context.getString(R.string.lido, split[0]);
                            String badNews = context.getString(R.string.lido, split[1]);
                            SpannableString spannableString = StrUtil.mergeTextWithColor(liDuo, badNews, R.color.redPrimary, R.color.greenPrimary);
                            mStatus.setText(spannableString);
                        }
                    }
                }
            }
        }
    }
}

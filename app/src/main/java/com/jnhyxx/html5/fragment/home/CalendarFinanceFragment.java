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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.msg.CalendarFinanceModel;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.WeekCalendarLayout;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.StrUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.id.star;
import static com.jnhyxx.html5.R.id.status;
import static com.jnhyxx.html5.R.string.lido;

/**
 * Created by ${wangJie} on 2017/2/16.
 */

public class CalendarFinanceFragment extends BaseFragment implements WeekCalendarLayout.OnWeekSelectListener, AbsListView.OnScrollListener {

    @BindView(R.id.calendarWeek)
    WeekCalendarLayout mCalendarWeek;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * 所要查看财经日历数据的请求时间  默认为当天
     */
    private String mTime = DateUtil.format(System.currentTimeMillis(), "yyyy-MM-dd");

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
        mEmpty.setText(R.string.coming_soon);
        mListView.setEmptyView(mEmpty);
        mListView.setOnScrollListener(this);

        if (mCalendarFinanceAdapter == null) {
            mCalendarFinanceAdapter = new CalendarFinanceAdapter(getActivity());
            mListView.setAdapter(mCalendarFinanceAdapter);
        }

        getCalendarFinanceData(mTime);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCalendarFinanceData(mTime);
            }
        });
    }

    private void getCalendarFinanceData(String time) {
        API.Message.findNewsByUrl(API.getCalendarFinanceUrl(time))
                .setTag(TAG)
                .setCallback(new Callback2<Resp<Object>, Object>() {

                    @Override
                    public void onRespSuccess(Object object) {
                        stopRefreshAnimation();
                        try {
                            CalendarFinanceModel calendarFinanceModel = new Gson().fromJson(object.toString().replaceAll("\\\"", "\""), CalendarFinanceModel.class);
                            updateCalendarFinanceData(calendarFinanceModel);
                            for (CalendarFinanceModel.EconomicCalendarsBean data : calendarFinanceModel.getEconomicCalendars()) {
                                Log.d(TAG, "EconomicCalendarsBean  " + data.toString());
                            }
                        } catch (JsonSyntaxException e) {

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
        if (calendarFinanceModel != null && !calendarFinanceModel.getEconomicCalendars().isEmpty()) {
            mCalendarFinanceAdapter.addAll(calendarFinanceModel.getEconomicCalendars());
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onWeekSelected(int index, String week, String dayTime) {
        if (mCalendarFinanceAdapter != null) {
            mCalendarFinanceAdapter.clear();
        }
        mTime = dayTime;
        getCalendarFinanceData(dayTime);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }


    static class CalendarFinanceAdapter extends ArrayAdapter<CalendarFinanceModel.EconomicCalendarsBean> {

        private static final String LOW = "低";
        private static final String MIDDLE = "中";
        private static final String TALL = "高";
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
            @BindView(star)
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
                String organizeMarkUrl = API.Message.getCalendarFinanceCountryBanner(item.getState());
                if (!TextUtils.isEmpty(organizeMarkUrl)) {
                    Picasso.with(context).load(organizeMarkUrl).into(mCountryBanner);
                }
                mTime.setText(item.getPredicttime());

                handleStatus(item, context);

                String title = item.getState() + item.getTitle();
                mTitle.setText(title);
                int textColor = ContextCompat.getColor(context, R.color.blackPrimary);
                mBeforeData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.before_data_), "  " + item.getBefore(), textColor));
                mExpectData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.expect_data_), "  " + item.getForecast(), textColor));
                mPublishData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.real_data_), "  " + item.getReality(), textColor));

                switch (item.getImportance()) {
                    case LOW:
                        mStar.setImageResource(R.drawable.ic_star_general);
                        break;
                    case MIDDLE:
                        mStar.setImageResource(R.drawable.ic_star_important);
                        break;
                    case TALL:
                        mStar.setImageResource(R.drawable.ic_star_very_important);
                        break;
                    default:
                        mStar.setImageResource(R.drawable.ic_star_important);
                        break;
                }
            }

            /**
             * 处理利空和利多消息
             *
             * @param item
             * @param context
             */
            private void handleStatus(CalendarFinanceModel.EconomicCalendarsBean item, Context context) {
                if (item.getEffecttype() == CalendarFinanceModel.TYPE_HAS_MORE_STATUS) {
                    String effect = item.getEffect();
                    if (effect.startsWith("|") && effect.endsWith("|")) {
                        mStatus.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                        mStatus.setText(context.getString(R.string.bad_news, effect.substring(1, effect.length() - 1)));
                    } else {
                        String substring = effect.substring(effect.indexOf("|"));
                        mStatus.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mStatus.setText(StrUtil.mergeTextWithColor(context.getString(R.string.lido, effect.substring(0, effect.length() - substring.length())), "  " + context.getString(R.string.bad_news, substring.substring(1, substring.length() - 1)), ContextCompat.getColor(context, R.color.greenPrimary)));
                    }

                } else if (item.getEffecttype() == CalendarFinanceModel.TYPE_LIDO) {
                    if (item.getEffect().contains("||")) {
                        mStatus.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mStatus.setText(context.getString(lido, item.getEffect().replace("||", "")));
                    } else {
                        String effect = item.getEffect();
                        if (effect.length() > 5) {
                            /**
                             * 利多消息
                             */
                            String lido = effect.substring(0, 5);
                            /**
                             * 利空消息
                             */
                            mStatus.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            String badNews = effect.substring(lido.length() + 1, effect.length() - 1);
                            SpannableString spannableString = StrUtil.mergeTextWithColor(context.getString(R.string.lido, lido), "  " + context.getString(R.string.bad_news, badNews), ContextCompat.getColor(context, R.color.greenPrimary));
                            mStatus.setText(spannableString);
                        } else {
                            mStatus.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
                            mStatus.setText(item.getEffect());
                        }
                    }
                } else if (item.getEffecttype() == CalendarFinanceModel.TYPE_EMPTY_NEWS) {
                    mStatus.setTextColor(ContextCompat.getColor(context, R.color.colorDisable));
                    mStatus.setText(R.string.not_publish);
                }
            }
        }
    }
}

package com.jnhyxx.html5.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
import com.johnz.kutils.ImageUtil;
import com.johnz.kutils.StrUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jnhyxx.html5.R.string.lido;


/**
 * Created by ${wangJie} on 2017/2/16.
 */

public class CalendarFinanceFragment extends BaseFragment implements WeekCalendarLayout.OnWeekSelectListener, AbsListView.OnScrollListener {

    @BindView(R.id.calendarWeek)
    WeekCalendarLayout mCalendarWeek;
    //    @BindView(R.id.listView)
//    ListView mListView;
    @BindView(android.R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

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
        getCalendarFinanceData(mTime);
    }

    private void getCalendarFinanceData(String time) {
        /**
         * 修改超时时间为5秒
         */
        API.Message.findNewsByUrl(API.getCalendarFinanceUrl(time))
                .setTag(TAG)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setCallback(new Callback2<Resp<Object>, Object>() {

                    @Override
                    public void onRespSuccess(Object object) {
                        stopRefreshAnimation();
                        try {
                            CalendarFinanceModel calendarFinanceModel = new Gson().fromJson(object.toString().replaceAll("\\\"", "\""), CalendarFinanceModel.class);
                            updateCalendarFinanceData(calendarFinanceModel);
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
        CalendarFinanceRecycleViewAdapter calendarFinanceRecycleViewAdapter = new CalendarFinanceRecycleViewAdapter(getActivity(), calendarFinanceModel.getEconomicCalendars());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(calendarFinanceRecycleViewAdapter);
    }



    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onWeekSelected(int index, String week, String dayTime) {
        mTime = dayTime;
        getCalendarFinanceData(dayTime);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        int topRowVerticalPosition =
//                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
//        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }


    static class CalendarFinanceAdapter extends ArrayAdapter<CalendarFinanceModel.EconomicCalendarsBean> {
        //
//        private static final String LOW = "低";
//        private static final String MIDDLE = "中";
//        private static final String TALL = "高";
        Context mContext;

        public CalendarFinanceAdapter(Context context) {
            super(context, 0);
            mContext = context;
        }
//
//        @NonNull
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_calendar_finance, null);
//                viewHolder = new ViewHolder(convertView);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.bindDataWithView(getItem(position), mContext);
//            return convertView;
//        }
//
//        static class ViewHolder {
//            @BindView(R.id.countryBanner)
//            ImageView mCountryBanner;
//            @BindView(R.id.time)
//            TextView mTime;
//            @BindView(R.id.star)
//            ImageView mStar;
//            @BindView(R.id.lidoNews)
//            TextView mLidoNews;
//            @BindView(R.id.badNews)
//            TextView mBadNews;
//            @BindView(R.id.title)
//            TextView mTitle;
//            @BindView(R.id.beforeData)
//            TextView mBeforeData;
//            @BindView(R.id.expectData)
//            TextView mExpectData;
//            @BindView(R.id.publishData)
//            TextView mPublishData;
//            @BindView(R.id.reviseBefore)
//            TextView mReviseBefore;
//
//            ViewHolder(View view) {
//                ButterKnife.bind(this, view);
//            }
//
//            public void bindDataWithView(CalendarFinanceModel.EconomicCalendarsBean item, Context context) {
//                String organizeMarkUrl = API.Message.getCalendarFinanceCountryBanner(item.getState());
//                if (!TextUtils.isEmpty(organizeMarkUrl)) {
//                    Picasso.with(context).load(ImageUtil.utf8Togb2312(organizeMarkUrl)).error(R.mipmap.ic_launcher)
//                            .resizeDimen(R.dimen.country_flag_width, R.dimen.country_flag_height)
//                            .into(mCountryBanner);
//                }
//                mTime.setText(item.getPredicttime());
//
//                handleStatus(item, context);
//
//                String title = item.getState() + item.getTitle();
//                mTitle.setText(title);
//                int textColor = ContextCompat.getColor(context, R.color.blackPrimary);
//                mBeforeData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.before_data_), "  " + item.getBefore(), textColor));
//                mExpectData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.expect_data_), "  " + item.getForecast(), textColor));
//                mPublishData.setText(StrUtil.mergeTextWithColor(context.getString(R.string.real_data_), "  " + item.getReality(), textColor));
//
//                switch (item.getImportance()) {
//                    case LOW:
//                        mStar.setImageResource(R.drawable.ic_star_general);
//                        break;
//                    case MIDDLE:
//                        mStar.setImageResource(R.drawable.ic_star_important);
//                        break;
//                    case TALL:
//                        mStar.setImageResource(R.drawable.ic_star_very_important);
//                        break;
//                    default:
//                        mStar.setImageResource(R.drawable.ic_star_important);
//                        break;
//                }
//            }
//
//            /**
//             * 处理利空和利多消息
//             *
//             * @param item
//             * @param context
//             */
//            private void handleStatus(CalendarFinanceModel.EconomicCalendarsBean item, Context context) {
//                if (item.getEffecttype() == CalendarFinanceModel.TYPE_HAS_MORE_STATUS) {
//                    String effect = item.getEffect();
//                    if (effect.startsWith("|") && effect.endsWith("|")) {
//                        mLidoNews.setVisibility(View.GONE);
//                        mBadNews.setVisibility(View.VISIBLE);
//                        mBadNews.setText(context.getString(R.string.bad_news, effect.substring(1, effect.length() - 1)));
//                    } else {
//                        mLidoNews.setVisibility(View.VISIBLE);
//                        mBadNews.setVisibility(View.VISIBLE);
//                        mLidoNews.setBackgroundResource(R.drawable.btn_red);
//                        mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
//
//                        String substring = effect.substring(effect.indexOf("|"));
//                        mLidoNews.setText(context.getString(lido, effect.substring(0, effect.length() - substring.length())));
//                        mBadNews.setText(context.getString(R.string.bad_news, substring.substring(1, substring.length() - 1)));
//                    }
//
//                } else if (item.getEffecttype() == CalendarFinanceModel.TYPE_LIDO) {
//                    if (item.getEffect().contains("||")) {
//                        mBadNews.setVisibility(View.GONE);
//                        mLidoNews.setVisibility(View.VISIBLE);
//                        mLidoNews.setBackgroundResource(R.drawable.btn_red);
//                        mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
//                        mLidoNews.setText(context.getString(R.string.lido, item.getEffect().replace("||", "")));
//                    } else {
//                        String effect = item.getEffect();
//                        if (effect.length() > 5) {
//                            /**
//                             * 利多消息
//                             */
//                            String lido = effect.substring(0, 5);
//                            /**
//                             * 利空消息
//                             */
//                            mBadNews.setVisibility(View.VISIBLE);
//                            mLidoNews.setVisibility(View.VISIBLE);
//                            mLidoNews.setBackgroundResource(R.drawable.btn_red);
//                            mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
//                            String badNews = effect.substring(lido.length() + 1, effect.length() - 1);
//                            mLidoNews.setText(context.getString(R.string.lido, lido));
//                            mBadNews.setText(context.getString(R.string.bad_news, badNews));
//                        } else {
////                            mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
////                            mLidoNews.setText(item.getEffect());
//                        }
//                    }
//                } else if (item.getEffecttype() == CalendarFinanceModel.TYPE_EMPTY_NEWS) {
//                    mBadNews.setVisibility(View.GONE);
//                    mLidoNews.setBackgroundResource(R.drawable.btn_transparent);
//                    mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.colorDisable));
//                    if (item.getEffect().contains("||")) {
//                        mLidoNews.setText(R.string.less_affected);
//                    } else {
//                        mLidoNews.setText(R.string.not_publish);
//                    }
//                }
//            }
//        }
    }


    static class CalendarFinanceRecycleViewAdapter extends RecyclerView.Adapter<CalendarFinanceRecycleViewAdapter.ViewHolder> {

        List<CalendarFinanceModel.EconomicCalendarsBean> mCalendarsBeanList;
        Context mContext;

        public CalendarFinanceRecycleViewAdapter(Context context, List<CalendarFinanceModel.EconomicCalendarsBean> calendarsBeanList) {
            this.mCalendarsBeanList = calendarsBeanList;
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_calendar_finance, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mCalendarsBeanList.get(position), position, mContext);
        }

        @Override
        public int getItemCount() {
            return mCalendarsBeanList != null ? mCalendarsBeanList.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private static final String LOW = "低";
            private static final String MIDDLE = "中";
            private static final String TALL = "高";

            @BindView(R.id.countryBanner)
            ImageView mCountryBanner;
            @BindView(R.id.time)
            TextView mTime;
            @BindView(R.id.star)
            ImageView mStar;
            @BindView(R.id.lidoNews)
            TextView mLidoNews;
            @BindView(R.id.badNews)
            TextView mBadNews;
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            private void bindDataWithView(CalendarFinanceModel.EconomicCalendarsBean item, int position, Context context) {

                String organizeMarkUrl = API.Message.getCalendarFinanceCountryBanner(item.getState());
                if (!TextUtils.isEmpty(organizeMarkUrl)) {
                    Picasso.with(context).load(ImageUtil.utf8Togb2312(organizeMarkUrl)).error(R.mipmap.ic_launcher)
                            .resizeDimen(R.dimen.country_flag_width, R.dimen.country_flag_height)
                            .into(mCountryBanner);
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

            private void handleStatus(CalendarFinanceModel.EconomicCalendarsBean item, Context context) {
                if (item.getEffecttype() == CalendarFinanceModel.TYPE_HAS_MORE_STATUS) {
                    String effect = item.getEffect();
                    if (effect.startsWith("|") && effect.endsWith("|")) {
                        mLidoNews.setVisibility(View.GONE);
                        mBadNews.setVisibility(View.VISIBLE);
                        mBadNews.setText(context.getString(R.string.bad_news, effect.substring(1, effect.length() - 1)));
                    } else {
                        mLidoNews.setVisibility(View.VISIBLE);
                        mBadNews.setVisibility(View.VISIBLE);
                        mLidoNews.setBackgroundResource(R.drawable.btn_red);
                        mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));

                        String substring = effect.substring(effect.indexOf("|"));
                        mLidoNews.setText(context.getString(lido, effect.substring(0, effect.length() - substring.length())));
                        mBadNews.setText(context.getString(R.string.bad_news, substring.substring(1, substring.length() - 1)));
                    }

                } else if (item.getEffecttype() == CalendarFinanceModel.TYPE_LIDO) {
                    if (item.getEffect().contains("||")) {
                        mBadNews.setVisibility(View.GONE);
                        mLidoNews.setVisibility(View.VISIBLE);
                        mLidoNews.setBackgroundResource(R.drawable.btn_red);
                        mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mLidoNews.setText(context.getString(R.string.lido, item.getEffect().replace("||", "")));
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
                            mBadNews.setVisibility(View.VISIBLE);
                            mLidoNews.setVisibility(View.VISIBLE);
                            mLidoNews.setBackgroundResource(R.drawable.btn_red);
                            mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            String badNews = effect.substring(lido.length() + 1, effect.length() - 1);
                            mLidoNews.setText(context.getString(R.string.lido, lido));
                            mBadNews.setText(context.getString(R.string.bad_news, badNews));
                        } else {
//                            mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
//                            mLidoNews.setText(item.getEffect());
                        }
                    }
                } else if (item.getEffecttype() == CalendarFinanceModel.TYPE_EMPTY_NEWS) {
                    mBadNews.setVisibility(View.GONE);
                    mLidoNews.setBackgroundResource(R.drawable.btn_transparent);
                    mLidoNews.setTextColor(ContextCompat.getColor(context, R.color.colorDisable));
                    if (item.getEffect().contains("||")) {
                        mLidoNews.setText(R.string.less_affected);
                    } else {
                        mLidoNews.setText(R.string.not_publish);
                    }
                }
            }
        }
    }
}

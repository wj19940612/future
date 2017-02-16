package com.jnhyxx.html5.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.fragment.BaseFragment;
import com.jnhyxx.html5.utils.ToastUtil;
import com.jnhyxx.html5.view.WeekCalendarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/2/16.
 */

public class CalendarFinanceFragment extends BaseFragment implements WeekCalendarLayout.OnWeekSelectListener {

    @BindView(R.id.calendarWeek)
    WeekCalendarLayout mCalendarWeek;

    private Unbinder mBind;

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
    }

    @Override
    public void onWeekSelected(int index, String week) {
        ToastUtil.curt("所选择的日期" + index + " 星期 " + week);
    }
}

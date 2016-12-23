package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.web.TradeAnalyzeDetailsActivity;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.Network;
import com.johnz.kutils.DateUtil;
import com.johnz.kutils.Launcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class IndustryMessageFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = "InfoListFragment";

    private static final String TYPE = "fragmentType";


    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.emptyView)
    TextView mEmptyView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    private int mType;
    private int mOffset;
    private int mPageSize;

    private NewsListAdapter mNewsListAdapter;
    private Set<String> mSet;
    private TextView mFooter;
    private Unbinder mBind;

    public static IndustryMessageFragment newInstance(int type) {
        IndustryMessageFragment fragment = new IndustryMessageFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setEmptyView(mEmptyView);
        mOffset = 0;
        mPageSize = 15;
        mSet = new HashSet<>();

        mListView.setDivider(null);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mOffset = 0;
                mSet.clear();
                requestInfoList();
                if (!Network.isNetworkAvailable()) {
                    stopRefreshAnimation();
                }
            }
        });
        requestInfoList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void requestInfoList() {
        API.Message.findNewsList(mType, mOffset, mPageSize)
                .setTag(TAG)
                .setCallback(new Callback<Resp<List<Information>>>() {
                    @Override
                    public void onReceive(Resp<List<Information>> listResp) {
                        if (listResp.isSuccess()) {
                            updateInfoList(listResp.getData());
                        } else {
                            stopRefreshAnimation();
                        }

                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateInfoList(List<Information> messageLists) {
        if (messageLists == null) {
            stopRefreshAnimation();
            return;
        }
        if (mFooter == null) {
            mFooter = new TextView(getContext());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                    getResources().getDisplayMetrics());
            mFooter.setPadding(padding, padding, padding, padding);
            mFooter.setGravity(Gravity.CENTER);
            mFooter.setText(R.string.click_to_load_more);
            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSwipeRefreshLayout.isRefreshing()) return;
                    mOffset +=mPageSize;
                    requestInfoList();
                }
            });
            mListView.addFooterView(mFooter);
        }

        if (messageLists.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            mListView.removeFooterView(mFooter);
        }


        if (mNewsListAdapter == null) {
            mNewsListAdapter = new NewsListAdapter(getActivity());
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mNewsListAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        for (Information item : messageLists) {
            if (mSet.add(item.getId())) {
                mNewsListAdapter.add(item);
            }
        }
        mListView.setAdapter(mNewsListAdapter);
        mNewsListAdapter.notifyDataSetChanged();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Information information = (Information) parent.getAdapter().getItem(position);
        if (information != null) {
            Launcher.with(getActivity(), TradeAnalyzeDetailsActivity.class).putExtra(Launcher.EX_PAYLOAD, information).execute();
            Log.d(TAG, "详情信息" + information.toString());
        }
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

    class NewsListAdapter extends ArrayAdapter<Information> {

        public NewsListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_info_message, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bindingData(getItem(position), getContext());
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.summary)
            TextView mSummary;
            @BindView(R.id.createDate)
            TextView mCreateDate;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Information item, Context context) {
                String time = DateUtil.format(item.getCreateTime(), DateUtil.DEFAULT_FORMAT, "yyyy/MM/dd HH:mm");
                mCreateDate.setText(time);
                if (!TextUtils.isEmpty(item.getTitle())) {
                    mSummary.setText(item.getTitle());
                } else if (!TextUtils.isEmpty(item.getSummary())) {
                    mSummary.setText(item.getSummary());
                }
            }
        }
    }

}

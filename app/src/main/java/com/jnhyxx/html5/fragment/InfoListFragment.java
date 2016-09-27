package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.message.MessageList;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.net.ApiIndeterminate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoListFragment extends ListFragment implements ApiIndeterminate {

    private static final String TAG = "InfoListFragment";

    private static final String TYPE = "fragmentType";
    public static final int TYPE_MARKET_ANALYSING = 0;
    public static final int TYPE_INDUSTRY_NEWS = 1;


    //首页资讯
    public static final int TYPE_MESSAGE_HOME_PAGE = 0;
    //列表资讯
    public static final int TYPE_MESSAGE_lIST = 1;
    //弹窗资讯
    public static final int TYPE_MESSAGE_POPUP = 2;

    private OnNewItemClickListener mListener;
    private int mType;

    private int mPageNo;
    private int mPageSize;

    private NewsListAdapter mNewsListAdapter;
    private Set<Integer> mSet;
    private TextView mFooter;

    public static InfoListFragment newInstance(int type) {
        InfoListFragment fragment = new InfoListFragment();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPageNo = 1;
        mPageSize = 10;
        mSet = new HashSet<>();
        setEmptyText(getString(R.string.there_is_no_info_for_now));
        getListView().setDivider(null);

        requestInfoList();
    }

    private void requestInfoList() {
//        if (mType == TYPE_MARKET_ANALYSING) {
//            int SECTION_ID_MARKET_ANALYSING = 58;
//            Log.d(TAG, "交易明细类型 " + mType + "行情分析");
////            API.User.getInfo(User.getUser().getToken(), SECTION_ID_MARKET_ANALYSING, mPageNo, mPageSize)
////                    .setCallback(new Callback2<Resp<List<Information>>, List<Information>>() {
////                        @Override
////                        public void onRespSuccess(List<Information> informationList) {
////                            updateInfoList(informationList);
////                        }
////                    }).setTag(TAG).setIndeterminate(this).fire();
//        } else {
//            int SECTION_ID_INDUSTRY = 57;
//            Log.d(TAG, "交易明细类型 " + mType + "行业资讯");
////            API.User.getInfo(User.getUser().getToken(), SECTION_ID_INDUSTRY, mPageNo, mPageSize)
////                    .setCallback(new Callback2<Resp<List<Information>>, List<Information>>() {
////                        @Override
////                        public void onRespSuccess(List<Information> informationList) {
////                            updateInfoList(informationList);
////                        }
////                    }).setTag(TAG).setIndeterminate(this).fire();
//        }

        API.User.findNewsList(mType, mPageNo, mPageSize)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2<Resp<List<MessageList>>, List<MessageList>>() {
            @Override
            public void onRespSuccess(List<MessageList> messageLists) {
                for (int i = 0; i < messageLists.size(); i++) {
                    Log.d(TAG, "type是 " + mType + "\n 获取的数据 " + messageLists.get(i) + "\n");
                }
                updateInfoList(messageLists);

            }
        }).fire();
    }

    private void updateInfoList(List<MessageList> messageLists) {
        if (messageLists == null || isDetached()) return;
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
                    mPageNo++;
                    requestInfoList();
                }
            });
            getListView().addFooterView(mFooter);
        }

        if (messageLists.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            getListView().removeFooterView(mFooter);
        }

        if (mNewsListAdapter == null) {
            mNewsListAdapter = new NewsListAdapter(getContext());
            setListAdapter(mNewsListAdapter);
        }

        for (MessageList item : messageLists) {
            if (mSet.add(item.getId())) {
                mNewsListAdapter.add(item);
            }
        }
        mNewsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShow(String tag) {
        setListShown(false);
    }

    @Override
    public void onDismiss(String tag) {
        setListShown(true);
    }

    private interface OnNewItemClickListener {
        void onNewItemClick(MessageList messageList);
    }

    static class NewsListAdapter extends ArrayAdapter<MessageList> {

        public NewsListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_info, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bindingData(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.summary)
            TextView mSummary;
            @BindView(R.id.createDate)
            TextView mCreateDate;
            @BindView(R.id.cmtAndReadCount)
            TextView mCmtAndReadCount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(MessageList item, Context context) {
//                if (item.getSection() == Information.SECTION_MARKET_ANALYSING) {
//                    mTitle.setVisibility(View.GONE);
//                    mSummary.setText(item.getSummary());
//                    mCreateDate.setText(item.getCreateDate());
//                    mCmtAndReadCount.setText(context.getString(R.string.comment_read_count,
//                            item.getCmtCount(), item.getReadCount()));
//                } else {
//                    mTitle.setVisibility(View.VISIBLE);
//                    mTitle.setText(item.getTitle());
//                    mSummary.setText(item.getSummary());
//                    mCreateDate.setText(item.getCreateDate());
//                    mCmtAndReadCount.setText(context.getString(R.string.comment_read_count,
//                            item.getCmtCount(), item.getReadCount()));
//                }
            }
        }
    }
}

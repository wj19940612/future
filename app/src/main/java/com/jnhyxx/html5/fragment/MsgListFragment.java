package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.local.User;
import com.jnhyxx.html5.domain.msg.SysTradeMessage;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.johnz.kutils.net.ApiIndeterminate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MsgListFragment extends ListFragment implements ApiIndeterminate {

    private static final String TAG = "MsgListFragment";

    private static final String TYPE = "fragmentType";
    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_TRADE = 1;

    private OnMsgItemClickListener mListener;
    private int mType;

    private int mPageNo;
    private int mPageSize;

    private MessageListAdapter mMessageListAdapter;
    private Set<Integer> mSet;
    private TextView mFooter;

    public static MsgListFragment newInstance(int type) {
        MsgListFragment fragment = new MsgListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMsgItemClickListener) {
            mListener = (OnMsgItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMsgItemClickListener");
        }
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
        setEmptyText(getString(R.string.there_is_no_message_for_now));

        requestMessageList();
    }

    private void requestMessageList() {
        if (mType == TYPE_SYSTEM) {
            API.Message.getSystemMessageList(User.getUser().getToken(), mPageNo, mPageSize)
                    .setCallback(new Callback2<Resp<List<SysTradeMessage>>, List<SysTradeMessage>>() {
                        @Override
                        public void onRespSuccess(List<SysTradeMessage> sysTradeMessages) {
                            updateMessageList(sysTradeMessages);
                        }
                    }).setTag(TAG).setIndeterminate(this).fire();
        } else {
            API.Message.getTradeMessageList(User.getUser().getToken(), mPageNo, mPageSize)
                    .setCallback(new Callback2<Resp<List<SysTradeMessage>>, List<SysTradeMessage>>() {
                        @Override
                        public void onRespSuccess(List<SysTradeMessage> sysTradeMessages) {
                            updateMessageList(sysTradeMessages);
                        }
                    }).setTag(TAG).setIndeterminate(this).fire();
        }
    }

    private void updateMessageList(List<SysTradeMessage> sysTradeMessages) {
        if (sysTradeMessages == null || isDetached()) return;

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
                    requestMessageList();
                }
            });
            getListView().addFooterView(mFooter);
        }

        if (sysTradeMessages.size() < mPageSize) {
            // When get number of data is less than mPageSize, means no data anymore
            // so remove footer
            getListView().removeFooterView(mFooter);
        }

        if (mMessageListAdapter == null) {
            mMessageListAdapter = new MessageListAdapter(getContext());
            setListAdapter(mMessageListAdapter);
        }

        for (SysTradeMessage item : sysTradeMessages) {
            if (mSet.add(item.getId())) {
                mMessageListAdapter.add(item);
            }
        }
        mMessageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        SysTradeMessage message = (SysTradeMessage) l.getItemAtPosition(position);
        if (mListener != null) {
            mListener.onMsgItemClick(message);
        }
    }

    @Override
    public void onShow(String tag) {
        setListShown(false);
    }

    @Override
    public void onDismiss(String tag) {
        setListShown(true);
    }

    static class MessageListAdapter extends ArrayAdapter<SysTradeMessage> {

        public MessageListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_message, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bindingData(getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.updateDate)
            TextView mUpdateDate;
            @BindView(R.id.content)
            TextView mContent;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(SysTradeMessage item) {
                mTitle.setText(item.getTitle());
                mUpdateDate.setText(item.getUpdateDate());
                mContent.setText(item.getContent());
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        API.cancel(TAG);
    }

    public interface OnMsgItemClickListener {
        void onMsgItemClick(SysTradeMessage sysTradeMessage);
    }
}

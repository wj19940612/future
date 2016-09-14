package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.Information;
import com.jnhyxx.html5.domain.account.ChannelBankList;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.net.ApiIndeterminate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 渠道银行列表
 */
public class BankListFragment extends ListFragment implements ApiIndeterminate {


    private static final String TAG = "BankListFragment";
    public static final String BANK_LIST = "bankList";

    private OnBankItemClickListener mListener;

    private ArrayList<ChannelBankList> mChannelBankList;

    private HashSet<Integer> mSet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBankItemClickListener) {
            mListener = (OnBankItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBankItemClickListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: 2016/9/9 原来的模拟数据
//        List<Bank> fakedata = new ArrayList<>();
//        fakedata.add(new Bank("广发银行"));
//        fakedata.add(new Bank("招商银行"));
//        bankListAdapter.addAll(fakedata);
//        API.User.showChannelBankList().setCallback(new Callback1<Resp>() {
//            @Override
//            protected void onRespSuccess(Resp resp) {
//                Log.d(TAG, "渠道银行列表  data " + resp.getData().toString() + "\n msg" + resp.getMsg());
////                List<ChannelBankList> channelBankLists = ChannelBankList.arrayChannelBankListFromData(resp.getMsg());
//            }
//        }).fire();

        mSet = new HashSet<>();
        getListView().setDivider(null);

        getChannelBankList();
    }

    private void updateChannelBankList(ArrayList<ChannelBankList> channelBankLists) {
        if (channelBankLists == null || isDetached()) return;
        BankListAdapter bankListAdapter = new BankListAdapter(getActivity());
        if (channelBankLists != null && !channelBankLists.isEmpty()) {
            bankListAdapter.addAll(channelBankLists);
        }
        setListAdapter(bankListAdapter);
        for (ChannelBankList item : channelBankLists) {
            if (mSet.add(item.getId())) {
                bankListAdapter.add(item);
            }
        }
        bankListAdapter.notifyDataSetChanged();
    }

    private void getChannelBankList() {
        API.User.showChannelBankList().setCallback(new Callback2<Resp<List<ChannelBankList>>, List<ChannelBankList>>() {

            @Override
            public void onRespSuccess(List<ChannelBankList> channelBankLists) {
                mChannelBankList = (ArrayList) channelBankLists;
                updateChannelBankList(mChannelBankList);
            }
        }).fire();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ChannelBankList mChannelBankList = (ChannelBankList) l.getItemAtPosition(position);
        if (mListener != null) {
            mListener.onBankItemClick(mChannelBankList);
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


    static class BankListAdapter extends ArrayAdapter<ChannelBankList> {
        Context mContext;

        public BankListAdapter(Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_bank, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.bindingData(getItem(position), mContext);

            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.bankName)
            TextView mBank;
            @BindView(R.id.bankLimit)
            TextView mBankLimit;
            @BindView(R.id.bankIcon)
            ImageView mBankIcon;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(ChannelBankList channelBankList, Context context) {
                if (channelBankList == null) return;
                this.mBank.setText(channelBankList.getName());
                mBankLimit.setText(context.getString(R.string.bind_bank_card_limit, channelBankList.getLimitSingle(), channelBankList.getLimitDay()));
                if (!TextUtils.isEmpty(channelBankList.getIcon()))
                    Picasso.with(context).load(channelBankList.getIcon()).into(mBankIcon);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBankItemClickListener {
        void onBankItemClick(ChannelBankList bank);
    }
}

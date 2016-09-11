package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.account.ChannelBankList;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback2;
import com.jnhyxx.html5.net.Resp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BankListFragment extends ListFragment {
    private static final String TAG = "BankListFragment";
    public static final String BANK_LIST = "bankList";

    private OnBankItemClickListener mListener;

    private ArrayList mChannelBankList;

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
        final BankListAdapter bankListAdapter = new BankListAdapter(getContext());
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

        API.User.showChannelBankList().setCallback(new Callback2<Resp<List<ChannelBankList>>, List<ChannelBankList>>() {

            @Override
            public void onRespSuccess(List<ChannelBankList> channelBankLists) {
                mChannelBankList = (ArrayList) channelBankLists;

            }
        }).fire();
        if (mChannelBankList != null && !mChannelBankList.isEmpty()) {
            bankListAdapter.addAll(mChannelBankList);
        }
        setListAdapter(bankListAdapter);
        Log.d(TAG,"onActivityCreated");
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Bank bank = (Bank) l.getItemAtPosition(position);
        if (mListener != null) {
            mListener.onBankItemClick(bank);
        }
    }

    static class BankListAdapter extends ArrayAdapter<Bank> {

        public BankListAdapter(Context context) {
            super(context, 0);
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

            holder.bindingData(getItem(position));

            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.bankName)
            TextView mBank;
            @BindView(R.id.bankLimit)
            TextView mBankLimit;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Bank item) {
                this.mBank.setText(item.name);
                this.mBankLimit.setText(item.bankLimit);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBankItemClickListener {
        void onBankItemClick(Bank bank);
    }

    public static class Bank {

        public Bank(String name) {
            this.name = name;
        }

        public String name;
        String bankLimit;
    }
}

package com.jnhyxx.html5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jnhyxx.html5.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BankListFragment extends ListFragment {

    public static final String BANK_LIST = "bankList";

    private OnBankItemClickListener mListener;

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
        BankListAdapter bankListAdapter = new BankListAdapter(getContext());
        List<Bank> fakedata = new ArrayList<>();
        fakedata.add(new Bank("广发银行"));
        fakedata.add(new Bank("招商银行"));
        bankListAdapter.addAll(fakedata);
        setListAdapter(bankListAdapter);
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
            @BindView(R.id.bank)
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

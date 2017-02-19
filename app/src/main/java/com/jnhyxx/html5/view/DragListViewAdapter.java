package com.jnhyxx.html5.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by MR.YANG on 2017/2/17.
 */

public abstract class DragListViewAdapter<T> extends BaseAdapter {
    public Context mContext;
    public List<T> mDragData1;
    public List<T> mDragData2;

    public DragListViewAdapter(Context context, List<T> list1) {
        this.mContext = context;
        mDragData1 = list1;
    }

    public DragListViewAdapter(Context context, List<T> list1, List<T> list2) {
        this.mContext = context;
        mDragData1 = list1;
        mDragData2 = list2;
    }

    @Override
    public int getCount() {
        return mDragData1 == null ? 0 : mDragData1.size();
    }

    public int getDragCount() {
        return mDragData1 == null ? 0 : mDragData1.size() + headCount();
    }

    @Override
    public T getItem(int position) {
        return mDragData1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent);

    public void swapData(int from, int to) {
        Collections.swap(mDragData1, from, to);
        notifyDataSetChanged();
    }

    public void deleteData(int position) {
        mDragData1.remove(position);
        notifyDataSetChanged();
    }

    public int headCount() {
        return 0;
    }
}

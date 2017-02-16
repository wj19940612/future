package com.jnhyxx.html5.view;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangtang on 15/5/12.
 */
public abstract class DragAdapter<T> extends BaseAdapter {

    private List<T> list = new ArrayList<>();
    private Context context;

    public DragAdapter(Context context, List<T> list) {
        this.context = context;
        if (list != null)
            this.list = list;
    }


    public void remove(int position) {
        if (list != null)
            list.remove(getItem(position));
    }

    public void insert(int position, T t) {
        list.add(position, t);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

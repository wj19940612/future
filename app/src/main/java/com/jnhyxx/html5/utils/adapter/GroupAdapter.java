package com.jnhyxx.html5.utils.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class GroupAdapter<T extends GroupAdapter.Groupable> extends BaseAdapter {

    protected static final int HEAD = 0;
    protected static final int ITEM = 1;

    private Context mContext;
    private List<Group<T>> mGroupableList;

    public GroupAdapter(Context context) {
        mContext = context;
        mGroupableList = new ArrayList<>();
    }

    public GroupAdapter(Context context, List<T> groupableList) {
        mContext = context;
        mGroupableList = group(groupableList);
    }

    protected List<Group<T>> group(List<T> list) {
        List<Group<T>> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        for (int i = 0; i < list.size(); i++) {
            Groupable data = list.get(i);
            Group group = new Group(data.getGroupName());
            group.add(data);

            int index = i;
            for (int j = index + 1; j < list.size(); j++) {
                Groupable groupable = list.get(j);
                if (group.getGroupName().equals(groupable.getGroupName())) {
                    group.add(groupable);
                    i = j;
                } else {
                    break;
                }
            } // for(j)
            result.add(group);
        }
        return result;
    }

    public void setGroupableList(List<T> groupableList) {
        mGroupableList = group(groupableList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        for (Group group : mGroupableList) {
            count += group.getCount();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        int firstIndex = 0;
        for (Group group: mGroupableList) {
            int size = group.getCount();
            int index = position - firstIndex;
            if (index < size) {
                return group.getGroupable(index);
            }
            firstIndex += size;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        return getView(position, view, viewGroup, type);
    }

    protected abstract View getView(int position, View view, ViewGroup viewGroup, int type);

    @Override
    public int getItemViewType(int position) {
        int firstIndex = 0;
        for (Group group: mGroupableList) {
            int size = group.getCount();
            int index = position - firstIndex;
            if (index == 0) {
                return HEAD;
            }
            if (index < size) {
                return ITEM;
            }
            firstIndex += size;
        }
        return ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == ITEM;
    }

    public Context getContext() {
        return mContext;
    }

    public interface Groupable {
        String getGroupName();
    }

    public static class Group<T extends Groupable> implements Groupable {

        private String mGroupName;
        private List<T> mList;

        public Group(String groupName) {
            mGroupName = groupName;
            mList = new ArrayList<>();
        }

        @Override
        public String getGroupName() {
            return mGroupName;
        }

        private void add(T t) {
            mList.add(t);
        }

        public int getCount() {
            return mList != null ? mList.size() + 1 : 0;
        }

        public Groupable getGroupable(int index) {
            return index == 0 ? this : mList.get(index - 1);
        }
    }

}

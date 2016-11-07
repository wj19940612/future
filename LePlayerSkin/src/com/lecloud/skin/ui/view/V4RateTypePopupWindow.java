package com.lecloud.skin.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lecloud.skin.R;
import com.lecloud.skin.popupwindow.BaseRateTypePopupWindow;
import com.lecloud.skin.entity.RateTypeItem;
import com.lecloud.skin.ui.utils.ReUtils;

public class V4RateTypePopupWindow extends BaseRateTypePopupWindow {
	private static final String TAG = "V4RateTypePopupWindow";

    public V4RateTypePopupWindow(Context contex) {
		super(contex);
	}

    @Override
    protected BaseAdapter setAdapter() {
        adapter = new RateTypeAdateper();
        return adapter;
    }

    @Override
    protected String setLayoutId() {
        return "letv_skin_v4_ratetype_layout";
    }


    private RateTypeAdateper adapter;

    private class RateTypeAdateper extends BaseAdapter {
        private static final String Selected_Text_Color = "#ff00a0e9";
        private int currentIndex;

        @Override
        public int getCount() {
            if (ratetypes != null) {
                return ratetypes.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) LayoutInflater.from(context).inflate(ReUtils.getLayoutId(context, "letv_skin_v4_ratetype_item"), null);
            if (ratetypes != null) {
                RateTypeItem item = new RateTypeItem();
                item.setName(ratetypes.get(position));
                view.setText(item.getName());
                if (item.getName().equals(currentRateType)) {
                    view.setTextColor(Color.parseColor(Selected_Text_Color));
                } else {
                    view.setTextColor(Color.WHITE);
                }
            }
            return view;
        }
    }

    @Override
    protected int getPopHeight(View anchor) {
        if(anchor==null||adapter==null){
            return 0;
        }
        return ((anchor.getHeight() + anchor.getPaddingRight()) * adapter.getCount());
    }

    @Override
    protected int getPopWidth(View anchor) {
        return 0;
    }

	@Override
	protected int getLayoutResId() {
		return R.layout.letv_skin_v4_ratetype_layout;
	}
}

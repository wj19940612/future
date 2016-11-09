package com.lecloud.skin.popupwindow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.lecloud.skin.entity.RateTypeItem;

/**
 * 码率切换弹出框
 * 
 * @author pys
 *
 */
	public abstract class BaseRateTypePopupWindow extends BasePopupWindow {

//	protected LetvUIListener mLetvVodUIListener;
	protected List<String> ratetypes;
	protected String currentRateType;
	public BaseRateTypePopupWindow(Context context) {
		super(context);
	}
	
//	public void setLetvUIListener(LetvUIListener mLetvVodUIListener) {
//		this.mLetvVodUIListener = mLetvVodUIListener;
//	}


	public void setRateType(String rateType) {
	    this.currentRateType = rateType;
	}
	
	public void setRateTypeItems(List<String> ratetypes,String definition) {
		this.ratetypes = ratetypes;
		this.currentRateType = definition;
	}


	private PopupWindow popupWindow;
	private ListView listView;
	private BaseAdapter adapter;
	private String layoutId;
	private ArrayList<RateTypeItem> rateTypeItems;


	@Override
	protected void init() {
		initView(context);
		adapter = setAdapter();
		listView.setAdapter(adapter);
	}

	protected abstract BaseAdapter setAdapter();

	protected abstract String setLayoutId();
	
	protected abstract int getLayoutResId();

	protected void initView(Context context) {
		layoutId = setLayoutId();
		listView = (ListView) LayoutInflater.from(context).inflate(getLayoutResId(), null);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setVerticalScrollBarEnabled(false);
		listView.setHorizontalScrollBarEnabled(false);
		
	}

	protected View getPopContentView() {
		return listView;
	};
	
	

//	protected  void itemClick(AdapterView<?> parent, View view, int position, long id){
//		if (mLetvVodUIListener != null) {
//		    currentRateType = ratetypes.get(position);
//			mLetvVodUIListener.onSetDefination(position);
//			hide();
//		}
////		if(uiPlayContext!=null&&player!=null){
////			RateTypeItem item = uiPlayContext.getRateTypeItems().get(position);
////			hide();
////			adapter.notifyDataSetChanged();
////			player.setDefination(item.getTypeId());
////		}
//		
//	}

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (listView != null) {
            listView.setOnItemClickListener(onItemClickListener);
        }
    }
	

}

package com.lecloud.skin.ui.base;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.lecloud.skin.ui.LetvUIListener;
import com.lecloud.skin.ui.view.V4RateTypePopupWindow;

/**
 * 码率切换按钮
 * @author pys
 * 
 */
public abstract class BaseRateTypeBtn extends Button {
	
	private V4RateTypePopupWindow popupWindow;
	
	protected LetvUIListener mLetvUIListener;

	public BaseRateTypeBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseRateTypeBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public BaseRateTypeBtn(Context context) {
		super(context);
	}
	
	public void setLetvUIListener(LetvUIListener mLetvUIListener) {
		this.mLetvUIListener = mLetvUIListener;
	}
    protected List<String> ratetypes;
    protected String currentRateType;
	
	public void setRateTypeItems(List<String> ratetypes,String definition) {
	    this.ratetypes = ratetypes;
	    this.currentRateType = definition;
		init();
		if (popupWindow != null) {
			popupWindow.setRateTypeItems(ratetypes, definition);
		}
		setText(definition);
	}

	protected void init() {
		popupWindow = new V4RateTypePopupWindow(getContext());
		popupWindow.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentRateType = ratetypes.get(position);
                mLetvUIListener.onSetDefination(position);
                popupWindow.hide();
                popupWindow.setRateType(currentRateType);
                BaseRateTypeBtn.this.setText(currentRateType);
            }
		    
        });
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (popupWindow != null && !popupWindow.isShowning()) {
                  popupWindow.show(v);
				}
			}
		});
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility){
		super.onVisibilityChanged(changedView,visibility);
		if(visibility != VISIBLE && popupWindow != null && popupWindow.isShowning()){
			popupWindow.hide();
		}
	}

}

package com.jnhyxx.html5.activity.userinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jnhyxx.html5.R;
import com.jnhyxx.html5.domain.local.LocalUser;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;

/**
 * 获取地址数据并显示地址选择器
 *
 * @author
 * @since 2015/12/15
 */
public class AddressInitTask extends AsyncTask<String, Void, ArrayList<Province>> {
    private static final String TAG = "AddressInitTask";

    private Activity activity;
    private ProgressDialog dialog;
    private String selectedProvince = "", selectedCity = "", selectedCounty = "";
    private boolean hideCounty = false;

    private OnAddressListener mOnAddressListener;

    public void setOnAddressListener(OnAddressListener onAddressListener) {
        this.mOnAddressListener = onAddressListener;
    }

    public interface OnAddressListener {
        void onSelectAddress(Province province, City city, County county);
    }

    /**
     * 初始化为不显示区县的模式
     */
    public AddressInitTask(Activity activity, boolean hideCounty) {
        this.activity = activity;
        this.hideCounty = hideCounty;
//        dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
    }

    public AddressInitTask(Activity activity) {
        this.activity = activity;
//        dialog = ProgressDialog.show(activity, null, "正在初始化数据...", true, true);
    }

    @Override
    protected ArrayList<Province> doInBackground(String... params) {
        if (params != null) {
            switch (params.length) {
                case 1:
                    selectedProvince = params[0];
                    break;
                case 2:
                    selectedProvince = params[0];
                    selectedCity = params[1];
                    break;
                case 3:
                    selectedProvince = params[0];
                    selectedCity = params[1];
                    selectedCounty = params[2];
                    break;
                default:
                    break;
            }
        }
        ArrayList<Province> data = new ArrayList<Province>();
        try {
            String json = ConvertUtils.toString(activity.getAssets().open("city.json"));

            Type listType = new TypeToken<ArrayList<Province>>() {
            }.getType();

            ArrayList<Province> provinces = new Gson().fromJson(json, listType);
            data.addAll(provinces);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(ArrayList<Province> result) {
//        dialog.dismiss();
        if (result.size() > 0) {
            AddressPicker picker = new AddressPicker(activity, result);
            picker.setHideCounty(hideCounty);
            if (hideCounty) {
                picker.setColumnWeight(1 / 3.0, 2 / 3.0);//将屏幕分为3份，省级和地级的比例为1:2
            } else {
                picker.setColumnWeight(2 / 8.0, 3 / 8.0, 3 / 8.0);//省级、地级和县级的比例为2:3:3
            }
            picker.setCancelTextColor(R.color.lucky);
//            picker.setSubmitTextColor(R.color.blueAssist);
            picker.setSubmitTextColor(Color.parseColor("#358CF3"));
            picker.setAnimationStyle(R.style.BottomDialogStyle);
            picker.setSelectedItem(selectedProvince, selectedCity, selectedCounty);
            WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
//            lineConfig.setColor(R.color.lucky);//设置分割线颜色
            picker.setLineConfig(lineConfig);
            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                @Override
                public void onAddressPicked(Province province, City city, County county) {
                    Log.d(TAG, "国家" + county + "省" + province + "城市" + city);
                    LocalUser.getUser().getUserInfo().setLand(province.getAreaName() + "-" + city.getAreaName());
                    if (mOnAddressListener != null) {
                        mOnAddressListener.onSelectAddress(province, city, county);
                    }
                }
            });
            picker.show();
        } else {
            Toast.makeText(activity, "数据初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

}

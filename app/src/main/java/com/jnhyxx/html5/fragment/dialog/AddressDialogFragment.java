package com.jnhyxx.html5.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.jnhyxx.html5.activity.userinfo.AddressInitTask;

/**
 * Created by ${wangJie} on 2017/1/9.
 */

public class AddressDialogFragment extends BaseGravityBottomDialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void show(FragmentManager manager) {
        super.show(manager);

    }

    public void showDialog(){
        new AddressInitTask(getActivity(), true).execute("浙江", "杭州市", "滨江区");
    }
}

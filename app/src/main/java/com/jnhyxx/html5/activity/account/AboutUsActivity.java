package com.jnhyxx.html5.activity.account;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.johnz.kutils.AppInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {
    private static final String TAG = "AboutUsActivity";
    @BindView(R.id.activity_about_us_tv_versionName)
    TextView tv_versionName;
//    //公司简介
//    @BindView(R.id.activity_about_us_rl_company_info)
//    RelativeLayout rl_companyInfo;
//    @BindView(R.id.activity_about_us_tv_company_info)
//    TextView tv_companyInfo;
//    //管理团队
//    @BindView(R.id.activity_about_us_rl_manager_team)
//    RelativeLayout rl_manager_team;
//    @BindView(R.id.activity_about_us_tv_manager_team)
//    TextView tv_manager_team;
//    //企业文化
//    @BindView(R.id.activity_about_us_rl_company_culture)
//    RelativeLayout rl_company_cultrue;
//    @BindView(R.id.activity_about_us_tv_company_culture)
//    TextView tv_company_cultrue;
//    //合作案例
//    @BindView(R.id.activity_about_us_rl_collaborate_case)
//    RelativeLayout rl_collaborate_case;
//    @BindView(R.id.activity_about_us_tv_collaborate_case)
//    TextView tv_collaborate_case;
    //公司热线
    @BindView(R.id.activity_about_us_rl_company_telphone)
    RelativeLayout rl_company_telphone;
    //记录被点击的item;
    private int selectPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        String versionName = AppInfo.getVersionName(getApplicationContext());
        tv_versionName.setText(getString(R.string.account_about_us_app_version, getString(R.string.app_name), versionName));
    }

    @OnClick({R.id.activity_about_us_rl_company_info, R.id.activity_about_us_rl_manager_team, R.id.activity_about_us_rl_company_culture, R.id.activity_about_us_rl_collaborate_case, R.id.activity_about_us_rl_company_telphone})
    public void onClick(View view) {
        switch (view.getId()) {
            //公司信息
            case R.id.activity_about_us_rl_company_info:
                changeViewStatus(R.id.activity_about_us_rl_company_info);
                break;
            //管理团队
            case R.id.activity_about_us_rl_manager_team:
                changeViewStatus(R.id.activity_about_us_rl_manager_team);
                break;
            //企业文化
            case R.id.activity_about_us_rl_company_culture:
                changeViewStatus(R.id.activity_about_us_rl_company_culture);
                break;
            //合作案例
            case R.id.activity_about_us_rl_collaborate_case:
                changeViewStatus(R.id.activity_about_us_rl_collaborate_case);
                break;
            //公司热线
            case R.id.activity_about_us_rl_company_telphone:
                // TODO: 2016/8/24  
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.account_about_us_company_telphone_number)));
                startActivity(intent);
                break;

        }
    }

    /**
     * 传入的view的id
     *
     * @param viewId
     */
    private void changeViewStatus(int viewId) {
        boolean tv_companyInfoStatus;
        boolean tvManagerTeamStatus;
        switch (viewId) {
            //公司信息
//            case R.id.activity_about_us_rl_company_info:
//                tv_companyInfoStatus = tv_companyInfo.isShown();
//                if (tv_companyInfoStatus) {
//                    tv_companyInfo.setVisibility(View.GONE);
//                } else {
//                    tv_companyInfo.setVisibility(View.VISIBLE);
//                }
//                tv_manager_team.setVisibility(View.GONE);
//                tv_company_cultrue.setVisibility(View.GONE);
//                tv_collaborate_case.setVisibility(View.GONE);
//                break;
//            //管理团队
//            case R.id.activity_about_us_rl_manager_team:
//                tvManagerTeamStatus = tv_manager_team.isShown();
//                if (tvManagerTeamStatus) {
//                    tv_manager_team.setVisibility(View.GONE);
//                } else {
//                    tv_manager_team.setVisibility(View.VISIBLE);
//                }
//                tv_companyInfo.setVisibility(View.GONE);
//                tv_company_cultrue.setVisibility(View.GONE);
//                tv_collaborate_case.setVisibility(View.GONE);
//                break;
//            //企业文化
//            case R.id.activity_about_us_rl_company_culture:
//                boolean tvCompanyCultrueStatus = tv_company_cultrue.isShown();
//                if (tvCompanyCultrueStatus) {
//                    tv_company_cultrue.setVisibility(View.GONE);
//                } else {
//                    tv_company_cultrue.setVisibility(View.VISIBLE);
//                }
//                tv_companyInfo.setVisibility(View.GONE);
//                tv_manager_team.setVisibility(View.GONE);
//                tv_collaborate_case.setVisibility(View.GONE);
//                break;
//            //合作案例
//            case R.id.activity_about_us_rl_collaborate_case:
//                boolean tvCollaborateCaseStatus = tv_collaborate_case.isShown();
//                if (tvCollaborateCaseStatus) {
//                    tv_collaborate_case.setVisibility(View.GONE);
//                } else {
//                    tv_collaborate_case.setVisibility(View.VISIBLE);
//                }
//                tv_companyInfo.setVisibility(View.GONE);
//                tv_manager_team.setVisibility(View.GONE);
//                tv_company_cultrue.setVisibility(View.GONE);
//                break;
            //公司热线
            case R.id.activity_about_us_rl_company_telphone:
                break;
        }
    }
}


//        groupDataList = getGroupDataList();
//        childDataSparseArray = getChildDataSparseArray();
//
//        for (int i = 0; i < childDataSparseArray.size(); i++) {
//            Log.d(TAG, "SparseArray 中第" + i + "  个，数据是" + childDataSparseArray.get(i) + "\n");
//        }
//        for (int i = 0; i < groupDataList.size(); i++) {
//            Log.d(TAG, "父类 中第" + i + "  个，数据是" + groupDataList.get(i) + "\n");
//        }
//    }

//    @NonNull
//    private SparseArray<String> getChildDataSparseArray() {
//        SparseArray<String> childData = new SparseArray<>();
//        childData.put(0, getString(R.string.account_about_us_company_info_child));
//        childData.put(1, getString(R.string.account_about_us_manager_team_child));
//        childData.put(2, getString(R.string.account_about_us_company_culture_child));
//        childData.put(3, getString(R.string.account_about_us_collaborate_case_child));
////        childData.put(4, getString(R.string.account_about_us_company_telphone_number));
////        childData.put(5, getString(R.string.account_about_us_company_qq_number));
//        return childData;
//    }
//
//    private List<String> getGroupDataList() {
//        String groupData[] = new String[]{getString(R.string.account_about_us_company_info),
//                getString(R.string.account_about_us_manager_team),
//                getString(R.string.account_about_us_company_culture),
//                getString(R.string.account_about_us_collaborate_case),
//                getString(R.string.account_about_us_company_telphone),
//                getString(R.string.account_about_us_company_qq)};
//        List<String> groupDataList = Arrays.asList(groupData);
//        return groupDataList;
//    }

//    class ExpandableListViewAdapter extends BaseExpandableListAdapter {
//
//        @Override
//        public int getGroupCount() {
//            return groupDataList.size();
//        }
//
//        @Override
//        public int getChildrenCount(int groupPosition) {
//            return childDataSparseArray.size();
//        }
//
//        @Override
//        public Object getGroup(int groupPosition) {
//            return groupDataList.get(groupPosition);
//        }
//
//        @Override
//        public Object getChild(int groupPosition, int childPosition) {
//            return childDataSparseArray.get(childPosition);
//        }
//
//        @Override
//        public long getGroupId(int groupPosition) {
//            return groupPosition;
//        }
//
//        @Override
//        public long getChildId(int groupPosition, int childPosition) {
//            return childPosition;
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//
//        @Override
//        public View getGroupView(int groupPosition, boolean isExpanded,
//                                 View convertView, ViewGroup parent) {
//            GroupViewHolder mGroupViewHolder;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(AboutUsActivity.this).inflate(R.layout.account_item_expanlistview_group, null);
//                mGroupViewHolder = new GroupViewHolder(convertView);
//                convertView.setTag(mGroupViewHolder);
//            } else {
//                mGroupViewHolder = (GroupViewHolder) convertView.getTag();
//            }
//            String groupData = groupDataList.get(groupPosition);
//            mGroupViewHolder.tv_leftTitle.setText(groupData);
//            if (TextUtils.isEmpty(childDataSparseArray.get(groupPosition))) {
//                mGroupViewHolder.iv_rightArrow.setVisibility(View.GONE);
//            }
//
//            return convertView;
//        }
//
//        @Override
//        public View getChildView(int groupPosition, int childPosition,
//                                 boolean isLastChild, View convertView, ViewGroup parent) {
//
//        }
//
//        @Override
//        public boolean isChildSelectable(int groupPosition, int childPosition) {
//            return true;
//        }
//
//        class GroupViewHolder {
//            //左边的标题
//            @BindView(R.id.account_about_us_activity_expandlist_tv)
//            TextView tv_leftTitle;
//            @BindView(R.id.account_about_us_activity_expandlist_image)
//            ImageView iv_rightArrow;
//            //右边最下边的电话和qq显示
//            @BindView(R.id.account_about_us_activity_expandlist_tv_right)
//            TextView tv_rightNumber;
//
//            public GroupViewHolder(View view) {
//                ButterKnife.bind(this, view);
//            }
//        }
////    }
//}

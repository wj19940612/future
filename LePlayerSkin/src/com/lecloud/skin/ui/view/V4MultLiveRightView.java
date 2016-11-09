package com.lecloud.skin.ui.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.R;
import com.lecloud.sdk.api.md.entity.action.LiveInfo;
import com.lecloud.skin.ui.utils.ScreenUtils;
import com.lecloud.skin.videoview.live.UIActionLiveSubVideoView;
import com.lecloud.skin.ui.utils.ReUtils;

/**
 * mutiple live window view
 * 多路窗口
 */

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class V4MultLiveRightView extends RelativeLayout {
	protected Context context;
    public static char[] numArray = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public LinearLayout mMultLivelayout;
    public FrameLayout mMultParentLayout;
    public ImageView mMultLiveBtn;
    public boolean isShowSubLiveView = false;
    public boolean isFirstShowSubLive = true;
    /**
     * to save all the multiple live players
     * 这个是保存每次弹出的小视屏
     */
    public List<MultLivePlayHolder> actionPlays;
    public LinearLayout.LayoutParams multLiveParams;
    protected OnClickListener showMultLiveViewClick;
    /**
     * now choosed live window
     * 需要参数
     */
    private int mCurrentIndex;
    private LinearLayout.LayoutParams multLiveViewParams;

    private ArrayList<UIActionLiveSubVideoView> mVideoViews;
    
    public V4MultLiveRightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    public V4MultLiveRightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public V4MultLiveRightView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    protected void initView() {
        LayoutInflater.from(context).inflate(ReUtils.getLayoutId(context, "letv_skin_v4_large_mult_live_layout"), this);
        actionPlays = new ArrayList<MultLivePlayHolder>();
        mVideoViews = new ArrayList<UIActionLiveSubVideoView>();

        showMultLiveViewClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleSubMultLiveView();
            }
        };

        multLiveViewParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        multLiveViewParams.gravity = Gravity.CENTER;
        multLiveViewParams.setMargins(5, 5, 5, 5);
    }

    protected void showMultLiveViewBtn() {
        mMultParentLayout = (FrameLayout) findViewById(R.id.mutl_live_lay);
        mMultLivelayout = (LinearLayout) findViewById(R.id.floating_right_mutl_live_lay);
        mMultLiveBtn = (ImageView) findViewById(R.id.mutl_live_btn);

        showMultLiveLayout();
        mMultLiveBtn.setOnClickListener(showMultLiveViewClick);
    }

    public void toggleSubMultLiveView() {
        if (isShowSubLiveView) {
            mMultLivelayout.setVisibility(View.GONE);
            mMultLiveBtn.setImageResource(ReUtils.getDrawableId(context, "letv_skin_v4_large_mult_live_action_on"));
            isShowSubLiveView = false;
        } else {
            mMultLivelayout.setVisibility(View.VISIBLE);
            if (isFirstShowSubLive) {
                // addLiveView();
                addLiveView();
                isFirstShowSubLive = false;

            } else {
                mMultLivelayout.setVisibility(View.VISIBLE);
                // resumeMultLiveView();
            }
            mMultLiveBtn.setImageResource(ReUtils.getDrawableId(context, "letv_skin_v4_large_mult_live_action_off"));
            isShowSubLiveView = true;
            showMultLiveLayout();
        }
    }

    private void showMultLiveLayout() {
        mMultLiveBtn.setVisibility(View.VISIBLE);
        if (isShowSubLiveView) {
            mMultLivelayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < mVideoViews.size(); i++) {
                mVideoViews.get(i).onResume();
            }
        } else {
            mMultLivelayout.setVisibility(View.GONE);
        }
    }

    private void hideMultLiveLayout() {
        mMultLivelayout.setVisibility(View.GONE);
        mMultLiveBtn.setVisibility(View.GONE);
    }

    /*
     * isShow multSubLiveLayout
     * 是否显示多路子机位View
     */
    public void setVisiableActiveSubLiveView(boolean b) {
        if (b) {
            showMultLiveLayout();
        } else {
            hideMultLiveLayout();
        }
    }

    public void addLiveView() {

        actionPlays.clear();
        mMultLivelayout.removeAllViews();

        for (int i = 0; i < liveInfos.size(); i++) {
            final MultLivePlayHolder holder = new MultLivePlayHolder();
            final String url = liveInfos.get(i).getPreviewStreamPlayUrl();

            final View layout = View.inflate(getContext(), ReUtils.getLayoutId(context, "letv_skin_v4_large_mult_live_action_sub_live_lay"), null);
            TextView title = (TextView) layout.findViewById(ReUtils.getId(context, "jiwei"));
            final FrameLayout framelayout = (FrameLayout) layout.findViewById(ReUtils.getId(context, "jiwei_lay"));
            final ProgressBar loading = (ProgressBar) layout.findViewById(ReUtils.getId(context, "pb_loading"));
            holder.loading = loading;

            initVideoView(framelayout, url, holder);
            holder.location = i;
            holder.url = url;
            title.setText(getResources().getString(R.string.video_camera) + numArray[i]);

            holder.layout = layout;
            MyOnClickListener onClick = new MyOnClickListener();
            layout.setOnClickListener(onClick);
            //
            int width = (ScreenUtils.getWidth(getContext()) - dip2px(getContext(), 40)) / 4;
            // int height = dip2px(getContext(), 135);
            multLiveParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
            mMultLivelayout.addView(layout, multLiveParams);

            holder.framelayout = framelayout;
            holder.textview = title;
            if (i == mCurrentIndex) {// 默认第一机位选中
                actionSelected(holder, true);
            }
            actionPlays.add(holder);
            
//            mVideoViews.get(i).getHolder().setFormat(PixelFormat.TRANSPARENT);
//            mVideoViews.get(i).setZOrderOnTop(true);
        }
    }

    private void initVideoView(final ViewGroup rootView, final String url, final MultLivePlayHolder mMultLivePlayHolder) {
        
        final UIActionLiveSubVideoView videoView = new UIActionLiveSubVideoView(getContext());
        mVideoViews.add(videoView);
        videoView.setDataSource(url);
        videoView.setVideoViewListener(new VideoViewListener() {
            
            @Override
            public void onStateResult(int event,  Bundle bundle) {
                switch (event) {
                case PlayerEvent.PLAY_PREPARED:
                    // 播放器准备完成，此刻调用start()就可以进行播放了
                    if (videoView != null) {
                        videoView.onStart();
                    }
                    break;
                    
                case PlayerEvent.PLAY_ERROR:
                    if (mMultLivePlayHolder.no_video_layout != null) {
                        rootView.removeView(mMultLivePlayHolder.no_video_layout);
                    }
                    View view = View.inflate(context, ReUtils.getLayoutId(context, "letv_skin_v4_large_mult_live_action_no_video_layout"), null);
                    rootView.addView(view, multLiveViewParams);
                    mMultLivePlayHolder.videoState = false;
                    mMultLivePlayHolder.no_video_layout = view;
                    break;
                case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                    // 设置视频比例
//                    videoView.setVideoLayout(VideoViewSizeHelper.VIDEO_LAYOUT_STRETCH, 0);
                    break;
                case PlayerEvent.PLAY_INFO:
                    int code =bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
                    if(code==StatusCode.PLAY_INFO_VIDEO_RENDERING_START){
                        mMultLivePlayHolder.videoState = true;
                    }
                    
                    break;
                    
                default:
                    break;
                }
                
            }

            @Override
            public String onGetVideoRateList(LinkedHashMap<String, String> map) {
                return null;
            }
        });
        videoView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rootView.addView(videoView, multLiveViewParams);
    }

    /**
     * set to choose one SubVideo or not
     * 下面的小视屏选中和取消选中
     */
    private void actionSelected(MultLivePlayHolder holder, boolean selected) {
        Drawable drawable1 = getContext().getResources().getDrawable(ReUtils.getDrawableId(context, "letv_skin_v4_large_mult_live_action_v_1"));
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        Drawable drawable2 = getContext().getResources().getDrawable(ReUtils.getDrawableId(context, "letv_skin_v4_large_mult_live_action_v_2"));
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        if (selected) {
            holder.framelayout.setBackgroundResource(ReUtils.getDrawableId(context, "letv_skin_v4_large_mult_live_action_list_onclick_style"));
            holder.textview.setCompoundDrawables(drawable2, null, null, null);
            holder.textview.setTextColor(getContext().getResources().getColor(ReUtils.getColorId(context, "action_mult_live_shape")));
        } else {
            holder.textview.setCompoundDrawables(drawable1, null, null, null);
            holder.textview.setTextColor(0xffffffff);
            holder.framelayout.setBackgroundResource(0);
        }
    }

    /**
     * stop to play
     * 停止播放，并且记录最后播放时间
     */
    public void stopAndRelease() {
        if (mVideoViews != null) {
            for (int i = 0; i < mVideoViews.size(); i++) {
                mVideoViews.get(i).onDestroy();
            }
        }
    }

    public void setActionInfoDone() {
        showMultLiveViewBtn();
    }
    
    List<LiveInfo> liveInfos;
    public void setStreams(List<LiveInfo> liveInfos) {
        this.liveInfos = liveInfos;
    }
    
    public void setCurrentMultLive(String liveId) {
        initCurrentIndex(liveId);
        if (actionPlays != null && mCurrentIndex != -1 && actionPlays.size() > mCurrentIndex) {
            actionSelected(actionPlays.get(mCurrentIndex), true);
        }
    }

    private void initCurrentIndex(String liveId) {
        if (liveInfos != null) {
            mCurrentIndex = indexOfLiveInfo(liveId);
        }
    }
    
    public int indexOfLiveInfo(String liveId) {
        for (LiveInfo info : liveInfos) {
            if (TextUtils.equals(info.getLiveId(), liveId)) {
                return liveInfos.indexOf(info);
            }
        }
        return -1;
    }

    /**
     * 切换直播窗口
     */
    public interface SwitchMultLiveCallback {
        public void switchMultLive(String liveId);
    }

    private SwitchMultLiveCallback mSwitchMultLiveCallback;

    public void setSwitchMultLiveCallbackk(SwitchMultLiveCallback mSwitchMultLiveCallback) {
        this.mSwitchMultLiveCallback = mSwitchMultLiveCallback;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    class MultLivePlayHolder {
        FrameLayout framelayout;
        TextView textview;
        ProgressBar loading;
        View no_video_layout;
        View layout;
        /**
         * show video is ok or not , true is ok, false is not
         * 这个表示视屏是不是正常。如果正常就为true。否则为false
         */
        boolean videoState = true;
        int location;
        String url;
    }

    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            if (actionPlays.size() > 0) {
                // long time = System.currentTimeMillis();
                for (final MultLivePlayHolder holder : actionPlays) {
                    if (holder.layout.equals(v)) {
                        if (holder.videoState && mCurrentIndex != holder.location) {
                            actionSelected(holder, true);
                            mCurrentIndex = holder.location;
                            if (mSwitchMultLiveCallback != null) {
                                mSwitchMultLiveCallback.switchMultLive(liveInfos.get(mCurrentIndex).getLiveId());
                            }
                        } else {
                            if (mVideoViews != null && mVideoViews.size() > holder.location) {
                                mVideoViews.get(holder.location).onResume();
                                actionSelected(actionPlays.get(mCurrentIndex), true);
                                if (holder.loading != null) {
                                    holder.framelayout.removeView(holder.loading);
                                }
                                if (holder.no_video_layout != null) {
                                    holder.framelayout.removeView(holder.no_video_layout);
                                }
                                if (holder.loading != null) {
                                    holder.framelayout.addView(holder.loading);
                                }
                                return;
                            }
                        }
                    } else {
                        actionSelected(holder, false);
                    }
                }
            }
            toggleSubMultLiveView();
        }
    }

}

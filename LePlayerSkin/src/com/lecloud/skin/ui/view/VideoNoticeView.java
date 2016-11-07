package com.lecloud.skin.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecloud.sdk.api.md.entity.action.LiveInfo;
import com.lecloud.sdk.api.status.ActionStatus;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.utils.NetworkUtils;
import com.lecloud.skin.R;
import com.lecloud.skin.activity.FeedBackActivity;

public class VideoNoticeView extends RelativeLayout implements View.OnTouchListener{
    private TextView mErrorCode;
    private TextView mErrorReason;
    private TextView mTips;
    private Button mReplay;
    private Button mErrorReport;
    private String mEventCode;
    private IReplayListener mRePlayListener;
    private Context mContext;
    private boolean isLive;

    public VideoNoticeView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public void setIsLive(boolean live) {
        isLive = live;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.letv_skin_v4_notice_layout, this, true);
        mErrorCode = (TextView) findViewById(R.id.tv_error_code);
        mErrorReason = (TextView) findViewById(R.id.tv_error_message);
        mTips = (TextView) findViewById(R.id.tv_error_msg);
        mReplay = (Button) findViewById(R.id.btn_error_replay);
        mErrorReport = (Button) findViewById(R.id.btn_error_report);
        mReplay.setOnTouchListener(this);
//        mReplay.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 重新播放
//                if (!NetworkUtils.hasConnect(mContext)) {
//                    Toast.makeText(mContext, getText(R.string.network_none), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                setVisibility(View.GONE);
//                mRePlayListener.onRePlay();
//            }
//        });
//        mErrorReport.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 错误反馈
//                Intent intent = new Intent(mContext, FeedBackActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                if (mRePlayListener != null && mRePlayListener.getReportParams() != null) {
//                    Bundle params = mRePlayListener.getReportParams();
//                    intent.putExtra("params", params);
//                }
//                mContext.startActivity(intent);
//
//            }
//        });
        mErrorReport.setOnTouchListener(this);
        setOnTouchListener(this);
    }

    private void videoTips(String state, String errorReason, String tips, String confirm, String cancel) {
        showMsg(state, tips);
        mTips.setVisibility(TextUtils.isEmpty(errorReason) ? GONE : VISIBLE);
        mTips.setText("(" + errorReason + ")");
        mReplay.setText(confirm);
        mErrorReport.setText(cancel);
        setVisibility(View.VISIBLE);
        bringToFront();
    }

    private void showMsg(String event, String msg) {
        if (event == null || event.equals("") || event.equals("0") || event.equals("-1")) {
            mErrorCode.setVisibility(GONE);
        } else {
            mErrorCode.setVisibility(VISIBLE);
            mErrorCode.setText(getContext().getResources().getString(R.string.error_code) + event);
        }
        if (msg != null && !msg.isEmpty()) {
            mErrorReason.setText("" + msg + "");
            mErrorReason.setVisibility(View.VISIBLE);
        } else {
            mErrorReason.setVisibility(View.GONE);
        }
    }

    public void processMediaState(int event, Bundle data) {
//        Log.e("hua", "状态提示state=" + event);
        if (NetworkUtils.hasConnect(mContext)) {
            int status = data.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
            mEventCode = String.valueOf(data.getInt(PlayerParams.KEY_STATS_CODE));
//            mEventCode = data.getString(PlayerParams.KEY_RESULT_ERROR_CODE, "");
            if (status == StatusCode.MEDIADATA_NETWORK_ERROR) {
                String errMsg = getText(R.string.request_fail);
                videoTips(mEventCode, errMsg, getText(R.string.letv_notice_message), getText(R.string.replay), getText(R.string.submit_error_info));
            } else if (status == StatusCode.MEDIADATA_INTERNAL_ERROR) {
                String errMsg = getText(R.string.play_error);
                videoTips(mEventCode, errMsg, getText(R.string.letv_notice_message), getText(R.string.replay), getText(R.string.submit_error_info));
            } else if (status == StatusCode.MEDIADATA_SERVER_ERROR) {
                if (event == PlayerEvent.MEDIADATA_ACTION) {
                    processActionMediaError(data);
                } else {
                    String errMsg = data.getString(PlayerParams.KEY_RESULT_ERROR_MSG);
                    videoTips(mEventCode, errMsg, getText(R.string.letv_notice_message), getText(R.string.replay), getText(R.string.submit_error_info));
                }
            }
        } else {
            videoTips("" + 10000, getText(R.string.net_fail), getText(R.string.net_error), getText(R.string.replay), getText(R.string.submit_error_info));
        }
    }
    private String getText(int id){
        return getResources().getString(id);
    }
    public void processActionStatus(int status) {
        String errMsg = "";
        if (status == ActionStatus.SATUS_NOT_START) {
            errMsg = getText(R.string.live_no_start);
        } else if (status == ActionStatus.STATUS_END) {
            errMsg = getText(R.string.live_end);
        } else if (status == ActionStatus.STATUS_LIVE_ING) {
            errMsg = getText(R.string.live_sig_recovery);
        } else {
            errMsg = getText(R.string.live_no_sig);
        }
        videoTips("", "", errMsg, getText(R.string.replay), getText(R.string.submit_error_info));
    }

    public void processLiveStatus(int status) {
        String errMsg = "";
        if (status == LiveInfo.STATUS_NOT_USE) {
            errMsg = getText(R.string.live_no_sig);
        } else if (status == LiveInfo.STATUS_END) {
            errMsg = getText(R.string.live_end);
        } else if (status == LiveInfo.STATUS_ON_USE) {
            errMsg = getText(R.string.live_sig_recovery);
        } else {
            errMsg = getText(R.string.live_no_sig);
        }
        videoTips("", "", errMsg, getText(R.string.replay), getText(R.string.submit_error_info));
    }

    private void processActionMediaError(Bundle data) {
        mEventCode = data.getString(PlayerParams.KEY_RESULT_ERROR_CODE);
        String errMsg = "";
        if ("E06101".equals(mEventCode)) {
            // 超出观看人数
            errMsg = getText(R.string.people_more);
        } else if ("E06102".equals(mEventCode)) {
            // 代理服务器出现黑名单，无法播放
            errMsg = getText(R.string.proxy_black_list);
        } else if ("E06103".equals(mEventCode)) {
            // 加密失败
            errMsg = getText(R.string.linkshell_fail);
        } else if ("E06104".equals(mEventCode)) {
            // 无直播计划
            errMsg = getText(R.string.no_live_plan);
        } else {
            // 未知错误
            errMsg = data.getString(PlayerParams.KEY_RESULT_ERROR_MSG);
        }
        videoTips(mEventCode, errMsg, getText(R.string.letv_notice_message), getText(R.string.replay), getText(R.string.submit_error_info));
    }

    // TODO: 16/5/27 1.player state 2.mediadata state 两个方法
    public void processPlayerState(int event, Bundle data) {
    	int code = 0;
    	if(data != null){
    		code = data.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
    	}
        switch (event) {
            case PlayerEvent.AD_ERROR:
            case PlayerEvent.PLAY_ERROR:
//                mEventCode = "" + event;
                if(code == StatusCode.PLAY_ERROR_NONETWORK){
                    videoTips("" + 10000, getText(R.string.net_fail), getText(R.string.net_error), getText(R.string.replay), getText(R.string.submit_error_info));
                }else{
                	videoTips("" + code, getText(R.string.play_fail), getText(R.string.letv_notice_message), getText(R.string.replay), getText(R.string.submit_error_info));
                }
                break;
            case PlayerEvent.PLAY_INFO: {
                if (code == StatusCode.PLAY_INFO_BUFFERING_START && !NetworkUtils.hasConnect(mContext)) {
                    mEventCode = "" + 10000;
                    videoTips("" + 10000, getText(R.string.net_fail), getText(R.string.net_error), getText(R.string.replay), getText(R.string.submit_error_info));
                } else if (code == StatusCode.PLAY_INFO_VIDEO_RENDERING_START ||
                        code == StatusCode.PLAY_INFO_BUFFERING_END
                        || code == StatusCode.PLAY_INFO_BUFFERING_START) {
                    setVisibility(View.GONE);
                }
                break;
            }
            case PlayerEvent.PLAY_COMPLETION:
//                if (isLive && !NetworkUtils.hasConnect(mContext)) {
//                    mEventCode = "" + 10000;
//                    videoTips("" + 10000, getText(R.string.net_fail), getText(R.string.net_error), getText(R.string.replay), getText(R.string.submit_error_info));
//                }
                if (isLive) {
                    mEventCode = "" + 10000;
                    videoTips("" + 10000, getText(R.string.play_fail), getText(R.string.letv_notice_message), getText(R.string.replay), getText(R.string.submit_error_info));
                }
                break;
            default:
                break;
        }
    }
    public void setRePlayListener(IReplayListener rePlayListener) {
        mRePlayListener = rePlayListener;
    }

    public interface IReplayListener {
        Bundle getReportParams();

        void onRePlay();
    }


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_UP){
			
			if(v.getId() == R.id.btn_error_replay){
				v.performClick();
			     // 重新播放
                if (!NetworkUtils.hasConnect(mContext)) {
                    Toast.makeText(mContext, getText(R.string.network_none), Toast.LENGTH_SHORT).show();
                    return true;
                }
                setVisibility(View.GONE);
                mRePlayListener.onRePlay();
			}else if(v.getId() == R.id.btn_error_report){
				v.performClick();
				  // 错误反馈
                Intent intent = new Intent(mContext, FeedBackActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mRePlayListener != null && mRePlayListener.getReportParams() != null) {
                    Bundle params = mRePlayListener.getReportParams();
                    intent.putExtra("params", params);
                }
                mContext.startActivity(intent);
                return true;
			}
		}
		return true;
	}
    
	@Override
	public boolean performClick() {
		super.performClick();
	    return true;
	}
}

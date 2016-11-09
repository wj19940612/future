package com.lecloud.skin.videoview.live;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import com.lecloud.sdk.api.md.entity.live.LiveInfo;
import com.lecloud.sdk.api.md.entity.live.Stream;
import com.lecloud.sdk.api.md.entity.vod.cloud.MediaEntity;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.IPlayer;
import com.lecloud.sdk.videoview.live.LiveVideoView;
import com.lecloud.skin.ui.ILetvLiveUICon;
import com.lecloud.skin.ui.ILetvVodUICon;
import com.lecloud.skin.ui.LetvLiveUIListener;
import com.lecloud.skin.ui.impl.LetvLiveUICon;
import com.lecloud.skin.ui.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class UILiveVideoView extends LiveVideoView {
    protected ILetvLiveUICon letvLiveUICon;
    protected List<MediaEntity> medialists;
    protected int width = -1;
    protected int height = -1;
    private boolean isSeeking = false;
    
    public UILiveVideoView(Context context) {
        super(context);
        initUICon(context);
    }

    private void initUICon(final Context context) {
        letvLiveUICon = new LetvLiveUICon(context);
//        letvLiveUICon.setGravitySensor(false);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(letvLiveUICon.getView(), params);
        letvLiveUICon.setLetvLiveUIListener(new LetvLiveUIListener() {


            @Override
            public void setRequestedOrientation(int requestedOrientation) {
                if (context instanceof Activity) {
                    ((Activity) context).setRequestedOrientation(requestedOrientation);
                }
            }

            @Override
            public void resetPlay() {
                // LeLog.dPrint(TAG, "--------resetPlay");
            }

            @Override
            public void onSetDefination(int type) {
//            	((IMediaDataPlayer) player).setDataSourceByRate(medialists.get(type).getVtype());
            }

            @Override
            public void onSeekTo(float sec) {
//                long msec = (long) (sec * player.getDuration());
                if (player != null) {
                    player.seekTo((long) sec);
                }
                
            }

            @Override
            public void onClickPlay() {
                letvLiveUICon.setPlayState(!player.isPlaying());
                if (player.isPlaying()) {
                    player.stop();
                    player.reset();
                    player.release();
                    letvLiveUICon.showController(false);
                    enablePanoGesture(false);
                } else {
                    player.retry();
                    letvLiveUICon.showController(true);
                    enablePanoGesture(false);
                }
            }

            @Override
            public void onUIEvent(int event, Bundle bundle) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public int onSwitchPanoControllMode(int controllMode) {
                return switchControllMode(controllMode);
            }

            @Override
            public int onSwitchPanoDisplayMode(int displayMode) {
                return switchDisplayMode(displayMode);
            }

			@Override
			public void onProgressChanged(int progress) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStartSeek() {
				// TODO Auto-generated method stub
				isSeeking = true;
			}

            @Override
            public void onEndSeek() {
                isSeeking = false;
            }

        });
    }

    protected int switchControllMode(int interactiveMode) {
        return 0;
    }

    protected int switchDisplayMode(int displayMode) {
        return 0;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (ScreenUtils.getOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            letvLiveUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT, this);
        } else {
            letvLiveUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_LANDSCAPE,this);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onInterceptLiveMediaDataSuccess(int event, Bundle bundle) {
        super.onInterceptLiveMediaDataSuccess(event, bundle);
        List<String> ratetypes = new ArrayList<String>();
        LiveInfo liveInfo = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
        List<Stream> mStreams = liveInfo.getStreams();
        String definition = onInterceptSelectDefiniton(liveInfo.getVtypes(), liveInfo.getDefaultVtype());
        for (Stream stream : mStreams) {
            ratetypes.add(stream.getRateName());
        }
        letvLiveUICon.setRateTypeItems(ratetypes, definition);
    }

    @Override
    protected void notifyPlayerEvent(int state, Bundle bundle) {
        super.notifyPlayerEvent(state, bundle);
        switch (state) {
            case PlayerEvent.PLAY_ERROR: {
                int code = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
                if (code == StatusCode.PLAY_ERROR_PREPARE) {
                    letvLiveUICon.setPlayState(false);
                }
                break;
            }
            case PlayerEvent.PLAY_COMPLETION:
                letvLiveUICon.setPlayState(false);
                break;
            case PlayerEvent.PLAY_INFO:
                int code = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
                if (code == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {

                }
                break;
            case PlayerEvent.PLAY_SEEK_COMPLETE: {//209
                isSeeking = false;
                break;
            }
            default:
                break;
        }
    }
    
    protected void enablePanoGesture(boolean enable){
    	
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isComplete() {
        //TODO
        return player != null && player.getStatus() == IPlayer.PLAYER_STATUS_EOS;
    }
    
    @Override
    public void onResume() {
        //横屏设置ILetvVodUICon.SCREEN_LANDSCAPE,竖屏设置ILetvVodUICon.SCREEN_PORTRAIT
        if(isFirstPlay){
            letvLiveUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT, this);
            isFirstPlay = false;
        }
        letvLiveUICon.showController(true);
        enablePanoGesture(true);
        super.onResume();
    }
}

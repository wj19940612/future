package com.jnhyxx.html5.domain.live;

/**
 * Created by ${wangJie} on 2016/10/25.
 */

public class LiveRoomInfo {


    /**
     * activityId : jijwiejijfeiwojfo
     * liveTv : 0
     */

    private String activityId;
    private int liveTv;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public int getLiveTv() {
        return liveTv;
    }

    public void setLiveTv(int liveTv) {
        this.liveTv = liveTv;
    }

    @Override
    public String toString() {
        return "LiveRoomInfo{" +
                "activityId='" + activityId + '\'' +
                ", liveTv=" + liveTv +
                '}';
    }
}

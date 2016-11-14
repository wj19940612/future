package com.jnhyxx.html5.domain.live;

import android.text.TextUtils;

import java.util.List;

public class LiveMessage {

    /**
     * active: {
        activityId: "12",
        hls: "",
        liveTv: 0,
        rtmp: ""
        },
     */
    private ActiveInfo active;

    /**
     * account : haibo
     * channelId : 12
     * createTime : 2016-10-25 19:00:53
     * goodAt : 外盘：原油，黄金
     * id : 22
     * introduction : 主修金融证券专业，微盘期货首席分析师 8年实战操盘经验
     做单风格：以雷厉风行著称，以快、准、稳为主，通常小止损大盈利。擅长内盘镍铜橡胶波段狙击，外盘美原油 纳指 恒指 德指 短线收割。千万谨记要跟紧哦~
     座右铭：良好的心态+精湛的技术——-成功的砝码
     * name : 海波
     * pictureUrl : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1477393215749088083.png
     * teacherAccountId : 146
     * updateTime : 2016-10-25 19:00:53
     */

    private TeacherInfo teacher;
    /**
     * channelId : 12
     * content : 平台直播时间：^ [上午]09:00-11:00 刘老师^ [下午]13:30-15:30 刘老师^ [晚上]19:30-23:30 金虎老师^
     * createTime : 2016-10-25 14:07:49
     * id : 14
     * status : 1
     * title : 财经直播
     * updateTime : 2016-11-01 17:30:09
     */

    private NoticeInfo notice;
    /**
     * channelId : 12
     * createTime : 2016-10-21 12:50:28
     * cycleStr : 周一,周三,周四,周五
     * id : 8
     * liveTime : 09:00-16:50
     * pictureUrl : https://hystock.oss-cn-qingdao.aliyuncs.com/ueditor/1477449444225008026.png
     * teacherId : 17
     * teacherName : 叶老师
     * updateTime : 2016-10-26 09:19:04
     */

    private List<ProgramInfo> program;

    public TeacherInfo getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherInfo teacher) {
        this.teacher = teacher;
    }

    public ActiveInfo getActive() {
        return active;
    }

    public NoticeInfo getNotice() {
        return notice;
    }

    public void setNotice(NoticeInfo notice) {
        this.notice = notice;
    }

    public List<ProgramInfo> getProgram() {
        return program;
    }

    public void setProgram(List<ProgramInfo> program) {
        this.program = program;
    }

    public static class ActiveInfo {
        private String activityId;
        private String hls;
        private int liveTv;
        private String rtmp;

        public String getRtmp() {
            return rtmp;
        }
    }

    public static class TeacherInfo {
        private String account;
        private int channelId;
        private String createTime;
        private String goodAt;
        private int id;
        private String introduction;
        private String name;
        private String pictureUrl;
        private int teacherAccountId;
        private String updateTime;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getGoodAt() {
            return goodAt;
        }

        public void setGoodAt(String goodAt) {
            this.goodAt = goodAt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPictureUrl() {
            return pictureUrl;
        }

        public void setPictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
        }

        public int getTeacherAccountId() {
            return teacherAccountId;
        }

        public void setTeacherAccountId(int teacherAccountId) {
            this.teacherAccountId = teacherAccountId;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        @Override
        public String toString() {
            return "TeacherInfo{" +
                    "account='" + account + '\'' +
                    ", channelId=" + channelId +
                    ", createTime='" + createTime + '\'' +
                    ", goodAt='" + goodAt + '\'' +
                    ", id=" + id +
                    ", introduction='" + introduction + '\'' +
                    ", name='" + name + '\'' +
                    ", pictureUrl='" + pictureUrl + '\'' +
                    ", teacherAccountId=" + teacherAccountId +
                    ", updateTime='" + updateTime + '\'' +
                    '}';
        }
    }

    public static class NoticeInfo {
        private int channelId;
        private String content;
        private String createTime;
        private int id;
        private int status;
        private String title;
        private String updateTime;

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getFormattedContent() {
            if (!TextUtils.isEmpty(content)) {
                return content.replace("^", "\n");
            }
            return content;
        }

        @Override
        public String toString() {
            return "NoticeInfo{" +
                    "channelId=" + channelId +
                    ", content='" + content + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", id=" + id +
                    ", status=" + status +
                    ", title='" + title + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    '}';
        }
    }

    public static class ProgramInfo {
        private int channelId;
        private String createTime;
        private String cycleStr;
        private int id;
        private String liveTime;
        private String pictureUrl;
        private int teacherId;
        private String teacherName;
        private String updateTime;

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCycleStr() {
            return cycleStr;
        }

        public void setCycleStr(String cycleStr) {
            this.cycleStr = cycleStr;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLiveTime() {
            return liveTime;
        }

        public void setLiveTime(String liveTime) {
            this.liveTime = liveTime;
        }

        public String getPictureUrl() {
            return pictureUrl;
        }

        public void setPictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
        }

        public int getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(int teacherId) {
            this.teacherId = teacherId;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        @Override
        public String toString() {
            return "ProgramInfo{" +
                    "channelId=" + channelId +
                    ", createTime='" + createTime + '\'' +
                    ", cycleStr='" + cycleStr + '\'' +
                    ", id=" + id +
                    ", liveTime='" + liveTime + '\'' +
                    ", pictureUrl='" + pictureUrl + '\'' +
                    ", teacherId=" + teacherId +
                    ", teacherName='" + teacherName + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LiveMessage{" +
                "teacher=" + teacher +
                ", notice=" + notice +
                ", program=" + program +
                '}';
    }
}

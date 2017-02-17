package com.jnhyxx.html5.domain.msg;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/2/17.
 */

public class CalendarFinanceModel {


    /**
     * title : 第四季度零售销售年率
     * code : NZRREXIN Index
     * time : 2017-02-17 05:45
     * predicttime : 05:45
     * importance : 中
     * autoid : 270902
     * id : NZRREXINIndex201702170545
     * type : 2
     * state : 新西兰
     * forecast : 1%
     * effecttype : 0
     * effect : 美元|金银 石油|
     * before : 0.9%
     * reality : 0.8%
     */

    private List<EconomicCalendarsBean> EconomicCalendars;
    private List<?> HolidayNotices;

    /**
     * title : 挪威央行行长奥尔森将发表年度演说
     * state : 挪威
     * importance : 低
     * time : 02:00
     */

    private List<ImportThingsBean> ImportThings;

    public List<EconomicCalendarsBean> getEconomicCalendars() {
        return EconomicCalendars;
    }

    public void setEconomicCalendars(List<EconomicCalendarsBean> EconomicCalendars) {
        this.EconomicCalendars = EconomicCalendars;
    }

    public List<?> getHolidayNotices() {
        return HolidayNotices;
    }

    public void setHolidayNotices(List<?> HolidayNotices) {
        this.HolidayNotices = HolidayNotices;
    }

    public List<ImportThingsBean> getImportThings() {
        return ImportThings;
    }

    public void setImportThings(List<ImportThingsBean> ImportThings) {
        this.ImportThings = ImportThings;
    }

    public static class EconomicCalendarsBean {
        private String title;
        private String code;
        private String time;
        private String predicttime;
        private String importance;
        private int autoid;
        private String id;
        private String type;
        private String state;
        private String forecast;
        private int effecttype;
        private String effect;
        private String before;
        private String reality;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPredicttime() {
            return predicttime;
        }

        public void setPredicttime(String predicttime) {
            this.predicttime = predicttime;
        }

        public String getImportance() {
            return importance;
        }

        public void setImportance(String importance) {
            this.importance = importance;
        }

        public int getAutoid() {
            return autoid;
        }

        public void setAutoid(int autoid) {
            this.autoid = autoid;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getForecast() {
            return forecast;
        }

        public void setForecast(String forecast) {
            this.forecast = forecast;
        }

        public int getEffecttype() {
            return effecttype;
        }

        public void setEffecttype(int effecttype) {
            this.effecttype = effecttype;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }

        public String getBefore() {
            return before;
        }

        public void setBefore(String before) {
            this.before = before;
        }

        public String getReality() {
            return reality;
        }

        public void setReality(String reality) {
            this.reality = reality;
        }

        /**
         * 是否利多  否则利空
         *
         * @return
         */
        public boolean isLido() {
            if (getEffect().contains("||")) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "EconomicCalendarsBean{" +
                    "title='" + title + '\'' +
                    ", code='" + code + '\'' +
                    ", time='" + time + '\'' +
                    ", predicttime='" + predicttime + '\'' +
                    ", importance='" + importance + '\'' +
                    ", autoid=" + autoid +
                    ", id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", state='" + state + '\'' +
                    ", forecast='" + forecast + '\'' +
                    ", effecttype=" + effecttype +
                    ", effect='" + effect + '\'' +
                    ", before='" + before + '\'' +
                    ", reality='" + reality + '\'' +
                    '}';
        }
    }

    public static class ImportThingsBean {
        private String title;
        private String state;
        private String importance;
        private String time;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getImportance() {
            return importance;
        }

        public void setImportance(String importance) {
            this.importance = importance;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "ImportThingsBean{" +
                    "title='" + title + '\'' +
                    ", state='" + state + '\'' +
                    ", importance='" + importance + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CalendarFinanceModel{" +
                "EconomicCalendars=" + EconomicCalendars +
                ", HolidayNotices=" + HolidayNotices +
                ", ImportThings=" + ImportThings +
                '}';
    }
}

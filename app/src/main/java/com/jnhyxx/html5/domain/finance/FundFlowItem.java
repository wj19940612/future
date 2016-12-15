package com.jnhyxx.html5.domain.finance;

public class FundFlowItem {

    public static final int TYPE_FUND_IN = 1;
    public static final int TYPE_FUND_OUT = -1;

    /**
     * id : 35118
     * type : 1
     * flowway : 200402
     * intro : 任务奖励
     * curflowAmt : 100000.00
     * status : 0
     * createDate : 2016-07-04 13:44:44
     */

    private int id;
    private int type;
    private int flowway;
    private String intro;
    private String curflowAmt;
    private int status;
    private String createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFlowway() {
        return flowway;
    }

    public void setFlowway(int flowway) {
        this.flowway = flowway;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCurflowAmt() {
        return curflowAmt;
    }

    public void setCurflowAmt(String curflowAmt) {
        this.curflowAmt = curflowAmt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getFormattedCreateDate() {
        String[] createDate = getCreateDate().trim().split(" ");
        if (createDate.length == 2) {
            return createDate[0] + "\n" + createDate[1];
        }
        return getCreateDate();
    }
}

package com.jnhyxx.html5.domain.live;

/**
 * Created by ${wangJie} on 2016/11/14.
 * 用户发言后返回的数据
 */

public class LiveSpeakInfo {

    /**
     * msg : testJ
     * owner : true
     * slience : false
     * accountType : 2
     * expire : false
     * name : 昵称123abc10
     * time : 1479092614509
     * isText : true
     * isOrder : false
     */

    private String msg;
    private boolean owner;
    private boolean slience;
    private int accountType;
    private boolean expire;
    private String name;
    private long time;
    private boolean isText;
    private boolean isOrder;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isSlience() {
        return slience;
    }

    public void setSlience(boolean slience) {
        this.slience = slience;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public boolean isExpire() {
        return expire;
    }

    public void setExpire(boolean expire) {
        this.expire = expire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isIsText() {
        return isText;
    }

    public void setIsText(boolean isText) {
        this.isText = isText;
    }

    public boolean isIsOrder() {
        return isOrder;
    }

    public void setIsOrder(boolean isOrder) {
        this.isOrder = isOrder;
    }
}

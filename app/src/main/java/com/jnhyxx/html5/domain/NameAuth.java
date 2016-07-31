package com.jnhyxx.html5.domain;

import java.io.Serializable;

public class NameAuth implements Serializable {

    public static final int STATUS_NOT_FILLED = 0; // 未填写,未认证
    public static final int STATUS_BE_BOUND = 1; // 已填写,已认证(绑定)
    public static final int STATUS_FILLED = 2; // 已填写,未认证

    /**
     * status : 1
     * userName : 张三
     * idCardNum : 330****3245
     */

    private int status;
    private String userName;
    private String idCardNum;

    public NameAuth(int status, String userName, String idCardNum) {
        this.status = status;
        this.userName = userName;
        this.idCardNum = idCardNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    /**
     * Real name auth result should be the same as NameAuth. fuck!!
     */
    public class Result implements Serializable {

        /**
         * realName : 张栩恺
         * idCard : 3****0
         */

        private String realName;
        private String idCard;

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }
    }
}

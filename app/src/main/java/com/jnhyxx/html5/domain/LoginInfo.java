package com.jnhyxx.html5.domain;

public class LoginInfo {

    /**
     * nickStatus : 1
     * nickUpdateDate : 2015-06-08 10:55:43
     * tokenInfo : {"userSecret":"102097EAF3184759CE6289534679F068","token":"0b3b6b67832586d053cac5016536f67786f050dad85051b ","isdeline":false,"userId":null}
     * userInfo : {"tele":"18268198627","name":"李四","nick":"testnick1","user_cls":"普通用户","sex":"未知","birth":null,"head_pic":"/data/webserver/stock/user/ACFD581F3E748.png","person_sign":"tffs","is_set_gesture_pwd":"1","is_start_gesture":"1","is_openid_login":"0"}
     */

    private int nickStatus;
    private String nickUpdateDate;
    /**
     * userSecret : 102097EAF3184759CE6289534679F068
     * token : 0b3b6b67832586d053cac5016536f67786f050dad85051b
     * isdeline : false
     * userId : null
     */

    private TokenInfo tokenInfo;
    /**
     * tele : 18268198627
     * name : 李四
     * nick : testnick1
     * user_cls : 普通用户
     * sex : 未知
     * birth : null
     * head_pic : /data/webserver/stock/user/ACFD581F3E748.png
     * person_sign : tffs
     * is_set_gesture_pwd : 1
     * is_start_gesture : 1
     * is_openid_login : 0
     */

    private UserInfo userInfo;

    public int getNickStatus() {
        return nickStatus;
    }

    public void setNickStatus(int nickStatus) {
        this.nickStatus = nickStatus;
    }

    public String getNickUpdateDate() {
        return nickUpdateDate;
    }

    public void setNickUpdateDate(String nickUpdateDate) {
        this.nickUpdateDate = nickUpdateDate;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class TokenInfo {
        private String userSecret;
        private String token;
        private boolean isdeline;
        private Object userId;

        public String getUserSecret() {
            return userSecret;
        }

        public void setUserSecret(String userSecret) {
            this.userSecret = userSecret;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isIsdeline() {
            return isdeline;
        }

        public void setIsdeline(boolean isdeline) {
            this.isdeline = isdeline;
        }

        public Object getUserId() {
            return userId;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }
    }

    public static class UserInfo {
        private String tele;
        private String name;
        private String nick;
        private String user_cls;
        private String sex;
        private Object birth;
        private String head_pic;
        private String person_sign;
        private String is_set_gesture_pwd;
        private String is_start_gesture;
        private String is_openid_login;

        public String getTele() {
            return tele;
        }

        public void setTele(String tele) {
            this.tele = tele;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getUser_cls() {
            return user_cls;
        }

        public void setUser_cls(String user_cls) {
            this.user_cls = user_cls;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Object getBirth() {
            return birth;
        }

        public void setBirth(Object birth) {
            this.birth = birth;
        }

        public String getHead_pic() {
            return head_pic;
        }

        public void setHead_pic(String head_pic) {
            this.head_pic = head_pic;
        }

        public String getPerson_sign() {
            return person_sign;
        }

        public void setPerson_sign(String person_sign) {
            this.person_sign = person_sign;
        }

        public String getIs_set_gesture_pwd() {
            return is_set_gesture_pwd;
        }

        public void setIs_set_gesture_pwd(String is_set_gesture_pwd) {
            this.is_set_gesture_pwd = is_set_gesture_pwd;
        }

        public String getIs_start_gesture() {
            return is_start_gesture;
        }

        public void setIs_start_gesture(String is_start_gesture) {
            this.is_start_gesture = is_start_gesture;
        }

        public String getIs_openid_login() {
            return is_openid_login;
        }

        public void setIs_openid_login(String is_openid_login) {
            this.is_openid_login = is_openid_login;
        }
    }
}

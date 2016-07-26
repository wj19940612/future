package com.jnhyxx.html5.domain;

public class LoginInfo {

    /**
     * tele : 13567124531
     * name : null
     * nick : 操盘手30010580
     * user_cls : 普通用户
     * sex : 0
     * birth :
     * head_pic :
     * provice :
     * city :
     * region :
     * address :
     * reg_date :
     * person_sign : null
     * is_staff : 0
     * is_set_gesture_pwd : 0
     * is_start_gesture : 0
     * is_openid_login : 0
     * couponcount : 0
     * promotion_url : 9a3623
     */

    private UserInfoBean userInfo;
    /**
     * userInfo : {"tele":"13567124531","name":null,"nick":"操盘手30010580","user_cls":"普通用户","sex":"0","birth":"","head_pic":"","provice":"","city":"","region":"","address":"","reg_date":"","person_sign":null,"is_staff":"0","is_set_gesture_pwd":"0","is_start_gesture":"0","is_openid_login":"0","couponcount":0,"promotion_url":"9a3623"}
     * nickStatus : 0
     * tokenInfo : {"userSecret":"DF1C9FFB7BD2E9E5171EDE2416563991","token":"4a7525256064c3b4da378ac1df505649e0c58e72d1533a08979628a6a7047d1f8de9c13ecce52192d2e31e3a7777e08ccb1fb9bef6b88e842674c452ba49694a","isdeline":false,"userId":null}
     */

    private int nickStatus;
    /**
     * userSecret : DF1C9FFB7BD2E9E5171EDE2416563991
     * token : 4a7525256064c3b4da378ac1df505649e0c58e72d1533a08979628a6a7047d1f8de9c13ecce52192d2e31e3a7777e08ccb1fb9bef6b88e842674c452ba49694a
     * isdeline : false
     * userId : null
     */

    private TokenInfoBean tokenInfo;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public int getNickStatus() {
        return nickStatus;
    }

    public void setNickStatus(int nickStatus) {
        this.nickStatus = nickStatus;
    }

    public TokenInfoBean getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfoBean tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public static class UserInfoBean {
        private String tele;
        private Object name;
        private String nick;
        private String user_cls;
        private String sex;
        private String birth;
        private String head_pic;
        private String provice;
        private String city;
        private String region;
        private String address;
        private String reg_date;
        private Object person_sign;
        private String is_staff;
        private String is_set_gesture_pwd;
        private String is_start_gesture;
        private String is_openid_login;
        private int couponcount;
        private String promotion_url;

        public String getTele() {
            return tele;
        }

        public void setTele(String tele) {
            this.tele = tele;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
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

        public String getBirth() {
            return birth;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public String getHead_pic() {
            return head_pic;
        }

        public void setHead_pic(String head_pic) {
            this.head_pic = head_pic;
        }

        public String getProvice() {
            return provice;
        }

        public void setProvice(String provice) {
            this.provice = provice;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getReg_date() {
            return reg_date;
        }

        public void setReg_date(String reg_date) {
            this.reg_date = reg_date;
        }

        public Object getPerson_sign() {
            return person_sign;
        }

        public void setPerson_sign(Object person_sign) {
            this.person_sign = person_sign;
        }

        public String getIs_staff() {
            return is_staff;
        }

        public void setIs_staff(String is_staff) {
            this.is_staff = is_staff;
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

        public int getCouponcount() {
            return couponcount;
        }

        public void setCouponcount(int couponcount) {
            this.couponcount = couponcount;
        }

        public String getPromotion_url() {
            return promotion_url;
        }

        public void setPromotion_url(String promotion_url) {
            this.promotion_url = promotion_url;
        }
    }

    public static class TokenInfoBean {
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
}

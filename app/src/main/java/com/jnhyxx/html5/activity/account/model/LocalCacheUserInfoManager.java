package com.jnhyxx.html5.activity.account.model;

import com.jnhyxx.html5.domain.local.User;

/**
 * Created by Administrator on 2016/8/31.
 * 本地的用户信息缓存
 */

public class LocalCacheUserInfoManager {
    private UserInfo user;

    private LocalCacheUserInfoManager() {

    }

    static class Instance {
        static LocalCacheUserInfoManager localCacheUserInfoManager = new LocalCacheUserInfoManager();
    }

    public static LocalCacheUserInfoManager getInstance() {
        return Instance.localCacheUserInfoManager;
    }

    public UserInfo getUser() {
        synchronized (LocalCacheUserInfoManager.class) {
            return user;
        }
    }

    public void setUser(UserInfo user) {
        synchronized (LocalCacheUserInfoManager.class) {
            this.user = user;
        }
    }

    public boolean isLogin() {
        return getUser() != null;
    }

}

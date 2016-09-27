package com.jnhyxx.html5.domain.account;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/10.
 * 判断用户是否更新过昵称
 */

public class NickNameIsModified implements Serializable {
    private static final long serialVersionUID = 559906528129182531L;
    /**
     * bIsSetNickName : true
     * userName : 123
     */
    /**
     * 是否更新过  false 没有更新
     *            true 更新过
     */
    private boolean bIsSetNickName;
    private String userName;

    public boolean isModified() {
        return bIsSetNickName;
    }

    @Override
    public String toString() {
        return "UserIsModifyNickName{" +
                "bIsSetNickName=" + bIsSetNickName +
                ", userName='" + userName + '\'' +
                '}';
    }
}

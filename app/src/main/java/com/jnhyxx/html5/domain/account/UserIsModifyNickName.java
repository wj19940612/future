package com.jnhyxx.html5.domain.account;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/10.
 * 判断用户是否更新过昵称
 */

public class UserIsModifyNickName implements Serializable {
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

    public static UserIsModifyNickName objectFromData(String str) {

        return new Gson().fromJson(str, UserIsModifyNickName.class);
    }

    public static UserIsModifyNickName objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), UserIsModifyNickName.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<UserIsModifyNickName> arrayUserIsModifyNickNameFromData(String str) {

        Type listType = new TypeToken<ArrayList<UserIsModifyNickName>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<UserIsModifyNickName> arrayUserIsModifyNickNameFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<UserIsModifyNickName>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public boolean isBIsSetNickName() {
        return bIsSetNickName;
    }

    public void setBIsSetNickName(boolean bIsSetNickName) {
        this.bIsSetNickName = bIsSetNickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserIsModifyNickName{" +
                "bIsSetNickName=" + bIsSetNickName +
                ", userName='" + userName + '\'' +
                '}';
    }
}

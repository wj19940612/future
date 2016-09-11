package com.jnhyxx.html5.netty;

import com.johnz.kutils.SecurityUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;

@Deprecated
public class NettyLoginFactory {

    private static final String FUTURES_TYPE = "futuresType";
    private static final String SIGN = "sign";
    private static final String TIMESTAMP = "timestamp";
    private static final String TYPE = "type";
    private static final String USER_NAME = "userName";
    private static final String NEW_VERSION = "newversion";

    private static final String TYPE_LOGIN = "LOGIN";

    /**
     * 返回连接登入Json数据
     * @return
     */
    public static JSONObject createLoginJson(String futureType) throws JSONException, NoSuchAlgorithmException {
        String timestamp = createTimestamp();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FUTURES_TYPE, futureType.toUpperCase());
        jsonObject.put(SIGN, SecurityUtil.md5Encrypt("cainiu" + timestamp + NEW_VERSION));
        jsonObject.put(TIMESTAMP, timestamp);
        jsonObject.put(TYPE, TYPE_LOGIN);
        return jsonObject;
    }

    private static String createTimestamp() {
        Calendar calendar = Calendar.getInstance();
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        return timestamp.toString();
    }
}

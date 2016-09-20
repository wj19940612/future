package com.jnhyxx.html5.net;

import android.os.AsyncTask;
import android.util.Log;

import com.johnz.kutils.net.CookieManger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by ${wangJie} on 2016/9/19.
 */

public class RechargeAsyncTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        try {
// 建立连接
            URL url = new URL(params[0]);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            String cookies = CookieManger.getInstance().getCookies();
            httpConn.setRequestProperty("Cookie", cookies);

// //设置连接属性
            httpConn.setDoOutput(true);// 使用 URL 连接进行输出
            httpConn.setDoInput(true);// 使用 URL 连接进行输入
            httpConn.setUseCaches(false);// 忽略缓存
            httpConn.setRequestMethod("POST");// 设置URL请求方法
            String requestString = "客服端要以以流方式发送到服务端的数据...";


// 设置请求属性
// 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
            httpConn.setRequestProperty("Content-Type", "application/octet-stream");
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpConn.setRequestProperty("Charset", "UTF-8");
//
            String name = URLEncoder.encode("50", "utf-8");
            httpConn.setRequestProperty("money", name);

            String param="money="+URLEncoder.encode("50","UTF-8");
// 建立输出流，并写入数据
            OutputStream outputStream = httpConn.getOutputStream();
//            outputStream.write(param);
            outputStream.close();
// 获得响应状态
            int responseCode = httpConn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                return sb.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("wj", s.toString());
    }
}

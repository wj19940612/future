package com.jnhyxx.html5.net;

import android.os.AsyncTask;
import android.util.Log;

import com.johnz.kutils.net.CookieManger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ${wangJie} on 2016/9/19.
 */

public class RechargeAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "RechargeAsyncTask";
    double mMoney;

    RechargeListener mRechargeListener;

    public RechargeAsyncTask(double money) {
        this.mMoney = money;
    }

    public RechargeAsyncTask(double money, RechargeListener listener) {
        this.mMoney = money;
        this.mRechargeListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            String data = "money=" + mMoney;

            URL url = new URL(params[0]);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            String cookies = CookieManger.getInstance().getCookies();
            httpConn.setRequestProperty("Cookie", cookies);

            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/Json");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("Charset", "UTF-8");
//            httpConn.setRequestProperty("Content-Length", data.length() + "");


//            OutputStream outputStream = httpConn.getOutputStream();
//            outputStream.write(data.getBytes());
//            outputStream.flush();
//            outputStream.close();
//
////            String param = "money=" + URLEncoder.encode(String.valueOf(mMoney), "UTF-8");
////            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
////            dataOutputStream.writeBytes(param);
////            outputStream.flush();
//            outputStream.close();
            int responseCode = httpConn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
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
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "下载的结果" + result);
        mRechargeListener.getData(result);
    }

    public interface RechargeListener {
        void getData(String result);
    }
}

package com.liuming.mylibrary.utils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2017/1/12.
 */

public class XHttpHelper {


    /**
     * 执行请求任务
     *
     * @param url
     * @param params
     */
    public static void doRequest(String url, HashMap<String, String> params, final Callback.CommonCallback callback) {
        x.http().post(createRequest(url, params), callback);

    }

    /**
     * 创建请求参数
     *
     * @param url
     * @param params
     * @return
     */
    private static RequestParams createRequest(String url, HashMap<String, String> params) {
        RequestParams requestParams = new RequestParams(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            requestParams.addBodyParameter(entry.getKey(), entry.getValue());
        }
        return requestParams;
    }


    public static void get(final String url, final HashMap<String, String> params, final Callback.CommonCallback callback) {
        new Thread() {
            @Override
            public void run() {
                try {
                    StringBuilder urlBuilder = new StringBuilder();
//                    urlBuilder.append("?");
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                    urlBuilder.deleteCharAt(urlBuilder.lastIndexOf("&"));
                    String encode = URLEncoder.encode(urlBuilder.toString(),"utf-8");

                    URL requestURL = new URL(url+"?"+encode);
                    HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
                    conn.setConnectTimeout(10 * 1000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                        BufferedReader br = new BufferedReader(reader);
                        StringBuilder result = new StringBuilder();
                        String buffer;
                        while ((buffer = br.readLine()) != null) {
                            result.append(buffer);
                        }
                        br.close();
                        callback.onSuccess(result.toString());
                        callback.onFinished();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    callback.onError(e, true);
                } catch (IOException e) {
                    callback.onError(e, true);
                    e.printStackTrace();
                }
            }
        }.start();
    }

}

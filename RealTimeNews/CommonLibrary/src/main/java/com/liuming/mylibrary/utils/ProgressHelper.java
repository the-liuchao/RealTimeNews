package com.liuming.mylibrary.utils;

import com.liuming.mylibrary.utils.okbody.ProgressRequestBody;
import com.liuming.mylibrary.utils.okbody.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hp on 2017/1/12.
 */

public class ProgressHelper {
    public static OkHttpClient addProgressResponseListener(OkHttpClient client
            , final OkHttpHelper.ProgressListener progressListener) {
        OkHttpClient clone = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originResponseBody = chain.proceed(chain.request());

                return originResponseBody.newBuilder()
                        .body(new ProgressResponseBody(originResponseBody.body(), progressListener))
                        .build();
            }
        }).build();
        return clone;
    }

    /**
     * 包装请求体用于上传文件的回调
     *
     * @param requestBody             请求体RequestBody
     * @param progressRequestListener 进度回调接口
     * @return 包装后的进度回调请求体
     */
    public static ProgressRequestBody addProgressRequestListener(RequestBody requestBody
            , OkHttpHelper.ProgressListener progressRequestListener) {
        //包装请求体
        return new ProgressRequestBody(requestBody, progressRequestListener);
    }
}

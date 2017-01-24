package com.liuming.mylibrary.utils;


import com.liuming.mylibrary.inter.UIProgressRequestListener;
import com.liuming.mylibrary.inter.UIProgressResponseListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by sll on 2015/3/10.
 */
public class OkHttpHelper {
    private OkHttpClient mOkHttpClient;

    public OkHttpHelper(OkHttpClient mOkHttpClient) {
        this.mOkHttpClient = mOkHttpClient;
    }

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public void enqueue(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public String getStringFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 上传文件
     *
     * @param url
     * @param file
     * @param progressListener
     */
    public void httpUpload(String url, long startPoints, File file, UIProgressRequestListener progressListener) {
        mOkHttpClient = new OkHttpClient.Builder().build();
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("type", "picture")
                .addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"another\";filename=\"another.dex\"")
//                        , RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder().url(url)
                .header("RANGE", "bytes=" + startPoints + "-")//断点续传
                .post(ProgressHelper.addProgressRequestListener(requestBody, progressListener)).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }


    /**
     * 下载
     *
     * @param url
     * @param target
     * @param progressListener
     * @throws Exception
     */
    public void httpDownload(String url, final long startPoints, final File target, UIProgressResponseListener progressListener) throws Exception {
        final Request request = new Request.Builder()
                .header("RANGE", "bytes=" + startPoints + "-")//断点下载
                .url(url).build();
        ProgressHelper
                .addProgressResponseListener(mOkHttpClient, progressListener)
                .newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
//                    BufferedSink sink = Okio.buffer(Okio.sink(target));
//                    sink.writeAll(response.body().source());
//                    sink.flush();
//                    sink.close();
                    save(response, target, startPoints);

                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    /**
     * 断点保存下载文件
     *
     * @param response
     * @param target
     * @param startPoints
     * @throws IOException
     */
    private void save(Response response, File target, long startPoints) throws IOException {
        ResponseBody body = response.body();
        FileChannel channelOut = null;
        RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rwd");
        channelOut = randomAccessFile.getChannel();
        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startPoints, body.contentLength());
        InputStream in = response.body().byteStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            mappedBuffer.put(buffer, 0, len);
        }
        in.close();
        channelOut.close();
        randomAccessFile.close();
    }

    public interface ProgressListener {
        void onProgress(long bytesRead, long contentLength, boolean done);
    }
}


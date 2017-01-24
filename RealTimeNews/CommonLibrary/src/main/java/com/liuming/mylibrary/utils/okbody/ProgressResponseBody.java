package com.liuming.mylibrary.utils.okbody;

import com.liuming.mylibrary.utils.OkHttpHelper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 响应体重新包装
 */
public class ProgressResponseBody extends ResponseBody {
    ResponseBody responseBody;
    OkHttpHelper.ProgressListener progressListener;
    //包装完成的BufferedSource
    BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, OkHttpHelper.ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (null == bufferedSource) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(BufferedSource source) {
        return new ForwardingSource(source) {
            long totalByte;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalByte += bytesRead != -1 ? bytesRead : 0;
                progressListener.onProgress(totalByte, contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}

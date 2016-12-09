
package com.xiaomi.client.download;

import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BaseRequest;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileDownloadRequest extends BaseRequest<File> {

    private static final Object sDecodeLock = new Object();
    private String mFilePath;
    private Handler mHandler;
    private Map<String, String> vParams = new HashMap<String, String>();

    public FileDownloadRequest(Builder<?> builder) {
        super(builder);
        this.mFilePath = builder.mFilePath;
        this.mHandler = builder.mHandler;
    }

    @Override
    protected Map<String, String> getPostParams() throws AuthFailureError {
        Map<String, String> vparams = super.getPostParams();
        if (vparams != null) {
            vparams.putAll(this.vParams);
        }
        return vparams;
    }

    @Override
    protected Response<File> parseNetworkResponse(NetworkResponse response) throws VolleyError {
        synchronized (sDecodeLock) {
            byte[] data = response.data;
            if (mGzipEnabled && isGzipped(response)) {
                try {
                    data = decompressResponse(data);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            File file = null;
            if (data != null && data.length != 0) {
                try {
                    file = new File(mFilePath);
                    FileOutputStream fOutStream = new FileOutputStream(file);
                    if (mHandler != null) {
                        final int nbufferSize = 1024;
                        int readSize = 0;
                        int nTotalSize = data.length;
                        while (readSize < nTotalSize) {
                            int nSize = nTotalSize - readSize > nbufferSize ? nbufferSize
                                    : nTotalSize - readSize;
                            fOutStream.write(data, readSize, nSize);
                            readSize += nbufferSize;
                            Message msg = new Message();
                            msg.obj = readSize + "k / " + nTotalSize + "k";
                            msg.arg1 = (int) (readSize * 100 / nTotalSize);
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        fOutStream.write(data);
                    }
                    fOutStream.flush();
                    fOutStream.close();
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                if (file == null) {
                    throw new ParseError(response);
                } else {
                    return Response.success(file, HttpHeaderParser.parseCacheHeaders(response));
                }
            }
        }
        throw new ParseError(response);
    }

    public static Builder<?> builder() {
        return new Builder();
    }

    public static class Builder<B extends Builder<B>> extends BaseRequest.Builder<B> {
        String mFilePath;
        Handler mHandler;

        public B setFileLocalPath(String filePath) {
            this.mFilePath = filePath;
            return self();
        }

        public B setDownloadHander(Handler handler) {
            this.mHandler = handler;
            return self();
        }

        public FileDownloadRequest build() {
            return new FileDownloadRequest(this);
        }

    }
}

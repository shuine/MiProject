package com.xiaomi.client.download;

import android.content.Context;
import android.os.Handler;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.HashMap;

/**
 * @author yeshuxin on 16-10-24.
 */

public class FileQueueManager extends RequestQueueManager{

    private final String TAG = "PluginQueueManager";
    private static FileQueueManager pluginInstance = null; // 给插件使用的实例

    public static FileQueueManager getQueueInstance() {
        if (null == pluginInstance) {
            pluginInstance = new FileQueueManager();
        }
        return pluginInstance;
    }

    @Override
    public void init(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public void downLoadFile(final Object tag, final String url,
                             final HashMap<String, String> vParams, final String localFileName,
                             final Handler updatehandler, final Response.Listener<File> listener) {
        FileDownloadRequest fileRequest = null;
        try {
            fileRequest = FileDownloadRequest.builder()
                    .setUrl(url)
                    .setRequestBody(vParams != null ? vParams.toString().getBytes("utf-8") : null)
                    .setListner(listener)
                    .setMethod(Request.Method.GET)
                    .setShouldCache(false)
                    .setTag(tag)
                    .setPriority(Request.Priority.LOW)
                    .setFileLocalPath(localFileName)
                    .setDownloadHander(updatehandler).build();
            // 文件下载，重新尝试1次
            fileRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 2, 1.0f));
            mRequestQueue.add(fileRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

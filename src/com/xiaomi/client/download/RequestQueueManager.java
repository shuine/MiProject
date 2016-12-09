
package com.xiaomi.client.download;

import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class RequestQueueManager {

    private static RequestQueueManager instance;
    protected RequestQueue mRequestQueue;
    protected Context mContext;

    public static RequestQueueManager getInstance() {
        if (null == instance) {
            instance = new RequestQueueManager();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= 9) {
            mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
        } else {
            mRequestQueue = Volley.newRequestQueue(context);
        }
    }

    public RequestQueue getRequestQueue() {
        if (null != mRequestQueue) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("Not initialized");
        }

    }

    public <T> Request postApiRequest(final Object tag, final String url,
                                      final HashMap<String, String> paramsMap, final Class clz, boolean isShouldCache,
                                      final Listener<T> listener) {
        ShopJSONRequest request = ShopJSONRequest.builder().setTag(tag).setUrl(url).setClass(clz)
                .setShouldCache(isShouldCache).setListner(listener).addParams(paramsMap).build();

        return mRequestQueue.add(request);
    }

    public <T> Request postApiRequest(final Object tag, final String url,
                                      final HashMap<String, String> paramsMap, final Class clz,
                                      final Listener<T> listener) {
        return postApiRequest(tag, url, paramsMap, clz, true, listener);
    }

    public <T> Request postApiRequest(final Object tag, final String url,
                                      final HashMap<String, String> paramsMap, final TypeReference<T> typeReference,
                                      boolean isShouldCache, final Listener<T> listener) {
        ShopJSONRequest request = ShopJSONRequest.builder().setTag(tag).setUrl(url)
                .setTypeToken(typeReference).setShouldCache(isShouldCache).setListner(listener)
                .addParams(paramsMap).build();

        return mRequestQueue.add(request);
    }

    public <T> Request postApiRequest(final Object tag, final String url,
                                      final HashMap<String, String> paramsMap, final TypeReference<T> typeReference,
                                      final Listener<T> listener) {
        return postApiRequest(tag, url, paramsMap, typeReference, true, listener);
    }

    public Request addRequest(Request request) {
        return mRequestQueue.add(request);
    }

    // 清空一个Post请求
    public void clearRequest(final Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}

package com.xiaomi.client.download;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;

/**
 * Created by wuyexiong on 4/3/15.
 */
public class NetworkCompletedEvent {
    public NetworkResponse mNetworkResponse;
    public Request mRequest;
    public VolleyError mError;

    public NetworkCompletedEvent(NetworkResponse networkResponse, Request request) {
        mNetworkResponse = networkResponse;
        mRequest = request;
    }

    public NetworkCompletedEvent(NetworkResponse networkResponse, Request<?> request, VolleyError error) {
        this.mNetworkResponse = networkResponse;
        this.mRequest = request;
        this.mError = error;
    }

    public NetworkCompletedEvent(Request<?> request, VolleyError error) {
        this.mRequest = request;
        this.mError = error;
    }
}

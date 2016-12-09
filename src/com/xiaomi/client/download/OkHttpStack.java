/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Circle Internet Financial
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.xiaomi.client.download;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.ssl.HttpsTrustManager;
import com.android.volley.toolbox.HttpStack;
import com.xiaomi.client.ClientApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * OkHttp backed {@link HttpStack HttpStack} that does not
 * use okhttp-urlconnection
 */
public class OkHttpStack implements HttpStack {
    private final static String TAG = "OkHttpStack";
    private final static String DEFAULT_CACHE = "okhttp-cache";
    private final static long DEFAULT_CACHE_SIZE = 10 * 1024 * 1024;//默认缓存10M，Picasso是额外的缓存位置
    public final static int DEFAULT_CONNECT_TIMEOUT_MILLI_SECOND = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
    public final static int DEFAULT_WRITE_TIMEOUT_MILLI_SECOND = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
    public final static int DEFAULT_READ_TIMEOUT_MILLI_SECOND = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;

    protected OkHttpClient mOkHttpClient;

    public OkHttpStack() {
        Cache cache = createCache();
        mOkHttpClient = createOkhttpClient(cache);
    }

    public void shutdown() {
        Cache cache = mOkHttpClient.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                Log.e(TAG, "close mCache failed.", e);
            }
        }
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {

        okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());

        Map<String, String> headers = request.getHeaders();
        for (final String name : headers.keySet()) {
            okHttpRequestBuilder.addHeader(name, headers.get(name));
        }
        for (final String name : additionalHeaders.keySet()) {
            okHttpRequestBuilder.addHeader(name, additionalHeaders.get(name));
        }
        setConnectionParametersForRequest(okHttpRequestBuilder, request);
        okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
        Call okHttpCall;
        long timeout = request.getTimeoutMs();
        if (timeout != DefaultRetryPolicy.DEFAULT_TIMEOUT_MS) {
            okHttpCall = mOkHttpClient.newBuilder().connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(okHttpRequest);
        } else {
            okHttpCall = mOkHttpClient.newCall(okHttpRequest);
        }
        Response okHttpResponse = okHttpCall.execute();

        StatusLine responseStatus = new BasicStatusLine(parseProtocol(okHttpResponse.protocol()),
                okHttpResponse.code(), okHttpResponse.message());
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(entityFromOkHttpResponse(okHttpResponse));

        Headers responseHeaders = okHttpResponse.headers();
        for (int i = 0, len = responseHeaders.size(); i < len; i++) {
            final String name = responseHeaders.name(i), value = responseHeaders.value(i);
            if (name != null) {
                response.addHeader(new BasicHeader(name, value));
            }
        }

        return response;
    }

    protected Cache createCache() {
        File cacheDir = createDefaultCacheDir(ClientApplication.instance, DEFAULT_CACHE);
        Cache cache = new Cache(cacheDir.getAbsoluteFile(), DEFAULT_CACHE_SIZE);
        return cache;
    }

    protected OkHttpClient createOkhttpClient(Cache cache) {
        SSLSocketFactory sslSocketFactory = new HttpsTrustManager().getSocketFactory();
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLI_SECOND, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLI_SECOND, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLI_SECOND, TimeUnit.MILLISECONDS)
                .cache(cache)
                .sslSocketFactory(sslSocketFactory)
                .build();
    }

    protected File createDefaultCacheDir(Context context, String dirName) {
        File cache = new File(context.getApplicationContext().getCacheDir(), dirName);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    private static HttpEntity entityFromOkHttpResponse(Response r) throws IOException {
        BasicHttpEntity entity = new BasicHttpEntity();
        ResponseBody body = r.body();

        entity.setContent(body.byteStream());
        entity.setContentLength(body.contentLength());
        entity.setContentEncoding(r.header("Content-Encoding"));

        if (body.contentType() != null) {
            entity.setContentType(body.contentType().type());
        }
        return entity;
    }

    @SuppressWarnings("deprecation")
    private static void setConnectionParametersForRequest(okhttp3.Request.Builder builder, Request<?> request)
            throws IOException, AuthFailureError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                // Ensure backwards compatibility.  Volley assumes a request with a null body is a GET.
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    builder.post(RequestBody.create(MediaType.parse(request.getPostBodyContentType()), postBody));
                }
                break;
            case Request.Method.GET:
                builder.get();
                break;
            case Request.Method.DELETE:
                builder.delete();
                break;
            case Request.Method.POST:
                builder.post(createRequestBody(request));
                break;
            case Request.Method.PUT:
                builder.put(createRequestBody(request));
                break;
            case Request.Method.HEAD:
                builder.head();
                break;
            case Request.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;
            case Request.Method.TRACE:
                builder.method("TRACE", null);
                break;
            case Request.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static ProtocolVersion parseProtocol(final Protocol p) {
        switch (p) {
            case HTTP_1_0:
                //Log.d(TAG, "the protocal is http 1.0");
                return new ProtocolVersion("HTTP", 1, 0);
            case HTTP_1_1:
               //Log.d(TAG, "the protocal is http 1.1");
                return new ProtocolVersion("HTTP", 1, 1);
            case SPDY_3:
                //Log.d(TAG, "the protocal is spdy 3.1");
                return new ProtocolVersion("SPDY", 3, 1);
            case HTTP_2:
                //Log.d(TAG, "the protocal is http 2.0");
                return new ProtocolVersion("HTTP", 2, 0);
        }

        throw new IllegalAccessError("Unkwown protocol");
    }

    private static RequestBody createRequestBody(Request r) throws AuthFailureError {
        final byte[] body = r.getBody();
        if (body == null) {
            //这里是okhttp3的一个bug，必须不为空.
            return RequestBody.create(null, new byte[0]);
        } else {
            return RequestBody.create(MediaType.parse(r.getBodyContentType()), body);
        }
    }
}

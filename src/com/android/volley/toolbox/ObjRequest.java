
package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xiaomi.client.download.ShopApiError;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class ObjRequest<T> extends BaseRequest<T> {

    public ObjRequest(Builder<?> builder) {
        super(builder);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) throws VolleyError {
        try {
            String charset = HttpHeaderParser.parseCharset(response.headers);
            String responseString = null;
            if (mGzipEnabled && isGzipped(response)) {
                try {
                    byte[] data = decompressResponse(response.data);
                    responseString = new String(data, charset);
                } catch (IOException e) {
                    // it seems that result is not GZIP
                }
            }

            if (responseString == null) {
                responseString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
            }

            return parseApiResponse(responseString, response);
        } catch (UnsupportedEncodingException e) {
            throw new ShopApiError(e, response);
        }
    }

    protected abstract Response<T> parseApiResponse(String responseString, NetworkResponse response)
            throws VolleyError;

}

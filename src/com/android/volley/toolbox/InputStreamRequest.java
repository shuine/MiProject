
package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wuyexiong on 4/3/15.
 */
public class InputStreamRequest extends BaseRequest<InputStream> {

    public InputStreamRequest(Builder<?> builder) {
        super(builder);
    }

    @Override
    protected Response<InputStream> parseNetworkResponse(NetworkResponse response) {
        // InputStream inputStream = new BufferedInputStream(new
        // ByteArrayInputStream(response.data));
        // try {
        // boolean supported = inputStream.markSupported();
        // if (supported) {
        // inputStream.reset();
        // }
        // } catch (IOException e) {
        // }
        final byte[] responseByte = response.data;
        InputStream inputStream = null;
        if (mGzipEnabled && isGzipped(response)) {
            try {
                byte[] data = decompressResponse(responseByte);
                inputStream = new ByteArrayInputStream(data);
            } catch (IOException e) {
                // it seems that result is not GZIP
            }
        }
        if (inputStream == null) {
            inputStream = new ByteArrayInputStream(responseByte);
        }
        return Response.success(inputStream, HttpHeaderParser.parseCacheHeaders(response));
    }

    public static Builder<?> builder() {
        return new Builder();
    }

    public static class Builder<B extends Builder<B>> extends BaseRequest.Builder<B> {
        public InputStreamRequest build() {
            return new InputStreamRequest(this);
        }
    }
}

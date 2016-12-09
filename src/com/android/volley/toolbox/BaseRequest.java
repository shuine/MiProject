
package com.android.volley.toolbox;

import android.os.Build;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.xiaomi.client.util.ExtendedAuthToken;
import com.xiaomi.client.util.NetworkUtil;
import com.xiaomi.client.util.RequestConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

/**
 */
public abstract class BaseRequest<T> extends Request<T> {

    protected static final String TAG = "BaseRequest";

    protected static final String HEADER_ENCODING = "Content-Encoding";
    protected static final String HEADER_USER_AGENT = "User-Agent";
    protected static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    protected static final String ENCODING_GZIP = "gzip";
    protected static final String CLIENT_ID = "Mishop-Client-Id";
    protected static final String COOKIE = "Cookie";

    private static final CookieManager sCookieManager = new CookieManager(null,
            CookiePolicy.ACCEPT_ALL);
    private String mUserId;
    private String mCUserId;
    private ExtendedAuthToken mToken;
    private static String sCookie;

    protected boolean mGzipEnabled = true;
    protected Priority mPriority;

    private byte[] mRequestBody;
    protected ConcurrentHashMap<String, String> mParams = new ConcurrentHashMap<String, String>();
    protected ConcurrentHashMap<String, String> mHeaders = new ConcurrentHashMap<String, String>();
    protected ConcurrentHashMap<String, String> mParamsForCacheKey = new ConcurrentHashMap<String, String>();

    public BaseRequest(Builder<?> builder) {
        super(builder.mMethod, builder.mUrl, builder.mListener);
        mGzipEnabled = builder.mGzipEnabled;
        mPriority = builder.mPriority;
        mParams.putAll(builder.mParams);
        mHeaders.putAll(builder.mHeaders);
        mParamsForCacheKey.putAll(builder.mParams);
        mParamsForCacheKey.putAll(builder.mHeaders);
        handleIgnoreCacheKeys(builder.mIgnoreCachekeys, mParamsForCacheKey);
        addCustomParams(mParams);

        setTag(builder.mTag);
        setShouldHttps(builder.mHttpsEnabled);
        setShouldCache(builder.mShouldCache);
        mRequestBody = builder.mRequestBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mGzipEnabled) {
            mHeaders.put(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        }
        mHeaders.put(CLIENT_ID, RequestConstants.Values.CLIENT_ID);
        mHeaders.put(RequestConstants.Keys.NETWORK_STAT, NetworkUtil.getnetworkTypeStr());
       /* mHeaders.put(RequestConstants.Keys.SCREEN_WIDTH_PX,
                String.valueOf(ScreenInfo.getInstance().getScreenWidth()));
        mHeaders.put(RequestConstants.Keys.SCREEN_DENSITYDPI,
                String.valueOf(ScreenInfo.getInstance().getScreenDensitydpi()));
        mHeaders.put(RequestConstants.Keys.VERSION_CDOE,
                String.valueOf(Device.SHOP_VERSION));
        mHeaders.put(RequestConstants.Keys.VERSION_NAME,
                TextUtils.isEmpty(Device.SHOP_VERSION_STRING) ? ""
                        : String.valueOf(Device.SHOP_VERSION_STRING));
        mHeaders.put(RequestConstants.Keys.DEVICE_ID,
                DeviceUtil.getInstance().getUniqueId());*/
        mHeaders.put(RequestConstants.Keys.IS_PAD, RequestConstants.Values.IS_PAD ? "1" : "0");
      //  mHeaders.put(RequestConstants.Keys.MODEL, Device.MODEL);
        mHeaders.put(RequestConstants.Keys.ANDROID_VERSION, String.valueOf(Build.VERSION.SDK_INT));
       /* if (null != ShopApp.instance && !TextUtils.isEmpty(ShopApp.instance.channelId)) {
            mHeaders.put(RequestConstants.Keys.ChANNEL_ID, ShopApp.instance.channelId);
        }*/
        String cookie = getCookies();
        if (!TextUtils.isEmpty(cookie)) {
            mHeaders.put(COOKIE, cookie);
        }
        if (null != mParams) {
        //    mHeaders.put(RequestConstants.Keys.Auth, ShopSigned.getSignedStr(mParams));
        }
        String uuid = getUuid();
        if (!TextUtils.isEmpty(uuid)) {
            mHeaders.put(RequestConstants.Keys.uuid, uuid);
        }
        return mHeaders;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mRequestBody != null) {
            return mRequestBody;
        }
        return super.getBody();
    }

    // 追加用户自定义的参数，切记不要在getParams中额外添加了
    protected boolean addCustomParams(Map<String, String> vParams) {
        return true;
    }

    /**
     * 清除一些一直在变换的参数,为了保证Etag能正常运行
     *
     * @param ignoreCachekeys
     * @param params
     */
    protected void handleIgnoreCacheKeys(ArrayList<String> ignoreCachekeys,
                                         ConcurrentHashMap<String, String> params) {
        if (ignoreCachekeys != null) {
            for (String key : ignoreCachekeys) {
                params.remove(key);
            }
        }
    }

    @Override
    protected final Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public String getCacheKey() {
        SortedSet<String> keys = new TreeSet<String>(mParamsForCacheKey.keySet());
        String nextSeparator = "";
        StringBuilder builder = new StringBuilder(getUrl()).append("{");
        for (String key : keys) {
            builder.append(nextSeparator);
            nextSeparator = ",";
            String value = mParamsForCacheKey.get(key);
            builder.append(key).append("=").append(value);
        }
        return builder.append("}").toString();
    }

    @Override
    public Priority getPriority() {
        return mPriority != null ? mPriority : super.getPriority();
    }

    protected boolean isGzipped(NetworkResponse response) {
        Map<String, String> headers = response.headers;
        return headers != null && !headers.isEmpty() && headers.containsKey(HEADER_ENCODING)
                && headers.get(HEADER_ENCODING).equalsIgnoreCase(ENCODING_GZIP);
    }

    protected byte[] decompressResponse(byte[] compressed) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            int size;
            ByteArrayInputStream memstream = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(memstream);
            final int buffSize = 8192;
            byte[] tempBuffer = new byte[buffSize];
            baos = new ByteArrayOutputStream();
            while ((size = gzip.read(tempBuffer, 0, buffSize)) != -1) {
                baos.write(tempBuffer, 0, size);
            }
            return baos.toByteArray();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
    }

    public static Builder<?> builder() {
        return new Builder();
    }

    public static class Builder<B extends Builder<B>> {

        private String mUrl;
        private Object mTag;
        private int mMethod = Method.POST;
        protected byte[] mRequestBody;
        private Priority mPriority = Priority.NORMAL;

        private boolean mGzipEnabled = true;
        private boolean mHttpsEnabled = false;
        private boolean mShouldCache = true;

        private ConcurrentHashMap<String, String> mParams = new ConcurrentHashMap<String, String>();
        private ConcurrentHashMap<String, String> mHeaders = new ConcurrentHashMap<String, String>();
        private ArrayList<String> mIgnoreCachekeys = new ArrayList<String>();

        private Response.Listener mListener;

        protected B self() {
            return (B) this; // Unchecked cast!
        }

        public B addHeader(String key, String value) {
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                mHeaders.put(key, value);
            }
            return self();
        }

        public B addParams(String key, String value) {
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                mParams.put(key, value);
            }
            return self();
        }

        public B addParams(String key, long value) {
            if (!TextUtils.isEmpty(key) && value >= 0) {
                mParams.put(key, String.valueOf(value));
            }
            return self();
        }

        public B addParams(String key, int value) {
            if (!TextUtils.isEmpty(key) && value >= 0) {
                mParams.put(key, String.valueOf(value));
            }
            return self();
        }

        public B addParams(HashMap<String, String> paramsMap) {
            if (null != paramsMap && paramsMap.size() > 0) {
                mParams.putAll(paramsMap);
            }
            return self();
        }

        public B setUrl(String url) {
            mUrl = url;
            if (!TextUtils.isEmpty(url) && url.toLowerCase().startsWith("https")) {
                setShouldHttps(true);
            }
            return self();
        }

        public B setMethod(int method) {
            mMethod = method;
            return self();
        }

        public B setGzipEnabled(boolean isGzipEnabled) {
            mGzipEnabled = isGzipEnabled;
            return self();
        }

        public B setShouldHttps(boolean shouldHttps) {
            mHttpsEnabled = shouldHttps;
            return self();
        }

        public B setShouldCache(boolean isShouldCache) {
            mShouldCache = isShouldCache;
            return self();
        }

        public B setPriority(Priority priority) {
            mPriority = priority;
            return self();
        }

        public B setTag(Object tag) {
            mTag = tag;
            return self();
        }

        public B setListner(Response.Listener listener) {
            mListener = listener;
            return self();
        }

        public B setRequestBody(byte[] requestBody) {
            mRequestBody = requestBody;
            return self();
        }

        public B addIgnoreCacheKey(String key) {
            if (!TextUtils.isEmpty(key)) {
                mIgnoreCachekeys.add(key);
            }
            return self();
        }

        // public BaseRequest build() {
        // return new BaseRequest(this);
        // }
    }

    private String getCookies() {
        String userId = "";
        String cUserId = "";
        ExtendedAuthToken token = null;
        /*LoginManager loginManager = LoginManager.getInstance();
        if (loginManager != null && loginManager.hasLogin()) {
            userId = LoginManager.getInstance().getAccountId();
            cUserId = LoginManager.getInstance().getPrefEncryptedUserId();
            token = LoginManager.getInstance().getExtendedAuthToken(
                    AccountConstants.DEFAULT_SERVICE_ID);
        }*/

        if (token == null) {
            token = ExtendedAuthToken.build("", "");
        }

        if (!TextUtils.equals(userId, mUserId) || !token.equals(mToken)
                || !TextUtils.equals(cUserId, mCUserId)) {
            mUserId = userId;
            mCUserId = cUserId;
            if (TextUtils.isEmpty(token.authToken)) {
                mToken = null;
            } else {
                mToken = token;
            }
            sCookie = null;
        }

        if (mToken != null && !TextUtils.isEmpty(mToken.authToken)
                && sCookie == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("serviceToken=");
            sb.append(mToken.authToken);
            sCookie = sb.toString();
        }

        List<HttpCookie> cookieList = sCookieManager.getCookieStore().getCookies();
        if (cookieList == null || cookieList.size() == 0) {
            return sCookie;
        }

        StringBuilder sbCookie = new StringBuilder();
        for (HttpCookie cookie : cookieList) {
            if (TextUtils.indexOf(URI.create(getUrl()).getHost(), cookie.getDomain()) > 0) {
                sbCookie.append(cookie.getName());
                sbCookie.append("=");
                sbCookie.append(cookie.getValue());
                sbCookie.append("; ");
            }
        }

        return sbCookie.toString() + sCookie;
    }

    private String getUuid() {
/*        String uuid = PreferenceUtil.getStringPref(ShopApp.instance,
                Constants.Preference.PREF_KEY_HEADER_UUID, "");*/
        return "";
    }
}

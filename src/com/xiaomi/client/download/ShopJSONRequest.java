
package com.xiaomi.client.download;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BaseRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ObjRequest;
import com.xiaomi.client.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class ShopJSONRequest<T> extends ObjRequest<T> {

    private static final String TAG = "ShopJsonRequest";

    public static boolean DEBUG = Log.isDebug();

    protected Class<T> mClass;
    protected TypeReference<T> mTypeToken;

    public ShopJSONRequest(Builder<?> builder) {
        super(builder);
        if (null != builder.mClass) {
            mClass = builder.mClass;
        }
        if (null != builder.mTypeToken) {
            mTypeToken = builder.mTypeToken;
        }

        if (mClass == null && mTypeToken == null) {
            throw new RuntimeException("ShopJSONRequest must have one Parse class or TypeToken");
        }
    }

    @Override
    protected Response parseApiResponse(String responseString, NetworkResponse response)
            throws VolleyError {
        try {
            handleServiceToken(response);
            if (DEBUG) {
                Log.v(TAG, "ShopJSONRequest >> " + responseString);
            }
            ShopApiBaseResult baseResult = parseShopApi(responseString);
            if (!TextUtils.isEmpty(baseResult.mCloseLink)) {
               // EventBus.getDefault().post(new CloseSiteEvent(baseResult.mCloseLink));
                throw new ParseError(response);
            }
            if (baseResult.isApiOk(mClass != null && mClass.isAssignableFrom(Void.class))) {
                T data = null;
                if (mTypeToken == null) {
                    if (mClass.isAssignableFrom(JSONObject.class)) {
                        data = (T) new JSONObject(baseResult.mJsonData.toString());
                    } else if (mClass.isAssignableFrom(JSONArray.class)) {
                        data = (T) new JSONArray(baseResult.mJsonData.toString());
                    } else if (mClass.isAssignableFrom(String.class)) {
                        data = (T) baseResult.mJsonData.toString();
                    } else if (mClass.isAssignableFrom(Void.class)) {
                        return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                    } else {
                        data = JSON.parseObject(baseResult.mJsonData.toString(), mClass);
                    }
                } else {
                    data = JSON.parseObject(baseResult.mJsonData.toString(), mTypeToken);
                }

                if (data == null) {
                    ShopApiError error = new ShopApiError(response);
                    error.setErrorType(ShopApiError.ErrorType.PARSE);
                    throw error;
                } else {
                    return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
                }
            } else {
                ShopApiError error = new ShopApiError(response);
                error.setApiBaseResult(baseResult);
                error.setErrorType(ShopApiError.ErrorType.CUSTOM);
                throw error;
            }
        } catch (JSONException e) {
            ShopApiError error = new ShopApiError(response);
            error.setErrorType(ShopApiError.ErrorType.PARSE);
            throw error;
        }
    }

    private void handleServiceToken(NetworkResponse response) {
        if (response != null && response.headers != null) {
            String refreshTokenString = response.headers.get("Token-Refresh");
            if (!TextUtils.isEmpty(refreshTokenString)) {
                boolean refreshToken = Boolean.parseBoolean(refreshTokenString);
              /*  if (refreshToken && LoginManager.getInstance().hasLogin()) {
                    if (System.currentTimeMillis() - ShopApp.invalidate_event_time > 10 *1000) {
                        ShopApp.invalidate_event_time = System.currentTimeMillis();
                        String oldServiceToken = LoginManager.getInstance().getExtendedAuthToken(AccountConstants.DEFAULT_SERVICE_ID).toPlain();
                        LoginManager.iShopAccountManager.invalidateAuthToken("com.xiaomi",
                                oldServiceToken);
                        String newServiceToken = LoginManager.getInstance().getAccountAuthToken(AccountConstants.DEFAULT_SERVICE_ID);
                        ExtendedAuthToken token = ExtendedAuthToken.parse(newServiceToken);
                        PreferenceUtil.setStringPref(ShopApp.instance, AccountConstants.PREF_EXTENDED_TOKEN,
                                token.toPlain());
                    }
                }*/
            }
        }
    }

    protected ShopApiBaseResult parseShopApi(String apiJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(apiJson);
        String closeLink = null;
        StringBuilder sBuilder = null;
        String result = jsonObject.optString("info");
        long code = jsonObject.optLong("code", -1);
        String desc = jsonObject.optString("desc");
        sBuilder = new StringBuilder(jsonObject.optString("data"));
        closeLink = jsonObject.optString("close_link");
        ShopApiBaseResult.Builder builder = new ShopApiBaseResult.Builder()
                .setCode(code)
                .setInfo(result).setDescription(desc).setCloseLink(closeLink)
                .setJsonData(sBuilder);
        //强制登录
       /* if (code == 10000200010001000L || code == 21457512) {
            LoginManager loginManager = LoginManager.getInstance();
            if (loginManager != null && loginManager.hasLogin()) {
                LoginManager.getInstance().logout();
                SubProcessLoginManager.getInstance().broadcastLogout();
            }
        }*/
        //token过期
     /*   if (code == 10000200010001004L || code == 10000200010001007L) {
            LoginManager loginManager = LoginManager.getInstance();
            if (loginManager != null && loginManager.hasLogin()) {
                EventBus.getDefault().post(new RelogoutEvent(System.currentTimeMillis()));
            }
        }*/
        return builder.build();
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError instanceof ShopApiError) {
            return volleyError;
        }
        ShopApiError shopApiError = new ShopApiError(volleyError.networkResponse);
        shopApiError.initCause(volleyError.getCause());
        shopApiError.setNetworkTimeMs(volleyError.getNetworkTimeMs());
        if (volleyError instanceof NoConnectionError) {
            shopApiError.setErrorType(ShopApiError.ErrorType.NOCONNECTION);
        } else if (volleyError instanceof ServerError) {
            shopApiError.setErrorType(ShopApiError.ErrorType.SERVER);
        } else if (volleyError instanceof TimeoutError) {
            shopApiError.setErrorType(ShopApiError.ErrorType.TIMEOUT);
        } else if (volleyError instanceof ParseError) {
            shopApiError.setErrorType(ShopApiError.ErrorType.PARSE);
        } else if (volleyError instanceof NetworkError) {
            shopApiError.setErrorType(ShopApiError.ErrorType.NETWORK);
        }
        return shopApiError;
    }

    public static Builder<?> builder() {
        return new Builder();
    }

    public static class Builder<B extends Builder<B>> extends BaseRequest.Builder<B> {

        private Class mClass;
        private TypeReference mTypeToken;

        public B setClass(Class clz) {
            mClass = clz;
            return self();
        }

        public B setTypeToken(TypeReference typeToken) {
            mTypeToken = typeToken;
            return self();
        }

        public ShopJSONRequest build() {
            return new ShopJSONRequest(this);
        }

    }

}

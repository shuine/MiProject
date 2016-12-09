
package com.xiaomi.client.download;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.xiaomi.client.ClientApplication;
import com.xiaomi.client.R;
import com.xiaomi.client.util.ToastUtil;

/**
 */
public class ShopApiError extends VolleyError {

    private ShopApiBaseResult mApiBaseResult;
    private ErrorType mErrorType = ErrorType.UNKNOW;

    public ShopApiError(NetworkResponse response) {
        super(response);
    }

    public ShopApiError(Throwable throwable, NetworkResponse networkResponse) {
        super(throwable, networkResponse);
    }

    public ShopApiBaseResult getApiBaseResult() {
        return mApiBaseResult;
    }

    public void setApiBaseResult(ShopApiBaseResult data) {
        mApiBaseResult = data;
    }

    public enum ErrorType {
        NOCONNECTION(R.string.network_errortip_noconnection,
                R.string.network_errorbtntip_noconnection),
        SERVER(R.string.network_errortip_server, R.string.network_errorbtntip_server),
        NETWORK(R.string.network_errortip_server, R.string.network_errorbtntip_server),
        TIMEOUT(R.string.network_errortip_timeout, R.string.network_errorbtntip_timeout),
        PARSE(R.string.network_errortip_parse, R.string.network_errorbtntip_parse),
        CUSTOM(R.string.network_errortip_custom, R.string.network_errorbtntip_custom),
        UNKNOW(R.string.network_errortip_unknow, R.string.network_errorbtntip_unknow),
        LOGIN(R.string.network_errortip_login, R.string.network_errorbtntip_login);

        private int mErrorTipRes;
        private int mErrorBtnRes;

        ErrorType(int errorTipRes, int errorBtnRes) {
            mErrorTipRes = errorTipRes;
            mErrorBtnRes = errorBtnRes;
        }

        public int getErrorTipRes() {
            return mErrorTipRes;
        }

        public int getErrorBtnRes() {
            return mErrorBtnRes;
        }
    }

    public void setErrorType(ErrorType errorType) {
        mErrorType = errorType;
    }

    public ErrorType getErrorType() {
        return mErrorType;
    }

    @Override
    public String toString() {
        return "ShopApiError{" +
                "Cause=" + (null == getCause() ? null : getCause().toString()) +
                ", mApiBaseResult=" + mApiBaseResult +
                ", mErrorType=" + mErrorType +
                '}';
    }

    public static void toastError(VolleyError error) {
        if (error instanceof ShopApiError) {
            ShopApiError apiError = (ShopApiError) error;
            if (apiError.getErrorType() == ErrorType.CUSTOM) {
                if (!TextUtils.isEmpty(apiError.getApiBaseResult().mDescription)) {
                    ToastUtil.show(apiError.getApiBaseResult().mDescription);
                    return;
                }
            }
            ToastUtil.show(ClientApplication.instance.getString(apiError.getErrorType().getErrorTipRes()));
        } else {
            ToastUtil.show(ClientApplication.instance.getString(ErrorType.SERVER.getErrorTipRes()));
        }
    }
}

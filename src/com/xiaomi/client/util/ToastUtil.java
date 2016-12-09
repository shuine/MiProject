
package com.xiaomi.client.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.xiaomi.client.ClientApplication;

public class ToastUtil {

    private static Toast sLastToast = null;

    public static void clear() {
        if (sLastToast != null) {
            sLastToast.cancel();
        }
    }

    private static void show(Context context, CharSequence text, int duration) {
        if (sLastToast != null) {
            sLastToast.cancel();
        }
        //注意，这里不可使用参数context作为makeText的输入，否则会导致sLastToast持有context，造成context泄露
        Toast toast = Toast.makeText(ClientApplication.instance, text, duration);
        sLastToast = toast;
        toast.show();
    }

    public static void show(CharSequence text) {
        show(ClientApplication.instance, text, Toast.LENGTH_SHORT);
    }

    public static void show(Activity context, int resId) {
        if (null == context) {
            return;
        }
        show(context, context.getString(resId), Toast.LENGTH_SHORT);
    }

    public static void showLong(CharSequence text) {
        show(ClientApplication.instance, text, Toast.LENGTH_LONG);
    }

    public static void showLong(Activity context, int resId) {
        if (null == context) {
            return;
        }
        show(context, context.getString(resId), Toast.LENGTH_LONG);
    }

    public static void showApiError(VolleyError error) {
        /*if (error instanceof ShopApiError) {
            ShopApiError apiError = (ShopApiError) error;
            ShopApiBaseResult apiBaseResult = apiError.getApiBaseResult();
            if (apiBaseResult != null && !TextUtils.isEmpty(apiBaseResult.mDescription)) {
                show(apiBaseResult.mDescription);
            }
        }*/
    }
}


package com.xiaomi.client.util;

/**
 */
public class RequestConstants {
    private final static String TAG = "RequestConstants";

    public static final class Keys {

        public static final String USER_ID = "user_Id";
        // 手机分辨率
        public static final String SCREEN_WIDTH_PX = "Screen-width-px";

        public static final String SCREEN_DENSITY = "Screen-density";

        public static final String SCREEN_DENSITYDPI = "Screen-DensityDpi";

        public static final String NETWORK_STAT = "Network-Stat";

        public static final String DEVICE_ID = "Device-Id";

        public static final String PHONE_TYPE = "phone_type";

        public static final String PHONE_NAME = "phone_name";

        public static final String CITY_NAME = "city_name";

        public static final String Auth = "Mishop-Auth";

        public static final String ClientID = "Mishop-Client-Id";

        public static final String VERSION_CDOE = "Mishop-Client-VersionCode";

        public static final String VERSION_NAME = "Mishop-Client-VersionName";

        public static final String uuid = "Uuid";

        public static final String ChANNEL_ID = "Mishop-Channel-Id";
        /**
         * 是否是pad
         * 没有此参数或者值为0,表示是手机
         * 值为1，表示是pad
         */
        public static final String IS_PAD = "Mishop-Is-Pad";

        /**
         * Model
         * 表示具体是哪个手机
         */
        public static final String MODEL = "Mishop-Model";
        /**
         * Android系统版本
         */
        public static final String ANDROID_VERSION = "Android-Ver";
    }

    public static final class Values {
        // 每页请求数量
        public static final int PAGESIZE_VALUE = 20;
        // APP Id
        public static String CLIENT_ID;
        //IS_PAD
        public static boolean IS_PAD;

        static {
            IS_PAD = false;//Constants.REAL_PACKAGE_NAME_PAD.equalsIgnoreCase(ShopApp.instance.getPackageName());
            CLIENT_ID = IS_PAD ? "180100041074" : "180100031052";
            Log.d(TAG, "IS_PAD=%s.CLIENT_ID=%s.", IS_PAD, CLIENT_ID);
        }
    }
}
